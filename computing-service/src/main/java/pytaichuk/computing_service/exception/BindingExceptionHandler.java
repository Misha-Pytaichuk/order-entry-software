package pytaichuk.computing_service.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class BindingExceptionHandler {
    public String ex(BindingResult bindingResult){
        StringBuilder stringErrorBuilder = new StringBuilder();

        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error: errors) {
            stringErrorBuilder.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
        }

        return stringErrorBuilder.toString();
    }
}
