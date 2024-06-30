package pytaichuk.computing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatesRequest {
    private int fromYear;
    private int fromMonth;
    private int fromDay;
    private int toYear;
    private int toMonth;
    private int toDay;
}
