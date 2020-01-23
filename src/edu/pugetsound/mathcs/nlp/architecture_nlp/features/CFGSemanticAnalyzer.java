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
 * This class takes a constituency parse tree (derived from a context free grammar) and traverses 
 * the tree to generate an equivalent first-order logical representation. It is assumed that the 
 * constituent parse tree of the utterance uses tags from the Penn Treebank Tag Set whose 
 * tagging guidelines are given in,
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
public class CFGSemanticAnalyzer implements SemanticAnalyzer {	

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
	public CFGSemanticAnalyzer(KBController kb){
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
	 */
	public void analyze(Utterance utt, Conversation convo){
		current = utt;		
		
		System.out.println("Starting.... with parse tree: " + utt.constituencyParse);

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
		System.out.println("FINAL PARSE IS: " + utt.firstOrderRep);
		
		
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
		System.out.println("DFS: " + nodeLabel + " with " + node.numChildren() + " children");
		
		// The node has only 1 child
		if(node.numChildren() == 1) {
			return singleChildRules(node, nodeLabel);
		}
		
		// Recurse on each child
		ArrayList<ArrayList<MyPredicate>> children = new ArrayList<>(); // God this is ugly....
		for(int i = 0; i < node.numChildren(); i++) {
			MyTree child = node.getChild(i);
			if(analyzer.isPunctuation(label(child))) {
				children.add(null);
			}
			else {
				children.add(depthFirstSearch(child));
			}
		}
		
		
		// Okay, now we have the predicates for each child. Put them together according to the rule
		

		/*
		 * NP --> Det NN
		 * The dog is black.
		 * 
		 * TODO: Does this work if NN is a proper noun?
		 */
		if(pattern1(node)){
			// We are ignoring children.get(0) which is the determinant...by default all variables are existentially
			// quantified. How do we want to deal with the universal quantifier?
			assert(children.get(1) != null);
			System.out.println("Pattern1: " + children.get(1));
			return children.get(1);
		}

		/*
		 * VP --> Verb NP(directObject)
		 * The boy kicked the ball.
		 */		
		else if(pattern2(node)){	
			ArrayList<MyPredicate> verb = children.get(0);
			assert(verb.size() == 1);
			
			ArrayList<MyPredicate> nounPhrase = children.get(1);			
			MyPredicate directObject = nounPhrase.get(0);						
			String recipient = directObject.getArity() == 0 ? directObject.getName() : directObject.getArgument(0);			
			
			// Create a new predicate to express the object of the verb
			MyPredicate vp = kb.makeBinaryPredicate(KBController.THEME, verb.get(0).getArgument(0), recipient);
			
			// Add everything to the verb phrase
			verb.add(vp);			
			verb.addAll(nounPhrase);
			System.out.println("Pattern2: " + verb);
			return verb;
		}

		
		/*
		 * S --> NP(subject) VP(copula)
		 * The dog is black.
		 */
		else if(pattern3(node) && analyzer.isCopula(label(node.getChild(1).getTerminal()))){
			MyPredicate noun = children.get(0).get(0); // IsA(X, noun) or Term
			System.out.println("\t>>>>" + noun);
 			ArrayList<MyPredicate> verbPhrase = children.get(1); // property(X, descriptor)
 			System.out.println("\t>>>>" + verbPhrase);

 			// The verb phrase is of the form property(X, descriptor). We need to unify X with the noun
 			// In this case, the noun is a proper noun and so we overwrite X with the name of the proper noun
			if( noun.getArity() == 0){
				verbPhrase.get(0).addArgument(noun.getName(), 0);
			}
			
			// In this case, the verb phrase is of the form property(X, descriptor) and the noun is of the form
			// isA(Y, noun). We need to unify X and Y so we create a new variable Z and we overwrite 
			// property(X, descriptor) with property(Z, descriptor) and isA(Y, noun) with isA(Z, noun).
			else{
				String newVar = kb.makeVariable();
				noun.addArgument(newVar, 0);
				verbPhrase.get(0).addArgument(newVar, 0);
				verbPhrase.addAll(children.get(0));				
			}			
			System.out.println("Pattern3: " + verbPhrase);
			return verbPhrase;
		}


		/*
		 * VP --> Copula NP
		 * Bob is a teacher.
		 */
		else if(pattern4(node)){
			 // TODO: Right now, the copula is being ignored. Later, if we add tense (e.g. past, present)
			 // we'll need to examine the copula
			ArrayList<MyPredicate> nounPhrase = children.get(1); // IsA(X,noun) or Term

			if(nounPhrase.get(0).getArity()==0){
				MyPredicate binaryPred = kb.makeBinaryPredicate(KBController.ISA, kb.makeVariable(), nounPhrase.get(0).getName());
				ArrayList<MyPredicate> list = new ArrayList<MyPredicate>();
				list.add(binaryPred);
				System.out.println(nodeLabel + "Pattern4 if statement returned: " + list);
				return list;
			}
			else{
				System.out.println(nodeLabel + "Pattern4 else statement returned: " + nounPhrase);
				return nounPhrase;
			}
		}

		
		/**
		 * PICK UP HERE WITH REFACTORING!!!
		 */
		
		
		/*
		 * VP --> Copula ADJP/ADVP
		 * 
		 * WHY DOES THIS ENCOMPASS ADVP AS WELL?
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
				System.out.println(nodeLabel + "Inside pattern 5: " + list);
			}
			
			System.out.println(nodeLabel + "Pattern5 returned: " + list);
			return list;
		}
		
		/*
		 * VP --> Verb ADVP
		 */
		else if(pattern6(node)) {
			ArrayList<MyPredicate> child0 = depthFirstSearch(node.getChild(0)); // IsA(E, verbEvent)						
			MyPredicate child1 = depthFirstSearch(node.getChild(1)).get(0); // Return adverb			
			assert(child1.getArity() == 0);	
				
			MyPredicate manner = kb.makeBinaryPredicate(KBController.MANNER, child0.get(0).getArgument(0), child1.getName()); // manner(E, adverb)

			child0.add(manner);
			System.out.println(nodeLabel + "Pattern6 returned: " + child0);
			return child0;
		}
		/*
		 * VP --> ADVP Verb
		 */
		else if(pattern6b(node)) {
			MyPredicate child0 = depthFirstSearch(node.getChild(0)).get(0); // Return adverb						
			ArrayList<MyPredicate> child1 = depthFirstSearch(node.getChild(1)); // IsA(E, verbEvent)			
			assert(child0.getArity() == 0);	
				
			MyPredicate manner = kb.makeBinaryPredicate(KBController.MANNER, child1.get(0).getArgument(0), child0.getName()); // manner(E, adverb)

			child1.add(manner);
			System.out.println(nodeLabel + "Pattern6 returned: " + child1);
			return child1;
		}
		/*
		 * S --> NP ADVP VP
		 */
		else if(pattern7(node)) {
			MyPredicate  noun = depthFirstSearch(node.getChild(0)).get(0); // Return noun

			MyPredicate adverb = depthFirstSearch(node.getChild(1)).get(0); // Return adverb						
			assert(adverb.getArity() == 0);
			
			ArrayList<MyPredicate> verb = depthFirstSearch(node.getChild(2)); // Return IsA(E, verbEvent)			
							
			MyPredicate manner = kb.makeBinaryPredicate(KBController.MANNER, verb.get(0).getArgument(0), adverb.getName()); // manner(E, adverb)
			verb.add(manner);
			
			String actor = noun.getArity() == 0 ? noun.getName() : noun.getArgument(0);			
			MyPredicate agent = kb.makeBinaryPredicate(KBController.AGENT, verb.get(0).getArgument(0), actor); 					

			verb.add(agent);
			if(noun.getArity() != 0){
				verb.add(noun);
			}			
			
				
			System.out.println(nodeLabel + "Pattern7 returned: " + verb);

			return verb;
		}
		
		
		
		/*
		 * S --> NP VP(non-copula)
		 */
		 if(pattern3(node) && !analyzer.isCopula(label(node.getChild(1).getTerminal()))){			
			ArrayList<MyPredicate> nounList = depthFirstSearch(node.getChild(0)); // predicates related to the subject/noun
			ArrayList<MyPredicate> verbList = depthFirstSearch(node.getChild(1)); // predicates related to the verb
			
			// TODO: Need to guarantee that isA(E, verbEvent) is always the first predicate in the list. THIS IS HUGELY IMPORTANT!!!
			// TODO: Need to do something analogous for the noun phrase
			String actor = nounList.get(0).getArity() == 0 ? nounList.get(0).getName() : nounList.get(0).getArgument(0);			
			
			MyPredicate agent = kb.makeBinaryPredicate(KBController.AGENT, verbList.get(0).getArgument(0), actor); 					

			// We add all predicates to the verb list...
			verbList.add(agent);
			verbList.addAll(nounList);
			
			System.out.println(nodeLabel + "Pattern3 returned: " + verbList);
			return verbList;
		}
		 
		 System.out.println(nodeLabel + "Found no matching pattern. Returning empty arraylist");
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
		System.out.println("\t" + nodeLabel);
		
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
			list.add(noun(node));			
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
		else if(analyzer.isAdverb(nodeLabel)) {
			list.add(adverb(node));
			System.out.println("\tList: " + list);
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

	// Adverb -- e.g., "regularly", "quickly," "slowly"
	private MyPredicate adverb(MyTree node) {
		System.out.println("\t\t" + node);
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
	private boolean pattern5(MyTree node){
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

	/*
	 * VP --> Verb ADVP
	 */
	private boolean pattern6(MyTree node) {
		String nodeLabel = label(node);
		if(!nodeLabel.equals("VP") || node.numChildren() != 2) {
			return false;
		}
		
		String childLabel1 = label(node.getChild(0));
		String childLabel2 = label(node.getChild(1));
		if((childLabel1.equals("VB") || childLabel1.equals("VBP")) && childLabel2.equals("ADVP")) {
			return true;
		}
		return false;
	}
	
	/*
	 * VP --> ADVP Verb
	 */
	private boolean pattern6b(MyTree node) {
		String nodeLabel = label(node);
		if(!nodeLabel.equals("VP") || node.numChildren() != 2) {
			return false;
		}
		
		String childLabel1 = label(node.getChild(0));
		String childLabel2 = label(node.getChild(1));
		if((childLabel2.equals("VB") || childLabel2.equals("VBP")) && childLabel1.equals("ADVP")) {
			return true;
		}
		return false;
	}
	
	/*
	 * S --> NP ADVP VP
	 */
	private boolean pattern7(MyTree node) {
		String nodeLabel = label(node);
		if(!nodeLabel.equals("S") || node.numChildren() < 3) {
			System.out.println("Returned false1++++++");
			return false;
		}
		
		String childLabel1 = label(node.getChild(1)); // Adverb phrase
		String childLabel2 = label(node.getChild(2)); // Verb phrase
		if(childLabel1.equals("ADVP") && childLabel2.equals("VP")) {
			System.out.println("Returned true++++++");
			return true;
		}
		System.out.println("Returned false2++++++");
		
		return false;
	}
	
	public static void main(String[] args) {
		CFGSemanticAnalyzer analyzer = new CFGSemanticAnalyzer(new KBController(""));
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
