package edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp;

import java.util.HashMap;
import java.util.List;

import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.DialogueActTag;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

/**
 * The state is represented by the dialogue act tags of the human's past two utterances 
 * That is, the state is given by the pair (da_{t-2}, da_{t}) where 
 * da_{t-2} is the dialogue act tag for the second to last human utterance
 * da_{t} is the dialogue act tag for the last human utterance
 * 
 * In general, the conversation can be viewed as
 * 
 *  da_{0}			The agent's first utterance
 *  da_{1}			The human's first utterance
 *  da_{2} 			The agent's second utterance
 *  da_{3}			The human's second utterance
 *  ...
 *  da_{t-3}		The agent's second to last utterance
 *  da_{t-2}		The human's second to last utterance
 *  da_{t-1}		The agent's last utterance
 *  da_{t}			The human's last utterance
 *  
 *  NOTE THIS ASSUMES THAT THE CONVERSATIONAL AGENT ALWAYS BEGINS THE CONVERSATION
 *  
 * @author alchambers
 *
 */
public class BasicStateSpace implements StateSpace {
	protected HashMap<State, Integer> state_to_id;
	protected HashMap<Integer, State> id_to_state;

	public BasicStateSpace(){
		state_to_id = new HashMap<>();
		id_to_state = new HashMap<>();

		//Assigns all possible pairs of actions an id and adds to the hashes
		int id = 0;
		for (DialogueActTag tag1 : DialogueActTag.values()) {
			for (DialogueActTag tag2 : DialogueActTag.values()) {
				populateHash(tag1, tag2, id);
				id++;
			}
		}
	}


	/**
	 * Returns a string representation of the state represented by the given id
	 * 
	 * @param id The id of the state
	 * @return A string representation of the states
	 */
	public String idToState(int id){
		if(id < 0 || id >= id_to_state.size()) {
			return null;
		}		
		return id_to_state.get(id).toString();
	}


	/**
	 * Returns the id of the current state of the conversation
	 * All state spaces must be a function solely of the conversation
	 * 
	 * @param conversation The conversation
	 * @return The id of the current state
	 */
	public int getStateId(Conversation conversation){
		// The last utterance was made by the agent and not the human 
		if(conversation.size() % 2 == 1) {
			System.out.println("Size of conversation: " + conversation.size());
			System.out.println(conversation.getStringRepresentation());
			throw new IllegalStateException();
		}		
				
		// At this point, we know that the size of the conversation is a multiple of 2
		
		// Assumes the agent always speaks first in the conversation 
		if(conversation.size() == 0) {
			return state_to_id.get(new State(DialogueActTag.NULL, DialogueActTag.NULL));
		}

		List<Utterance> utterances = conversation.getConversation();

		DialogueActTag olderDAtag = (conversation.size() == 2) ? 
									DialogueActTag.NULL : 				
									utterances.get(utterances.size()-3).daTag;

		DialogueActTag recentDAtag = utterances.get(utterances.size()-1).daTag;

		// Now that we have constructed the state, map it to the corresponding state id
		State curr = new State(olderDAtag, recentDAtag);
		return state_to_id.get(curr);
	}

	/**
	 * Returns the number of states in the state space
	 * @return The number of states in the state space
	 */
	public int numStates(){
		return state_to_id.size();
	}


	private void populateHash(DialogueActTag tag1, DialogueActTag tag2, int index){
		// TODO: Does each hash need its own unique copy of the state?
		State state = new State(tag1, tag2); 
		state_to_id.put(state, index);
		id_to_state.put(index,state);
	}

	
	/**
	 * An inner-class that unifies all of the pieces of information that make up a single state
	 * @author alchambers
	 * @version 11/8/18
	 */
	private class State {
	    private DialogueActTag tag1;
	    private DialogueActTag tag2;

	    public State(DialogueActTag daTag1, DialogueActTag daTag2) {
	        this.tag1 = daTag1;
	        this.tag2 = daTag2;
	    }

	    public DialogueActTag getTag1() {
	        return tag1;
	    }
	    
	    public DialogueActTag getTag2() {
	        return tag2;
	    }
	    
	    @Override
	    public int hashCode(){
	        return tag1.toString().hashCode() + tag2.toString().hashCode();
	    }

	    @Override
	    public boolean equals(Object obj) {        
	    	if (obj == null) {
	            return false;
	        }
	        if (this == obj) {
	            return true;
	        }
	        if (getClass() != obj.getClass()) {
	            return false;
	        }
	        
	        final State other = (State) obj;
	        if (this.tag1 != other.tag1) {
	            return false;
	        }
	        if (this.tag2 != other.tag2) {
	            return false;
	        }
	        return true;
	    }
	    
	    @Override
	    public String toString(){
	        return "["+tag1.toString()+", "+tag2.toString()+"]";
	    }
	}
}
