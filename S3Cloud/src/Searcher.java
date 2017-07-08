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

public class Searcher {
	private static IndexFile index;
	private static Ranking rank;
	private static ArrayList<String> queryVector;
	private static ArrayList<String> searchResults;
	private static HashMap<String, Double> res;
	private static ServerSocket serverSocket;
	private static Socket sock;
	private static ArrayList<String> listOfFiles;
	
	public Searcher(IndexFile index) {
		this.index = index;
		rank = new Ranking(index);
		queryVector = new ArrayList<>();
		searchResults = new ArrayList<>();
		listOfFiles = new ArrayList<>();
		serverSocket = null;
		sock = null;
		res = new HashMap<>();
	}
	

	public void retrieveSearchQuery() {
		// TODO Auto-generated method stub
		try{
			serverSocket = new ServerSocket(Config.searchPort);
			sock = serverSocket.accept();
			
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			
			queryVector = (ArrayList<String>) ois.readObject();
			for (String i:queryVector) 
				System.out.println(i);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void rankRelatedQuery() {
		// TODO Auto-generated method stub
		listOfFiles = findRelatedFiles();
		
		res = rank.ScoreAllDocuments(listOfFiles, queryVector);
	}

	private ArrayList<String> findRelatedFiles() {
		// TODO Auto-generated method stub
		HashMap<String, HashSet<String>> indexTable = index.getIndexTable();
		
		for (String item : queryVector){
			if(indexTable.containsKey(item)){
				searchResults.addAll(indexTable.get(item));
			}
		}
		return searchResults;
	}

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
