package edu.pugetsound.mathcs.nlp.controller;

import edu.pugetsound.mathcs.nlp.brain.*;
import edu.pugetsound.mathcs.nlp.brain.mdp.HyperVariables;
import edu.pugetsound.mathcs.nlp.brain.mdp.QLearner;
import edu.pugetsound.mathcs.nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.generator.*;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.*;
import edu.pugetsound.mathcs.nlp.util.Logger;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * This class contains the main input/output loop of the conversational agent
 * @author alchambers, kstern
 */
public class Controller {
	protected static final String KNOWLEDGE_BASE_PATH = "knowledge/";
	
	/*
	 * The stream where all output is sent. Refactored to make it easier
	 * to test input/output functionality
	 */
	protected static PrintStream out;
	protected static Conversation conversation;		
	protected static Scanner input;
	protected static TextAnalyzer analyzer;
	protected static DecisionMaker brain;
	protected static KBController kb;
	protected static Generator decoder;
		
		
	
	/**
	 * Sets up the necessary tools for the conversational agent
	 */
	protected static void setup(InputStream in, PrintStream outStream){
		out = outStream;
		kb = new KBController(KNOWLEDGE_BASE_PATH);
		conversation = new Conversation();	
		analyzer = new TextAnalyzer(kb);
		input = new Scanner(in);
		decoder = new DumbGenerator(); 
				
		// Using a Markov Decision Process for the brain
		final double GAMMA = 0.1; // discounted value for the MDP	
		final int EXPLORE = 1000; // explore/exploit parameter (larger value corresponds to longer explore phase)
		brain = new QLearner(new HyperVariables(GAMMA, EXPLORE),true);
	}

	/**
	 * @param utt The response to the user
	 */
	protected static void respondToUser(Utterance utt){		
		out.println();
		out.println();
		out.println("Agent: " + utt.utterance);
	}

	/**
	 * Prints an initial greeting to the user. This initial greeting
	 * is the first utterance in the conversation
	 */
	protected static void initiateGreeting(){
		Action action = brain.getAction(conversation);
		String response = decoder.generateResponse(conversation, action, kb);
		Utterance agentUtt = analyzer.analyze(response, conversation);
		conversation.addUtterance(agentUtt);
		respondToUser(agentUtt);
	}

	
	/**
	 * Runs a single interaction with the human
	 */
	protected static boolean run(){
		long start, stop;
		if(Logger.debug()) {
			System.out.println("\n\n==== STARTING ROUND ====");
		}
		
		// Read the human's typed input
		out.print("> ");
		String line = input.nextLine();

		// Process the typed input
		start = System.currentTimeMillis();
		Utterance utt = analyzer.analyze(line, conversation);
		stop = System.currentTimeMillis();		
		conversation.addUtterance(utt);
		if(Logger.debug()) {
			System.out.println("Time to run analyzer: " + (stop-start) + " milliseconds");
		}
		
		
		// Get an action from the decision maker
		start = System.currentTimeMillis();
		Action action = brain.getAction(conversation);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("Time to run brain: " + (stop-start) + " milliseconds");
		}

		// Process the action and produce a response for the user
		start = System.currentTimeMillis();
		String response = decoder.generateResponse(conversation, action, kb);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("Time to generate response: " + (stop-start) + " milliseconds");
		}
		
		start = System.currentTimeMillis();
		Utterance agentUtt = analyzer.analyze(response, conversation);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("Time to analyze response: " + (stop-start) + " milliseconds");
		}
		conversation.addUtterance(agentUtt);
		respondToUser(agentUtt);
		
		if(action.equals(Action.CONVENTIONAL_CLOSING)){
			return false;
		}
        return true;		
	}

	/**
	 * Saves the state from the conversation 
	 */
	private static void saveState(){
		brain.saveToFile("");
	}
	
	/**
	 * Main controller for the conversational agent. 
	 * TODO: Add description of any command line arguments
	 */
	public static void main(String[] args){	
		setup(System.in, System.out);		
		initiateGreeting();
		boolean conversing = true;
		while(conversing){
			conversing = run();
		}
		saveState();
	}	
}
