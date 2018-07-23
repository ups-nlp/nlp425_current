package edu.pugetsound.mathcs.nlp.brain.mdp;

import java.util.HashMap;
import java.util.List;

import edu.pugetsound.mathcs.nlp.brain.DialogueActTag;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

/**
 * The state is represented by the dialogue act tags of the user's past two utterances 
 * That is, the state is given by the pair (da_{t-3}, da_{t-1}) where 
 * da_{t-3} is the dialogue act tag for the second to last user utterance
 * da_{t-1} is the dialogue act tag for the last user utterance
 * 
 * In general, the conversation can be viewed as
 * 
 *  da_{t}			The agent's last utterance
 *  da_{t-1}		The user's last utterance
 *  da_{t-2}		The agent's second to last utterance
 *  da_{t-3}		The user's second to last utterance
 *  
 * @author alchambers
 *
 */
// TODO: Rename this class to reflect that it uses the user's previous two utterances
public class StateBuilder implements StateSpace {
	private HashMap<State, Integer> state_to_id;
	private HashMap<Integer, State> id_to_state;

	public StateBuilder(){
		state_to_id = new HashMap<>();
		id_to_state = new HashMap<>();

		//starting with the null state, adds all states to the state list
		int id = 1;
		populateHash(DialogueActTag.NULL, DialogueActTag.NULL, 0);
		for (DialogueActTag tag1 : DialogueActTag.values()) {
			for (DialogueActTag tag2 : DialogueActTag.values()) {
				if(tag2 != DialogueActTag.NULL){
					populateHash(tag1, tag2, id);
					id++;
				}
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
	
		List<Utterance> utterances = conversation.getConversation();
		DialogueActTag olderDAtag;
		if (utterances.size() == 2) {
			olderDAtag = DialogueActTag.NULL;
		} else {
			olderDAtag = utterances.get(utterances.size() - 3).daTag;
		}
		DialogueActTag mostRecentDAtag = utterances.get(utterances.size() - 1).daTag;

		// Now that we have constructed the state, map it to the corresponding state id
		// TODO: Maybe get rid of State class? We're allocating memory to use it as a key and then trash it...
		State curr = new State(olderDAtag, mostRecentDAtag);
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

}
