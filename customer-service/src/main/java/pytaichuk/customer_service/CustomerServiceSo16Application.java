package pytaichuk.customer_service;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import pytaichuk.customer_service.exception.BindingExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerServiceSo16Application {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceSo16Application.class, args);
	}

	@Bean
	public BindingExceptionHandler bindingExceptionHandler(){
		return new BindingExceptionHandler();
	}

}
