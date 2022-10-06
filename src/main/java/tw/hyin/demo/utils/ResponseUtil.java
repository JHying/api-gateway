/**
 * 
 */
package tw.hyin.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import tw.hyin.java.utils.Log;
import tw.hyin.java.utils.http.ResponseObj;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YingHan 2021-12-23
 *
 */
public class ResponseUtil {

	private ServerHttpResponse response;

	public ResponseUtil(HttpStatus httpStatus, ServerHttpResponse res) {
		res.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		res.getHeaders().set("Access-Control-Allow-Origin", "*");
		res.getHeaders().set("Cache-Control", "no-cache");
		res.setStatusCode(httpStatus);
		this.response = res;
	}

	public Mono<Void> onError(Exception e) {
		Log.error(">> " + e.getMessage());
		List<String> errors = new ArrayList<>();
		errors.add("Error Message: " + e.getMessage());
		DataBuffer buffer = null;
		// 返回錯誤 response
		try {
			String body = this.setBody(this.response.getStatusCode(), errors, null);
			buffer = this.response.bufferFactory().wrap(body.getBytes("UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return this.response.writeWith(Mono.just(buffer));
	}

	private String setBody(HttpStatus httpStatus, List<String> errors, Object result) throws JsonProcessingException {
		ResponseObj<Object> responseObj = new ResponseObj<>(httpStatus, errors, result);
		return new ObjectMapper().writeValueAsString(responseObj);
	}

}
