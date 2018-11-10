package edu.pugetsound.mathcs.nlp.assignments;

import java.util.Scanner;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.kb.KBController;

/**
 * This class seeks to demonstrate basic understanding of the inner modules within the agent
 * @author jpolonitza
 */
public class JaredTextAnalyzer {
	private static Conversation convo;
	private static Scanner scan;
	private static TextAnalyzer analyzer;
	private static KBController kb;
	private static final String KBP = "knowledge/";
	
	/**
	 * Main loop for class
	 */
	public static void main(String[] args) {
		 kb = new KBController(KBP);
		 convo = new Conversation();
		 scan = new Scanner(System.in);
		 analyzer = new TextAnalyzer(kb);
		 
		 //provide way of knowing if conversation is completed
		 boolean hiThere = true;
		 while (hiThere) {
			 //Act interested
			 System.out.println("Talk to me! (type q to make me go away...)");
			 //Read human typed input
			 String line = scan.nextLine();
			 //Break if done, else add to conversation
			 if (line.equals("q")) {
				 hiThere = false;
				 for (Utterance u : convo.getConversation()) {
					 System.out.println(u);
				 }
				 scan.close();
			 }
			 else {
				 Utterance u = analyzer.analyze(line,convo);
				 convo.addUtterance(u);
			 }
		 }
	}	
}
