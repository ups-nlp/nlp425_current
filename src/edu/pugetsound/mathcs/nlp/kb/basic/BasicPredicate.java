package edu.pugetsound.mathcs.nlp.kb.basic;

import edu.pugetsound.mathcs.nlp.kb.MyPredicate;

public class BasicPredicate implements MyPredicate {
	/**
	 * The name of the predicate
	 */
	protected String name;

	/**
	 * The arguments of the predicate
	 */
	protected String[] arguments;

	/**
	 * The arity of the predicate. A constant has 0-arity
	 */
	protected int arity;


	/**
	 * Constructs a Prolog term or predicate
	 * @param parity The parity of the predicate or zero for a term
	 * @throws IllegalArgumentException If parity is negative
	 */
	public BasicPredicate(int arity) throws IllegalArgumentException{
		if(arity < 0){
			throw new IllegalArgumentException();
		}	
		this.arity = arity;
		name = null;
		arguments = (arity == 0) ? null : new String[arity];
	}
	
	/**
	 * Set the name of the predicate or term
	 * @param name
	 */
	@Override
	public void setName(String name){
		this.name = name;
}

	/**
	 * Returns the name of the predicate
	 * @return The name of the predicate
	 */
	@Override	
	public String getName() {
		return name;
	}

	/**
	 * Returns the arity
	 * @return zero for a constant, a positive integer for a predicate 
	 */
	@Override
	public int getArity() {
		return arity;
	}

	/**
	 * Add an argument to the predicate
	 * @param arg Predicate argument
	 * @param index Position of the argument in the predicate
	 * @throws IllegalArgumentException Invalid index
	 * @throws IllegalStateException Illegally calling method on a term
	 */
	@Override
	public void addArgument(String arg, int index) throws IllegalArgumentException {
		if(isConstant()){
			throw new IllegalStateException();
		}
		if(!validIndex(index)){
			throw new IllegalArgumentException();
		}
		arguments[index] = arg;
	}


	/**
	 * Returns the specified argument
	 * @return The specified argument
	 * @throws IllegalStateException Calling method on a term
	 * @throws IllegalArgumentException Invalid index
	 */
	@Override
	public String getArgument(int index) throws IllegalStateException, IllegalArgumentException {
		if(isConstant()){
			throw new IllegalStateException();
		}
		if(!validIndex(index)){
			throw new IllegalArgumentException();
		}
		return arguments[index];
	}

	/**
	 * Returns entire argument list
	 * @return list of arguments for predicate or null if term
	 */
	@Override
	public String[] getArguments() {
		return arguments;
	}

	/**
	 * Returns a string representation
	 * @return A string representation of the predicate
	 */
	public String toString(){
		if(isConstant()){
			return name;
		}

		// Construct string representing predicate
		String toReturn = name + "(";
		for(int i = 0; i < arguments.length; i++){
			toReturn += arguments[i];
			if(i != arguments.length-1){
				toReturn += ",";
			}
		}
		toReturn += ")";
		return toReturn;
	}


	/*---------------------------------------------
	 * 			PRIVATE HELPER METHODS
	 *---------------------------------------------*/
	
	private boolean validIndex(int index){
		return index >= 0 && index < arity;
	}

	private boolean isConstant(){
		return arity == 0;
	}
}
