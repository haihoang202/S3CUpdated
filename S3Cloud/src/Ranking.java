import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Ranking {
	private static IndexFile index;
	private static int numDoc;
	private static float k1;
	private static float b;
	private static double avgDocLength;
	private double IDF;
	private float termFrequency;
	
	public Ranking(IndexFile index) {
		// TODO Auto-generated constructor stub
		k1 = 0.2f;
		b = 0.75f;
		Ranking.index = index;
		numDoc = index.getDocumentSizes().size();
		double totalSize = 0;
		for (long size : index.getDocumentSizes().values())
			totalSize += size;
		avgDocLength = totalSize / numDoc;
	}

	public HashMap<String, Double> ScoreAllDocuments(ArrayList<String> listOfFiles, ArrayList<String> queryVector) {
		// TODO Auto-generated method stub
		ArrayList<ScoredDocument> scoredDocs = new ArrayList<>();
		HashMap<String, Double> documentsInOrder = new HashMap<>();
		
		for (String docName : listOfFiles) {
			scoredDocs.add(new ScoredDocument(docName, ScoreSingleDocument(docName, queryVector)));
		}
		
		Collections.sort(scoredDocs, ScoredDocument.DocComparator);
		
		for(ScoredDocument sc : scoredDocs){
			documentsInOrder.put(sc.docName, sc.score);
//			documentsInOrder.add(sc.docName + " " + sc.score);
		}
		System.out.println(documentsInOrder);
		return documentsInOrder;
	}

	private double ScoreSingleDocument(String docName, ArrayList<String> queryVector) {
		// TODO Auto-generated method stub
		int score = 0;
		for (int i = 0; i < queryVector.size(); i+= 2){
			score += ScoreSingleDocument(docName, queryVector.get(i), Float.parseFloat(queryVector.get(i+1)));	
		}
		return score;
	}

	private double ScoreSingleDocument(String docName, String term, float termImportance) {
		// TODO Auto-generated method stub
		HashSet<String> fs = index.getIndexTable().get(term);
		
		if(fs != null) {
			int IDFofTerm = index.getIndexTable().size();
			double IDFEquation = (numDoc - IDFofTerm + 0.5) / (IDFofTerm + 0.5);
			IDF = Math.log10(IDFEquation);
		}
		else
			return 0;
		
		termFrequency = (index.getIndexTable().get(term).contains(docName)) ? termImportance : 0;
		
		try {
			double docLengthNormalization = k1 * ( 1 - b + (b * (index.getDocumentSizes().get(docName) / avgDocLength)));
			
			double score = IDF * ( (termFrequency * (k1 +1)) / (termFrequency + docLengthNormalization));
			
			return score;
			
		} catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println("Erro normalization");
			e.printStackTrace();
		}
		
		return 0;
	}

}






















