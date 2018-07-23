package edu.pugetsound.mathcs.nlp.brain.mdp;

import edu.pugetsound.mathcs.nlp.brain.DialogueActTag;


/**
 * This class represents the possible states for the Reinforcement Learner
 * 
 * @author alchambers
 */

public class State {

	//TODO: Come up with better names for variables
    private DialogueActTag prevDA; // the dialogue act tag of the previous utterance
    private DialogueActTag prevprevDA; // the dialogue act tag of the utterance before the previous utterance

    public State(DialogueActTag daTag1, DialogueActTag daTag2) {
        this.prevDA = daTag1;
        this.prevprevDA = daTag2;
    }

    public DialogueActTag getEarlyDATag() {
        return prevDA;
    }
    
    public DialogueActTag getRecentDATag() {
        return prevprevDA;
    }
    

    @Override
    public int hashCode(){
        return prevDA.toString().hashCode() + prevprevDA.toString().hashCode();
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
        if (this.prevDA != other.prevDA) {
            return false;
        }
        if (this.prevprevDA != other.prevprevDA) {
            return false;
        }
        return true;
    }
    
    public String toString(){
        return "["+prevDA.toString()+", "+prevprevDA.toString()+"]";
    }

}
