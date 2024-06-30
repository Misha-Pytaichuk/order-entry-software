package pytaichuk.customer_service.service;

import lombok.RequiredArgsConstructor;
import pytaichuk.customer_service.dto.customer.CustomerResponse;
import pytaichuk.customer_service.dto.customer.CustomerRequest;
import pytaichuk.customer_service.dto.customer.search.CustomerSearchRequest;
import pytaichuk.customer_service.dto.customer.search.CustomerSearchResponse;
import pytaichuk.customer_service.dto.order.OrderResponse;
import pytaichuk.customer_service.model.Customer;
import pytaichuk.customer_service.repository.CustomerRepository;
import pytaichuk.customer_service.exception.FindException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final WebClient.Builder webClientBuilder;
    private final CustomerRepository customerRepository;

    @Transactional
    public void createCustomer(CustomerRequest customerRequest){
        customerRepository.save(mapCustomerToPojo(customerRequest));
    }

    @Transactional
    public void updateCustomer(Long id, CustomerRequest customerRequest){
        Customer customer = findCustomerById(id);

        customer.setName(customerRequest.getName());
        customer.setSurname(customerRequest.getSurname());
        customer.setTelephoneNumber("+38" + customerRequest.getTelephoneNumber());

        customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id){
        Customer customer = findCustomerById(id);

        System.out.println(customer.getId());

        webClientBuilder.build().delete()
                .uri("http://order-service/api/v1/order/byCustomer/{customerId}", customer.getId())
                .retrieve()
                .toBodilessEntity()
                .block();

        customerRepository.delete(customer);
    }

    public CustomerSearchResponse findCustomer(String telephoneNumber){

        Customer customer = findCustomerByTelephoneNumber(telephoneNumber);

        List<OrderResponse> orderResponseList = webClientBuilder.build().get()
                .uri("http://order-service", uriBuilder -> uriBuilder
                        .path("/api/v1/order/{customerId}")
                        .build(customer.getId()))
                .retrieve()
                .bodyToFlux(OrderResponse.class)
                .collectList()
                .block();

        CustomerSearchResponse customerSearchResponse = (CustomerSearchResponse) mapCustomerToDto(customer, CustomerSearchResponse.class);
        customerSearchResponse.setOrder(orderResponseList);

        return customerSearchResponse;
    }

    private Customer findCustomerByTelephoneNumber(String telephoneNumber){
        return customerRepository
                .findCustomerByTelephoneNumber("+38" + telephoneNumber)
                .orElseThrow(()-> new FindException("The client with this telephone number: " + telephoneNumber + " does not exist"));
    }

    private Customer findCustomerById(Long id){
        return customerRepository
                .findById(id)
                .orElseThrow(()-> new FindException("The client with this ID: " + id + " does not exist"));
    }

    public CustomerResponse findCustomer(Long id){
        return (CustomerResponse) mapCustomerToDto(findCustomerById(id), CustomerResponse.class);
    }

    public Boolean customerIsPresent(Long id){
        return customerRepository.findById(id).isPresent();
    }

    private Object mapCustomerToDto(Customer customer, Class<?> clazz){
        if(clazz.equals(CustomerResponse.class)){
            return CustomerResponse.builder()
                    .name(customer.getName())
                    .surname(customer.getSurname())
                    .telephoneNumber(customer.getTelephoneNumber())
                    .build();
        } else {
            return CustomerSearchResponse.builder()
                    .name(customer.getName())
                    .surname(customer.getSurname())
                    .telephoneNumber(customer.getTelephoneNumber())
                    .build();
        }
    }

    private Customer mapCustomerToPojo(CustomerRequest customerRequest){
        return Customer.builder()
                .name(customerRequest.getName().trim())
                .surname(customerRequest.getSurname().trim())
                .telephoneNumber("+38" + customerRequest.getTelephoneNumber().trim())
                .build();
    }
}
