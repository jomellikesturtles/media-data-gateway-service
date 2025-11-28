package com.mdb.media_data_gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MediaDataGatewayServiceApplication {

	public static void main(String[] args) {

//		SpringApplication.run(MediaDataGatewayServiceApplication.class, args);

		ConfigurableApplicationContext applicationContext = SpringApplication.run(MediaDataGatewayServiceApplication.class, args);
//		Class.forName("org.postgresql.Driver");
		String[] beans = applicationContext.getBeanDefinitionNames();
		for (int i = 0; i < beans.length; i++) {
//			LOGGER.log("#"+ i + " "+ beans[i]);
//			System.out.println("#"+ i + " "+ beans[i]);
		}
	}

}
