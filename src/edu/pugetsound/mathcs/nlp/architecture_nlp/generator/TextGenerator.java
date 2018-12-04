package edu.pugetsound.mathcs.nlp.architecture_nlp.generator;

import java.io.IOException;

public interface TextGenerator 
{	
	/**
	 * This method will produce a natural language response.
	 * 
	 * @param input 	A string containing an input utterance 
	 * @return String	A string containing a natural language output
	 * @throws IOException 	  if error occurs while sending input to script and receiving response
	 */
	String generateResponse(String input) throws IOException;

}