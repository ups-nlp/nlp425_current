package edu.pugetsound.mathcs.nlp.kb;

/**
 * Notes to self: For Tweety, this is going to need to store internally a signature that contains all constants,
 * sorts, and predicates. How big can a signature be before it slows down inference notably? 
 * 
 * @author alchambers
 *
 */
public interface KnowledgeBase {

	
	/**
	 * Creates a constant and adds it to the knowledge base. A constant is represented as
	 * a 0-ary predicate.	
	 * 
	 * @param constant A string containing the name of the constant to be added
	 * @param sort The type of the constant
	 * @return A 0-ary predicate representing the constant
	 */
	public MyPredicate makeConstant(String constant, String sort);
		
	/**
	 * Creates a binary predicate and adds it to the knowledge base
	 * @return A binary predicate
	 */
	public MyPredicate makeBinaryPredicate(String name);
	
	/**
	 * Creates a unary predicate and adds it to the knowledge base
	 * @return A unary predicate
	 */
	public MyPredicate makeUnaryPredicate(String name);
	
	/**
	 * Queries the knowledge base to determine the truth value of the given formula
	 * @param formula A string representing a formula in first-order logic
	 * @return True if the formula evaluates to true under the knowledge base, false otherwise
	 */
	public boolean query(String formula);

}
