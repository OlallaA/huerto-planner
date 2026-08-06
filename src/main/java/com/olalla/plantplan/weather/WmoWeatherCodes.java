package com.olalla.plantplan.weather;

final class WmoWeatherCodes {

    private WmoWeatherCodes() {
    }

    static String describe(Integer code) {
        if (code == null) {
            return "Desconocido";
        }

        return switch (code) {
            case 0 -> "Despejado";
            case 1 -> "Mayormente despejado";
            case 2 -> "Parcialmente nublado";
            case 3 -> "Nublado";
            case 45, 48 -> "Niebla";
            case 51, 53, 55 -> "Llovizna";
            case 56, 57 -> "Llovizna helada";
            case 61, 63, 65 -> "Lluvia";
            case 66, 67 -> "Lluvia helada";
            case 71, 73, 75 -> "Nieve";
            case 77 -> "Granos de nieve";
            case 80, 81, 82 -> "Chubascos";
            case 85, 86 -> "Chubascos de nieve";
            case 95 -> "Tormenta";
            case 96, 99 -> "Tormenta con granizo";
            default -> "Codigo meteorologico " + code;
        };
    }
}
