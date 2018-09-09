package edu.pugetsound.mathcs.nlp.architecture_nlp.features.stanford;

import edu.stanford.nlp.trees.Tree;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.MyTree;
import edu.stanford.nlp.trees.CollinsHeadFinder;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;

import java.util.ArrayList;
import java.util.List;


public class StanfordTree implements MyTree {
	protected Tree node;	
	protected static CollinsHeadFinder headFinder = new CollinsHeadFinder();
	protected static LabeledScoredTreeFactory factory = new LabeledScoredTreeFactory();

	
	public StanfordTree(Tree node) {		
		this.node = node;
	}
	
	
	public StanfordTree(String value, MyTree[] children) {
		// Instead of value, the Stanford Tree class has a label() method.
		// This is what should really be pased in. The label() not the value()
		// TODO: Is this a problem?
		
		List<Tree> childList = new ArrayList<Tree>();
		for(int i = 0; i < children.length; i++) {
			if(! (children[i] instanceof Tree)) {
				//error
			}
			StanfordTree node = (StanfordTree)children[i];
			childList.add(i, node.node);
		}
		this.node = factory.newTreeNode(value, childList);		
	}
	
	@Override
	public String value() {
		// The Tree class has a label() method which returns...the label. What's a label?
		// It's not really clear. But from what I can infer it's the constituent class or
		// the word but there must be other symbols involved because the value() is the 
		// boiled down most important part of thel label. This is really crappy naming
		// paired with equally crappy documentation
		return node.value();		
	}

	@Override
	public void setValue(String value) {
		node.setValue(value);
	}

	@Override
	public MyTree getTerminal() {
		Tree terminal = node.headTerminal(headFinder);
		return new StanfordTree(terminal);		
	}

	@Override
	public MyTree getChild(int childIndex) {
		return new StanfordTree(node.getChild(childIndex));
	}

	
	public MyTree removeChild(int childIndex) {
		Tree removed = node.removeChild(childIndex);
		return new StanfordTree(removed);
	}
	
	
	public void addChild(int pos, String value, MyTree[] children) {
		StanfordTree child = new StanfordTree(value, children);
		addChild(pos, child);
	}
	
	
	@Override
	public void addChild(int pos, MyTree child) {		
		if( !(child instanceof StanfordTree)) {
			// throw error
		}
		StanfordTree tree = (StanfordTree) child;
		node.addChild(pos, tree.node);
	}

	@Override
	public int numChildren() {
		return node.numChildren();
	}

	@Override
	public boolean isLeaf() {
		return node.isLeaf();
	}

	@Override
	public MyTree[] children() {
		Tree[] children = node.children();
		MyTree[] childList = new MyTree[node.numChildren()];
		for(int i = 0; i < childList.length; i++) {
			childList[i] = new StanfordTree(children[i]);
		}
		return childList;
	}
	
	@Override
	public String toString() {
		return node.toString();
	}
	
}
