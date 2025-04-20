package TestTask.controller;

import TestTask.dto.VacationRequest;
import TestTask.dto.VacationResponse;
import TestTask.service.VacationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculate")
public class VacationController {

    private final VacationService vacationService;

    public VacationController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @GetMapping
    public VacationResponse calculateVacationPay(@RequestBody VacationRequest request) {
        double pay = vacationService.calculate(request);
        return new VacationResponse(pay);
    }
}

