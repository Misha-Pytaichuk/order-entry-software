package pytaichuk.customer_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import pytaichuk.customer_service.dto.customer.CustomerResponse;
import pytaichuk.customer_service.dto.customer.CustomerRequest;
import pytaichuk.customer_service.dto.customer.search.CustomerSearchRequest;
import pytaichuk.customer_service.dto.customer.search.CustomerSearchResponse;
import pytaichuk.customer_service.exception.BindingExceptionHandler;
import pytaichuk.customer_service.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final BindingExceptionHandler bindingExceptionHandler;

    /* Get customer using id. */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public CustomerResponse findCustomer(@PathVariable("id") Long id){
        if(id == null) throw new ValidationException("Wrong path!");

        return customerService.findCustomer(id);
    }

    /* Find out if a customer with this id exists. */
    @GetMapping("/isPresent/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean customerIsPresent(@PathVariable("id") Long id){
        if(id == null) throw new ValidationException("Wrong path!");

        System.out.println(id);
        return customerService.customerIsPresent(id);
    }

    /* Get customer data using telephone number. */
    @GetMapping()
    @ResponseStatus(HttpStatus.FOUND)
    public CustomerSearchResponse findCustomer(@RequestBody @Valid CustomerSearchRequest customerSearchRequest, BindingResult bindingResult) {
        String telephoneNumber = customerSearchRequest.getTelephoneNumber().trim();
        System.out.println(telephoneNumber);

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        return customerService.findCustomer(telephoneNumber);
    }

    /* Takes customer data as input and stores it in the database. */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody @Valid CustomerRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        customerService.createCustomer(request);

        return "Customer created successfully";
    }

    /* Takes customer data and id as input to update. */
    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String update(@PathVariable("id") Long id, @RequestBody @Valid CustomerRequest request, BindingResult bindingResult) {
        if(id == null) throw new ValidationException("Wrong path!");

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }
        customerService.updateCustomer(id, request);
        return "Customer updated successfully";
    }

    /* Delete customer using id. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String delete(@PathVariable("id") Long id) {
        if(id == null) throw new ValidationException("Wrong path!");

        customerService.delete(id);

        return "Customer deleted successfully";
    }

}
