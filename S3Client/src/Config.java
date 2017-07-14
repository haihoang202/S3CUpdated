import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/*
 * Read configuration of the engine from config.properties file
 * where all the path of data folder, port number, etc 
 */
public class Config {
	public static String serverIP;
	public static int searchPort;
	public static int uploadPort;
	public static int removePort;
	public static int maxSearchResults;
	public static String stopWordsLocation;
	public static String tempLocation;
	public static String tempEncryptedLocation;
	public static String encryptionKey;
	public static String inputEncrypted;
	public static String outputDecrypted;
	
	public static void loadProperties(){
		Properties properties = new Properties();
		try{
			properties.load(new FileReader("config.properties"));
			
			serverIP = properties.getProperty("serverIP");
			searchPort = Integer.parseInt(properties.getProperty("searchPort"));
			uploadPort = Integer.parseInt(properties.getProperty("uploadPort"));
			removePort = Integer.parseInt(properties.getProperty("removePort"));
			maxSearchResults = Integer.parseInt(properties.getProperty("maxSearchResults"));
			
			tempLocation = properties.getProperty("tempLocation");
			tempEncryptedLocation = properties.getProperty("tempEncryptedLocation");
			stopWordsLocation = properties.getProperty("stopWordsLocation");
			encryptionKey = properties.getProperty("encryptionKey");
			inputEncrypted = properties.getProperty("inputEncrypted");
			outputDecrypted = properties.getProperty("outputDecrypted");
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
