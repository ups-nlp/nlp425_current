package edu.pugetsound.mathcs.nlp.architecture_nlp.generator;

public interface TextGenerator {
	
	/**
	 * This method will produce a natural language response from an input using a trained python model.
	 * 
	 * @param input 	A string containing an input utterance 
	 * @return String	A string containing a natural language output
	 */
	String generateResponse(String input);

}