package pytaichuk.order_service.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pytaichuk.order_service.dto.item.OrderLineItemsRequestDto;
import pytaichuk.order_service.dto.order.DateRequest;
import pytaichuk.order_service.dto.order.OrderRequest;
import pytaichuk.order_service.dto.order.OrderResponse;
import pytaichuk.order_service.exception.BindingExceptionHandler;
import pytaichuk.order_service.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final BindingExceptionHandler bindingExceptionHandler;
    private final String MESSAGE = "Service temporarily unavailable. Please try again later.";

    /* Get a list of orders by customer ID/ */
    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByCustomer(@PathVariable("customerId") Long customerId){
        if(customerId == null) throw new ValidationException("wrong path;");

        return orderService.findOrders(customerId);
    }

    /* Get all its details, including product and customer details, from the order number. */
    @GetMapping("/get/{orderNumber}")
    @CircuitBreaker(name = "customer", fallbackMethod = "fallbackMethod")
    public ResponseEntity<Object> getOrder(@PathVariable("orderNumber") String orderNumber){
        if(orderNumber == null) throw new ValidationException("wrong path;");

        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrder(orderNumber));
    }

    /* Get a list of 30 orders on each page,
    sorted by order registration date in descending or ascending order. */
    @GetMapping("/{page}/{sort}") //"ASC"/ "DESC"
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrders(@PathVariable("page") Integer page, @PathVariable("sort") String sort){
        if(page == null || sort == null) throw new ValidationException("wrong path;");
        return orderService.getOrders(page, sort);
    }

    /* Receive all orders placed on a specific date. */
    @GetMapping("/date")
    public ResponseEntity<List<OrderResponse>> getOrderByDate(@RequestBody DateRequest dateRequest){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(dateRequest));
    }

    /* Adds a new order, to an existing customer. */
    @PostMapping()
    @Retry(name = "customerRetry")
    @CircuitBreaker(name = "customer", fallbackMethod = "fallbackMethod")
    public ResponseEntity<String> createOrder(@RequestBody @Valid OrderRequest orderRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        orderService.save(orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Order placed successfully!");
    }

    /* Deletes an order and its item by order number. */
    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderNumber") String orderNumber){
        if(orderNumber == null) throw new ValidationException("wrong path;");

        orderService.deleteOrder(orderNumber);

        return ResponseEntity.status(HttpStatus.OK).body("Order deleted successfully!");
    }

    /* This endpoint is used when deleting a customer that deletes all of his orders. */
    @DeleteMapping("/byCustomer/{customerId}")
    public ResponseEntity<String> deleteOrder(@PathVariable("customerId") Long customerId){
        if(customerId == null) throw new ValidationException("wrong path;");

        orderService.deleteOrderByCustomer(customerId);

        return ResponseEntity.status(HttpStatus.OK).body("Order deleted successfully!");
    }

    /* This endpoint is used to update the data of a specific item in the order. */
    @PatchMapping("/{orderNumber}/{itemId}")
    public ResponseEntity<String> updateOrderItem(@PathVariable("orderNumber") String orderNumber,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestBody @Valid OrderLineItemsRequestDto orderLineItemsRequestDto,
                                  BindingResult bindingResult){
        if(orderNumber == null || itemId == null) throw new ValidationException("wrong path;");

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        orderService.updateItem(orderNumber, itemId, orderLineItemsRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body("Order updated successfully!");
    }

    /* This endpoint is used to add items to an existing order. */
    @PutMapping("/{orderNumber}")
    public ResponseEntity<String> addItemToOrder(@PathVariable("orderNumber") String orderNumber,
                                                 @RequestBody @Valid OrderLineItemsRequestDto orderLineItemsRequestDto,
                                                 BindingResult bindingResult){
        if(orderNumber == null ) throw new ValidationException("wrong path;");

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        orderService.addItemToOrder(orderNumber, orderLineItemsRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body("Item added successfully!");
    }

    /* This endpoint is used to delete products from an existing order.
    If you delete all products, the order will be deleted automatically. */
    @DeleteMapping("/{orderNumber}/{itemId}")
    public ResponseEntity<String> deleteItemFromOrder(@PathVariable("orderNumber") String orderNumber,
                                                      @PathVariable("itemId") Long itemId){
        if(orderNumber == null || itemId == null) throw new ValidationException("wrong path;");
        orderService.deleteItemFromOrder(orderNumber, itemId);

        return ResponseEntity.status(HttpStatus.OK).body("Item deleted successfully!");
    }

    public ResponseEntity<String> fallbackMethod(OrderRequest orderRequest, BindingResult bindingResult, Throwable exception){
        return new ResponseEntity<>(MESSAGE + "\n" + exception.toString(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ResponseEntity<String> fallbackMethod(String orderNumber, Throwable exception){
        return new ResponseEntity<>(MESSAGE + "\n" + exception.toString(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
