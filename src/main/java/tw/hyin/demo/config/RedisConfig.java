/**
 * 
 */
package tw.hyin.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import lombok.Getter;

/**
 * @author YingHan 2021-12-22
 *
 */
/*
 * `maxInactiveIntervalInSeconds`:
 * session的過期時間，預設是1800秒，如果設定了此屬性，專案中的`server.session.timeout`屬性將失效
 * `redisNamespace`: 設定redis 的名稱空間，就是設定資料儲存到哪裡(相當於關係型資料庫中的庫) `redisFlushMode`:
 * redis 操作模式，是否立即重新整理到redis資料庫中，預設的是不會的，系統並不是在剛設定就重新整理，而是選擇在某個時間點重新整理到資料庫中
 */
@Configuration
public class RedisConfig {
	
	public static enum RedisConstants {

		USER_TAG("user", "使用者資料"),
		ROLE_RESOURCE("role_resource", "角色權限對應表");

		@Getter
		private String value;

		@Getter
		private String desc;

		RedisConstants(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}
	}

	/**
	 * 預設是JDK的序列化策略，這裡配置redisTemplate採用的是Jackson2JsonRedisSerializer的序列化策略
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<RedisConstants, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		RedisTemplate<RedisConstants, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(jackson2JsonRedisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(jackson2JsonRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	/***
	 * stringRedisTemplate (用於 key value 都是 string 的狀況)
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
		return stringRedisTemplate;
	}

}
