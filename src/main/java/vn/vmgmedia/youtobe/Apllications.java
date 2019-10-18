package vn.vmgmedia.youtobe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Apllications {
	public static void main(String[] args) {
		SpringApplication.run(Apllications.class, args);
		
/*		ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(Apllications.class)
                .properties("spring.config.name:application,myapp",
                        "spring.config.location:classpath:C:/application.properties")
                .build().run(args);
 
        ConfigurableEnvironment environment = applicationContext.getEnvironment();*/
	}
}
