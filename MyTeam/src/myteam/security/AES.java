package myteam.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AES {
	static String key="secretekey";
	static String salt="salt";
	static String cypher="AES/ECB/PKCS5padding";
	 public static String decryptString( String textToDecrypt) {
	        byte[] rawKey = new byte[32];
	        java.util.Arrays.fill(rawKey, (byte) 0);

	        byte[] keyOk = hmacSha1(salt, key);
	        for (int i = 0; i < keyOk.length; i++) {
	            rawKey[i] = keyOk[i];
	        }

	        SecretKeySpec skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");

	        try {
	            Cipher cipher = Cipher.getInstance(cypher);
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	            byte[] encryptedData = cipher.doFinal(Base64.decode(textToDecrypt, Base64.NO_CLOSE));

	            if (encryptedData == null) return null;

	            return new String(encryptedData);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return null;
	    }

	// encryption method
	    public static String encryptString(String clearText) {
	        byte[] rawKey = new byte[32];
	        java.util.Arrays.fill(rawKey, (byte) 0);

	        byte[] keyOk = hmacSha1(salt, key);
	        for (int i = 0; i < keyOk.length; i++) {
	            rawKey[i] = keyOk[i];
	        }

	        SecretKeySpec skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");

	        try {
	            Cipher cipher = Cipher.getInstance(cypher);
	            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	            byte[] encryptedData = cipher.doFinal(clearText.getBytes());

	            if (encryptedData == null) return null;

	            return Base64.encodeToString(encryptedData, Base64.NO_CLOSE);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return null;

	    }

	// key generator method
	    public static byte[] hmacSha1(String salt, String key) {

	        SecretKeyFactory factory = null;
	        SecretKey keyByte = null;

	        try {
	            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	            KeySpec keyspec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 1024, 256);
	            keyByte = factory.generateSecret(keyspec);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (InvalidKeySpecException e) {
	            e.printStackTrace();
	        }
	        return keyByte.getEncoded();
	    }

}
