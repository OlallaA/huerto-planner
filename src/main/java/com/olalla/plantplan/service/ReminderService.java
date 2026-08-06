package com.olalla.plantplan.service;

import com.olalla.plantplan.dto.ReminderResponse;
import com.olalla.plantplan.entity.Crop;
import com.olalla.plantplan.entity.ReminderStatus;
import com.olalla.plantplan.entity.CropSheet;
import com.olalla.plantplan.entity.Garden;
import com.olalla.plantplan.entity.Seedling;
import com.olalla.plantplan.entity.Reminder;
import com.olalla.plantplan.entity.Seed;
import com.olalla.plantplan.entity.ReminderType;
import com.olalla.plantplan.entity.User;
import com.olalla.plantplan.exception.ForbiddenException;
import com.olalla.plantplan.exception.ResourceNotFoundException;
import com.olalla.plantplan.repository.CropRepository;
import com.olalla.plantplan.repository.SeedlingRepository;
import com.olalla.plantplan.repository.ReminderRepository;
import com.olalla.plantplan.repository.SeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class ReminderService {

    private static final int SOWING_WARNING_DAYS = 14;
    private static final int TRANSPLANT_WARNING_DAYS = 5;

    private static final Set<ReminderType> SOWING_TYPES = EnumSet.of(
            ReminderType.SOWING_START,
            ReminderType.SOWING_END_WARNING
    );

    private static final Set<ReminderType> TRANSPLANT_TYPES = EnumSet.of(
            ReminderType.TRANSPLANT_START,
            ReminderType.TRANSPLANT_END_WARNING
    );

    private final ReminderRepository reminderRepository;
    private final SeedRepository seedRepository;
    private final SeedlingRepository seedlingRepository;
    private final CropRepository cropRepository;

    public ReminderService(
            ReminderRepository reminderRepository,
            SeedRepository seedRepository,
            SeedlingRepository seedlingRepository,
            CropRepository cropRepository
    ) {
        this.reminderRepository = reminderRepository;
        this.seedRepository = seedRepository;
        this.seedlingRepository = seedlingRepository;
        this.cropRepository = cropRepository;
    }

    @Transactional
    public List<ReminderResponse> findByUserAndRange(Long userId, LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("El rango de fechas no es valido");
        }

        ensureUserReminders(userId, from, to);

        return reminderRepository
                .findByUserIdAndStatusInAndScheduledDateBetweenOrderByScheduledDateAsc(
                        userId,
                        List.of(ReminderStatus.PENDING, ReminderStatus.COMPLETED),
                        from,
                        to
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReminderResponse findById(Long userId, Long id) {
        return toResponse(findOwnedEntity(userId, id));
    }

    @Transactional
    public ReminderResponse complete(Long userId, Long id) {
        Reminder reminder = findOwnedEntity(userId, id);

        if (reminder.getStatus() == ReminderStatus.CANCELLED) {
            throw new IllegalArgumentException("No se puede completar un recordatorio cancelado");
        }
        if (reminder.getStatus() == ReminderStatus.COMPLETED) {
            return toResponse(reminder);
        }

        reminder.setStatus(ReminderStatus.COMPLETED);
        reminder.setCompletedAt(LocalDateTime.now());

        if (SOWING_TYPES.contains(reminder.getType()) && reminder.getSeed() != null) {
            cancelRelatedPending(
                    reminderRepository.findBySeedIdAndTypeInAndStatus(
                            reminder.getSeed().getId(),
                            SOWING_TYPES,
                            ReminderStatus.PENDING
                    ),
                    reminder.getId()
            );
        }

        if (TRANSPLANT_TYPES.contains(reminder.getType()) && reminder.getSeedling() != null) {
            cancelRelatedPending(
                    reminderRepository.findBySeedlingIdAndTypeInAndStatus(
                            reminder.getSeedling().getId(),
                            TRANSPLANT_TYPES,
                            ReminderStatus.PENDING
                    ),
                    reminder.getId()
            );
        }

        if (reminder.getType() == ReminderType.WATERING && reminder.getCrop() != null) {
            createNextWatering(reminder.getCrop(), reminder.getScheduledDate());
        }

        return toResponse(reminder);
    }

    @Transactional
    public void generateForSeed(Seed seed) {
        generateSowingForSeed(seed, LocalDate.now().getYear());
        generateSowingForSeed(seed, LocalDate.now().getYear() + 1);
    }

    @Transactional
    public void generateForSeedling(Seedling seedling) {
        if (!seedlingPendingTransplant(seedling)) {
            cancelPendingSeedling(seedling.getId());
            return;
        }
        generateTransplantForSeedling(seedling, LocalDate.now().getYear());
        generateTransplantForSeedling(seedling, LocalDate.now().getYear() + 1);
    }

    @Transactional
    public void generateForCrop(Crop crop) {
        if (crop.getEndDate() != null && !crop.getEndDate().isAfter(LocalDate.now())) {
            cancelPendingCrop(crop.getId());
            return;
        }
        ensureNextWatering(crop);
    }

    @Transactional
    public void deleteForSeed(Long seedId) {
        reminderRepository.findBySeedId(seedId).forEach(reminderRepository::delete);
    }

    @Transactional
    public void deleteForSeedling(Long seedlingId) {
        reminderRepository.findBySeedlingId(seedlingId).forEach(reminderRepository::delete);
    }

    @Transactional
    public void deleteForCrop(Long cropId) {
        reminderRepository.findByCropId(cropId).forEach(reminderRepository::delete);
    }

    private void ensureUserReminders(Long userId, LocalDate from, LocalDate to) {
        List<Seed> seeds = seedRepository.findByCropSheetUserId(userId);
        for (Seed seed : seeds) {
            for (int year = from.getYear(); year <= to.getYear() + 1; year++) {
                generateSowingForSeed(seed, year);
            }
        }

        List<Seedling> seedlings = seedlingRepository.findByCropSheetUserId(userId);
        for (Seedling seedling : seedlings) {
            if (!seedlingPendingTransplant(seedling)) {
                continue;
            }
            for (int year = from.getYear(); year <= to.getYear() + 1; year++) {
                generateTransplantForSeedling(seedling, year);
            }
        }

        List<Crop> crops = cropRepository.findByGardenUserId(userId);
        for (Crop crop : crops) {
            if (crop.getEndDate() != null && crop.getEndDate().isBefore(from)) {
                continue;
            }
            ensureNextWatering(crop);
        }
    }

    private void generateSowingForSeed(Seed seed, int year) {
        CropSheet ficha = seed.getCropSheet();
        Month mesInicio = ficha.getSowingStartMonth();
        Month mesFin = ficha.getSowingEndMonth();
        if (mesInicio == null || mesFin == null) {
            return;
        }

        User user = ficha.getUser();
        LocalDate fechaInicio = LocalDate.of(year, mesInicio, 1);
        LocalDate fechaAviso = YearMonth.of(year, mesFin).atEndOfMonth().minusDays(SOWING_WARNING_DAYS);
        String name = ficha.getName();

        createIfAbsent(
                ReminderType.SOWING_START,
                fechaInicio,
                "Inicio de siembra de " + name,
                "Ha comenzado el periodo de siembra de " + name + ".",
                user,
                ficha,
                seed,
                null,
                null,
                null
        );

        if (!fechaAviso.isBefore(fechaInicio)) {
            createIfAbsent(
                    ReminderType.SOWING_END_WARNING,
                    fechaAviso,
                    "Quedan 2 semanas para sembrar " + name,
                    "El periodo de siembra de " + name + " termina pronto.",
                    user,
                    ficha,
                    seed,
                    null,
                    null,
                    null
            );
        }
    }

    private void generateTransplantForSeedling(Seedling seedling, int year) {
        CropSheet ficha = seedling.getCropSheet();
        Month mesInicio = ficha.getTransplantStartMonth();
        Month mesFin = ficha.getTransplantEndMonth();
        if (mesInicio == null || mesFin == null) {
            return;
        }

        User user = ficha.getUser();
        LocalDate fechaInicio = LocalDate.of(year, mesInicio, 1);
        LocalDate fechaAviso = YearMonth.of(year, mesFin).atEndOfMonth().minusDays(TRANSPLANT_WARNING_DAYS);
        String name = ficha.getName();

        createIfAbsent(
                ReminderType.TRANSPLANT_START,
                fechaInicio,
                "Inicio de trasplante de " + name,
                "Ha comenzado el periodo de trasplante de " + name + ".",
                user,
                ficha,
                null,
                seedling,
                null,
                null
        );

        if (!fechaAviso.isBefore(fechaInicio)) {
            createIfAbsent(
                    ReminderType.TRANSPLANT_END_WARNING,
                    fechaAviso,
                    "Quedan 5 dias para trasplantar " + name,
                    "El periodo de trasplante de " + name + " termina pronto.",
                    user,
                    ficha,
                    null,
                    seedling,
                    null,
                    null
            );
        }
    }

    private void ensureNextWatering(Crop crop) {
        CropSheet ficha = crop.getCropSheet();
        Integer frecuencia = ficha.getWateringFrequencyDays();
        LocalDate transplantDate = crop.getTransplantDate();

        if (frecuencia == null || frecuencia <= 0 || transplantDate == null) {
            return;
        }
        if (crop.getEndDate() != null && !crop.getEndDate().isAfter(LocalDate.now())) {
            cancelPendingCrop(crop.getId());
            return;
        }

        if (reminderRepository.existsByCropIdAndTypeAndStatus(
                crop.getId(),
                ReminderType.WATERING,
                ReminderStatus.PENDING
        )) {
            return;
        }

        LocalDate base = reminderRepository
                .findFirstByCropIdAndTypeAndStatusOrderByScheduledDateDesc(
                        crop.getId(),
                        ReminderType.WATERING,
                        ReminderStatus.COMPLETED
                )
                .map(Reminder::getScheduledDate)
                .orElse(transplantDate);

        LocalDate proximo = base.plusDays(frecuencia);
        LocalDate hoy = LocalDate.now();
        while (proximo.isBefore(hoy)) {
            proximo = proximo.plusDays(frecuencia);
        }

        if (crop.getEndDate() != null && proximo.isAfter(crop.getEndDate())) {
            return;
        }

        Garden garden = crop.getGarden();
        String name = ficha.getName();
        String gardenName = garden.getName();

        Reminder riego = new Reminder();
        riego.setType(ReminderType.WATERING);
        riego.setStatus(ReminderStatus.PENDING);
        riego.setScheduledDate(proximo);
        riego.setTitle("Regar " + name);
        riego.setDescription("Proximo riego de " + name + " en " + gardenName + ".");
        riego.setUser(garden.getUser());
        riego.setCropSheet(ficha);
        riego.setCrop(crop);
        riego.setGarden(garden);
        reminderRepository.save(riego);
    }

    private void createNextWatering(Crop crop, LocalDate fechaUltimoRiego) {
        CropSheet ficha = crop.getCropSheet();
        Integer frecuencia = ficha.getWateringFrequencyDays();
        if (frecuencia == null || frecuencia <= 0) {
            return;
        }

        LocalDate siguiente = fechaUltimoRiego.plusDays(frecuencia);
        if (crop.getEndDate() != null && siguiente.isAfter(crop.getEndDate())) {
            return;
        }

        if (reminderRepository.existsByCropIdAndTypeAndStatus(
                crop.getId(),
                ReminderType.WATERING,
                ReminderStatus.PENDING
        )) {
            return;
        }

        Garden garden = crop.getGarden();
        String name = ficha.getName();

        Reminder riego = new Reminder();
        riego.setType(ReminderType.WATERING);
        riego.setStatus(ReminderStatus.PENDING);
        riego.setScheduledDate(siguiente);
        riego.setTitle("Regar " + name);
        riego.setDescription("Proximo riego de " + name + " en " + garden.getName() + ".");
        riego.setUser(garden.getUser());
        riego.setCropSheet(ficha);
        riego.setCrop(crop);
        riego.setGarden(garden);
        reminderRepository.save(riego);
    }

    private void createIfAbsent(
            ReminderType tipo,
            LocalDate fecha,
            String title,
            String description,
            User user,
            CropSheet ficha,
            Seed seed,
            Seedling seedling,
            Crop crop,
            Garden garden
    ) {
        if (seed != null && reminderRepository.existsBySeedIdAndTypeAndStatusAndScheduledDate(
                seed.getId(), tipo, ReminderStatus.PENDING, fecha
        )) {
            return;
        }
        if (seed != null && reminderRepository.existsBySeedIdAndTypeAndStatusAndScheduledDate(
                seed.getId(), tipo, ReminderStatus.COMPLETED, fecha
        )) {
            return;
        }
        if (seedling != null && reminderRepository.existsBySeedlingIdAndTypeAndStatusAndScheduledDate(
                seedling.getId(), tipo, ReminderStatus.PENDING, fecha
        )) {
            return;
        }
        if (seedling != null && reminderRepository.existsBySeedlingIdAndTypeAndStatusAndScheduledDate(
                seedling.getId(), tipo, ReminderStatus.COMPLETED, fecha
        )) {
            return;
        }

        Reminder reminder = new Reminder();
        reminder.setType(tipo);
        reminder.setStatus(ReminderStatus.PENDING);
        reminder.setScheduledDate(fecha);
        reminder.setTitle(title);
        reminder.setDescription(description);
        reminder.setUser(user);
        reminder.setCropSheet(ficha);
        reminder.setSeed(seed);
        reminder.setSeedling(seedling);
        reminder.setCrop(crop);
        reminder.setGarden(garden);
        reminderRepository.save(reminder);
    }

    private boolean seedlingPendingTransplant(Seedling seedling) {
        if (seedling.getTransplantDate() != null
                && seedling.getTransplantedQuantity() != null
                && seedling.getSownQuantity() != null
                && seedling.getTransplantedQuantity() >= seedling.getSownQuantity()) {
            return false;
        }
        return seedling.getTransplantDate() == null
                || seedling.getTransplantedQuantity() == null
                || seedling.getSownQuantity() == null
                || seedling.getTransplantedQuantity() < seedling.getSownQuantity();
    }

    private void cancelRelatedPending(List<Reminder> pendientes, Long idActual) {
        for (Reminder pendiente : pendientes) {
            if (!pendiente.getId().equals(idActual)) {
                pendiente.setStatus(ReminderStatus.CANCELLED);
            }
        }
    }

    private void cancelPendingSeedling(Long seedlingId) {
        reminderRepository.findBySeedlingIdAndTypeInAndStatus(
                seedlingId,
                TRANSPLANT_TYPES,
                ReminderStatus.PENDING
        ).forEach(reminder -> reminder.setStatus(ReminderStatus.CANCELLED));
    }

    private void cancelPendingCrop(Long cropId) {
        reminderRepository.findByCropIdAndStatus(cropId, ReminderStatus.PENDING)
                .forEach(reminder -> reminder.setStatus(ReminderStatus.CANCELLED));
    }

    private Reminder findEntityById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un recordatorio con id " + id));
    }

    private Reminder findOwnedEntity(Long userId, Long id) {
        Reminder reminder = findEntityById(id);

        if (!reminder.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No puedes acceder al recordatorio con id " + id);
        }

        return reminder;
    }

    private ReminderResponse toResponse(Reminder reminder) {
        CropSheet ficha = reminder.getCropSheet();
        Garden garden = reminder.getGarden();
        Seed seed = reminder.getSeed();
        Seedling seedling = reminder.getSeedling();
        Crop crop = reminder.getCrop();

        return new ReminderResponse(
                reminder.getId(),
                reminder.getType(),
                reminder.getStatus(),
                reminder.getScheduledDate(),
                reminder.getCompletedAt(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getUser().getId(),
                ficha != null ? ficha.getId() : null,
                ficha != null ? ficha.getName() : null,
                seed != null ? seed.getId() : null,
                seedling != null ? seedling.getId() : null,
                crop != null ? crop.getId() : null,
                garden != null ? garden.getId() : null,
                garden != null ? garden.getName() : null
        );
    }
}
