import java.util.ArrayList;

/*
 * Child class of Searcher / override createTrapdoor method
 * Use combination/permutation from the search query and their 
 * synonym from WordNik API as trapdoor extension 
 */
public class SearcherSN extends Searcher_temp {

	public SearcherSN() {
		// TODO Auto-generated constructor stub
		super();
		System.out.println("Searching with option of Synonym!");
	}
	@Override
	public void createTrapdoor(String[] queries) {
		// TODO Auto-generated method stub
		for (String term : queries) {
			if (term != "") {
				
				if (term.charAt(term.length()-1) == ' ')
					term = term.substring(0, term.length()-1);
				
				float termweight = (super.CORE_WEIGHT / queries.length) * term.split(" ").length;
				
				weights.put(term, termweight);

				ArrayList<String> synonyms = new ArrayList<>();
				
				/*
				 * Get synonym from WordNik
				 */
				synonyms = thesaurus.getSynonym(term);
				for (String syn : synonyms) {
					syn = syn.toLowerCase();

					if (!weights.containsKey(syn)) {
						weights.put(syn, termweight / synonyms.size());
						synonymsKey.put(syn, termweight / synonyms.size());
					}
				}			
			}
		}
		
		System.out.println("Synonyms expansion: " + synonymsKey);

		System.out.print("Query extension: ["); 
		for (String i : queries) 
			System.out.print(i + " ");
		System.out.println("]");
	
	}	
}
