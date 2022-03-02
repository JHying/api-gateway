package tw.hyin.demo.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author H-yin on 2020.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
    private String userName;
    private Integer deptNo;
    private List<String> roles;

}
