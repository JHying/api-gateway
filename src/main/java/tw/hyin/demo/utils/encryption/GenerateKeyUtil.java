package tw.hyin.demo.utils.encryption;

import tw.hyin.demo.utils.FileUtil;

public class GenerateKeyUtil {
	
	public static String KEY = null;
	private static final String KEY_SAVEPATH = "D:/myApps/My Project/MyAuthService/temp";
	
	static {
		if (KEY == null) {
			KEY = String.valueOf(generateKey(16));
		}
	}
	
	/**
	 * 隨機產生金鑰
	 * @param num 幾位數
	 * @return 驗證碼
	 */
	private static String generateKey(int num) {
		// 定義驗證碼的字符集
		String chars = "0123456789aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ+=/";
		char[] rands = new char[num];
		for (int i = 0; i < num; i++) {
			int rand = (int) (Math.random() * 65);
			rands[i] = chars.charAt(rand);
		}
		return String.valueOf(rands);
	}

	/**
	 * 將金鑰存成檔案
	 */
	public static void saveKey(String keyFile) throws Exception {
		FileUtil.writeFile(KEY_SAVEPATH + "/" + keyFile, KEY.getBytes());
	}
	
}
