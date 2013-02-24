package com.foodcircles.android.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class TokenUtil {

	private static String SECRET = "2de4bebce24c38d34bf838a407ee2022";

		public static String hash(String token) {
			try {
				MessageDigest digester = MessageDigest.getInstance("MD5");
				digester.update(token.getBytes());
				digester.update(SECRET.getBytes());
		        byte[] hashedByteArray = digester.digest();

		        String result = new String(hashedByteArray);
				Log.d("TokenUtil", result);
				return result;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
}
