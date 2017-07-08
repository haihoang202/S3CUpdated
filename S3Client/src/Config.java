import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
	public static String serverIP;
	public static int searchPort;
	public static int uploadPort;
	public static int removePort;
	public static String stopWordsLocation;
	public static String tempLocation;
	public static String tempEncryptedLocation;
	public static String encryptionKey;
	
	public static void loadProperties(){
		Properties properties = new Properties();
		try{
			properties.load(new FileReader("config.properties"));
			
			serverIP = properties.getProperty("serverIP");
			searchPort = Integer.parseInt(properties.getProperty("searchPort"));
			uploadPort = Integer.parseInt(properties.getProperty("uploadPort"));
			removePort = Integer.parseInt(properties.getProperty("removePort"));
			
			tempLocation = properties.getProperty("tempLocation");
			tempEncryptedLocation = properties.getProperty("tempEncryptedLocation");
			stopWordsLocation = properties.getProperty("stopWordsLocation");
			encryptionKey = properties.getProperty("encryptionKey");
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
