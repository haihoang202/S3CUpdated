import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/*
 * Ranking to give score for each document of the stored collection upon the trapdoor query vector
 */
public class Ranking {
	IndexFile index;
	private static int numDoc;
	private static int numDocHasKeyword;
	private static float k1;
	private static float b;
	private static double avgDocLength;
	private double IDF;
	private float termFrequency;
	
	//Constructor
	public Ranking(IndexFile in) {
		// TODO Auto-generated constructor stub
		k1 = 0.2f;
		b = 0.75f;
		index = in;
		numDoc = index.getDocumentSizes().size();
		double totalSize = 0;
		for (long size : index.getDocumentSizes().values())
			totalSize += size;
		avgDocLength = totalSize / numDoc;
		System.out.println("Average Length is: " + avgDocLength);
	}

	/*
	 * Get score for each document based on query
	 * Sort the document collection based on its score
	 */
	public HashMap<String, Double> ScoreAllDocuments(ArrayList<String> listOfFiles, ArrayList<String> queryVector) {
		// TODO Auto-generated method stub
		ArrayList<ScoredDocument> scoredDocs = new ArrayList<>();
		LinkedHashMap<String, Double> documentsInOrder = new LinkedHashMap<>();
        System.out.println("This is query vector in ScoreAllDocument function: "+queryVector.toString());
        numDocHasKeyword = listOfFiles.size();
        
		for (String docName : listOfFiles) {
			double score = ScoreSingleDocument(docName, queryVector);
			scoredDocs.add(new ScoredDocument(docName, score));
			System.out.println(docName + " " + score);
		}
		
		/*
		 * Sorting
		 */
		Collections.sort(scoredDocs, ScoredDocument.DocComparator);
		
		for(ScoredDocument sc : scoredDocs)
			documentsInOrder.put(sc.docName, sc.score);
		
		System.out.println(documentsInOrder);
		return documentsInOrder;
	}

	/*
	 * The score of each document is the sum of the score of that document against each element in the query trapdoor 
	 */
	private double ScoreSingleDocument(String docName, ArrayList<String> queryVector) {
		// TODO Auto-generated method stub
		System.out.println("Scoring single document");
		double score = 0;
		for (int i = 0; i < queryVector.size(); i+= 2){
			score += ScoreSingleDocument(docName, queryVector.get(i), Float.parseFloat(queryVector.get(i+1)));	
		}
		return score;
	}

	/*
	 * Formula to get the score of a document to a term of query trapdoor
	 * Paper for reference: http://hpcclab.org/paperPdf/bigdata16/bigdata16.pdf
	 */
	private double ScoreSingleDocument(String docName, String term, float termImportance) {
		// TODO Auto-generated method stub
		HashSet<String> fs = index.getIndexTable().get(term);
		double IDF;
		if(fs != null) {
			int IDFofTerm = index.getIndexTable().get(term).size();
			double IDFEquation = (numDoc - IDFofTerm + 0.5) / (IDFofTerm + 0.5);
			IDF = Math.log10(IDFEquation);
		}
		else
			return 0;
		
		termFrequency = (index.getIndexTable().get(term).contains(docName)) ? termImportance : 0;
		
		try {
			double docLengthNormalization = k1 * ( 1 - b + (b * (index.getDocumentSizes().get(docName) / avgDocLength)));
			
			double score = IDF * ( (termFrequency * (k1 +1)) / (termFrequency + docLengthNormalization)) ;
			
			return score;
			
		} catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println("Error normalization");
			e.printStackTrace();
		}
		
		return 0;
	}

}






















