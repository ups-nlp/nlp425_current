package edu.pugetsound.mathcs.nlp.architecture_nlp.features;

import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

public interface SemanticAnalyzer {
	
	/**
	 * Converts an utterance into a logical form. It is assumed that the utterance is the last
	 * utterance in the given conversation.
	 * 
	 * The logical form should be stored in the appropriate field inside of the utterance.
	 * 
	 * @param utt The utterance to be translated
	 * @param convo The entire conversation
	 */
	public void analyze(Utterance utt, Conversation convo);
}
