package edu.pugetsound.mathcs.nlp.features;



public interface MyTree {
	
	/**
	 * Return the string representation of the node
	 * @return
	 */
	public String value();
	
	
	public void setValue(String value);
	
	public MyTree getTerminal();
	
	/**
	 * TODO: Need to throw an exception if the index is out of bounds
	 * Returns the specified child
	 * @param The index of the child specified from left (0) to right
	 * @return The child
	 */
	public MyTree getChild(int childIndex);
	
	
	public void addChild(int pos, MyTree child);
	
	public void addChild(int pos, String value, MyTree[] children);
	
	
	public MyTree removeChild(int pos);
	
	/**
	 * Returns the number of children of the node
	 * @return The number of children of the node
	 */
	public int numChildren();
	
	/**
	 * Returns whether the node is a leaf
	 * @return True if the node is a leaf, false otherwise
	 */
	public boolean isLeaf();
	
	/**
	 * Return all children of the node
	 * @return An array  containing the children of the node
	 */
	public MyTree[] children();
	
	@Override
	public String toString();
}
