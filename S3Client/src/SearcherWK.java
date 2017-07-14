import java.util.ArrayList;

/*
 * Child class of Searcher / override createTrapdoor method
 * Use combination/permutation from the search query and their 
 * Wikipedia data extracted using Maui as trapdoor extension 
 */
public class SearcherWK extends Searcher_temp {
	
	public SearcherWK() {
		// TODO Auto-generated constructor stub
		super();
		System.out.println("Searching with option of Wikipedia!");
	}

	@Override
	public void createTrapdoor(String[] queries) {
		// TODO Auto-generated method stub
		for (String term : queries) {
			if (term != "") {
				
				if (term.charAt(term.length()-1) == ' ')
					term = term.substring(0, term.length()-1);
				
				float termweight = (super.CORE_WEIGHT / queries.length) * term.split(" ").length;
				
				if(!weights.containsKey(term))
					weights.put(term, termweight);
			/*
			 * Download wiki for the query term
			 */
				wikipedia.downloadWikiContent(term);
			}
		}
		
		/*
		 * Extract keyword from Wiki data 
		 */
		if (queries.length > 0) {
			wikipedia.getWikiTopics(weights, weights.get(query));
			System.out.println(weights.toString());
			System.out.println("Wikipedia expansion: " + wikipedia.getWikiKey());
		}
		
		System.out.print("Query extension: ["); 
		for (String i : queries) 
			System.out.print(i + " ");
		System.out.println("]");
	}
}
