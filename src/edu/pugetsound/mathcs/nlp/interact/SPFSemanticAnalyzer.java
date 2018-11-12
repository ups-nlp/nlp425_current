package edu.pugetsound.mathcs.nlp.interact;

import edu.cornell.cs.nlp.spf.base.string.IStringFilter;
import edu.cornell.cs.nlp.spf.base.string.StubStringFilter;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.SemanticAnalyzer;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

public class SPFSemanticAnalyzer implements SemanticAnalyzer{
	private Interactor<Sentence,LogicalExpression,Sentence> interactor;
	private GenerateInteractor translate = new GenerateInteractor(); 
	
	public SPFSemanticAnalyzer() {
		interactor = translate.generate();
	}
	
	@Override
	public void analyze(Utterance utt, Conversation convo) {
		IStringFilter textFilter = new StubStringFilter();
		//Create sentence object to pass to parser 
		final String currentSentence = textFilter.filter(utt.utterance);
		Sentence sentence = new Sentence(currentSentence);
		Sentence dataItem = new Sentence(sentence);
		IDerivation<LogicalExpression> parse = interactor.interact(dataItem);
		System.out.println(parse);
	}

}
