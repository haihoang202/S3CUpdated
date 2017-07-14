import java.io.File;
/*
 * Decrypt encrypted file in the inputEnc folder 
 * Take filename as parameter and use CipherFile class to decrypt
 */
public class Decrypter {
	
	CipherFile cipher;
	
	public Decrypter(){
		cipher = new CipherFile();
	}
	
	public void decrypt(String query) {
		// TODO Auto-generated method stub
		File fileToDecrypt = new File(Config.inputEncrypted + File.separator + query);
		if (fileToDecrypt.exists())
			try {
				System.out.println("Decrypting the file " + fileToDecrypt.getAbsolutePath() + " ...");
				cipher.decrypt(Config.encryptionKey, fileToDecrypt.getAbsolutePath());
				System.out.println("Decrypted!");
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else 
			System.out.println("File " + fileToDecrypt.getAbsolutePath() + " does not exist!");
	}

}
