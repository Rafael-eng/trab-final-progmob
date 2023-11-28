package com.example.trabalho02;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@TypeConverters
public class Converter {
    private static final DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @TypeConverter
    public static LocalDateTime fromString(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return LocalDateTime.parse(value, formatterWithTime);
            } catch (java.time.format.DateTimeParseException e) {
                // Se a análise com a hora falhar, tente sem hora
                LocalDate data = LocalDate.parse(value);

            return data.atTime(LocalTime.MIDNIGHT);
            }
        }
        return null;
    }

    @TypeConverter
    public static String toString(LocalDateTime value) {
        if (value != null) {
            return value.format(formatterWithTime);
        }
        return null;
    }
}
