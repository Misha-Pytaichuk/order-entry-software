package pytaichuk.customer_service.dto.customer.search;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchRequest {
    @Pattern(regexp = "0\\d{9}", message = "Не вірний формат номеру телефону")
    private String telephoneNumber;
}
