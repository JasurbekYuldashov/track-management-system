package uz.binart.trackmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import uz.binart.trackmanagementsystem.property.FileStorageProperties;
import uz.binart.trackmanagementsystem.service.TruckLoadService;

import java.util.List;

@EnableScheduling
@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class TrackManagementSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(TrackManagementSystemApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
