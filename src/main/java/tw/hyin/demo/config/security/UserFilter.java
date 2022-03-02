/**
 * 
 */
package tw.hyin.demo.config.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import tw.hyin.demo.config.RedisConfig.RedisConstants;
import tw.hyin.demo.pojo.LoginInfo;
import tw.hyin.demo.utils.ResponseUtil;

/**
 * @author YingHan 2021-12-22
 *
 *         globalFilter--作用於全路由的filter (gatewayFilter--只作用於特定route,
 *         要在route設定該filter)
 */
@Component
public class UserFilter implements GlobalFilter {

	@Autowired
	private RedisTemplate<RedisConstants, Object> redisTemplate;

	@SuppressWarnings("unchecked")
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();

		// 白名單直接放行
		if (WhiteListConfig.isWhiteList.test(request)) {
			return chain.filter(exchange);
		}

		// 未登入拒絕訪問
		if (redisTemplate.opsForValue().get(RedisConstants.USER_TAG) == null) {
			return this.onError(exchange.getResponse(), new Exception("User not found or unavailable."));
		}

		try {
			// 解析 redis data 獲得用戶訊息
			LoginInfo loginInfo = (LoginInfo) redisTemplate.opsForValue().get(RedisConstants.USER_TAG);

			// 角色不符拒絕訪問
			// 從authService初始化之權限表(key=pageUrl, value=List<String> roles)取得具有權限的使用者角色
			Map<Object, Object> allResourceRolesMap = redisTemplate.opsForHash().entries(RedisConstants.ROLE_RESOURCE);
			Iterator<Object> iterator = allResourceRolesMap.keySet().iterator();
			PathMatcher pathMatcher = new AntPathMatcher();
			String path = request.getURI().getPath();
			// 所有具有權限的角色
			List<String> resourceRoles = new ArrayList<>();
			String pattern = "";
			while (iterator.hasNext()) {
				pattern = (String) iterator.next();
				if (pathMatcher.match(pattern, path)) {
					resourceRoles.addAll((List<String>) allResourceRolesMap.get(pattern));
				}
			}
			// 如果登入的使用者是具有權限的角色
			if (resourceRoles.stream().anyMatch(roleId -> loginInfo.getRoles().contains(roleId))) {
				exchange = exchange.mutate()
						.request(this.updateRequestWithCredentials(request, loginInfo)).build();
			} else {
				return this.onError(exchange.getResponse(), new Exception("User role not allowed."));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chain.filter(exchange);
	}

	/**
	 * user 驗證失敗
	 */
	private Mono<Void> onError(ServerHttpResponse res, Exception e) {
		// 返回錯誤 response
		ResponseUtil resUtil = new ResponseUtil(HttpStatus.FORBIDDEN, res);
		return resUtil.onError(e);
	}

	private ServerHttpRequest updateRequestWithCredentials(ServerHttpRequest request, LoginInfo loginInfo)
			throws JsonProcessingException
	{
		String loginInfoJson = new ObjectMapper().writeValueAsString(loginInfo);
		return request.mutate()
				.header("loginInfo", loginInfoJson)
				.build();
	}

}
