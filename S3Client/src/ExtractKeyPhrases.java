import maui.main.MauiModelBuilder;
import maui.main.MauiTopicExtractor;

public class ExtractKeyPhrases {

	private MauiModelBuilder modelBuilder;
	private MauiTopicExtractor topicExtractor;

	public ExtractKeyPhrases() {
		// TODO Auto-generated constructor stub
		modelBuilder = new MauiModelBuilder();
		topicExtractor = new MauiTopicExtractor();
	}
	
	public void extract(String[] option) {
		// TODO Auto-generated method stub
		if (option != null){
			topicExtractor.topicExtractor(option);
		} else {
			System.out.println("Please provide options for Maui");
		}
	}

}
