/**
 * 
 */
package tw.hyin.demo.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author YingHan 2021-12-24
 *
 */
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange()
				.anyExchange().permitAll()
				.and()
				//把 csrf 關掉否則會報 An expected CSRF token cannot be found 錯誤
				.csrf().disable()
				.build();
	}

}
