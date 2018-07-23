package edu.pugetsound.mathcs.nlp.brain.mdp;

import edu.pugetsound.mathcs.nlp.lang.Conversation;

public interface StateSpace {
	/**
	 * Returns a string representation of the state represented by the given id
	 * 
	 * @param id The id of the state
	 * @return A string representation of the states
	 */
	public String idToState(int id);
	
	/**
	 * Returns the id of the current state of the conversation
	 * All state spaces must be a function solely of the conversation
	 * 
	 * @param conversation The conversation
	 * @return The id of the current state
	 */
	public int getStateId(Conversation conversation);

	/**
	 * Returns the number of states in the state space
	 * @return The number of states in the state space
	 */
	public int numStates();
	
	
	
}
