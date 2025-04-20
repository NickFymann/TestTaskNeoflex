package TestTask.service;

import TestTask.dto.VacationRequest;
import TestTask.util.HolidayUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VacationService {

    private static final double AVERAGE_DAYS_IN_MONTH = 29.3;

    public double calculate(VacationRequest request) {
        double dailyRate = request.getAverageSalary() / AVERAGE_DAYS_IN_MONTH;

        // если указаны даты начала и конца отпуска
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }

            List<LocalDate> dateRange = Stream.iterate(request.getStartDate(), date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1)
                    .collect(Collectors.toList());

            long workingDays = dateRange.stream()
                    .filter(date -> !HolidayUtils.isHolidayOrWeekend(date))
                    .count();

            return round(dailyRate * workingDays);
        }

        // если просто указано количество отпускых дней
        if (request.getVacationDays() != null) {
            return round(dailyRate * request.getVacationDays());
        }

        // ничего не пеердано —ошибка
        throw new IllegalArgumentException("Provide either a vacation period or number of vacation days.");
    }
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}


