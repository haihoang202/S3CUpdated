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
 *  
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
		
			case "-s": //Searching part
				System.out.println("Search for:");
				query = scan.nextLine();
				
				Searcher searcher = new Searcher();
				searcher.search(query);
				searcher.displayResults();
				break;
			case "-u": //Uploading part
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
			case "-r": //Removing part
				System.out.println("Enter the file to remove:");
				query = scan.nextLine();
				
				Remover remover = new Remover();
				remover.remove(query);
				break;
			case "-d": //Decrypting part
				System.out.println("Ener the file to be decrypted:");
				query = scan.nextLine();
				
				Decrypter decrypter = new Decrypter();
				decrypter.decrypt(query);
				break;
		}
		System.exit(0);
	}
}
