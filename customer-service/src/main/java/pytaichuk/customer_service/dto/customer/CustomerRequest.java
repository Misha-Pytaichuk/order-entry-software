package pytaichuk.customer_service.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    @NotBlank(message = "Поле не може бути пустим")
    private String name;
    @NotBlank(message = "Поле не може бути пустим")
    private String surname;
    @Pattern(regexp = "0\\d{9}", message = "Невірний формат номеру телефону")
    private String telephoneNumber;
}
