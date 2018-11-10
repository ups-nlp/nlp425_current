package edu.pugetsound.mathcs.nlp.controller;

import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.CKYDerivation;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.Action;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.DecisionMaker;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp.HyperVariables;
import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp.QLearner;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.architecture_nlp.generator.DumbGenerator;
import edu.pugetsound.mathcs.nlp.architecture_nlp.generator.Generator;
import edu.pugetsound.mathcs.nlp.interact.Interactor;
import edu.pugetsound.mathcs.nlp.interact.GenerateInterator;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.util.Logger;
import edu.cornell.cs.nlp.spf.base.exceptions.FileReadingException;
import edu.cornell.cs.nlp.spf.base.string.IStringFilter;
import edu.cornell.cs.nlp.spf.base.string.StubStringFilter;
import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.data.collection.IDataCollection;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment.Parameters;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;


import java.util.Scanner;


public class SPFModel<SAMPLE extends IDataItem<?>, MR, DI extends IDataItem<SAMPLE>> implements Model{
	protected static final String KNOWLEDGE_BASE_PATH = "knowledge/";
	protected static final String INITIAL_GREETING = "Hello.";	
	
	protected static Conversation conversation;		
	protected Interactor<Sentence,LogicalExpression,Sentence> analyze;
	protected static DecisionMaker brain;
	protected static KBController kb;
	protected static Generator decoder;
	protected static boolean conversationOver;
	protected static GenerateInterator translate; //rename to thing that makes sense
	
	/**
	 * Constructs a new architecture based on an NLP pipeline
	 */
	public SPFModel() {
		kb = new KBController(KNOWLEDGE_BASE_PATH);
		conversation = new Conversation();	
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
		//Create a string filter
		IStringFilter textFilter = new StubStringFilter();
		//Create sentence object to pass to parser 
		final String currentSentence = textFilter.filter(utterance);
		Sentence sentence = new Sentence(currentSentence);
		Sentence dataItem = new Sentence(sentence);
		//Retrieve Parse
		IDerivation<LogicalExpression> parse = analyze.interact(dataItem);
		stop = System.currentTimeMillis();
		
		System.out.println("Time to run analyzer: " + (stop-start) + " milliseconds");
		
		//Information about the parse
		System.out.println(parse.getClass());
		System.out.println(parse);
		//This is the form of the parse we were already using
		System.out.println(parse.getAllLexicalEntries());
		
		//Say things back 
		String response = ("Hi there how are ya?");
		if(utterance.equals("q")) {
			conversationOver = true;
		}		
		return response;
	}

	@Override
	public void loadState() {
		translate = new GenerateInterator();
		analyze = translate.generate();		
	}

	@Override
	public void saveState() {
		// Need to save the brain so we can reload later
		
	}

	@Override
	public String initialResponse() {	
		return INITIAL_GREETING;		
	}
	
}
