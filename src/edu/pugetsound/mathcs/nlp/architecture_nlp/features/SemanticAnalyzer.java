package edu.pugetsound.mathcs.nlp.architecture_nlp.features;

import java.util.ArrayList;
import java.util.List;

import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.DialogueActTag;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford.StanfordSuite;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford.StanfordTree;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.kb.MyPredicate;
import edu.pugetsound.mathcs.nlp.lang.*;
import edu.pugetsound.mathcs.nlp.util.Logger;
import edu.stanford.nlp.trees.CollinsHeadFinder;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.Tree;

/**
 * This class takes a constituency parse tree and traverses the tree to generate an equivalent
 * first-order logical representation. It is assumed that the constituent parse tree of the 
 * utterance uses tags from the Penn Treebank Tag Set whose tagging guidelines are given in,
 * 
 * Part-of-Speech Tagging Guidelines for the Penn Treebank Project (3rd Revision)
 * Beatrice Santorini
 * University of Pennsylvania
 * 7/1/1990
 * 
 *  It is also assumed that certain analyses have already happened. In particular, this
 *  code relies upon:
 *  
 *  - The grammatical subjects and objects of the sentence having been identified and
 *    stored in the utterance
 *    
 *  - The constituency parse tree having already been stored in the utterance
 *  
 *  - All proper nouns have been stored in the utterance (i.e. basic anaphora resolution)
 * 
 * @author alchambers
 */
public class SemanticAnalyzer {	

	/**
	 * The utterance that is currently being processed by the analyzer
	 */
	protected Utterance current;

	/**
	 * Contains methods for analyzing the String representation of a constituency parse tree
	 */
	protected ParseTreeAnalyzer analyzer;

	/**
	 * Knowledge base to query for anaphora resolution
	 */
	protected KBController kb;


	/**
	 * Constructs a new semantic analyzer
	 */
	public SemanticAnalyzer(KBController kb){
		this.kb = kb;
		this.current = null;			
		analyzer = new ParseTreeAnalyzer();
	}

	/**
	 * Converts an utterance into a (neo-Davidsonian, event reified) first order
	 * expression
	 * 
	 * @param utt The utterance to be translated
	 * @param convo The entire conversation
	 * @return A List of Prolog predicate representing the content of the utterance
	 */
	public void analyze(Utterance utt, Conversation convo){
		current = utt;		

		if(utt.rootConstituency.equals("SQ") || utt.daTag == DialogueActTag.QUESTION_YES_NO){
			utt.firstOrderRep = processYesNoQuestion();
		}
		else if(utt.rootConstituency.equals("SBARQ")){
			// TODO: Fill in
		}
		else if(utt.punct == Punctuation.QUEST_MARK){
			// TODO: Fill in
		}
		else if(utt.rootConstituency.equals("S")){
			utt.firstOrderRep = processStatement();			
		}	
	}



	/*----------------------------------------------------------
	 * 				AUXILIARY METHODS
	 *----------------------------------------------------------*/

	/**
	 * Converts a yes-no question into a first-order logic expression
	 * @return The first-order logical representation
	 */
	private List<MyPredicate> processYesNoQuestion(){
		assert kb != null;

		String utt = current.utterance;		
		MyTree root = current.constituencyParse;

		if(utt.startsWith("Does") ||  utt.startsWith("Do") || utt.startsWith("Did")){			
			root.getChild(0).removeChild(0);
			root.getChild(0).setValue("S");
			return processStatement();
		}
		else if(utt.startsWith("Am") || utt.startsWith("Is") || utt.startsWith("Are") ||
				utt.startsWith("Was") || utt.startsWith("Were")){

			/*
			 * Identifies predicate nominatives and predicate adjectives, e.g.
			 * "Is Fluffy a cat?"
			 * "Is Fluffy white?"
			 */
			if(label(root.getChild(0).getChild(1)).equals("NP") &&
					(label(root.getChild(0).getChild(2)).equals("NP") ||
							label(root.getChild(0).getChild(2)).equals("ADJP"))){
				MyTree copula = root.getChild(0).removeChild(0);				

				// After removing the copula, the predicate nominative phrase is child 1
				MyTree nounPhrase = root.getChild(0).removeChild(1);
				MyTree[] children = new MyTree[2];
				children[0] = copula; 
				children[1] = nounPhrase; 

				// Creates a verbphrase
				//MyTree verbPhrase = new StanfordTree(current.constituencyParse.value(), children);
				//MyTree verbPhrase = factory.newTreeNode(current.constituencyParse.label(), children);
				//verbPhrase.setValue("VP");

				root.getChild(0).addChild(1, root.value(), children);				
				root.getChild(0).setValue("S");

				return processStatement();
			}			
		}
		return new ArrayList<MyPredicate>();
	}

