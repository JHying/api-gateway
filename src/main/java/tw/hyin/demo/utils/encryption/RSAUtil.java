package tw.hyin.demo.utils.encryption;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import tw.hyin.demo.utils.FileUtil;

public final class RSAUtil {

    private static String algorithm = "RSA";
    private static final int DEFAULT_KEY_LENGTH = 2048;//密碼長度
    private static final int ENCRYPT_LENGTH = 256;//分組加密長度
    private static final int DECRYPT_LENGTH = 256;//分組解密長度 (應等於密鑰長度/8)
    private static final String KEY_SAVEPATH = "/key";
    private static final String SECRET = "Xu.62k6cj86";

    /**
	 * 將密鑰存成檔案
	 */
	public static void generateKey(String publicFile, String privateFile) throws Exception {
		// 生成密鑰
		KeyPair keyPair = generateKeyPair(SECRET);
		// 儲存公鑰文件
		byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
		publicKeyBytes = Base64.getMimeEncoder().encode(publicKeyBytes);
		FileUtil.writeFileToResources(KEY_SAVEPATH + "/" + publicFile, publicKeyBytes);
		// 儲存私鑰文件
		byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
		privateKeyBytes = Base64.getMimeEncoder().encode(privateKeyBytes);
		FileUtil.writeFileToResources(KEY_SAVEPATH + "/" + privateFile, privateKeyBytes);
	}

	/**
	 * 用公鑰加密 <br>
	 * 每次加密的字符數，不能超過密鑰長度值減 11
	 *
	 * @param data      需加密的 byte 數據
	 * @param publicKey 公鑰
	 * @return 加密後的 byte 數據
	 */
	public static String encrypt(byte[] data, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		// 編碼前設定編碼方式及密鑰
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 傳入編碼數據並返回結果 (因 RSA 長度限制需分組加密)
		return Base64.getMimeEncoder().encodeToString(grouping(cipher, data, ENCRYPT_LENGTH));
	}

	/**
	 * 用私鑰解密
	 *
	 * @param data       經過加密的 byte 數據
	 * @param privateKey 私鑰
	 * @return 解密後 byte 數據
	 */
	public static String decrypt(byte[] data, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] bytes = Base64.getMimeDecoder().decode(data);
		return new String(grouping(cipher, bytes, DECRYPT_LENGTH));
	}

	/**
	 * 通過公鑰檔案取得公鑰
	 *
	 * @param fileName 加密公鑰
	 * @return 原公鑰
	 */
	public static PublicKey loadPublicKey(String fileName) throws Exception {
		byte[] key = Base64.getMimeDecoder().decode(FileUtil.readFileFromResources(KEY_SAVEPATH + "/" + fileName));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(keySpec);
	}

	/**
	 * 通過私鑰檔案取得私鑰
	 *
	 * @param fileName 加密私鑰
	 * @return 原公鑰
	 */
	public static PrivateKey loadPrivateKey(String fileName) throws Exception {
		byte[] key = Base64.getMimeDecoder().decode(FileUtil.readFileFromResources(KEY_SAVEPATH + "/" + fileName));
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 通過字串 (已經 base64 處理) 取得公鑰
	 *
	 * @param keyStr 加密公鑰
	 * @return 原公鑰
	 */
	public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
		byte[] decodeKey = Base64.getMimeDecoder().decode(publicKeyStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodeKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(keySpec);
	}

	/**
	 * 通過字串 (已經 base64 處理) 取得私鑰
	 *
	 * @param keyBytes 加密私鑰
	 * @return 私鑰
	 */
	public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
		byte[] decodeKey = Base64.getMimeDecoder().decode(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodeKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 使用N、e值還原公鑰
	 *
	 * @param modulus        公鑰模數 Modulus(n)
	 * @param publicExponent 公鑰指數 Exponent(e)
	 * @return 公鑰
	 */
	public static PublicKey getPublicKey(String modulus, String publicExponent) throws Exception {
		BigInteger bigIntModulus = new BigInteger(modulus);
		BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(keySpec);
	}

	/**
	 * 使用N、d值還原私鑰
	 *
	 * @param modulus         私鑰模數 (n)
	 * @param privateExponent 私鑰指數 (d)
	 * @return 私鑰
	 */
	public static PrivateKey getPrivateKey(String modulus, String privateExponent) throws Exception {
		BigInteger bigIntModulus = new BigInteger(modulus);
		BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 印出公鑰訊息
	 *
	 * @param publicKey 公鑰
	 */
	public static void printPublicKeyInfo(PublicKey publicKey) {
		RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
		System.out.println("----------RSAPublicKey----------");
		System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
		System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());
		System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
		System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
		System.out.println("----------RSAPublicKey End----------");
	}

	/**
	 * 印出私鑰訊息
	 *
	 * @param privateKey 私鑰
	 */
	public static void printPrivateKeyInfo(PrivateKey privateKey) {
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
		System.out.println("----------RSAPrivateKey ----------");
		System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
		System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());
		System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
		System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());
		System.out.println("----------RSAPrivateKey End----------");
	}

	/**
	 * 鑰匙轉字串
	 * 
	 * @author YingHan
	 * @since 2021-12-17
	 * 
	 * @Description key getEncoded 轉 byte
	 */
	public static String keyToString(byte[] encodedKey) {
		return Base64.getEncoder().encodeToString(encodedKey);
	}

	/**
	 * 根據密文生成 RSA 密鑰對
	 */
	private static KeyPair generateKeyPair(String secret) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
		SecureRandom secureRandom = new SecureRandom(secret.getBytes());
		keyPairGenerator.initialize(DEFAULT_KEY_LENGTH, secureRandom);
		return keyPairGenerator.genKeyPair();
	}

	/**
	 * 分組加解密
	 */
	private static byte[] grouping(Cipher cipher, byte[] data, int length) throws Exception {
		int inputLen = data.length;
		int offLen = 0;// 偏移量
		int i = 0;
		ByteArrayOutputStream bops = new ByteArrayOutputStream();
		while (inputLen - offLen > 0) {
			byte[] cache;
			if (inputLen - offLen > length) {
				cache = cipher.doFinal(data, offLen, length);
			} else {
				cache = cipher.doFinal(data, offLen, inputLen - offLen);
			}
			bops.write(cache);
			i++;
			offLen = length * i;
		}
		bops.close();
		return bops.toByteArray();
	}

}
