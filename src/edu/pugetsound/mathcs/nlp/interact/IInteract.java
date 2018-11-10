package edu.pugetsound.mathcs.nlp.interact;

import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelImmutable;

public interface IInteract<SAMPLE extends IDataItem<?>, LABEL, 
						   DI extends IDataItem<SAMPLE>> {

	//Feed the model a sentence
	IDerivation<LABEL> interact(DI sentence);

}