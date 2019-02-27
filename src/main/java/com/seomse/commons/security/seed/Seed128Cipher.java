/** 
 * <pre>
 *  파 일 명 : Seed128Cipher.java
 *  설    명 : 인터넷 오픈소스 활용 
 *         
 *  작 성 자 : macle
 *  작 성 일 : 2017.07
 *  버    전 : 1.0
 *  수정이력 : 
 *  기타사항 :
 * </pre>
 * @author Copyrights 2017 by ㈜섬세한사람들. All right reserved.
 */


package com.seomse.commons.security.seed;

import java.io.UnsupportedEncodingException;

import com.seomse.commons.security.Base64;
import com.seomse.commons.security.padding.BlockPadding;

/**
 * SEED algorithm to encrypt or decrypt the data is the class that provides the ability to.
 * @author devhome.tistory.com
 *
 */
public class Seed128Cipher {
	
	/**
	 * SEED encryption algorithm block size
	 */
	private static final int SEED_BLOCK_SIZE = 16;
	
	/**
	 * SEED algorithm to encrypt the data.
	 * @param data Target Data
	 * @param key Masterkey
	 * @param charset Data character set
	 * @return Encrypted data
	 * @throws UnsupportedEncodingException If character is not supported
	 */
	public static String encrypt(String data, byte[] key, String charset)
	throws UnsupportedEncodingException {
		
		byte[] encrypt = null;
		if( charset == null ) {
			encrypt = BlockPadding.getInstance().addPadding(data.getBytes(), SEED_BLOCK_SIZE);
		} else {
			encrypt = BlockPadding.getInstance().addPadding(data.getBytes(charset), SEED_BLOCK_SIZE);
		}
		
		int pdwRoundKey[] = new int[32];
		SEED128.SeedRoundKey(pdwRoundKey, key);
		
		int blockCount = encrypt.length / SEED_BLOCK_SIZE;
		for( int i = 0; i < blockCount; i++ ) {
			
			byte sBuffer[] = new byte[SEED_BLOCK_SIZE];
			byte tBuffer[] = new byte[SEED_BLOCK_SIZE];
			
			System.arraycopy(encrypt, (i * SEED_BLOCK_SIZE), sBuffer, 0, SEED_BLOCK_SIZE);
			
			SEED128.SeedEncrypt(sBuffer, pdwRoundKey, tBuffer);
			
			System.arraycopy(tBuffer, 0, encrypt, (i * SEED_BLOCK_SIZE), tBuffer.length);
		}
		
		return Base64.toString(encrypt);
	}
	
	/**
	 * ARIA algorithm to decrypt the data.
	 * @param data Target Data
	 * @param key Masterkey
	 * @param keySize Masterkey Size
	 * @param charset Data character set
	 * @return Decrypted data
	 * @throws UnsupportedEncodingException If character is not supported
	 */
	public static String decrypt(String data, byte[] key, String charset)
	throws UnsupportedEncodingException {
		
		int pdwRoundKey[] = new int[32];
		SEED128.SeedRoundKey(pdwRoundKey, key);
		
		byte[] decrypt = Base64.toByte(data);
		int blockCount = decrypt.length / SEED_BLOCK_SIZE;
		for( int i = 0; i < blockCount; i++ ) {
			
			byte sBuffer[] = new byte[SEED_BLOCK_SIZE];
			byte tBuffer[] = new byte[SEED_BLOCK_SIZE];
			
			System.arraycopy(decrypt, (i * SEED_BLOCK_SIZE), sBuffer, 0, SEED_BLOCK_SIZE);
			
			SEED128.SeedDecrypt(sBuffer, pdwRoundKey, tBuffer);
			
			System.arraycopy(tBuffer, 0, decrypt, (i * SEED_BLOCK_SIZE), tBuffer.length);
		}
		
		if( charset == null ) {
			return new String(BlockPadding.getInstance().removePadding(decrypt, SEED_BLOCK_SIZE));
		} else {
			return new String(BlockPadding.getInstance().removePadding(decrypt, SEED_BLOCK_SIZE), charset);
		}
	}
	
	/**
	 * The sample code in the Cipher class
	 * @param args none
	 */
	public static void main(String args[]) {
		
		try {
			
			byte[] key = new byte[32];
			for( int i = 0; i < key.length; i++ ) {
				key[i] = (byte)i;
			}
//			
//			String data = "김용수";
//					
////			data = Seed128Cipher.encrypt(data, "1234567890123456".getBytes(), "UTF-8");
////			System.out.println(data);
//			
//			
//			data = Seed128Cipher.decrypt("fegBbUCy+J1t9COdpFGbRVm598c561ORjw2DGzAIHhLj+KWiSl7kW3glyN4wN3ae", "1234567890123456".getBytes(), "UTF-8");
//			System.out.print(data);
//			System.out.println("공책체크");
//		
			String data = "안녕하세요. 아시아나 항공!!!";
			
			String encData = Seed128Cipher.encrypt(data, "1234567890123456".getBytes(), "UTF-8");
			System.out.println(encData);
			
			//복호화
			String decData = Seed128Cipher.decrypt(encData, "1234567890123456".getBytes(), "UTF-8");
			System.out.println(decData);
		
		} catch(Exception e) {
			System.out.println("E:" + e.getMessage());
		}
	}
}