	/**
	 * Converts a statement into a first-order logic expression
	 * @return The first-order logical representation
	 */
	private List<MyPredicate> processStatement(){	
		assert kb != null;		
		return depthFirstSearch(current.constituencyParse.getChild(0));
	}

	
	/**
	 * Performs a depth-first traversal of the parse tree. For each syntactic rule, it applies
	 * a corresponding semantic rule to incrementally build the first-order logical representation
	 * of the utterance
	 * @param node A node in the parse tree
	 * @return A list of Prolog terms and/or predicates
	 */
	private ArrayList<MyPredicate> depthFirstSearch(MyTree node){	
		String nodeLabel = label(node);
		if(Logger.debug()) {
			System.out.println("SemanticAnalyzer DFS: " + node + " with label: " + nodeLabel);
		}

		if(node.numChildren() == 1) {
			return singleChildRules(node, nodeLabel);
		}

		/*
		 * The following conditionals handle rewrite rules of the form:
		 *   A --> B1 	B2 	... BN
		 */

		
		/*
		 * NP --> Det NN
		 * 
		 * TODO: Does this work if NN is a proper noun?
		 * TODO: Right now, I'm ignoring the quantifier. By default, all variables are existentially
		 * quantified. How do we want to deal with the universal quantifier? 
		 */
		if(pattern1(node)){			
			System.out.println("NP --> Det NN entering");
			ArrayList<MyPredicate> child1 = depthFirstSearch(node.getChild(1)); // IsA(X, noun)
			if(Logger.debug()) {
				System.out.println("NP --> Det NN returns back " + child1);
			}
			return child1;
		}

		/*
		 * VP --> Verb NP(directObject)
		 */
		else if(pattern2(node)){	
			ArrayList<MyPredicate> child0 = depthFirstSearch(node.getChild(0)); //IsA(E, verbEvent)
			MyPredicate child1 = depthFirstSearch(node.getChild(1)).get(0); // IsA(X,noun) or Term

			String recipient = child1.getArity() == 0 ? child1.getName() : child1.getArgument(0);
			MyPredicate vp = kb.makeBinaryPredicate(KBController.THEME, child0.get(0).getArgument(0), recipient);

			child0.add(vp);			
			if(child1.getArity() != 0){ 
				child0.add(child1);
			}
			return child0;
		}


		/*
		 * S --> NP(subject) VP(non-copula)
		 */
		else if(pattern3(node) && !analyzer.isCopula(label(node.getChild(1).getTerminal()))){
			ArrayList<MyPredicate> list = depthFirstSearch(node.getChild(0));
			if(Logger.debug()) {
				System.out.println("S --> NP VP returns " + list);
			}
			MyPredicate child0 = list.get(0); // IsA(X,noun) or Term
			ArrayList<MyPredicate> child1 = depthFirstSearch(node.getChild(1)); // IsA(E, verbEvent)
			//assert child1.size() > 0;

			// Agent(E, X)
			// TODO: Can't guarantee that the first predicate in the VP clause has E as the first term
			String actor = child0.getArity() == 0 ? child0.getName() : child0.getArgument(0);			
			MyPredicate agent = kb.makeBinaryPredicate(KBController.AGENT, child1.get(0).getArgument(0), actor); 					

			child1.add(agent);
			if(child0.getArity() != 0){
				child1.add(child0);
			}			
			return child1;
		}


		/*
		 * S --> NP(subject) VP(copula)
		 */
		else if(pattern3(node) && analyzer.isCopula(label(node.getChild(1).getTerminal()))){
			MyPredicate child0 = depthFirstSearch(node.getChild(0)).get(0); // IsA(X,noun1) or Term
			ArrayList<MyPredicate> child1 = depthFirstSearch(node.getChild(1)); // IsA(Y,noun2)

			if(child0.getArity() == 0){
				child1.get(0).addArgument(child0.getName(), 0);
			}
			else{
				String newVar = kb.makeVariable();
				child0.addArgument(newVar, 0);
				child1.get(0).addArgument(newVar, 0);
				child1.add(child0);				
			}
			return child1;
		}


		/*
		 * VP --> Copula NP
		 * TODO: Right now, the copula is being ignored. Later, if we add tense (e.g. past, present)
		 * we'll need to examine the copula
		 */
		else if(pattern4(node)){
			// PrologStructure child0 = depthFirstSearch(node.getChild(0)).get(0); // copula
			ArrayList<MyPredicate> child1 = depthFirstSearch(node.getChild(1)); // IsA(X,noun) or Term

			if(child1.get(0).getArity()==0){
				MyPredicate binaryPred = kb.makeBinaryPredicate(KBController.ISA, kb.makeVariable(), child1.get(0).getName());
				ArrayList<MyPredicate> list = new ArrayList<MyPredicate>();
				list.add(binaryPred);
				return list;
			}
			else{
				return child1;
			}
		}

		/*
		 * VP --> Copula ADJP/ADVP
		 */
		else if(pattern5(node)){
			if(Logger.debug()) {
				System.out.println("Inside pattern 5: Copula ADJP/ADVP");
				System.out.println("Node has " + node.numChildren() + " children");
			}
			MyPredicate child1 = depthFirstSearch(node.getChild(1)).get(0); // Property
			assert(child1.getArity() == 0);	
			
			MyPredicate binaryPred = kb.makeBinaryPredicate(KBController.PROPERTY, kb.makeVariable(), child1.getName());
			ArrayList<MyPredicate> list = new ArrayList<MyPredicate>();
			list.add(binaryPred);
			
			if(Logger.debug()) {
				System.out.println("Inside pattern 5: " + list);
			}
			return list;
		}

		/*
		 * This will return back and cause a runtime exception... 
		 */
		MyTree[] children = node.children();
		for(MyTree child : children){
			depthFirstSearch(child);
		}
		return new ArrayList<MyPredicate>();
	}

