import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketImpl;
import java.util.Properties;
/*
 * Read configuration of the engine from config.properties file
 * where all the path of data folder, port number, etc 
 */
public class Config {
	
	public static enum indexStorage {NAMEKEYWORD, INVERTEDINDEX}

	public static indexStorage indexStorageMethod;
	public static boolean debug;
	public static boolean calcMetrics;
	public static int uploadPort;
	public static int searchPort;
	public static int removePort;
	public static String indexFile;
	public static String docSizeFile;
	public static String watchLocation;
	public static String utilitiesLocation;
	public static String storeLocation;
	
	public static void loadProperties() {
		// TODO Auto-generated method stub
		Properties properties = new Properties();
		
		try{
			properties.load(new FileInputStream("config.properties"));
			
			switch(properties.getProperty("indexStorageStyle","inverted-index")) {
			case "name-keyword":
				indexStorageMethod = indexStorage.NAMEKEYWORD;
				break;
			case "inverted-index":
				indexStorageMethod = indexStorage.INVERTEDINDEX;
				break;
			}
			
			debug = ("true".equals(properties.getProperty("debug")));
			
			calcMetrics = ("true".equals(properties.getProperty("calcMetrics")));
			
			uploadPort = Integer.parseInt(properties.getProperty("uploadPort"));
			
			searchPort = Integer.parseInt(properties.getProperty("searchPort"));
			
			removePort = Integer.parseInt(properties.getProperty("removePort"));
			
//			indexFile = properties.getProperty("indexFile");
			indexFile = "IndexFile.txt";
			
			docSizeFile = "DocSizes.txt";
			
			watchLocation = ".." + File.separator + "cloud" + File.separator + 
					"cloudserver" + File.separator + "watch";
			
			storeLocation = ".." + File.separator + "cloud" + File.separator + 
					"cloudserver" + File.separator + "storage";
			
			utilitiesLocation = ".." + File.separator + "cloud" + File.separator + 
					"cloudserver" + File.separator + "utilities";
			
		} catch (IOException e) {
			System.err.println("Configuration file not found");
		}
	}

}
