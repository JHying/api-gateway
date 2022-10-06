/**
 * 
 */
package tw.hyin.demo.config;

import tw.hyin.demo.config.security.UserFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author YingHan 2021-10-28
 *
 */
@Configuration
public class RouteConfig {

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		//lb為spring cloud gateway內建的負載平衡協議, 當創建多個相同服務, 會自動進行負載平衡
		return builder.routes()
				.route("auth-server", r -> r.path("/auth/**")
						.uri("lb://auth-server"))
				
				.route("first-server", r -> r.path("/first/**")
						.filters(f -> f.filter(new UserFilter()))
						.uri("lb://first-server"))
				.build();
	}

}
