package pytaichuk.computing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {
    private Double revenuesForThisMonth;
    private Double revenuesForLastMonth;
    private Double differencePercentage;
}
