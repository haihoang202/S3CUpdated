import java.util.ArrayList;
/*
 * Child class of Searcher / override createTrapdoor method
 * Use combination/permutation from the search query and their 
 * Wikipedia data extracted using Maui and synonym from WordNik as trapdoor extension 
 */
public class SearcherWKSN extends Searcher_temp {
	
	public SearcherWKSN() {
		// TODO Auto-generated constructor stub
		super();
		System.out.println("Searching with option of both Synonym and Wikipedia!");
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

				ArrayList<String> synonyms = new ArrayList<>();
				
				/*
				 * Get synonym from WorkNik
				 */
				synonyms = thesaurus.getSynonym(term);
				for (String syn : synonyms) {
					syn = syn.toLowerCase();

					if (!weights.containsKey(syn)) {
						weights.put(syn, termweight / synonyms.size());
						synonymsKey.put(syn, termweight / synonyms.size());
					}
				}
				
				/*
				 * Download Wikipedia data
				 */
				wikipedia.downloadWikiContent(term);
			}
		}
		
		/*
		 * Extract Wikipedia data to keyword and add to trapdoor
		 */
		if (queries.length > 0) {
			wikipedia.getWikiTopics(weights, weights.get(query));
			System.out.println("Wikipedia expansion: " + wikipedia.getWikiKey());
		}
		
		System.out.println("Synonyms expansion: " + synonymsKey);

		System.out.print("Query extension: ["); 
		for (String i : queries) 
			System.out.print(i + " ");
		System.out.println("]");
	}

}
