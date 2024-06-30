package pytaichuk.customer_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "t_customers",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "telephoneNumber")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Поле не може бути пустим")
    private String name;
    @NotBlank(message = "Поле не може бути пустим")
    private String surname;
    @Pattern(regexp = "\\+380\\d{9}", message = "Не вірний формат номеру телефону")
    private String telephoneNumber;
}
