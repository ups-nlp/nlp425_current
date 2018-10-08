package edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import edu.pugetsound.mathcs.nlp.architecture_nlp.features.MyTree;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.NLPSuite;
import edu.pugetsound.mathcs.nlp.lang.Symbol;
import edu.pugetsound.mathcs.nlp.lang.Token;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.util.Logger;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordSuite implements NLPSuite {
	// Certain annotators require other annotators to be loaded first.
	// So the order of the annotators in this list is actually important.	 
	//	protected static final String ANNOTATORS = "tokenize, ssplit, pos, lemma, parse, natlog, ner, coref";
	//	protected static final String ANNOTATORS = "tokenize, ssplit, pos, lemma, parse, ner";
	//protected static final String ANNOTATORS = "tokenize, ssplit, pos, lemma, parse, ner";
	protected static final String ANNOTATORS = "tokenize, ssplit, pos, lemma, parse";
	protected static StanfordCoreNLP pipeline;
	protected long start = 0L, stop = 0L;


	public StanfordSuite() {
		Properties props = new Properties();
		props.put("annotators", ANNOTATORS);
		props.put("ner.useSUTime", false);
		props.put("ner.applyNumericClassifiers", false);
		pipeline = new StanfordCoreNLP(props);
	}

	@Override
	public void analyze(String input, Utterance utterance) {

		// Annotate document with all tools registered with the pipeline
		start = System.currentTimeMillis();
		Annotation document = new Annotation(input);
		pipeline.annotate(document);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tAnnotating document: " + (stop-start) + " milliseconds");
		}

		start = System.currentTimeMillis();		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		if(sentences.size() == 0){
			return;
		}
		CoreMap sentence = sentences.get(0);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tCore Map: " + (stop-start) + " milliseconds");
		}


		// Compute basic syntactic features
		start = System.currentTimeMillis();		
		storeTokens(utterance, sentence);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tStore Tokens: " + (stop-start) + " milliseconds");
		}

		// Compute parse tree features
		start = System.currentTimeMillis();
		storeParseTrees(utterance, sentence);
		stop = System.currentTimeMillis();
		if(Logger.debug()) {
			System.out.println("\tParse Tree features: " + (stop-start) + " milliseconds");
		}

	}

	/**
	 * Tokenizes the input. Tokens are delimited by space
	 * @param h Utterance to store tokens
	 * @param sentence The sentence
	 */
	private void storeTokens(Utterance h, CoreMap sentence){
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		for(CoreLabel token : tokens){
			if(!Symbol.isSymbol(token.word())){
				Token t = new Token(token.word());
				t.beginPosition = token.beginPosition();
				t.endPosition = token.endPosition();
				t.pos = token.get(PartOfSpeechAnnotation.class);
				t.entityTag = token.getString(NamedEntityTagAnnotation.class);
				h.tokens.add(t);
			}
		}
	}

	/**
	 * Stores the constituency and dependency parse trees
	 *
	 */
	private void storeParseTrees(Utterance h, CoreMap sentence){
		// Get the constituency parse tree and its root
		h.constituencyParse = new StanfordTree(sentence.get(TreeAnnotation.class));
		h.rootConstituency = h.constituencyParse.getChild(0).value();

		/*
		 * Get the dependency parse tree and its root
		 * The following URL lists the different types of dependency parsers available:
		 * https://stanfordnlp.github.io/CoreNLP/depparse.html
		 */
		SemanticGraph tree = sentence.get(BasicDependenciesAnnotation.class);
		h.dependencyParse = tree.toString();
		h.rootDependency = tree.getFirstRoot().word();
		extractGrammaticalRelations(tree, tree.getFirstRoot(), h);

	}


	/**
	 * A recursive method that traverses the dependency parse tree searching
	 * for certain grammatical relations
	 * @param tree A dependency parse tree
	 * @param node A node in the parse tree
	 * @param h The utterance itself
	 */
	private void extractGrammaticalRelations(SemanticGraph tree, IndexedWord node, Utterance h){
		if(!tree.hasChildren(node)){
			return;
		}
		Set<GrammaticalRelation> reltns = tree.childRelns(node);
		for(GrammaticalRelation rel : reltns){
			Set<IndexedWord> childrenWithReltn = tree.getChildrenWithReln(node, rel);

			// Capture the subjects of the sentence
			if(rel.getShortName().equals("nsubj")){
				for(IndexedWord w : childrenWithReltn){
					h.subjects.add(w.word());
				}
			}

			// Capture the direct objects of the sentence
			else if(rel.getShortName().equals("dobj")){
				for(IndexedWord w : childrenWithReltn){
					h.directObjects.add(w.word());
				}
			}

			else if(rel.getShortName().equals("nsubjpass")){
				h.isPassive = true;
				for(IndexedWord w : childrenWithReltn){
					h.subjects.add(w.word());
				}
			}

			// Recurse regardless
			for(IndexedWord w : childrenWithReltn){
				extractGrammaticalRelations(tree, w, h);
			}
		}
	}

	public static void main(String[] args) {
		// Initialize NLP Pipeline		
		StanfordSuite analyzer = new StanfordSuite();
		
		Scanner input = new Scanner(System.in);
		System.out.println();
		System.out.println("Type text:");
		
		while(true){			
			String line = input.nextLine();
			Utterance utt = new Utterance(line);
			analyzer.analyze(line, utt);
			System.out.println(utt);			
			System.out.println("\n\n\nType text:");
		}		
	}

}
