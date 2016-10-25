package com.fhzz.cn.exploremap.util;
import java.security.MessageDigest;

/**
 * @（#）MD5Encode.java
 * 
 * @description: MD5加密
 * @version: Version No.
 * @modify: MODIFIER'S NAME YYYY/MM/DD
 */
public class MD5Encode {

	/**
	 * 加密字符串
	 * 
	 * @param str 需加密的字符串
	 * @return 加密后的字符串
	 */
	public static String encode(String str) {

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(str.getBytes("UTF-8"));

			byte[] hash = md.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
}
