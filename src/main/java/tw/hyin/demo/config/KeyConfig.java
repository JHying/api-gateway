package tw.hyin.demo.config;

import org.springframework.context.annotation.Configuration;
import tw.hyin.java.utils.security.RSAUtil;

import java.security.PublicKey;

/**
 * @author rita6 on 2021.
 */
@Configuration
public class KeyConfig {

    public static PublicKey publicKey = null;

    static {
        try {
        	//取得RSA公鑰
            publicKey = RSAUtil.loadPublicKeyFromJAR("publicKey.jks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
