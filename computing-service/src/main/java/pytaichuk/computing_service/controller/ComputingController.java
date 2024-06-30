package pytaichuk.computing_service.controller;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import pytaichuk.computing_service.dto.DatesRequest;
import pytaichuk.computing_service.dto.StatisticResponse;
import pytaichuk.computing_service.exception.BindingExceptionHandler;
import pytaichuk.computing_service.service.ComputingService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compute")
public class ComputingController {
    private final ComputingService computingService;
    private final BindingExceptionHandler bindingExceptionHandler;

    /* An endpoint that accepts a range of dates
    and calculates revenue for them
    based on the date the order was created. */
    @GetMapping("/range")
    @ResponseStatus(HttpStatus.OK)
    public Double getRevenuesForDateDiapason(@RequestBody DatesRequest datesRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            bindingExceptionHandler.ex(bindingResult);
        }

        return computingService.getRevenuesForDateDiapason(datesRequest);
    }

    /* An endpoint that takes the number of days
    and returns a return calculated from the difference between the current date
    and the number of days. */
    @GetMapping("/{days}")
    @ResponseStatus(HttpStatus.OK)
    public Double getRevenuesForLastDays(@PathVariable("days") Integer days){
        if(days == null) throw new ValidationException("wrong path;");

        return computingService.getRevenuesForLastDays(days);
    }

    /* Endpoint, which returns income data for the current and past months,
    also returns their percentages. */
    @GetMapping("/statisticForMonths")
    @ResponseStatus(HttpStatus.OK)
    public StatisticResponse getPrice(){
        return computingService.getRevenuesForThisAndLastMonth();
    }
}
