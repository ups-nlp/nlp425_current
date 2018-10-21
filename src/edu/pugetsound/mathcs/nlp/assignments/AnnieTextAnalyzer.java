package edu.pugetsound.mathcs.nlp.assignments;

import java.util.Scanner;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

/**
 * This class prompts a user to add Utterances to a Conversation.
 * When the user is finished, a detailed analysis of each Utterance is printed.
 * @author Annie K. Lamar
 * @version Assignment 5
 */

public class AnnieTextAnalyzer {
	
	protected static final String KNOWLEDGE_BASE_PATH = "knowledge/";
	
	/**
	 * Main method for AnnieTextAnalyzer.
	 */
	public static void main (String[] args) {
		//instantiate Conversation, TextAnalyzer, and Scanner
		Conversation conversation = new Conversation(); 
		TextAnalyzer analyzer = new TextAnalyzer(new KBController(KNOWLEDGE_BASE_PATH));
		Scanner keys = new Scanner (System.in); 
		//prompt the user for input
		boolean prompt = true;
		while (prompt) {
			System.out.println("Type text (press q to quit)");
			String input = keys.nextLine();
			if (input.equalsIgnoreCase("q")) {
				prompt = false;
				for (Utterance utterance : conversation.getConversation()) {
					System.out.println(utterance);
				}
			} else {
				Utterance newUtt = analyzer.analyze(input, conversation);
				conversation.addUtterance(newUtt);
			}	
		}
		keys.close();
	}
}
