package edu.pugetsound.mathcs.nlp.architecture_nlp.features;

import java.util.ArrayList;
import java.util.List;

import edu.pugetsound.mathcs.nlp.lang.*;

/**
 * Resolves all anaphoras in the utterance mapping them to Prolog entities
 * @author alchambers
 *
 */
public class AnaphoraAnalyzer {	
	/**
	 * Holds a list of candidate anaphoras
	 * Cleared every time the analyze method is called
	 */
	private List<String> anaphoras;

	/**
	 * The parse tree corresponding to the utterance being analyzed
	 */
	private MyTree parseTree;

	/**
	 * Contains various convenience methods for analyzing the String
	 * structure of the parse tree
	 */
	private ParseTreeAnalyzer analyzer;

	/**
	 * Constructs a new anaphora analyzer to analyze the anaphoric structure
	 * of utterances made
	 */
	public AnaphoraAnalyzer(){
		parseTree = null;
		analyzer = new ParseTreeAnalyzer();
		anaphoras = new ArrayList<String>();
	}

	/**
	 * Resolves anaphoras in a given utterance within the context of a conversation
	 * @param utt The utterance
	 * @param conversation The conversation
	 * @param pipeline A pipeline used to run the Stanford Coreference Resolver
	 */
	public void analyze(Utterance utt, Conversation conversation){
		if(utt.constituencyParse == null){
			return;
		}		
		anaphoras.clear();
		parseTree = utt.constituencyParse;	
		
		/*
		 * At this stage, only proper nouns are identified. The lower case of each proper noun
		 * becomes the Prolog entity tag
		 */
		for(Token tok : utt.tokens){
			if(analyzer.isProperNoun(tok.pos)){
				if(!utt.resolutions.containsKey(tok.token)){
					utt.resolutions.put(tok.token, tok.token.toLowerCase());
				}
			}
		}
		findCandidateAnaphoras(parseTree);					
	}

	/**
	 * Traverses the parse tree looking for candidate anaphoras
	 * @param node A node in the parse tree
	 */
	private void findCandidateAnaphoras(MyTree node){
		if(node.isLeaf()){
			return;
		}		
		if(node.value().equals("NP")){	
			anaphoras.add(analyzer.stripParserTags(node.toString()));
		}		
		MyTree[] children = node.children();
		assert(children.length > 0);
		for(MyTree child : children){
			findCandidateAnaphoras(child);
		}		
	}
}
