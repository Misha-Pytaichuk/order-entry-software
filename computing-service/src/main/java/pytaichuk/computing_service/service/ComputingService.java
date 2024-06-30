package pytaichuk.computing_service.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pytaichuk.computing_service.dto.DatesRequest;
import pytaichuk.computing_service.dto.StatisticResponse;
import pytaichuk.computing_service.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ComputingService {
    private final OrderRepository orderRepository;

    public Double getRevenuesForDateDiapason(DatesRequest datesRequest){

        LocalDateTime fromDateTime = LocalDateTime.of(datesRequest.getFromYear(), datesRequest.getFromMonth(), datesRequest.getFromDay(), 0, 0, 0, 0);
        LocalDateTime toDateTime = LocalDateTime.of(datesRequest.getToYear(), datesRequest.getToMonth(), datesRequest.getToDay(), 0, 0, 0, 0);
        if(fromDateTime.isAfter(toDateTime)){
            throw new ValidationException("date - dateFrom cannot be greater than dateTo");
        }

        return orderRepository.getPrice(fromDateTime, toDateTime);
    }

    public StatisticResponse getRevenuesForThisAndLastMonth(){
        //dates format yyyy-mm-dd-(time)
        LocalDateTime dateTimeFrom = resetTheMonth(LocalDateTime.now(), TemporalAdjusters.firstDayOfMonth());
        LocalDateTime dateTimeTo = resetTheMonth(LocalDateTime.now(), TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        Double revenuesForThisMonth = orderRepository.getPrice(dateTimeFrom, dateTimeTo);

        dateTimeFrom = resetTheMonth(LocalDateTime.now().minusMonths(1), TemporalAdjusters.firstDayOfMonth());
        dateTimeTo = resetTheMonth(LocalDateTime.now().minusMonths(1), TemporalAdjusters.lastDayOfMonth()).plusDays(1);

        Double revenuesForLastMonth = orderRepository.getPrice(dateTimeFrom, dateTimeTo);

        StatisticResponse statisticResponse = new StatisticResponse();

        if(revenuesForThisMonth == null || revenuesForLastMonth == null)
            statisticResponse.setDifferencePercentage(0.0);
        else
            statisticResponse.setDifferencePercentage(computePercent(revenuesForLastMonth, revenuesForThisMonth));

        statisticResponse.setRevenuesForThisMonth(revenuesForThisMonth);
        statisticResponse.setRevenuesForLastMonth(revenuesForLastMonth);

        return statisticResponse;
    }

    public Double getRevenuesForLastDays(Integer days){
        //dates format yyyy-mm-dd-(time)
        LocalDateTime currentDate = LocalDateTime.now().plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime dateTimeFrom = currentDate.minusDays(days);

        if(days <= 0){
            throw new ValidationException("date - days count should be greater than 0");
        }

        return orderRepository.getPrice(dateTimeFrom, currentDate);
    }

    private LocalDateTime resetTheMonth(LocalDateTime dateTime, TemporalAdjuster adjuster){
        return dateTime
                .with(adjuster)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private Double computePercent(Double lastMonthPrice, Double thisMonthPrice){
        return ((thisMonthPrice - lastMonthPrice)/lastMonthPrice) * 100;
    }

}
