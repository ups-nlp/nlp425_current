package edu.pugetsound.mathcs.nlp.architecture_nlp.features;

import edu.pugetsound.mathcs.nlp.lang.Utterance;

/**
 * An NLPSuite is any (3rd-party) library that performs a range of NLP analyses
 * typically including tokenization, POS tagging, parsing, named-entity recognition, etc. 
 * 
 * This interface allows us to separate the 3rd-party library from the conversational agent
 * making it easier to swap in/out various such libraries.
 */
public interface NLPSuite {
	
	public void analyze(String sentence, Utterance utterance);
	
		
}
