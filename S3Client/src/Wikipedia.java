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

	public void getWikiTopics(HashMap<String, Float> weights, Float float1) {
		// TODO Auto-generated method stub
		ArrayList<String> keyphrases = new ArrayList<>();

		wikiExtractKeyPhrases = new ExtractKeyPhrases();
		// wikiExtractKeyPhrases.extract(Config.getMauiExtractionOptions(Config.tempLocation));
		// String[] option = opts;
		// option[1] = Config.tempLocation;
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
					weights.put(key, float1 / keys.size());
			}
		}
	}

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
}
