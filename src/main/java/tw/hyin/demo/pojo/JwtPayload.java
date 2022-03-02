package tw.hyin.demo.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author rita6 on 2021.
 */
@Data
public class JwtPayload<T> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
    private T userInfo;
    private Date expiration;
}
