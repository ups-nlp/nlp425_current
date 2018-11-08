package edu.pugetsound.mathcs.nlp.controller;

import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.Action;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.DecisionMaker;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp.HyperVariables;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp.QLearner;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.architecture_nlp.generator.DumbGenerator;
import edu.pugetsound.mathcs.nlp.architecture_nlp.generator.Generator;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.util.Logger;

public class NLPModel implements Model {

	protected static final String KNOWLEDGE_BASE_PATH = "knowledge/";
	protected static final String INITIAL_GREETING = "Hello.";	
	
	protected static Conversation conversation;		
	protected static TextAnalyzer analyzer;
	protected static DecisionMaker brain;
	protected static KBController kb;
	protected static Generator decoder;
	protected static boolean conversationOver;
	
	/**
	 * Constructs a new architecture based on an NLP pipeline
	 */
	public NLPModel() {
		kb = new KBController(KNOWLEDGE_BASE_PATH);
		conversation = new Conversation();	
		analyzer = new TextAnalyzer(kb);
		decoder = new DumbGenerator(); 
				
		// Using a Markov Decision Process for the brain
		final double GAMMA = 0.1; // discounted value for the MDP	
		final int EXPLORE = 1000; // explore/exploit parameter (larger value corresponds to longer explore phase)
		brain = new QLearner(new HyperVariables(GAMMA, EXPLORE), false);
		
		conversationOver = false;
	}
	
	@Override
	public boolean conversationIsOver() {
		return conversationOver;
	}

	@Override
	public String getResponse(String utterance) {
		long start, stop;
		if(Logger.debug()) {
			System.out.println("\n\n==== STARTING ROUND ====");
		}
		
		// Process the typed input
		start = System.currentTimeMillis();
		Utterance utt = analyzer.analyze(utterance, conversation);
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
		if(action.equals(Action.CONVENTIONAL_CLOSING)) {
			conversationOver = true;
		}		
		return response;
	}

	@Override
	public void loadState() {
		// Need to load the brain so we're not starting from scratch every time
		
	}

	@Override
	public void saveState() {
		// Need to save the brain so we can reload later
		
	}

	@Override
	public String initialResponse() {
		Utterance agentUtt = analyzer.analyze(INITIAL_GREETING, conversation);
		conversation.addUtterance(agentUtt);		
		return INITIAL_GREETING;		
	}

}
