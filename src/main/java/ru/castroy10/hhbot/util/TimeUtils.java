package ru.castroy10.hhbot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

    private final List<LocalDate> russianHolidays2026 = List.of(
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2026-01-02"),
            LocalDate.parse("2026-01-03"),
            LocalDate.parse("2026-01-04"),
            LocalDate.parse("2026-01-05"),
            LocalDate.parse("2026-01-06"),
            LocalDate.parse("2026-01-07"),
            LocalDate.parse("2026-01-08"),
            LocalDate.parse("2026-01-09"),
            LocalDate.parse("2026-02-23"),
            LocalDate.parse("2026-03-09"),
            LocalDate.parse("2026-05-01"),
            LocalDate.parse("2026-05-11"),
            LocalDate.parse("2026-06-12"),
            LocalDate.parse("2026-11-04"),
            LocalDate.parse("2026-12-31")
    );

    public boolean isWorkingTime() {
        final int hour = LocalDateTime.now().getHour();
        return hour >= 9 && hour < 17;
    }

    public boolean isWeekend() {
        final LocalDate today = LocalDate.now();
        final int dayOfWeek = today.getDayOfWeek().getValue();
        return dayOfWeek == 6 ||
               dayOfWeek == 7 ||
               russianHolidays2026.contains(today);
    }

}
