package pytaichuk.order_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pytaichuk.order_service.dto.customer.CustomerResponse;
import pytaichuk.order_service.dto.customer.main_response.Response;
import pytaichuk.order_service.dto.item.OrderLineItemsRequestDto;
import pytaichuk.order_service.dto.item.OrderLineItemsResponseDto;
import pytaichuk.order_service.dto.order.DateRequest;
import pytaichuk.order_service.dto.order.OrderRequest;
import pytaichuk.order_service.dto.order.OrderResponse;
import pytaichuk.order_service.exception.FindException;
import pytaichuk.order_service.model.Order;
import pytaichuk.order_service.model.OrderLineItems;
import pytaichuk.order_service.repository.OrderLineItemsRepository;
import pytaichuk.order_service.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineItemsRepository orderLineItemsRepository;
    private final WebClient.Builder webClientBuilder;

    @Retry(name = "customerRetry")
    @CircuitBreaker(name = "customer")
    public Response getOrder(String orderNumber){
        Order order = findOrder(orderNumber);

        CustomerResponse customerResponse = webClientBuilder.build().get()
                .uri("http://customer-service", uriBuilder -> uriBuilder
                        .path("/api/v1/customer/{customerId}")
                        .build(order.getCustomerId()))
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .block();


        OrderResponse orderResponse = mapToOrderResponse(findOrder(orderNumber));
        return buildResponse(Objects.requireNonNull(customerResponse), orderResponse);
    }

    @Retry(name = "customerRetry")
    @CircuitBreaker(name = "customer")
    @Transactional
    public void save(OrderRequest orderRequest) {

        Boolean isPresent = webClientBuilder.build().get()
            .uri("http://customer-service", uriBuilder -> uriBuilder
                    .path("/api/v1/customer/isPresent/{customerId}")
                    .build(orderRequest.getCustomerId()))
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();

        if(Boolean.FALSE.equals(isPresent)) throw new FindException("id - Користувача з таким id не інсує");

        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsRequestDto()
                .stream()
                .map(this::mapToPojo)
                .toList();

        order.setOrderName(createNameForOrder(orderLineItems));
        order.setOrderLineItems(orderLineItems);
        order.setCreatedAt(LocalDateTime.now());
        order.setCustomerId(orderRequest.getCustomerId());

        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(String orderNumber){
        Order order = findOrder(orderNumber);

        orderRepository.delete(order);
    }

    @Transactional
    public void deleteOrderByCustomer(Long customerId) {
        List<Order> orderList = orderRepository.findOrdersByCustomerId(customerId);
        for (Order order: orderList){
            System.out.println(order.getOrderName());
        }
        orderRepository.deleteAll(orderList);
    }

    @Transactional
    public void updateItem(String orderNumber, Long itemId, OrderLineItemsRequestDto orderLineItemsRequestDto){
        Order order = findOrder(orderNumber);
        for (OrderLineItems oli: order.getOrderLineItems()) {
            if(oli.getId().equals(itemId)){
                oli.setSkuCode(orderLineItemsRequestDto.getSkuCode());
                oli.setItemName(orderLineItemsRequestDto.getItemName());
                oli.setPrice(orderLineItemsRequestDto.getPrice());
                oli.setQuantity(orderLineItemsRequestDto.getQuantity());
            }
        }
        order.setOrderName(createNameForOrder(order.getOrderLineItems()));
        orderRepository.save(order);
    }

    @Transactional
    public void addItemToOrder(String orderNumber, OrderLineItemsRequestDto orderLineItemsRequestDto){
        Order order = findOrder(orderNumber);
        order.getOrderLineItems().add(mapToPojo(orderLineItemsRequestDto));
        order.setOrderName(createNameForOrder(order.getOrderLineItems()));
        orderRepository.save(order);
    }

    @Transactional
    public void deleteItemFromOrder(String orderNumber, Long itemId){
        Order order = findOrder(orderNumber);

        orderLineItemsRepository.delete(orderLineItemsRepository.findById(itemId).orElseThrow(() -> new FindException("id - речі з таким id не існує;")));
        order.getOrderLineItems().removeIf(orderLineItems -> orderLineItems.getId().equals(itemId));

        if(order.getOrderLineItems().isEmpty()){
            deleteOrder(orderNumber);
            return;
        } else
            order.setOrderName(createNameForOrder(order.getOrderLineItems()));

        orderRepository.save(order);
    }

    public List<OrderResponse> getOrders(Integer page, String sort){
        List<Order> orderList;

        if(sort.equals("asc"))
            orderList = orderRepository.findAll(PageRequest.of(page, 30, Sort.by("createdAt").ascending())).getContent();
        else if(sort.equals("desc"))
            orderList = orderRepository.findAll(PageRequest.of(page, 30, Sort.by("createdAt").descending())).getContent();
        else {
            throw new ValidationException("wrong path;");
        }

        return orderList.stream().map(this::mapToOrderResponse).toList();
    }

    public List<OrderResponse> getOrders(DateRequest dateRequest){
        LocalDateTime createdAtFrom = LocalDateTime.of(dateRequest.getYear(), dateRequest.getMonth(), dateRequest.getDay(), 0, 0, 0, 0);
        LocalDateTime createdAtTo = createdAtFrom.plusDays(1);
        List<Order> orderList = orderRepository.findOrdersByCreatedAtBetween(createdAtFrom, createdAtTo);


        return orderList.stream().map(this::mapToOrderResponse).toList();
    }

    private String createNameForOrder(List<OrderLineItems> orderLineItems){
        StringBuilder orderNameBuilder = new StringBuilder();
        for (OrderLineItems oli: orderLineItems){
            if(orderLineItems.indexOf(oli) == 3) break;
            orderNameBuilder.append(oli.getItemName()).append("(").append(oli.getQuantity()).append(") | ");
        }
        String orderName = orderNameBuilder.toString();
        String sub = orderName.substring(0, orderName.length() - 3);
        if(orderLineItems.size() <= 3){
            return sub;
        } else {
            return sub + "...";
        }
    }

    private Order findOrder(String orderNumber){
        return orderRepository.findOrderByOrderNumber(orderNumber)
                .orElseThrow(()-> new FindException("orderNumber - такого замовлення не існує;"));
    }

    public List<OrderResponse> findOrders(Long customerId){
        return orderRepository.findOrdersByCustomerId(customerId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderLineItems mapToPojo (OrderLineItemsRequestDto orderLineItemsRequestDto){
        System.out.println(orderLineItemsRequestDto.getItemName());
        return OrderLineItems.builder()
                .skuCode(orderLineItemsRequestDto.getSkuCode())
                .itemName(orderLineItemsRequestDto.getItemName())
                .price(orderLineItemsRequestDto.getPrice())
                .quantity(orderLineItemsRequestDto.getQuantity())
                .build();
    }

    private OrderResponse mapToOrderResponse(Order order){
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderName(order.getOrderName())
                .createdAt(order.getCreatedAt())
                .orderLineItemsResponseDto(order.getOrderLineItems()
                        .stream()
                        .map(this::mapToItemResponse)
                        .toList())
                .build();
    }

    private OrderLineItemsResponseDto mapToItemResponse(OrderLineItems items){
        return OrderLineItemsResponseDto.builder()
                .id(items.getId())
                .skuCode(items.getSkuCode())
                .itemName(items.getItemName())
                .price(items.getPrice())
                .quantity(items.getQuantity())
                .build();
    }

    private Response buildResponse(CustomerResponse customerResponse, OrderResponse orderResponse){
        return Response.builder()
                .name(customerResponse.getName())
                .surname(customerResponse.getSurname())
                .telephoneNumber(customerResponse.getTelephoneNumber())
                .order(orderResponse)
                .build();
    }
}
