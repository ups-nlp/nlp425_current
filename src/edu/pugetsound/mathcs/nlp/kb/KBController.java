package edu.pugetsound.mathcs.nlp.kb;

import edu.pugetsound.mathcs.nlp.kb.basic.BasicKnowledgeBase;

/**
 * Manages access to all knowledge bases
 * 
 * In the future, this class should log statistics about how many hits/misses we've
 * gotten from the current knowledge base, control which knowledge bases to query (perhaps
 * have a hierarchical set of knowledge bases) and shift the current knowledge base depending
 * upon the focus of the conversation (and the hits/misses)
 *  
 * @author alchambers
 */
public class KBController{
	public static final String ISA = "isA";
	public static final String AGENT = "agent";
	public static final String THEME = "theme";
	public static final String EVENT = "event";
	public static final String ENTITY = "entity";
	public static final String POSSESSION = "possessedBy";	
	public static final String PROPERTY = "property";
	
	private KnowledgeBase current;
	private String dir;
	private int varCounter;

	/**
	 * Constructs a knowledge base controller. All knowledge bases will be read from and 
	 * written to the given directory.
	 * 
	 * @param The directory where knowledge bases are to be written/read
	 */
	public KBController(String dir){
		this.dir = dir;
		varCounter = 0;
		current = new BasicKnowledgeBase();
	}

	/**
	 * Creates and returns a variable with a unique name. All variables
	 * are of the format "XNN" where "NN" is a unique number.
	 * 
	 * NOTE: Should we kick variables back to the KnowledgeBase Interface?
	 */
	public String makeVariable() {
		String var = "X" + varCounter;
		varCounter++;
		return var;
	}

	/**
	 * Creates a constant and adds it to the knowledge base
	 * 
	 * @param constant A string containing the name of the constant to be added
	 * @param sort The type of the constant
	 * @return True if the constant is successfully added, false otherwise
	 */
	public MyPredicate makeConstant(String constant, String sort) {
		return current.makeConstant(constant, sort);
	}

	/**
	 * Creates a binary predicate and adds it to the knowledge base
	 * @return A binary predicate
	 */
	public MyPredicate makeBinaryPredicate(String name, String arg0, String arg1) {
		MyPredicate predicate = current.makeBinaryPredicate(name);
		predicate.addArgument(arg0, 0);
		predicate.addArgument(arg1, 1);
		return predicate;
	}

	/**
	 * Creates a unary predicate and adds it to the knowledge base
	 * @return A unary predicate
	 */
	public MyPredicate makeUnaryPredicate(String name, String arg) {
		MyPredicate predicate= current.makeUnaryPredicate(name);
		predicate.addArgument(arg, 0);
		return predicate;
	}

	/**
	 * Checks if the entity exists in the current knowledge base. All entities are
	 * represented as constants.
	 * 
	 * @param name The name of the entity to be checked
	 * @return The constant if it exists, null otherwise
	 * 
	 */
	public MyPredicate constantExists(String name) {
		// It's not clear to me if we simply need to query the signature (thus bypassing the KnowledgeBase
		// interface) or if we need to integrate this into the KnowledgeBase itself
		return null;		
	}
	
	/**
	 * Queries the knowledge base to determine the truth value of the given formula
	 * @param formula A string representing a formula in first-order logic
	 * @return True if the formula evaluates to true under the knowledge base, false otherwise
	 */
	public boolean query(String formula) {
		return current.query(formula);
	}

	/**
	 * Determines the truth value of the predicate in the current context 
	 * @param predicate A predicate
	 * @return True if the predicate evaluates to true, false otherwise
	 */
	public boolean query(MyPredicate predicate){  
		return current.query(predicate.toString());
	}


}
