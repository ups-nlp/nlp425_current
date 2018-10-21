package edu.pugetsound.mathcs.nlp.assignments;

import java.util.Scanner;
import java.util.List;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.NLPSuite;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford.StanfordSuite;

/**
 * Text analyzer for assignment 5
 * @author jjenks	
 * @version 10/15/18
 */
public class JesseTextAnalyzer {
	private static final boolean DEBUG = false;
	private static final String QUIT_KEYWORD = "q";
	private static final String PROMPT = "Please enter a sentence (or q to quit)\n>>> ";
	public static void main(String[] args) {
		Scanner inputScanner = new Scanner(System.in);
		String input;

		Conversation myConversation = new Conversation();
		Utterance userUtterance;
		NLPSuite analyzer = new StanfordSuite();

		while (true) {
			System.out.print(PROMPT);
			
			input = inputScanner.nextLine();

			if (DEBUG) {
				System.out.println(input);
			}

			if (input.equals(QUIT_KEYWORD)) {
				break;
			} else {
				userUtterance = new Utterance(input);
				analyzer.analyze(input, userUtterance);
				myConversation.addUtterance(userUtterance);
			}
		}

		System.out.println("Goodbye!");
		
		inputScanner.close();

		List<Utterance> allUtterences = myConversation.getConversation();

		for (Utterance utterance : allUtterences) {
			System.out.println(utterance);
		}
	}
}