	/**
	 * Return the label of a node in the parse tree
	 * @param myTree A node in the parse tree
	 * @return The label of the node
	 */
	private String label(MyTree myTree){
		return myTree.value();
	}


	/**
	 * The semantic attachment for the syntax rule:
	 * 			DT ---> [word]
	 * @param word
	 * @return
	 */
	private String semanticAttachmentDeterminant(String word){
		if(word.equalsIgnoreCase("a") || word.equalsIgnoreCase("an") ||
				word.equalsIgnoreCase("the") || word.equalsIgnoreCase("that") ||
				word.equalsIgnoreCase("this") || word.equalsIgnoreCase("those") ||
				word.equalsIgnoreCase("some")){
			return "EXISTS";
		}
		else if(word.equalsIgnoreCase("every") || word.equalsIgnoreCase("all")){
			return "FORALL";
		}
		else{
			return "???";
		}
	}

	/**
	 * Checks that node has a single child and that child is a leaf
	 * @param node A node in a parse tree
	 * @return true if node has single leaf child, false otherwise
	 */
	private boolean hasSingleLeafChild(MyTree node){
		return node.numChildren()==1 && node.getChild(0).isLeaf();
	}


	/*----------------------------------------------------------------------------------------
	 * 		THESE METHODS IDENTIFY RULES OF THE FORM: A --> B
	 *----------------------------------------------------------------------------------------*/

