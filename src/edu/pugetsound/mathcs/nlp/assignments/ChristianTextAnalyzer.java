/**
 * 
 */
package edu.pugetsound.mathcs.nlp.assignments;

import java.util.Scanner;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford.StanfordSuite;


/**
 * A simple text analyzer using NLP425 codebase
 * 
 * @author Christian Wiemer
 * @version 10/17/2018
 *
 */
public class ChristianTextAnalyzer {

	/**
	 * Prompts the user to enter a text to analyze or to end the program with "q"
	 * Takes each inputed sentence and stores them in a Conversation
	 * After the user quits, it lists the analysis of each utterance in order they were entered. 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Conversation convo = new Conversation();
		StanfordSuite suite = new StanfordSuite();
		Scanner scan = new Scanner(System.in);
		
		while(true) {
			System.out.println("Type text (press q to quit)");
			String input = scan.nextLine();
			input.trim();
			
			// End the prompt if the user enters "q"
			if (input.equals("q")) {
				System.out.println("Here is an analysis of what you typed");
				break;
			}
			
			//Initialize and analyze utterance while adding it to the conversation
			Utterance utt = new Utterance(input);
			suite.analyze(input, utt);
			convo.addUtterance(utt);
		}
		
		// Print out Utterance analysis
		for(Utterance utt : convo.getConversation()) {
			System.out.println(utt + "\n");
		}
		
		scan.close();
		
	}

}
