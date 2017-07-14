import java.util.ArrayList;
/*
 * Child class of Searcher / override createTrapdoor method
 * Only use combination/permutation from the search query as trapdoor extension
 */
public class SearcherKWO extends Searcher_temp {

	public SearcherKWO(){
		super();
		System.out.println("Searching with option of Keyword only!");
	}
	
	@Override
	public void createTrapdoor(String[] queries) {
		// TODO Auto-generated method stub
//		super.createTrapdoor(queries, opt);
		for (String term : queries) {
			if (term != "") {
				
				if (term.charAt(term.length()-1) == ' ')
					term = term.substring(0, term.length()-1);
				
				float termweight = (super.CORE_WEIGHT / queries.length) * term.split(" ").length;
				
				weights.put(term, termweight);

				ArrayList<String> synonyms = new ArrayList<>();
			}
		}
		
		System.out.print("Query extension: ["); 
		for (String i : queries) 
			System.out.print(i + " ");
		System.out.println("]");
	}
}
