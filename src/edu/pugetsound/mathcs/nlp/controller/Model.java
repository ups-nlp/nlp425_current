package edu.pugetsound.mathcs.nlp.controller;

/**
 * A conversational model encompasses an entire philosophy/architecture for conversing.
 * 
 * This interface abstracts away from *how* the agent is formulating responses. Internally,
 * the agent could use an entire NLP pipeline (parsing, entity recognition, ML, etc.) or internally
 * it could be using a single recursive neural network. Or some other formulation not yet developed.
 * 
 * @author alchambers
 *
 */
public interface Model {
	/**
	 * Provides an initial response with which to begin the conversation 
	 * @return A greeting
	 */
	public String initialResponse();
	
	/**
	 * Indicates whether the conversation is finished
	 * @return True if conversation is finished, false otherwise
	 */
	public boolean conversationIsOver();
	
	/**
	 * Given an utterance, returns an appropriate response
	 * @param utterance An input utterance
	 * @return An appropriate natural-language response
	 */
	public String getResponse(String utterance);
	
	
	/**
	 * Loads any necessary state before the conversation begins
	 */
	public void loadState();
	
	/**
	 * Stores any necessary state as a consequence of the conversation
	 */
	public void saveState();
}
