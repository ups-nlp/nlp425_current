package edu.pugetsound.nlp.geoanalyzer;

import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
public class GeoAnalyzer {
	
	public GeoAnalyzer() {};
	
	public static Utterance geoAnalyze(String sentence) {
			
		TextAnalyzer textAnalyzer = new TextAnalyzer(null);
		
		Utterance parsed = textAnalyzer.analyze(sentence, new Conversation());
		
		return parsed;
	}
}
