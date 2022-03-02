package tw.hyin.demo.config.security;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import tw.hyin.demo.config.KeyConfig;
import tw.hyin.demo.pojo.JwtPayload;
import tw.hyin.demo.utils.JwtUtil;
import tw.hyin.demo.utils.ResponseUtil;

/**
 * 攔截所有需要驗證 token 的請求，調用 JwtTokenUtil 方法做 token 驗證
 *
 * @author H-yin
 */
public class AuthFilter implements GatewayFilter {

	/**
	 * 驗證請求
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		// TODO Auto-generated method stub
		ServerHttpRequest req = exchange.getRequest();
		ServerHttpResponse res = exchange.getResponse();

		// 登入及帳號註冊需要驗證
		if (!WhiteListConfig.isAuthList.test(req)) {
			return chain.filter(exchange);
		}

		// OPTION直接放行
		if (req.getMethod() == HttpMethod.OPTIONS) {
			return chain.filter(exchange);
		}

		// 沒有驗證訊息拒絕訪問
		if (this.getAuthHeader(req).isEmpty()) {
			return this.onError(res, new Exception("Authorization not found."));
		}

		try {
			// 取得 token
			String token = this.getAuthHeader(req).get(0);
			// header 是否包含前綴字
			if (token == null || !token.startsWith(JwtUtil.PREFIX)) {
				return this.onError(res, new Exception("Token verify Unsuccessfully."));
			} else {
				// 獲取權限
				UsernamePasswordAuthenticationToken authentication = this.getAuthentication(req, token);
				// 將 Authentication 寫入 SecurityContextHolder 以便後續使用
				SecurityContextHolder.getContext().setAuthentication(authentication);
				return chain.filter(exchange);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.onError(res, new Exception("Token verify Unsuccessfully."));
		}
	}

	/**
	 * 取得驗證訊息
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(ServerHttpRequest request, String token)
			throws Exception
	{
		// 使用 RSA 公鑰解析 token 取得 payload
		JwtPayload<String> payload = JwtUtil.getPayload(token, KeyConfig.publicKey, String.class);
		if (payload != null) {
			return new UsernamePasswordAuthenticationToken(payload.getUserInfo(), payload, null);
		} else {
			return null;
		}
	}

	/**
	 * token 驗證失敗
	 */
	private Mono<Void> onError(ServerHttpResponse res, Exception e) {
		// 返回錯誤 response
		ResponseUtil resUtil = new ResponseUtil(HttpStatus.UNAUTHORIZED, res);
		return resUtil.onError(e);
	}

	private List<String> getAuthHeader(ServerHttpRequest request) {
		return request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
	}
}