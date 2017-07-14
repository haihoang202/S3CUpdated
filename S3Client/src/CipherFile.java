import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
/*
 * Cipher: encrypt and decrypt documents
 * Take the file name for encrypting/decrypting file
 */
public class CipherFile {

	public void encrypt(String encryptionKey, String file) throws Throwable {
		// TODO Auto-generated method stub
		encryptOrDecrypt(encryptionKey, Cipher.ENCRYPT_MODE, file);
	}

	public void decrypt(String encryptionKey, String file) throws Throwable {
		// TODO Auto-generated method stub
		encryptOrDecrypt(encryptionKey, Cipher.DECRYPT_MODE, file);
	}
	/*
	 * Using DES method and encryptionKey to encrypt/decrypt file
	 */
	private void encryptOrDecrypt(String encryptionKey, int mode, String file) throws Throwable {
		// TODO Auto-generated method stub
		File fileToCipher = new File(file);
		FileInputStream fis = new FileInputStream(file);
		
		String filename = fileToCipher.getName();
		File encryptedFile = new File(filename);
		if(encryptedFile.exists())
			encryptedFile.delete();
		encryptedFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(encryptedFile);
		
		DESKeySpec dks = new DESKeySpec(encryptionKey.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		
		if(mode == Cipher.ENCRYPT_MODE){
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			CipherInputStream cis = new CipherInputStream(fis, cipher);
			copy(cis, fos);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			copy(fis, cos);
		}
		Files.move(encryptedFile.toPath(), fileToCipher.toPath(), REPLACE_EXISTING);
	}

	/*
	 * Copy data from stream to stream
	 */
	private void copy( InputStream is, OutputStream os) throws IOException{
		// TODO Auto-generated method stub
		byte[] bytes = new byte[64];
		int numBytes;
		while((numBytes = is.read(bytes)) != -1){
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}


	/*
	 * Hashing data using hash code
	 */
	public void hash(String file) {
		// TODO Auto-generated method stub
		
		try{
			File fileToHash = new File(file);
			String filename = fileToHash.getName();
			
			BufferedReader br = new BufferedReader(new FileReader(fileToHash));
			String line;
			ArrayList<String> hashed = new ArrayList<>();
			
			while((line = br.readLine()) != null){
				Integer hash = line.toLowerCase().hashCode();
				hashed.add(hash.toString());
			}
			
			Files.write(fileToHash.toPath(), hashed, Charset.forName("UTF-8"));
		} catch (IOException e){
			System.err.println("Hashing issue !");
		}
	}

}



















