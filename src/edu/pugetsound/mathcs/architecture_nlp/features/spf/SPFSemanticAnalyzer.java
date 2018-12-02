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
 * @author jpolonitza
 * @version 1.0
 */
public class SPFSemanticAnalyzer implements SemanticAnalyzer{
	private Interactor<Sentence,LogicalExpression,Sentence> interactor;
	private GenerateInteractor translate; 
	private SPFPreProcessor preProcessor;
	
	public SPFSemanticAnalyzer() {
		translate = new GenerateInteractor();
		interactor = translate.generate();
		preProcessor = new SPFPreProcessor();
	}
	
	public SPFSemanticAnalyzer(String fileName) {
		translate = new GenerateInteractor(fileName);
		interactor = translate.generate();
		preProcessor = new SPFPreProcessor();
		testDataSet();
	}
	
	/*
	 * This method takes an utterance, populating the SPFParse field of the utterance
	 * with the resulting parse from the SPF model.
	 */
	@Override
	public void analyze(Utterance utt, Conversation convo) {
		IStringFilter textFilter = new StubStringFilter();
		//Create sentence object to pass to parser 
		final String currentSentence = preProcessor.process(textFilter.filter(utt.utterance));
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
	
	public void testDataSet() {
		interactor.conversation();
	}
}
