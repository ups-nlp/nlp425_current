package edu.pugetsound.mathcs.nlp.assignments;

import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import java.util.Scanner;

/**
 * This class prompts a user for utterances and displays an analysis of each utterance.
 * @author kmramos
 * @version3 10/17/2018
 */
public class KaylaTextAnalyzer 
{
	//Using protected so it can be seen by subclasses or other package members.
	//Also, entire KBController is protected in SemanticAnalyzer used by TextAnalyzer.
	protected static final String KnowledgeBasePath = "KnowledgeBase/"; 
	
	/**
	 * Main method that takes in a user input as a String and prints the analysis of each input
	 * @param args
	 */
	public static void main(String[] args)
	{
		Conversation convo = new Conversation();
		TextAnalyzer analyzer = new TextAnalyzer(new KBController(KnowledgeBasePath));
		Scanner scan = new Scanner(System.in);
		
		//Take in user input and analyze
		boolean isUtterance = true;
		while(isUtterance)
		{
			System.out.println("Please type something (type 'QUIT' to quit)");
			String input =  scan.nextLine();
			if (input.equals("QUIT"))
			{
				System.out.println("Here is your analysis of each input: ");
				for (Utterance utt : convo.getConversation()) 
				{
					System.out.println(utt);					
				}
				
				isUtterance = false;
				scan.close();
			}
			else
			{
				Utterance utterance = analyzer.analyze(input, convo);
				convo.addUtterance(utterance);
			}
		}
	}
}
