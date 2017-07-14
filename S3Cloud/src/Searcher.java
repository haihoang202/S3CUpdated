import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/*
 * Receive query trapdoor from the client to operate search functionality
 */
public class Searcher {
	private static IndexFile index;
	private static Ranking rank;
	private static ArrayList<String> queryVector;
	private static HashSet<String> searchResults;
	private static HashMap<String, Double> res;
	private static ServerSocket serverSocket;
	private static Socket sock;
	private static ArrayList<String> listOfFiles;
	
	//Constructor
	public Searcher(IndexFile index) {
		this.index = index;
		rank = new Ranking(index);
		queryVector = new ArrayList<>();
		searchResults = new HashSet<>();
		listOfFiles = new ArrayList<>();
		serverSocket = null;
		sock = null;
		res = new HashMap<>();
	}
	
	/*
	 * Open connection to receive search request from client
	 */
	public void retrieveSearchQuery() {
		// TODO Auto-generated method stub
		try{
			serverSocket = new ServerSocket(Config.searchPort);
			System.out.println("Now listening to search request on port " + Config.searchPort);
			sock = serverSocket.accept();
			
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			
			queryVector = (ArrayList<String>) ois.readObject();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/*
	 * Use Ranking class to get document score sorted in order
	 */
	public void rankRelatedQuery() {
		// TODO Auto-generated method stub
		listOfFiles = findRelatedFiles();
		
		res = rank.ScoreAllDocuments(listOfFiles, queryVector);
	}

	/*
	 * Look up index table to see which files contain keyword from the trapdoor
	 */
	private ArrayList<String> findRelatedFiles() {
		// TODO Auto-generated method stub
		System.out.println("Finding related files...");
		HashMap<String, HashSet<String>> indexTable = index.getIndexTable();
		
		for (String item : indexTable.keySet()){
			if (queryVector.contains(item))
				searchResults.addAll(indexTable.get(item));
		}
		
		return new ArrayList<String>(searchResults);
	}

	/*
	 * Send result back to the client and close openning stream and connection
	 */
	public void sendRankedFilesToClient() {
		// TODO Auto-generated method stub
		try {
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(res);
			oos.close();
			sock.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
