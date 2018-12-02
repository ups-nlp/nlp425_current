package edu.pugetsound.mathcs.architecture_nlp.features.spf;

import edu.cornell.cs.nlp.spf.base.string.IStringFilter;
import edu.cornell.cs.nlp.spf.base.string.StubStringFilter;
import edu.cornell.cs.nlp.spf.ccg.lexicon.LexicalEntry;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.SemanticAnalyzer;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

import java.util.LinkedHashSet;
import java.util.List;
/**
 * This class acts as a platform to take user utterances and derive Logical representations
 * of the meaning of the utterance. This is done so via the usage of Yoav Artzi's SPF model, 
 * contained within Interactor.
 * 
 * @author Jared Polonitza
 * @version 1.0
 */
public class SPFSemanticAnalyzer implements SemanticAnalyzer{
	private Interactor<Sentence,LogicalExpression,Sentence> interactor;
	private GenerateInteractor translate; 
	
	//Constructor for SPFSemanticAnalyzer with Interactor that does not contain prepackaged data
	public SPFSemanticAnalyzer() {
		translate = new GenerateInteractor();
		interactor = translate.generate();
	}
	
	//Constructor for SPFSemanticAnalyzer with Interactor that does contain prepackaged data
	public SPFSemanticAnalyzer(String fileName) {
		translate = new GenerateInteractor(fileName);
		interactor = translate.generate();
		testDataSet();
	}
	
	/*
	 * This method takes an utterance, populating the SPFParse field of the utterance
	 * with the resulting parse from the SPF model.
	 * 
	 * @param Utterance utt -> Utterance object to be populated with parse 
	 * 		  Converstaion convo -> Conversation object utterance is to be added too
	 */
	@Override
	public void analyze(Utterance utt, Conversation convo) {
		IStringFilter textFilter = new StubStringFilter();
		//Create sentence object to pass to parser 
		final String currentSentence = textFilter.filter(utt.utterance);
		Sentence sentence = new Sentence(currentSentence);
		Sentence dataItem = new Sentence(sentence);
		IDerivation<LogicalExpression> parse = interactor.interact(dataItem);
		//Set information about the utterance
		if (parse != null) {
			utt.SPFparse = parse.toString();
			utt.SpfWordBreaks = parse.getAllLexicalEntries();
			System.out.println(utt.SPFparse);
		}
 	}
	
	//Read all sentences, write out parses
	public void testDataSet() {
		interactor.conversation();
	}
}
