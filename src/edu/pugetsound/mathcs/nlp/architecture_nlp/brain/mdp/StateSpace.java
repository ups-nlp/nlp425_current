package edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp;

import edu.pugetsound.mathcs.nlp.lang.Conversation;

public interface StateSpace {
	/**
	 * Returns a string representation of the state represented by the given id
	 * 
	 * @param id The id of the state
	 * @return A string representation of the states or null if the id does not correspond to a state 
	 */
	public String idToState(int id);
	
	/**
	 * Returns the id of the current state of the conversation
	 * All state spaces must be a function solely of the conversation
	 * 
	 * @param conversation The conversation
	 * @return The id of the current state
	 * @throws IllegalStateException if the state of the conversation does not correspond to a state
	 * 			An example would be if the last utterance made was by the agent and not the human.  
	 */
	public int getStateId(Conversation conversation) throws IllegalStateException;

	/**
	 * Returns the number of states in the state space
	 * @return The number of states in the state space
	 */
	public int numStates();
	
	
	
}
