package pytaichuk.order_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import pytaichuk.order_service.dto.customer.main_response.Response;
import pytaichuk.order_service.dto.item.OrderLineItemsRequestDto;
import pytaichuk.order_service.dto.order.DateRequest;
import pytaichuk.order_service.dto.order.OrderRequest;
import pytaichuk.order_service.dto.order.OrderResponse;
import pytaichuk.order_service.service.OrderService;
import pytaichuk.order_service.exception.BindingExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final BindingExceptionHandler bindingExceptionHandler;

    /* Get a list of orders by customer ID/ */
    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrdersByCustomer(@PathVariable("customerId") Long customerId){
        if(customerId == null) throw new ValidationException("wrong path;");

        return orderService.findOrders(customerId);
    }

    /* Get all its details, including product and customer details, from the order number. */
    @GetMapping("/get/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Response getOrder(@PathVariable("orderNumber") String orderNumber){
        if(orderNumber == null) throw new ValidationException("wrong path;");

        return orderService.getOrder(orderNumber);
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
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrderByDate(@RequestBody DateRequest dateRequest){
        return orderService.getOrders(dateRequest);
    }

    /* Adds a new order, to an existing customer. */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody @Valid OrderRequest orderRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        orderService.save(orderRequest);
        return "Order placed successfully!";
    }

    /* Deletes an order and its item by order number. */
    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteOrder(@PathVariable("orderNumber") String orderNumber){
        if(orderNumber == null) throw new ValidationException("wrong path;");

        orderService.deleteOrder(orderNumber);

        return "Order deleted successfully!";
    }

    /* This endpoint is used when deleting a customer that deletes all of his orders. */
    @DeleteMapping("/byCustomer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteOrder(@PathVariable("customerId") Long customerId){
        if(customerId == null) throw new ValidationException("wrong path;");

        orderService.deleteOrderByCustomer(customerId);

        return "Order deleted successfully!";
    }

    /* This endpoint is used to update the data of a specific item in the order. */
    @PatchMapping("/{orderNumber}/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateOrderItem(@PathVariable("orderNumber") String orderNumber,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestBody @Valid OrderLineItemsRequestDto orderLineItemsRequestDto,
                                  BindingResult bindingResult){
        if(orderNumber == null || itemId == null) throw new ValidationException("wrong path;");

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        orderService.updateItem(orderNumber, itemId, orderLineItemsRequestDto);

        return "Order item updated successfully!";
    }

    /* This endpoint is used to add items to an existing order. */
    @PatchMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String addItemToOrder(@PathVariable("orderNumber") String orderNumber,
                                 @RequestBody @Valid OrderLineItemsRequestDto orderLineItemsRequestDto,
                                 BindingResult bindingResult){
        if(orderNumber == null ) throw new ValidationException("wrong path;");

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingExceptionHandler.ex(bindingResult));
        }

        orderService.addItemToOrder(orderNumber, orderLineItemsRequestDto);

        return "Order item created successfully!";
    }

    /* This endpoint is used to delete products from an existing order.
    If you delete all products, the order will be deleted automatically. */
    @DeleteMapping("/{orderNumber}/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteItemFromOrder(@PathVariable("orderNumber") String orderNumber,
                                      @PathVariable("itemId") Long itemId){
        if(orderNumber == null || itemId == null) throw new ValidationException("wrong path;");
        orderService.deleteItemFromOrder(orderNumber, itemId);

        return "Item deleted successfully!";
    }
}
