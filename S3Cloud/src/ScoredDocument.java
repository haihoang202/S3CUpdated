import java.util.Comparator;

public class ScoredDocument {
	public String docName;
	public double score;
	
	public ScoredDocument(String n, double s){
		this.docName = n;
		this.score = s;
	}
	
	public int compareTo(ScoredDocument o){
		return o.score > this.score ? 1 : o.score < this.score ? -1 : 0;
	}
	public static final Comparator<ScoredDocument> DocComparator = new Comparator<ScoredDocument>() {
		public int compare(ScoredDocument doc1, ScoredDocument doc2) {
			return doc1.compareTo(doc2);
		}
	};
}
