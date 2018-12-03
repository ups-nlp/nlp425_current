package edu.pugetsound.mathcs.architecture_nlp.features.spf;

import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelImmutable;

/*
 * Interface for Interactor. Modeled from Yoav Artzi's ITester interface. SAMPLE refers to the type of 
 * data item DI being passed to the Model. Interact will parse a sentence object, returning a lambda
 * calculus representation, while conversation reads a pre-loaded file and writes parses out.
 * 
 * @author Jared Polonitza
 */
public interface IInteract<SAMPLE extends IDataItem<?>, LABEL, DI extends IDataItem<SAMPLE>> {

	//Feed the model a sentence
	IDerivation<LABEL> interact(DI sentence);
	
	//Feed the model a set of sentences
	void conversation();
	
}