package edu.pugetsound.mathcs.nlp.architecture_nlp.features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import edu.pugetsound.mathcs.nlp.lang.*;
import edu.pugetsound.mathcs.nlp.util.Logger;
import edu.pugetsound.mathcs.nlp.util.PathFormat;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.spf.SPFSemanticAnalyzer;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.DialogueActTag;
import edu.pugetsound.mathcs.nlp.architecture_nlp.datag.DAClassifier;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford.StanfordSuite;
import edu.pugetsound.mathcs.nlp.kb.KBController;


/**
 * This is the main class responsible for constructing Utterance objects.
 * @author alchambers
 */
public class TextAnalyzer {
	
	/**
	 * Performs NLP analysis include tokenization, pos, parse trees, NER, etc
	 */
	protected NLPSuite nlpAnalyzer;
	
	/**
	 * Maps slang to standardized English forms
	 */
	protected HashMap<String, String> standardizedForms;

	/**
	 * Maps greetings and closing expressions to the respective
	 * dialogue act tags
	 */
	protected HashMap<String, DialogueActTag> greetClose;
	
	/**
	 * Maps contractions to common expansions
	 */
	
	protected HashMap<String,String> contractionExpansion;

	/**
	 * A semantic analyzer to translate from utterances to a first-order representation
	 */
	protected SemanticAnalyzer folSemAnalyzer;
	
	/**
	 * A semantic analyzer
	 */
	protected SemanticAnalyzer spfSemAnalyzer;
	

	/**
	 * An anaphora analyzer
	 */
	protected AnaphoraAnalyzer anaphoraAnalyzer;

	/**
	 * Classifies utterance by dialogue act
	 */
	protected DAClassifier dialogueClassifier;



	/**
	 * Creates a new TextAnalyzer
	 */
	public TextAnalyzer(KBController kb){
		nlpAnalyzer = new StanfordSuite();		
		folSemAnalyzer = new CFGSemanticAnalyzer(kb);
		spfSemAnalyzer = new SPFSemanticAnalyzer();
		anaphoraAnalyzer = new AnaphoraAnalyzer();
		standardizedForms = new HashMap<String, String>();
		contractionExpansion = new HashMap<String, String>();
		greetClose = new HashMap<String, DialogueActTag>();
		dialogueClassifier = new DAClassifier(DAClassifier.Mode.DUMB_NAIVE_BAYES);

		HashReader reader = new HashReader();
		reader.populateGreeting();
		reader.populateStandardForms();
		reader.populateContractions();
	}

	/**
	 * Computes syntactic, semantic, and pragmatic features of a piece of text
	 *
	 * @param input a piece of text
	 * @return an Utterance object that encapsulates all syntactic,
	 * 		   semantic, and pragmatic features of the input
	 *
	 * <br>
	 * <b>Preconditions:</b>
	 * <ul>
	 * 	<li>The input contains a single sentence.</li>
	 * </ul>
	 */
	public Utterance analyze(String input, Conversation conversation) throws IllegalArgumentException {
		if(input == null || conversation == null){
			throw new IllegalArgumentException();
		}
		long start, stop;
		
		/*
		 * The order in which the analysis is done is extremely important! Certain analyzers
		 * require/use the output of other analyzers
		 */

		// Strip ending punctuation
		start = System.currentTimeMillis();
		String stripped = input.replaceAll("\\p{Punct}*$", "");
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tStrip punctuation: " + (stop-start) + " milliseconds");
		}


