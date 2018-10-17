package edu.pugetsound.mathcs.nlp.assignments;

import edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford.StanfordSuite;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

import java.util.Scanner;

/**
 * Basic text analyzer as a test addition to the NLP425 Fall 2018 codebase.
 * 
 * @author David S Smith
 * @version 10/17/2018
 */
public class DavidTextAnalyzer {

	/**
	 * Prompts user to enter utterances or "q"/"Q" to cease entries.
	 * After entries are complete, prints an analysis of each utterance entered using the StanfordSuite.
	 * Utterance analysis is done in order of entry and are labeled starting from 1.
	 * 
	 * @param args
	 */
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        Conversation dialogue = new Conversation();
        StanfordSuite suite = new StanfordSuite();
        
        System.out.println("\n\nText Analyzer Test by David S Smith 2018\n");
        
        while(true){
        	System.out.println("Type a sentence and hit enter, or type 'q' and enter to quit.");
            String input = sc.nextLine();
            input = input.trim();

            //was the input just to exit the conversation?
            if(input.equals("q") || input.equals("Q")){ //i'd normally just to-lower-case the input but i don't know if our model uses capitalization and toLowerCase-ing every sentence for this seems overkill.
                System.out.println("Entries complete.\n");
            	break;
            }
            //otherwise, track utterance and process
            Utterance u = new Utterance(input);
            suite.analyze(input, u);
            dialogue.addUtterance(u);
        }
        
        int i = 1;
        for(Utterance u : dialogue.getConversation()) {
        	System.out.println("Sentence " + i + ":");
        	System.out.println(u + "\n");
        	i++;
        }

        sc.close();

 
    }
}
