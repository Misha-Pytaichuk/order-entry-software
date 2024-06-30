package pytaichuk.order_service.dto.customer.create_update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    @NotBlank(message = "Поле не може бути пустим")
    private String name;
    @NotBlank(message = "Поле не може бути пустим")
    private String surname;
    @Pattern(regexp = "0\\d{9}", message = "Не вірний формат номеру телефону")
    private String telephoneNumber;
}
