import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;

/*
 * Wikipedia class to get data from Wikipedia
 * Make request to Wikipedia + topic
 * Download content of that wikipedia page
 * Extract method uses Maui to extract keyword from downloaded content 
 */
public class Wikipedia {
	ExtractKeyPhrases wikiExtractKeyPhrases;
	final String endPoint = "http://en.wikipedia.org/wiki/";
	String[] opts = { "-l", "data/tmp", "-m", "keyphrextr", "-t", "PorterStemmer", "-v", "none" };
	StopwordRemover stop = new StopwordRemover();
	String data = "";
	StringTokenizer st;
	String key;
	ArrayList<String> result;
	BufferedReader br;
	String line;
	HashMap<String, Float> wikiKey;

	/*
	 * Make request to Wikipedia page contain topic
	 * Download content of that page if found
	 * Store the data to a text file
	 */
	public String downloadWikiContent(String term) {
		// TODO Auto-generated method stub
		System.out.println("extracting wiki");
		st = new StringTokenizer(term);
		key = "";

		File file = new File(Config.tempLocation + File.separator + term + ".txt");

		if (file.exists())
			file.delete();

		if (st.countTokens() > 1) {
			key = st.nextToken();
			while (st.hasMoreTokens())
				key = key + "_" + st.nextToken();
		} else
			key = term;

		String theurl = endPoint + term.replace(" ", "%20");

		/*
		 * Start download content from wikipedia topic page
		 * Write content to a file
		 */
		try {
			URL url = new URL(theurl);
			data = Jsoup.parse(url, 10000).text();
			System.out.println(url.toExternalForm());
			PrintWriter pw = new PrintWriter(Config.tempLocation + File.separator + term + ".txt", "UTF-8");
			pw.print(data);
			pw.close();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Invalid wiki url for search string: " + term + " url: " + theurl);
		}

		return data.trim();
	}

	/*
	 * Process the data file using Maui to get key file
	 */
	public void getWikiTopics(HashMap<String, Float> weights, Float float1) {
		// TODO Auto-generated method stub
		ArrayList<String> keyphrases = new ArrayList<>();

		/*
		 * Maui runs on text file to get key file
		 */
		wikiExtractKeyPhrases = new ExtractKeyPhrases();

		wikiKey = new HashMap();

		wikiExtractKeyPhrases.extract(opts);

		ArrayList<String> files = getFiles();

		for (String filename : files) {
			if (filename.endsWith(".txt")) {
				File file = new File(filename);
				file.delete();
			} else if (filename.endsWith(".key")) {
				ArrayList<String> keys = processKeyFile(filename);
				try {
					File keyFile = new File(filename);
					if (keyFile.delete())
						System.out.println("File " + keyFile + " is deleted!");

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				stop.truncate(keys);
				for (String key : keys)
					try {
						if (!weights.containsKey(key)) {
							weights.put(key.toLowerCase(), float1 / keys.size());
							wikiKey.put(key.toLowerCase(), float1 / keys.size());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	/*
	 * Get list of files in data/temp folder
	 */
	private ArrayList<String> getFiles() {
		// TODO Auto-generated method stub
		File dir = new File(Config.tempLocation);
		ArrayList<String> files = new ArrayList<>();
		if (dir.isDirectory()) {
			String[] lists = dir.list();
			for (String item : lists) {
				files.add(Config.tempLocation + File.separator + item);
			}
		} else {
			files.add(Config.tempLocation);
		}

		return files;
	}

	/*
	 * Get content of key file to an array list
	 */
	private ArrayList<String> processKeyFile(String filename) {
		// TODO Auto-generated method stub
		result = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(filename));

			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			br.close();
		} catch (IOException e) {
			System.out.println(filename + " is not found!");
		}

		return result;
	}

	public HashMap<String, Float> getWikiKey() {
		// TODO Auto-generated method stub
		return wikiKey;
	}

}
