package pytaichuk.order_service;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import pytaichuk.order_service.exception.BindingExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceSo16Application {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceSo16Application.class, args);
	}

	@Bean
	public BindingExceptionHandler bindingExceptionHandler(){
		return new BindingExceptionHandler();
	}
}
