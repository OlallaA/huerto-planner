package com.olalla.plantplan.repository;

import com.olalla.plantplan.entity.ReminderStatus;
import com.olalla.plantplan.entity.Reminder;
import com.olalla.plantplan.entity.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserIdAndStatusInAndScheduledDateBetweenOrderByScheduledDateAsc(
            Long userId,
            Collection<ReminderStatus> estados,
            LocalDate from,
            LocalDate to
    );

    boolean existsBySeedIdAndTypeAndStatusAndScheduledDate(
            Long seedId,
            ReminderType tipo,
            ReminderStatus estado,
            LocalDate scheduledDate
    );

    boolean existsBySeedlingIdAndTypeAndStatusAndScheduledDate(
            Long seedlingId,
            ReminderType tipo,
            ReminderStatus estado,
            LocalDate scheduledDate
    );

    boolean existsByCropIdAndTypeAndStatus(
            Long cropId,
            ReminderType tipo,
            ReminderStatus estado
    );

    Optional<Reminder> findFirstByCropIdAndTypeAndStatusOrderByScheduledDateDesc(
            Long cropId,
            ReminderType tipo,
            ReminderStatus estado
    );

    List<Reminder> findBySeedIdAndTypeInAndStatus(
            Long seedId,
            Collection<ReminderType> tipos,
            ReminderStatus estado
    );

    List<Reminder> findBySeedlingIdAndTypeInAndStatus(
            Long seedlingId,
            Collection<ReminderType> tipos,
            ReminderStatus estado
    );

    List<Reminder> findByCropIdAndStatus(Long cropId, ReminderStatus estado);

    List<Reminder> findBySeedId(Long seedId);

    List<Reminder> findBySeedlingId(Long seedlingId);

    List<Reminder> findByCropId(Long cropId);
}