		// Checks for a standardized form
		start = System.currentTimeMillis();
		if(standardizedForms.containsKey(stripped)){
			input = standardizedForms.get(stripped);
			stripped = input.replaceAll("\\p{Punct}*$", "");
		}
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tStandardized forms: " + (stop-start) + " milliseconds");
		}
		

		// Create the utterance
		start = System.currentTimeMillis();		
		Utterance h = new Utterance(input);
		storePunctuation(h, input);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tStrip punctuation: " + (stop-start) + " milliseconds");
		}

				
		//Sets sentence to lowercase and removes contractions, for use with spf semantic analyzer
		start = System.currentTimeMillis();		
		String canonical = stripped.toLowerCase();
		String[] words = canonical.split(" ");
		for(String w : words) {
			if(contractionExpansion.containsKey(w)) {
				canonical = canonical.replaceAll(w, contractionExpansion.get(w));
			}
		}
		h.canonicalUtterance = canonical;
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tContraction?Expansion?: " + (stop-start) + " milliseconds");
		}

		
		// Run the NLP Analyzer
		nlpAnalyzer.analyze(input, h);
	
		start = System.currentTimeMillis();		
		if(greetClose.containsKey(stripped)){
			h.daTag = greetClose.get(stripped);
			return h;
		}
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tGreeting?Closing?: " + (stop-start) + " milliseconds");
		}

		
		// Certain dialogue acts do not need deep semantic and anaphora analysis
		start = System.currentTimeMillis();

		h.daTag = dialogueClassifier.classify(h, conversation);
		if(canShortCircuit(h)){
			return h;
		}
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tDialogue Classifier: " + (stop-start) + " milliseconds");
		}

		
		start = System.currentTimeMillis();
		anaphoraAnalyzer.analyze(h, conversation);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tAnaphora Analyzer: " + (stop-start) + " milliseconds");
		}

		
		start = System.currentTimeMillis();
		try {
			folSemAnalyzer.analyze(h, conversation);
			spfSemAnalyzer.analyze(h, conversation);
		} catch (java.lang.IndexOutOfBoundsException e) {
			System.out.println("Error with semantic analysis");
			System.out.println(e);
		}
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tSemantic analyzer: " + (stop-start) + " milliseconds");
		}

		if(Logger.debug()) {
			System.out.println("\nThe final utterance is: ");
			System.out.println(h);
		}

		return h;
	}

	/*------------------------------------------------------------------
	 * 						Private Auxiliary Methods
	 *------------------------------------------------------------------*/

	/**
	 * Determine if dialogue act tag is simple enough that further processing (e.g. semantic
	 * and anaphoric) is not necessary
	 */
	private boolean canShortCircuit(Utterance h){
		return h.daTag == DialogueActTag.BACKCHANNEL ||
				h.daTag == DialogueActTag.SIGNAL_NON_UNDERSTANDING ||
				h.daTag == DialogueActTag.AGREEMENTS ||
				h.daTag == DialogueActTag.COMMENT ||
				h.daTag == DialogueActTag.COLLABORATIVE_COMPLETION ||
				h.daTag == DialogueActTag.THANKS ||
				h.daTag == DialogueActTag.WELCOME ||
				h.daTag == DialogueActTag.APOLOGY;
	}



	/**
	 * Sets the ending punctuation mark
	 * @param h The utterance
	 * @param sentence The sentence
	 */
	private void storePunctuation(Utterance h, String sentence){
		if(sentence.endsWith(".")){
			h.punct = Punctuation.PERIOD;
		}
		else if(sentence.endsWith("...")){
			h.punct = Punctuation.ELLIPSIS;
		}
		else if(sentence.endsWith("!")){
			h.punct = Punctuation.EXCLAMATION;
		}
		else if(sentence.endsWith("?")){
			h.punct =  Punctuation.QUEST_MARK;
		}else{
			h.punct = Punctuation.UNKNOWN;
		}
	}

	
	/**
	 * A nested class that reads in from file and populates the respective hashes
	 * @author alchambers
	 *
	 */
	class HashReader{
		/**
		 * Populates the hash of standardized forms
		 */
		public void populateStandardForms(){
			try{
				BufferedReader input = new BufferedReader(new FileReader(PathFormat.absolutePathFromRoot("models/phrases/slang.txt")));
				String line = input.readLine();
				while(line != null){
					int sep = line.indexOf("\t");
					if(sep != -1){
						standardizedForms.put(line.substring(0, sep), line.substring(sep+1));
					}
					line = input.readLine();
				}
				input.close();
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
		/**
		 * Populates the greeting and closing hash
		 */
		public void populateGreeting(){
			try{
				readTextFile(new BufferedReader(new FileReader(PathFormat.absolutePathFromRoot("models/phrases/closing.txt"))), DialogueActTag.CONVENTIONAL_CLOSING);
				readTextFile(new BufferedReader(new FileReader(PathFormat.absolutePathFromRoot("models/phrases/greeting.txt"))), DialogueActTag.CONVENTIONAL_OPENING);
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
		
		

		/**
		 * Populates the hash of contraction expansions.
		 */
		public void populateContractions(){
			try{
				BufferedReader input = new BufferedReader(new FileReader(PathFormat.absolutePathFromRoot("models/phrases/contractions.txt")));
				String line = input.readLine();
				while(line != null){
					int sep = line.indexOf("\t");
					if(sep != -1){
						contractionExpansion.put(line.substring(0, sep), line.substring(sep+1));
					}
					line = input.readLine();
				}
				input.close();
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
		

		/**
		 * Reads lines from a text file and adds them to the greetClose hash with the
		 * corresponding dialogue act tag
		 * @param input A text file
		 * @param tag Corresponding dialogue act tag
		 */
		private void readTextFile(BufferedReader input, DialogueActTag tag){
			try{
				String line = input.readLine();
				while(line != null){
					greetClose.put(line, tag);
					line = input.readLine();
				}
				input.close();
			}
			catch(IOException e){
				System.out.println(e);
			}

		}
	}



	/**
	 * Run this method to inspect the features computed for a given typed
	 * piece of text.
	 * @param args
	 */
	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		TextAnalyzer analyzer = new TextAnalyzer(null);
		Conversation convo = new Conversation();
		while(true){
			System.out.print("Enter a line of text: ");
			String line = scan.nextLine();
			Utterance utt = analyzer.analyze(line, convo);
			convo.addUtterance(utt);
			System.out.println(utt);
		}
	}

}
