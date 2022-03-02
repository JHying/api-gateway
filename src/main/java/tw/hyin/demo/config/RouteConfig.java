/**
 * 
 */
package tw.hyin.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tw.hyin.demo.config.security.AuthFilter;

/**
 * 
 * @author YingHan 2021-10-28
 *
 */
@Configuration
public class RouteConfig {

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("auth-service", r -> r.path("/auth/**")
						.filters(f -> f.filter(new AuthFilter()))
						.uri("http://localhost:8096/auth"))
				
				.route("member-service", r -> r.path("/member/**")
						.uri("http://localhost:8086/user"))
				.build();
	}

}
