/**
 * 
 */
package com.bitBusy.goingg.utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Base64;

/**
 * @author SumaHarsha
 *
 */
public class Encryption {

	private static final String ENCRYPTIONERROR = "Encryption/Decryption error";
	private static final String UTF = "UTF-8";

	/**
	 * @param the_str
	 * @return
	 */
	public static String encryptOneWay(String the_str) {
		if (the_str != null && the_str.length() > 0)
		{
			MessageDigest messageDigest;
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");		
				messageDigest.update(the_str.getBytes());
				String encryptedString = new String(messageDigest.digest());
				return encryptedString;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String encryptTwoWay(Context the_context, String the_str, String the_userName){
		return encryptDecrypt(true, the_context, the_str, the_userName);
	}
	
	public static String decryptTwoWay(Context the_context, String the_str, String the_userName){
		return encryptDecrypt(false, the_context, the_str, the_userName);
	}
	
	private static String encryptDecrypt(boolean isEncryptRequest, Context the_cntxt, String the_str, String the_userName){
		if (the_str != null &&  the_cntxt != null && the_str.getBytes() != null && the_userName != null  &&
				the_userName.length() >= 8){
			/*byte[] input;
			try {
				input = the_str.getBytes("UTF-8");
				byte[] keyBytes = getKeyBytes(the_userName);
				byte[] ivBytes = the_userName.substring(0, 8).getBytes();
				SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
				IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
				Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
				if (isEncryptRequest){
					return encrypt(the_cntxt, input, cipher, ivSpec, key);
				}
				else{
					return decrypt(the_cntxt, input, cipher, ivSpec, key);
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (NoSuchAlgorithmException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (NoSuchPaddingException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			}*/
				 try {
					 
			            DESKeySpec desKeySpec = new DESKeySpec(the_userName.substring(0, 8).getBytes(UTF));
			            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
			            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
			            Cipher cipher = Cipher.getInstance("DES");
			            if (isEncryptRequest){
				            byte[] dataBytes = the_str.getBytes(UTF);
				            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				            return Base64.encodeToString(cipher.doFinal(dataBytes), Base64.DEFAULT);
			            }
			            else{
				            byte[] dataBytes = Base64.decode(the_str, Base64.DEFAULT);
				            cipher.init(Cipher.DECRYPT_MODE, secretKey);
				            byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));
				            return new String(dataBytesDecrypted);
			            }
			        } catch (Exception e) {
			        }
			}
        return null;
	}

	private static byte[] getKeyBytes(String the_user) {
		if (the_user != null && the_user.length() >= 8){
			StringBuilder bldr = new StringBuilder();   
			bldr.append(the_user.substring(4, 8));     
			bldr.append(the_user.substring(0, 4));
			return bldr.toString().getBytes();
		}
		return null;
	}

	private static String decrypt(Context the_cntxt, byte[] the_input, Cipher the_cipher,
			IvParameterSpec the_ivSpec, SecretKeySpec the_key) {
		if (the_input != null && the_cipher != null && the_ivSpec != null && the_key != null){
			try {
				the_cipher.init(Cipher.DECRYPT_MODE, the_key, the_ivSpec);
				byte[] decrypted = new byte[the_cipher.getOutputSize(the_input.length)];
				int dec_len = the_cipher.update(the_input, 0, the_input.length, decrypted, 0);
				dec_len += the_cipher.doFinal(decrypted, dec_len);
				return new String(decrypted, "UTF-8");
			} catch (InvalidKeyException e) {
				//Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (InvalidAlgorithmParameterException e) {
			//	Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			//	Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (ShortBufferException e) {
			//	Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (BadPaddingException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;		
	}

	private static String encrypt(Context the_cntxt, byte[] the_input, Cipher the_cipher,
			IvParameterSpec the_ivSpec, SecretKeySpec the_key) {
		if (the_input != null && the_cipher != null && the_ivSpec != null && the_key != null){
			try {
				the_cipher.init(Cipher.ENCRYPT_MODE, the_key, the_ivSpec);
				byte[] encrypted= new byte[the_cipher.getOutputSize(the_input.length)];
				int enc_len = the_cipher.update(the_input, 0, the_input.length, encrypted, 0);
				enc_len += the_cipher.doFinal(encrypted, enc_len);
				return new String(encrypted);
			} catch (InvalidKeyException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (InvalidAlgorithmParameterException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (IllegalBlockSizeException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (ShortBufferException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			} catch (BadPaddingException e) {
				Utility.throwErrorMessage(the_cntxt, ENCRYPTIONERROR, e.getLocalizedMessage());
			}
		}
		return null;
	}
}
