import TestTask.dto.VacationRequest;
import TestTask.service.VacationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class VacationServiceTest {

    private final VacationService service = new VacationService();
    @Test
    void calculate_withVacationDaysOnly() {
        VacationRequest request = new VacationRequest();
        request.setAverageSalary(87750); // например
        request.setVacationDays(10);

        double expected = 87750.0 / 29.3 * 10;
        double result = service.calculate(request);

        assertEquals(expected, result, 0.01);
    }

    @Test
    void calculate_withDateRange_skipsWeekendsAndHolidays() {
        VacationRequest request = new VacationRequest();
        request.setAverageSalary(60000);
        request.setStartDate(LocalDate.of(2025, 1, 4)); // среда (праздник)
        request.setEndDate(LocalDate.of(2025, 1, 10));   // вторник (праздник)

        // дни: 4–10 января
        // празднки: 4, 6, 7, 8
        // рабочие: 9, 10 - 2 дня

        double expected = 60000.0 / 29.3 * 2;
        double result = service.calculate(request);
        assertEquals(expected, result, 0.01);
    }

    @Test
    void calculate_withInvalidDateRange_throwsException() {
        VacationRequest request = new VacationRequest();
        request.setAverageSalary(75000);
        request.setStartDate(LocalDate.of(2025, 5, 10));
        request.setEndDate(LocalDate.of(2025, 5, 5)); // раньше начала

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.calculate(request);
        });
        assertEquals("End date must be after start date", exception.getMessage());
    }
    @Test
    void calculate_withDaysAndDateRange_prefersDateRange() {
        VacationRequest request = new VacationRequest();
        request.setAverageSalary(90000);
        request.setVacationDays(15); // игнорируется
        request.setStartDate(LocalDate.of(2025, 6, 27)); // пятница
        request.setEndDate(LocalDate.of(2025, 6, 30));   // понедельник

        // 28, 29 - выходные
        // рабочих дней - 2

        double expected = 90000.0 / 29.3 * 2;
        double result = service.calculate(request);

        assertEquals(expected, result, 0.01);
    }

    @Test
    void calculate_withNoData_throwsException() {
        VacationRequest request = new VacationRequest();
        request.setAverageSalary(70000);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.calculate(request);
        });
        assertEquals("Provide either a vacation period or number of vacation days.", exception.getMessage());
    }

}
