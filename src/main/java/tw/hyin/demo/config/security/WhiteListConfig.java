/**
 * 
 */
package tw.hyin.demo.config.security;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author YingHan 2021-12-17
 *
 */
public class WhiteListConfig {

	// 需要token驗證的名單
	public static final List<String> AUTH_LIST = Arrays.asList(
			"/auth/login",
			"/auth/user/add"
	);

	// 不需user驗證的白名單
	public static final List<String> WHITE_LIST = Arrays.asList(
			"/auth/login",
			"/auth/register",
			"/auth/refresh",
			"/auth/key/getPublicKey"
	);

	public static Predicate<ServerHttpRequest> isAuthList = request -> AUTH_LIST
			.stream()
			.anyMatch(uri -> request.getURI().getPath().contains(uri));

	public static Predicate<ServerHttpRequest> isWhiteList = request -> WHITE_LIST
			.stream()
			.anyMatch(uri -> request.getURI().getPath().contains(uri));
}
