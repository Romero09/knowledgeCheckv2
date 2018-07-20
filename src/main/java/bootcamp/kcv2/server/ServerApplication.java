package bootcamp.kcv2.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ServerApplication {
	public static String AUTH_KEY;
	public static void main(String[] args) throws Exception {
		//  uncomment next line to start JettyApplication web app:

		// TODO read password from command line
//		AUTH_KEY = args[0];
		AUTH_KEY = "aaaa";
		
		SpringApplication.run(ServerApplication.class, args);
		// Look at
		// http://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/SpringApplication.html
		// for more information.
	}
}