package edu.pugetsound.mathcs.nlp.architecture_nlp.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.Action;
import edu.pugetsound.mathcs.nlp.architecture_nlp.features.TextAnalyzer;
import edu.pugetsound.mathcs.nlp.architecture_nlp.generator.*;
import edu.pugetsound.mathcs.nlp.kb.KBController;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.util.Logger;

import java.io.IOException;

/**
 * This class is a baseline text generator that returns hard-coded responses
 * 
 * @author alchambers
 * @version 5/30/18
 * 
 * @author Thomas Gagne & Jon Sims
 * @version 04/26/16
 */
public class DumbGenerator implements Generator {

    private HashMap<Action, List<String>> responses;
    private Random rng;

    /**
     * Constructs a new dumb generator that returns hard coded responses
     */
    public DumbGenerator(){
    	rng = new Random();
    	responses = new HashMap<Action, List<String>>();
    	for(Action a : Action.values()){
    		responses.put(a, new ArrayList<String>());
    	}
    	populateHash();
    	
    }
    
    
    /**
     * Takes in a conversation and a DA tag for what type of statement to respond from the MDP
     * Returns a string corresponding to the generated response
     * @param convo The conversation thus far, so we can use local info to generate the response
     * @param responseTag The type of response we should respond with. Ex: YesNoAnswer
     * @return A string representation of the response. In early versions, this might be an AMR
     */
    public String generateResponse(Conversation convo, Action responseTag,
                                          KBController kb) {
    	int numResponses = responses.get(responseTag).size();
    	return responses.get(responseTag).get(rng.nextInt(numResponses));
    }

    private void populateHash(){
    	responses.get(Action.APOLOGY).add("I am sorry!");
    	responses.get(Action.APOLOGY).add("Sorry");
    	responses.get(Action.BACKCHANNEL).add("uh huh");
    	responses.get(Action.BACKCHANNEL).add("I see");
    	responses.get(Action.CONVENTIONAL_CLOSING).add("Bye!");
    	responses.get(Action.CONVENTIONAL_CLOSING).add("Talk to you later");
    	responses.get(Action.CONVENTIONAL_OPENING).add("Hi!");
    	responses.get(Action.CONVENTIONAL_OPENING).add("Hello");
    	responses.get(Action.QUESTION_WH).add("What is your name?");
    	responses.get(Action.QUESTION_YES_NO).add("Do you like cats?");
    	responses.get(Action.SIGNAL_NON_UNDERSTANDING).add("What?");
    	responses.get(Action.SIGNAL_NON_UNDERSTANDING).add("I am confused.");
    	responses.get(Action.STATEMENT).add("The weather is nice today!");
    	responses.get(Action.STATEMENT).add("I like you.");
    	responses.get(Action.STATEMENT).add("I am having a good day!");
    	responses.get(Action.SYMPATHETIC_COMMENT).add("I am sorry.");
    	responses.get(Action.THANKS).add("Thanks!");
    	responses.get(Action.WELCOME).add("You are welcome");
    	responses.get(Action.YES_NO_ANSWER).add("Yes");
    	responses.get(Action.YES_NO_ANSWER).add("No");
    	responses.get(Action.YES_NO_ANSWER).add("Uhm...I guess so.");
    	responses.get(Action.YES_NO_ANSWER).add("No, I don't think so.");
    }

}