	/**
	 * Processes all rules of the form A --> B
	 */
	private ArrayList<MyPredicate> singleChildRules(MyTree node, String nodeLabel){
		ArrayList<MyPredicate> list = new ArrayList<>();
		if(analyzer.isProperNoun(nodeLabel)) {
			list.add(properNoun(node));
		}
		else if(analyzer.isPersonalPronoun(nodeLabel)) {
			list.add(personalPronoun(node));
		}
		else if(analyzer.isDeterminer(nodeLabel)) {
			list.add(determinant(node));
		}
		else if(analyzer.isPossessivePronoun(nodeLabel)){
			list.add(possessivePronoun(node));
		}		
		else if(analyzer.isNoun(nodeLabel)){
			System.out.println("before");
			list.add(noun(node));
			System.out.println("after");
		}
		else if(analyzer.isPreposition(nodeLabel)){
			list.add(preposition(node));
		}
		else if(analyzer.isVerb(nodeLabel)){
			list.add(verb(node));
		}
		else if(analyzer.isAdjective(nodeLabel)) {
			list.add(adjective(node));
		}
		else {
			list = depthFirstSearch(node.getChild(0));				
		}			
		if(Logger.debug()) {
			System.out.println("SemanticAnalyzer A->B: " + list);
		}
		return list;			
	}

	
	// Adjective -- e.g., "blue", "sorry", "gentle"
	private MyPredicate adjective(MyTree node) {
		assert hasSingleLeafChild(node);
		String property = label(node.getChild(0));
		return kb.makeConstant(property, null); 
	}
		
	// Determinant -- e.g., "a", "the", "some", "every", "none"
	private MyPredicate determinant(MyTree node){
		assert hasSingleLeafChild(node);
		String det = semanticAttachmentDeterminant(label(node.getChild(0)));
		return kb.makeConstant(det, null);				
	}

	// Personal pronoun -- e.g., "I", "you", "he"
	private MyPredicate personalPronoun(MyTree node){
		assert hasSingleLeafChild(node);
		return kb.makeConstant(label(node.getChild(0)), null);		
	}

	// Proper noun -- e.g., "John" or "London"
	private MyPredicate properNoun(MyTree node){
		assert hasSingleLeafChild(node);
		String childLabel = label(node.getChild(0));

		// The anaphora resolution stage is assumed to have happened
		// before this stage. During anaphora resolution, all proper
		// nouns are saved to a "resolutions" hash
		assert current.resolutions.containsKey(childLabel);

		MyPredicate entity = kb.constantExists(childLabel);
		if(entity == null) {
			entity = kb.makeConstant(childLabel, KBController.ENTITY);
		}
		return entity;		
	}


	// Possessive pronoun -- e.g., "my", "your" , "his"
	private MyPredicate possessivePronoun(MyTree node){
		assert hasSingleLeafChild(node);
		String possessor = label(node.getChild(0));
		return kb.makeBinaryPredicate(KBController.POSSESSION, kb.makeVariable(), possessor);
	}

	// A noun (non-proper) -- e.g., "cats", "fish", "love", "deer"
	private MyPredicate noun(MyTree node){
		assert hasSingleLeafChild(node);
		String noun = label(node.getChild(0));	
		return kb.makeBinaryPredicate(KBController.ISA, kb.makeVariable(), noun);
	}

	// A preposition -- e.g., "with", "on", "in", "by", "between"
	private MyPredicate preposition(MyTree node){
		assert hasSingleLeafChild(node);
		String prep = label(node.getChild(0));
		return kb.makeBinaryPredicate(prep, kb.makeVariable(), kb.makeVariable());
	}

