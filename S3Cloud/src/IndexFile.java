import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/*
 * IndexFile contains index table, document size table 
 * - Index table: hashed keyword mapped with associated files
 * - Document size: file name and its size in bytes
 * 
 */
public class IndexFile {
	private static HashMap<String, Long> documentSizes;
	private static File indexFile;
	private static File docSizeFile;
	private static HashMap<String, HashSet<String>> indexTable;
	private static HashMap<String, HashSet<String>> addedIndex;
	private static BufferedReader br;
	private static String line;
	
	//Constructor
	public IndexFile() throws IOException {
		documentSizes = new HashMap<>();
		indexTable = new HashMap<>();
		line = null;
		br = null;
		docSizeFile = null;
		indexFile= null;
		
		prepareIndexTable();
		prepareDocSizeFile();
		
	}

	/*
	 * Prepare index table depend on chosen method from config.properties file
	 */
	private void prepareIndexTable() throws IOException {
		// TODO Auto-generated method stub
		indexFile = new File(Config.utilitiesLocation + File.separator + Config.indexFile);

		if (!indexFile.exists())
			indexFile.createNewFile();
		
		switch (Config.indexStorageMethod) {
		case NAMEKEYWORD:
			readNameKeyWordStyle();
			break;
		case INVERTEDINDEX:
			readInvertedIndexStyle();
			break;
		default:
			readInvertedIndexStyle();
			break;
		}
		
	}
	
	/*
	 * Add data to index table
	 */
	public void addToPostingList(String topic, String filename) {
		// TODO Auto-generated method stub
		if(!indexTable.containsKey(topic)) {
			indexTable.put(topic, new HashSet<>(Arrays.asList(filename)));
		}
		else {
			if(!indexTable.get(topic).contains(filename))
				indexTable.get(topic).add(filename);
		}
	}

	/*
	 * Read data from Index file to store to index table
	 */
	private void readInvertedIndexStyle() {
		// TODO Auto-generated method stub
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile)));
			
			while ((line = br.readLine())!= null){
				String[] lineTokens = line.split(" ");
				String topic = lineTokens[0];
				
				for(int i = 1; i<lineTokens.length; i++){
					String filename = lineTokens[i];
					addToPostingList(topic, filename);
				}	
			}
			br.close();
		} catch (IOException e){
			System.err.println("Error reading index file");
		}
	}

	private void readNameKeyWordStyle() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Add data to document size
	 */
	private void prepareDocSizeFile() throws IOException {
		// TODO Auto-generated method stub
		docSizeFile = new File(Config.utilitiesLocation + File.separator + Config.docSizeFile);

		if (!docSizeFile.exists())
			docSizeFile.createNewFile();
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(docSizeFile)));
		
		while ((line = br.readLine()) != null){
			String[] data = line.split(" ");
			documentSizes.put(data[0], Long.parseLong(data[1]));
		}
		
		br.close();
	}
	
	public HashMap<String, HashSet<String>> getIndexTable() {
		return indexTable;
	}

	public HashMap<String, Long> getDocumentSizes() {
		return documentSizes;
	}

	/*
	 * Write data from index table back to the index file (for storing purpose)
	 */
	public void writeIndexTableToIndexFile() {
		// TODO Auto-generated method stub
		File newIndexFile = new File(Config.utilitiesLocation + File.separator + "tempIndex.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(newIndexFile));
			Iterator<String>it = indexTable.keySet().iterator();
			
			while (it.hasNext()){
				StringBuilder line = new StringBuilder();
				
				String topic = it.next();
				line.append(topic);
				
				HashSet<String> tempListFile = indexTable.get(topic);
				for(String i : tempListFile) {
					line.append(" ").append(i);
				}
				bw.write(line.toString());
				bw.newLine();
				line = new StringBuilder();
			}
			
			bw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error writing to index file!");
			e.printStackTrace();
		}
		
		Path storePath = Paths.get(Config.utilitiesLocation + File.separator + indexFile.getName()); 
		try {
			Files.copy(newIndexFile.toPath(), storePath, REPLACE_EXISTING);
			newIndexFile.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* 
	 * Write document size to document size file (for storing purpose)
	 */
	public void writeDocumentSizesToDocSizes() {
		// TODO Auto-generated method stub
		File newDocSizesFile = new File(Config.utilitiesLocation + File.separator + "tempDocSizes.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(newDocSizesFile));
			
			StringBuilder sb = new StringBuilder();
			for (String key : documentSizes.keySet()){
				sb.append(key).append(" ").append(documentSizes.get(key));
				
				bw.write(sb.toString());
				bw.newLine();
				
				sb = new StringBuilder();
			}
			bw.close();
			
			
		} catch (Exception e) {
			System.err.println("Error writing to document sizes file");
			e.printStackTrace();
		}
		
		Path storePath = Paths.get(Config.utilitiesLocation + File.separator + docSizeFile.getName()); 
		try {
			Files.copy(newDocSizesFile.toPath(), storePath, REPLACE_EXISTING);
			newDocSizesFile.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

























