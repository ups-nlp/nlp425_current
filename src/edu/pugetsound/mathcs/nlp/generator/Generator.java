package edu.pugetsound.mathcs.nlp.generator;

import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.brain.Action;
import edu.pugetsound.mathcs.nlp.kb.KBController;

public interface Generator {
	
	/**
	 * Given the current state of the conversation, the currently active knowledge base
	 * and the recommended action, this method produces a natural language response.
	 * 
	 * @param conversation
	 * 						Contains all utterances up to the current point in time
	 * @param action
	 * 						The action recommended by the decision maker for the agent to take
	 * @param kb
	 * 						The currently active knowledge base
	 * @return				A string containing a natural language response
	 */
	String generateResponse(Conversation conversation, Action action, KBController kb);

}
