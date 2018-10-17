package edu.pugetsound.mathcs.nlp.assignments;

import java.util.Scanner;

import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

/**
 * Text analyzer for assignment 5.
 * @author Jasper Raynolds
 */
public class JasperTextAnalyzer {
	
	/**
	 * Collects user input in a loop and prints Utterance analysis on stop.
	 * @param args
	 */
	public static void main(String[] args) {
		Conversation conv = new Conversation();
		TextAnalyzer analyzer = new TextAnalyzer(null);
		
		Scanner in = new Scanner(System.in);
		String input = "";
		
//		Conversational loop
		while(true) {
//			Talk to the user
			System.out.println("Present input: (q to quit)");
			
//			Take input
			input = in.nextLine();
//			Exit on "q" or "Q"
			if(input.toLowerCase().equals("q")) break;
			
//			Analyze, add to conversation
			Utterance utt = analyzer.analyze(input, conv);
			conv.addUtterance(utt);
		}
		
		in.close();
		
//		Print out the utterances
		for(Utterance u : conv.getConversation()) {
			System.out.println(u.toString() + "\n");
		}
	}

}
