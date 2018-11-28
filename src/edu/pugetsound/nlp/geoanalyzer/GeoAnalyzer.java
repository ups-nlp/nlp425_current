package edu.pugetsound.nlp.geoanalyzer;

import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
public class GeoAnalyzer {
	
	private TextAnalyzer analyzer;
	private KBController knowledge;

	public GeoAnalyzer(String directory) {
		this.knowledge = new KBController(directory);
		this.analyzer = new TextAnalyzer(this.knowledge);
		
	};
	
	public Utterance geoAnalyze(String sentence) {
		
		Utterance parsed = this.analyzer.analyze(sentence, new Conversation());
		
		return parsed;
	}
}
