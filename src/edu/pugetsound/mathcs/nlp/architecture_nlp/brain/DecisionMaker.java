package edu.pugetsound.mathcs.nlp.architecture_nlp.brain;

import edu.pugetsound.mathcs.nlp.lang.Conversation;

/**
 * A DecisionMaker is any algorithm that takes in the conversation up to a given point and 
 * decides on an appropriate response for the system to make. The assumption is that the last
 * utterance in the conversation was made by the user.
 * 
 * Examples of such decision makers include Markov decision processes, recurrent neural
 * networks, etc.
 * 
 *  
 * @author alchambers
 *
 */
public interface DecisionMaker {

	/**
	 * Given the conversation up to this point in time, this method
	 * returns an appropriate Dialogue Act action to take
	 * 
	 * @param conversation
	 * 						The history of the conversation up to the given point in time
	 * @return An appropriate action to take
	 */
	public Action getAction(Conversation conversation);
	
	/**
	 * A method which saves the current state of the decision maker to file.
	 * 
	 * @return True if save was successful, false otherwise
	 */
	public boolean saveToFile();
	

	/**
	 * A method that reads in a stored state for the decision maker from file.
	 * @param filename
	 * 					The name of the file containing the stored state
	 * @return True if read was successful, false otherwise
	 */
	public boolean readFromFile();

}
