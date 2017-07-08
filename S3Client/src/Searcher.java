import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Perform searching functionality - Split query to sub queries of terms -
 * Shuffle terms of the query to get 2^n number of combination - Create Trapdoor
 * of the query (term + synonym + wikipedia with weights) - Send the query to
 * server via Socket and receive result - Display the results
 * 
 * @author Hoang Pham
 *
 */
public class Searcher {
	private static float CORE_WEIGHT = 1.0f;
	String query;
	HashMap<String, Float> weights;
	ArrayList<String> queryVector;
	HashMap<String, Double> searchResult;
	Thesaurus thesaurus;
	Wikipedia wikipedia;
	StopwordRemover stop;
	String[] queries;
	Socket sock;

	// Constructor
	public Searcher() {
		this.query = "";
		this.weights = new HashMap<>();
		this.queryVector = new ArrayList<>();
		this.searchResult = new HashMap<>();
		this.thesaurus = new Thesaurus();
		this.wikipedia = new Wikipedia();
		this.stop = new StopwordRemover();
		this.sock = new Socket();
	}

	/**
	 * This will remove all the stop-words from the query Expand the query to a
	 * trapdoor (synonym + wikipedia expansion) Assign weights to each term in
	 * the trapdoor Send the trapdoor to the server Receive result from the
	 * server
	 * 
	 * @param query
	 */

	public void search(String searchterm) {
		// TODO Auto-generated method stub
		query = searchterm;
		queries = stop.truncate(query);

		weights.put(query, CORE_WEIGHT);

		shuffleQuery(queries);

		createTrapdoor(queries);

		encryptedTrapdoor(weights);

		sendToCloud(weights);

		displayResults();

	}

	/**
	 * Algorithm to get combination from a set of terms Total 2^n combination
	 * The combination is the binary number: [A,B,C] => 2^3 combination: 000
	 * 001: C 010: B 100: A ... 111: ABC
	 * 
	 * @param query_phrase
	 */
	private void shuffleQuery(String[] query_phrase) {
		// TODO Auto-generated method stub
		/**
		 * Shuffle to get combination
		 */
		ArrayList<String> sub = new ArrayList<>();
		ArrayList<String> res = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		int total = (int) Math.pow(2, query_phrase.length);

		for (int i = 0; i < total; i++) {
			String binaryString = Integer.toBinaryString(i);
			binaryString = binaryString.substring(
					binaryString.length() - query_phrase.length >= 0 ? binaryString.length() - query_phrase.length : 0);
			int num = query_phrase.length - 1;

			for (int k = binaryString.toCharArray().length - 1; k >= 0; k--) {
				char j = binaryString.toCharArray()[k];
				int index = (int) Character.getNumericValue(j);
				if (index == 1)
					if (sb.length() > 0)
						sb.append(" " + query_phrase[num]);
					else
						sb.append(query_phrase[num]);
				num--;
			}
			// System.out.println(sb);
			sub.add(sb.toString());
			sb = new StringBuilder();
		}
		
		/**
		 * Get permutation
		 */
		for(String item : sub){
			ArrayList<String> i = new ArrayList<String>(Arrays.asList(item.split(" ")));
			Permutation("", i, res);
		}

		String[] a = new String[0];
		res.removeAll(Collections.singleton(" "));

		this.queries = res.toArray(a);
	}

	public static void Permutation(String prefix, ArrayList<String> s, ArrayList<String> r){
		int n = s.size();
		if (n == 0) 
			r.add(prefix);
		else {
			for (int i = 0; i < n; i++){
				String a = prefix + s.get(i) + " ";
				ArrayList<String> ss = (ArrayList<String>) s.clone();
				ss.remove(i);
				Permutation(a, ss, r);
			}
		}
	}
	
	/**
	 * Assign weight to the term Get synonym from Thesaurus Download Wikipedia
	 * content (deeper processed in Wikipedia method)
	 * 
	 * @param queries
	 */
	private void createTrapdoor(String[] queries) {
		// TODO Auto-generated method stub
		for (String term : queries) {
			if (term != "") {
				float termweight = CORE_WEIGHT / queries.length;
				weights.put(term, termweight);

				ArrayList<String> synonyms = thesaurus.getSynonym(term);

				for (String syn : synonyms) {
					syn = syn.toLowerCase();

					if (!weights.containsKey(syn)) {
						weights.put(syn, termweight / synonyms.size());
					}
				}

				String data = wikipedia.downloadWikiContent(term);
			}
		}

		if (queries.length > 0) {
			wikipedia.getWikiTopics(weights, weights.get(queries[0]));
		}

	}

	private void encryptedTrapdoor(HashMap<String, Float> weights2) {
		// TODO Auto-generated method stub
		for (String term : weights2.keySet()) {
			this.queryVector.add(term.hashCode() + "");
			this.queryVector.add(this.weights.get(term) + "");
		}
	}

	private void sendToCloud(HashMap<String, Float> weights2) {
		// TODO Auto-generated method stub

		try {
			sock = new Socket(Config.serverIP, Config.searchPort);

			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(this.queryVector);

			System.out.println("Waiting for file list from server");

			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Object obj = ois.readObject();
			this.searchResult = (HashMap<String, Double>) obj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void displayResults() {
		// TODO Auto-generated method stub
		int i = 1;
		for (Entry<String, Double> entry : this.searchResult.entrySet()) {
			System.out.println(i + " - Document " + entry.getKey() + " has score: " + entry.getValue());
			i++;
		}
	}

}