	// A verb -- "hit", "love", "eat"
	private MyPredicate verb(MyTree node){
		assert hasSingleLeafChild(node);
		String verb = label(node.getChild(0));						
		if(analyzer.isCopula(verb)){
			return null;
			//return makeTerm(verb);	// currently this return value is never used
		}
		else{
			return kb.makeBinaryPredicate(KBController.ISA, kb.makeVariable(), verb+"Event");
		}
	}


	/*----------------------------------------------------------------------------------------
	 * 		THE "PATTERN" METHODS IDENTIFY DIFFERENT PRODUCTION RULES IN THE GRAMMAR
	 *----------------------------------------------------------------------------------------*/

	/*
	 * NP --> Det NN
	 */
	private boolean pattern1(MyTree node){
		String nodeLabel = label(node);
		if(!nodeLabel.equals("NP") || node.numChildren() != 2){
			return false;
		}		
		String childLabel0 = label(node.getChild(0));
		String childLabel1 = label(node.getChild(1));
		return analyzer.isDeterminer(childLabel0) && analyzer.isNoun(childLabel1);
	}


	/*
	 * VP --> Verb DirectObject
	 */
	private boolean pattern2(MyTree node){
		String nodeLabel = label(node);		
		if(!nodeLabel.equals("VP") || node.numChildren() != 2){
			return false;
		}
		String childLabel0 = label(node.getChild(0));
		String childLabel1 = label(node.getChild(1));		
		if(!analyzer.isVerb(childLabel0) || !childLabel1.equals("NP")){
			return false;
		}		
		String headNoun = label(node.getChild(1).getTerminal());
		return current.directObjects.size() == 1 && headNoun.equals(current.directObjects.get(0));
	}	


	/*
	 * S --> Subject VP
	 */
	private boolean pattern3(MyTree node){
		String nodeLabel = label(node);
		if(!nodeLabel.equals("S") || node.numChildren() < 2){
			return false;
		}				
		String childLabel0 = label(node.getChild(0));
		String childLabel1 = label(node.getChild(1));				
		if(!childLabel0.equals("NP") || !childLabel1.equals("VP")){
			return false;
		}		
		String headNoun = label(node.getChild(0).getTerminal());
		return current.subjects.size() == 1 && headNoun.equals(current.subjects.get(0));		
	}


	/*
	 * VP --> Copula NP
	 */
	private boolean pattern4(MyTree node){
		String nodeLabel = label(node);		
		if(!nodeLabel.equals("VP") || node.numChildren() != 2){
			return false;
		}
		if(!label(node.getChild(1)).equals("NP")){
			return false;
		}
		String headVerb = label(node.getChild(0).getTerminal());
		return analyzer.isCopula(headVerb);
	}


	/*
	 * VP --> Copula ADJP/ADVP 
	 */
	public boolean pattern5(MyTree node){
		String nodeLabel = label(node);		
		if(!nodeLabel.equals("VP") || node.numChildren() != 2){
			return false;
		}
		String childLabel = label(node.getChild(1));
		if(!childLabel.equals("ADJP") && !childLabel.equals("ADVP")){
			return false;
		}		
		String headVerb = label(node.getChild(0).getTerminal());
		return analyzer.isCopula(headVerb);
	}

	public static void main(String[] args) {
		SemanticAnalyzer analyzer = new SemanticAnalyzer(new KBController(""));
		Conversation convo = new Conversation();
		NLPSuite nlpAnalyzer = new StanfordSuite();

		String sentence = "I kicked the dog.";
		Utterance utt = new Utterance(sentence);

		// Run the NLP Analyzer
		nlpAnalyzer.analyze(sentence, utt);

		analyzer.analyze(utt, convo);
		System.out.println("SEMANTIC ANNOTATION: " + utt.firstOrderRep);
	}

}
