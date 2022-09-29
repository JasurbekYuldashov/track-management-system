package uz.binart.trackmanagementsystem;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import uz.binart.trackmanagementsystem.model.Delivery;
import uz.binart.trackmanagementsystem.model.Load;
import uz.binart.trackmanagementsystem.model.Pickup;
import uz.binart.trackmanagementsystem.property.FileStorageProperties;
import uz.binart.trackmanagementsystem.repository.DeliveryRepository;
import uz.binart.trackmanagementsystem.repository.LoadRepository;
import uz.binart.trackmanagementsystem.repository.PickupRepository;
import uz.binart.trackmanagementsystem.service.PickupService;
import uz.binart.trackmanagementsystem.service.TruckLoadService;

import java.time.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

@EnableScheduling
@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class TrackManagementSystemApplication {

	@Autowired
	private PickupRepository pickupRepository;

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Autowired
	private LoadRepository loadRepository;

	public static void main(String[] args) {
		SpringApplication.run(TrackManagementSystemApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(false);
		config.addExposedHeader("*");
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("PATCH");
		config.addAllowedMethod("PUT");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}


//	@Bean
//	public CommandLineRunner commandLineRunner(){
//		return args -> {
//			List<Load> loads = loadRepository.findAll();
//			for(Load load: loads){
//				if(!Objects.equals(load.getPickups().size(), 0)){
//
//					try{
//						Pickup pickup = pickupRepository.getById(load.getPickups().get(0));
//						System.out.println(pickup.getPickupDate().getTime());
//						load.setCentralStartTime(pickup.getPickupDate().getTime());
//						load.setStartTime(pickup.getPickupDate().getTime());
////						loadRepository.save(load);
//					}catch (Exception e){
//						System.out.println(load.getId());
//					}
//				}
//				if(!Objects.equals(load.getDeliveries().size(), 0)){
//
//					try{
//						Delivery delivery = deliveryRepository.getById(load.getDeliveries().get(load.getDeliveries().size()-1));
//						System.out.println(delivery.getDeliveryDate().getTime());
//						load.setCentralEndTime(delivery.getDeliveryDate().getTime());
//						load.setEndTime(delivery.getDeliveryDate().getTime());
//						loadRepository.save(load);
//					}
//					catch (Exception e){
//						System.out.println(load.getId());
//					}
//
//				}
//			}
//		};
//	}

}
