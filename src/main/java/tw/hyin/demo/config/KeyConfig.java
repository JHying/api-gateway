package tw.hyin.demo.config;

import java.security.PublicKey;

import org.springframework.context.annotation.Configuration;

import tw.hyin.demo.utils.encryption.RSAUtil;

/**
 * @author rita6 on 2021.
 */
@Configuration
public class KeyConfig {

    public static PublicKey publicKey = null;

    static {
        try {
        	//取得RSA公鑰
            publicKey = RSAUtil.loadPublicKey("publicKey.jks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
