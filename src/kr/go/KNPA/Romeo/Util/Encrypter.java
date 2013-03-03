package kr.go.KNPA.Romeo.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.kisa.SEED;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

public class Encrypter {
	private static Encrypter _sharedEncrypter = null;
	
	private static final int[] _keyHex = { 	
										0x12, 0x34, 0x56, 0x78,
										0x9A, 0xBC, 0xDE, 0xF0,
										0x01, 0x12, 0x23, 0x34,
										0x45, 0x56, 0x67, 0x78 };
	public static final byte[] key = new byte[_keyHex.length];
	private static final int[] _ivHex = { 
										0xFF, 0xEE, 0xDD, 0xCC,
										0xBB, 0xAA, 0x98, 0x76,
										0x54, 0x32, 0x10, 0x01,
										0x23, 0x45, 0x67, 0x89 };
	public static final byte[] iv = new byte[_ivHex.length];
	
	static {
		// JNI Library Load
		System.loadLibrary("KISACrypto");
	}
	public Encrypter() {
		for(int i=0; i<_keyHex.length ; i++) {
			key[i] = (byte)_keyHex[i];
			iv[i] = (byte)_ivHex[i];
		}
		
	}

	public static Encrypter sharedEncrypter() {
		if(_sharedEncrypter == null) {
			_sharedEncrypter = new Encrypter();
		}
		return _sharedEncrypter;
	}
	
	public String encrypteString (String text) {
		String output = null;
		if(text == null || text.equals("") || text=="") {
		} else {
			byte[] plainText = text.trim().getBytes();
			byte[] cipherText = enc(plainText);
			
			// byteArray - > hex string
			String hexText = new java.math.BigInteger(cipherText).toString(16);
			output = new String(hexText);
			output = output.trim();
		}
		
		return output;
	}
	
	public String decrypteString (String cipher) {
		String output = null;
		if(cipher == null ||  cipher.equals("") || cipher=="") {
		} else {
			// hex string -> byteArray
			byte[] cipherText = new java.math.BigInteger(cipher.trim(), 16).toByteArray();
			byte[] plainText = dec(cipherText);
			output = new String(plainText).trim();
		}
		
		return output;
	}
	
	public byte[] encrypteBytes (byte[] input) {
		return enc(input);
	}
	public byte[] decryteBytes (byte[] input) {
		return dec(input);
	}
	
	private byte[] dec (byte[] cipherText) {
		SEED seed = new SEED();
		seed.init(SEED.DEC, key, iv);
		byte[] plainText = new byte[144];
		int outputTextlen = seed.process(cipherText, cipherText.length, plainText, 0);
		seed.close(plainText,outputTextlen);
		return plainText;
	}
	
	private byte[] enc (byte[] plainText) {
		SEED seed = new SEED();
		seed.init(SEED.ENC, key, iv);
		
		byte[] cipherText = new byte[plainText.length+16];
		int outputTextLen = seed.process(plainText, plainText.length, cipherText, 0);
		seed.close(cipherText, outputTextLen);
		
		return cipherText;
	}
	
	public byte[] encrypte (byte[] input) {
		byte[] output = new byte[input.length];
		SEED seed = new SEED();
		// SEED CBC 알고리즘 테스트를 위한 입력·출력 버퍼가 필요하다.
		// 암호화 과정에서는 출력되는 버퍼의 크기는 입력되는 버퍼 보다 항상 크다.
		// 출력 버퍼의 크기는 최소 “입력되는 버퍼 크기 + 1” bytes 이상이고, “입력되는 버퍼 크기 + SEED 한 블럭 사이즈(16 bytes)” 이다.
		// 따라서, 암호화 시 출력 버퍼의 크기는 “입력버퍼 크기 +16”으로 설정하거나, seed.getOutputSize() 함수를 통해 설정해야한다.
		byte[] plainText = new byte[128];
		byte[] cipherText = new byte[144];
		int outputTextLen = 0;
		
		seed.init(SEED.ENC, key, iv);
		outputTextLen = seed.process(plainText, plainText.length, cipherText, 0);
		
		seed.close(cipherText, outputTextLen);
		return output;
	}
	
	public byte[] decrypte(byte[] input) {
		byte[] output = new byte[input.length];
		
		SEED seed = new SEED();
		
		byte[] plainText = input;//new byte[144];
		byte[] cipherText = new byte[input.length+16];//new byte[144];
		int outputTextLen = 0;
		
		seed.init(SEED.DEC, plainText, cipherText);
		outputTextLen = seed.process(cipherText, cipherText.length, plainText, 0);
		seed.close(plainText, outputTextLen);
		
		for(int i=0; i<output.length; i++) {
			output[i] = cipherText[i];
		}
		return output;
	}
	
	// hex to byte[]
	public static byte[] hexToByteArray(String hex) {
	    if (hex == null || hex.length() == 0) {
	        return null;
	    }
	 
	    byte[] ba = new byte[hex.length() / 2];
	    for (int i = 0; i < ba.length; i++) {
	        ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
	    }
	    return ba;
	}
	 
	// byte[] to hex
	public static String byteArrayToHex(byte[] ba) {
	    if (ba == null || ba.length == 0) {
	        return null;
	    }
	 
	    StringBuffer sb = new StringBuffer(ba.length * 2);
	    String hexNumber;
	    for (int x = 0; x < ba.length; x++) {
	        hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
	 
	        sb.append(hexNumber.substring(hexNumber.length() - 2));
	    }
	    return sb.toString();
	} 
	
	public static byte[] objectToBytes(Serializable obj) {
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( new Base64OutputStream(baos, Base64.NO_PADDING | Base64.NO_WRAP));
			oos.writeObject(obj);
			oos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String objectToString(Serializable obj) {
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( new Base64OutputStream(baos, Base64.NO_PADDING | Base64.NO_WRAP));
			oos.writeObject(obj);
			oos.close();
			return baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object stringToObject(String str) {
		try {
			return new ObjectInputStream(
						new Base64InputStream(
								new ByteArrayInputStream(str.getBytes()), Base64.NO_PADDING | Base64.NO_WRAP)).readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object bytesToObject(byte[] bytes) {
		try {
			return new ObjectInputStream(
						new Base64InputStream(
								new ByteArrayInputStream(bytes), Base64.NO_PADDING | Base64.NO_WRAP)).readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
