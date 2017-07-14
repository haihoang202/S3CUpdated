import java.net.ServerSocket;
import java.net.Socket;
/*
 * Server class
 * Openning thread for different operations (Search/Upload/Remove)
 */
public class SemanticSearchCloud {
	private static ServerSocket listener;
	private static Socket sock = null;
	private static String request;
	private static Object obj;
	private static IndexFile index;
	private static RetrieveUploadedFiles retriever;
	private static Searcher searcher;
	private static Remover remover;
	
	public static void main(String[] args) {
		System.out.println("Welcome to the Secured Semantic Search Cloud over Encrypted Cloud!");
		
		try {
			/*
			 * Load configuration from Config class
			 */
			Config.loadProperties();
			
			/*
			 * Populate index table
			 */
			index = new IndexFile();
			retriever = new RetrieveUploadedFiles(index);
			searcher = new Searcher(index);
			remover = new Remover(index);
			
			/*
			 * 
			 */
			Thread uThread = new Thread(){
				public void run(){
					while(true)
						retriever.retrieve();
				}
			};
			
			/*
			 * Search thread to listen to search request
			 */
			Thread sThread = new Thread(){
				public void run(){
					while(true){
						searcher.retrieveSearchQuery();
						searcher.rankRelatedQuery();
						searcher.sendRankedFilesToClient();
					}
				}
			};
			
			/*
			 * Remove thread to listen to remove request
			 */
			Thread rThread = new Thread() {
				public void run() {
					while(true){
						remover.waitToRemove();
					}
				}
			};
			
			System.out.println("Waiting for clients!");
			
			/*
			 * Start threads
			 */
			uThread.start();
			sThread.start();
			rThread.start();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
