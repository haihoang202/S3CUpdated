import java.io.File;
import java.util.Scanner;

public class SemanticSearchClient {
/**
 * Application:
 *  - Get user command
 *  	+ Search: -s 
 *  	+ Upload: -u
 *  	+ Remove: -r
 *  	+ Decrypt: -d
 *  - Upon user command, ask for additional information 
 * @param args
 */
	private static String option;
	private static String query;
	private static boolean hasArgs = false;
	
	public static void main(String[] args) {
		Config.loadProperties();
		
		System.out.println("Welcome to the Secured Semantic Search over Encrypted "
				+ "Data Over the Cloud");
		
		Scanner scan = new Scanner(System.in);
		
		if(args.length > 2){
			option = args[0];
		}
		
		System.out.println("\t+ Search: -s"
				+ "\n\t+ Upload: -u"
				+ "\n\t+ Remove: -r "
				+ "\n\t+ Decrypt: -d:");
		
		option = scan.nextLine();
		
		switch (option) {
			/*
			 * Searching part
			 * - Ask for query
			 * - Search option: 
			 * 		* 0: query combination only
			 * 		* 1: query combination + wikipedia + synonym  
			 * 		* 2: query combination + synonym
			 * 		* 3: query combination + wikipedia
			 */
			case "-s": 
				System.out.println("Search for:");
				query = scan.nextLine();
				System.out.println("Option:");
				int opt = scan.nextInt();
				
				Searcher_temp searcher; 
				switch(opt){
				case 0:
					searcher = new SearcherKWO();
					searcher.search(query);
					break;
				case 1:
					searcher = new SearcherWKSN();
					searcher.search(query);
					break;
				case 2:
					searcher = new SearcherSN();
					searcher.search(query);
					break;
				case 3:
					searcher = new SearcherWK();
					searcher.search(query);
					break;
				}
				break;
			
			/*
			 * Uploading part
			 * - Ask for upload folder
			 */
			case "-u": 
				System.out.println("Enter the upload folder:");
				query = scan.nextLine();
				
				File upload = new File(query);
				
				if(!upload.exists()){
					System.out.println("Folder does not exist");
					System.exit(0);
				} else {
					Uploader uploader = new Uploader(query);
					uploader.upload();
				}
				break;
				
			/*
			 * Removing part
			 * - Ask for file to remove
			 */
			case "-r": 
				System.out.println("Enter the file to remove:");
				query = scan.nextLine();
				
				Remover remover = new Remover();
				remover.remove(query);
				break;
			
			/*
			 * Decrypting part
			 * - Ask for file to decrypt
			 */
			case "-d": 
				System.out.println("Ener the file to be decrypted:");
				query = scan.nextLine();
				
				Decrypter decrypter = new Decrypter();
				decrypter.decrypt(query);
				break;
		}
		System.exit(0);
	}
}
