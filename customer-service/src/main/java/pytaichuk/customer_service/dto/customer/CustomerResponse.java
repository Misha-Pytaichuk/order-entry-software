package pytaichuk.customer_service.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private String name;
    private String surname;
    private String telephoneNumber;
}
