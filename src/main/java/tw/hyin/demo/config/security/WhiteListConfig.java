/**
 * 
 */
package tw.hyin.demo.config.security;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author YingHan 2021-12-17
 *
 */
public class WhiteListConfig {

	// 不需token驗證的名單
	public static final List<String> AUTH_WHITE_LIST = Arrays.asList(
			"/test", //所有服務都有寫一支 test 做為測試 api
			"/auth/login",  //登入不用
			"/auth/register",  //取得token不用
			"/auth/sidenav" //下拉選單不用
	);

	// 不需user驗證的白名單 (針對auth-server以外的服務)
	public static final List<String> WHITE_LIST = Arrays.asList(
			"/test" //所有服務都有寫一支 test 做為測試 api
	);

	public static Predicate<ServerHttpRequest> isAuthWhiteList = request -> AUTH_WHITE_LIST
			.stream()
			.anyMatch(uri -> request.getURI().getPath().contains(uri));

	public static Predicate<ServerHttpRequest> isWhiteList = request -> WHITE_LIST
			.stream()
			.anyMatch(uri -> request.getURI().getPath().contains(uri));
}
