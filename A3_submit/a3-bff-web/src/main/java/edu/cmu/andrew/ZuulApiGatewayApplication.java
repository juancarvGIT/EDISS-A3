package edu.cmu.andrew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.cmu.andrew.ZuulApiGatewayApplication;
import edu.cmu.andrew.security.AuthorizationFilter;

/**
* <h1>BFF main class</h1>
* 
*  This class initialize the component, it implements BFF pattern using Spring boot API Gateways libraries
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-29
*/
@EnableZuulProxy
@SpringBootApplication
public class ZuulApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApiGatewayApplication.class, args);
	}
	
	
	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		/**
		 * Initial configuration for Spring context it will add the Authorization Filter which validate the JWT token per request
		 * 
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .addFilterAfter(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);//adding JWTAuthorizationFilter
	

		}
	}
}
