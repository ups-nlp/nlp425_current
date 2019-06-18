package edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.DialogueActTag;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;

class BasicStateSpaceTest {
	private DialogueActTag[] actions = DialogueActTag.values();
	private BasicStateSpace states = new BasicStateSpace();
	private Random rng = new Random();

	@Test
	void testNullState() {
		String state = states.idToState(-1);
		assertEquals(state, null);
		
		state = states.idToState(states.numStates());
		assertEquals(state, null);
		
		state = states.idToState(states.numStates()+1);
		assertEquals(state, null);
	}
	
	/*
	 * Checks for the correct behavior after each utterance
	 */
	@Test
	void testInitialConversation() {
		Conversation convo = new Conversation();
		
		// No utterances so far ---> (NULL, NULL)
		int id = states.getStateId(convo);
		String state = states.idToState(id);		
		assertEquals(state.toString(), "[" + DialogueActTag.NULL.toString() + ", " + DialogueActTag.NULL.toString() + "]");
				
		// The agent has 1 utterance ---> IllegalStateException
		Utterance utt = new Utterance("agent1");
		utt.daTag = DialogueActTag.STATEMENT;
		convo.addUtterance(utt);		
		Assertions.assertThrows(IllegalStateException.class, ()->{states.getStateId(convo);});		

		
		// The human has 1 uttearnce ---> (NULL, STATEMENT)
		utt = new Utterance("human1");
		utt.daTag = DialogueActTag.STATEMENT;
		convo.addUtterance(utt);
		id = states.getStateId(convo);
		state = states.idToState(id);
		assertEquals("[" + DialogueActTag.STATEMENT.toString() + ", " + DialogueActTag.STATEMENT.toString() + "]", state.toString());
		
		
		// The agent has 2 utterances ---> IllegalStateException
		utt = new Utterance("agent2");
		utt.daTag = DialogueActTag.APOLOGY;
		convo.addUtterance(utt);		
		Assertions.assertThrows(IllegalStateException.class, ()->{states.getStateId(convo);});		

		// The human has 2 utterances ---> (APOLOGY, BACKCHANNEL)
		utt = new Utterance("human1");
		utt.daTag = DialogueActTag.BACKCHANNEL;
		convo.addUtterance(utt);
		id = states.getStateId(convo);
		state = states.idToState(id);
		assertEquals("[" + DialogueActTag.APOLOGY.toString() + ", " + DialogueActTag.BACKCHANNEL.toString() + "]", state.toString());
	
		
	}
	
	
	
	/*
	 * Ensures that an exception is thrown when the conversation consists of just 1 utterance: agent
	 */
	@Test
	void testIllegalConversation() {
		Conversation convo = new Conversation();	
		Utterance utt = new Utterance("agent1");
		convo.addUtterance(utt);
		Assertions.assertThrows(IllegalStateException.class, () -> {states.getStateId(convo);});
	}
	
	/*
	 * Ensures that an exception is thrown when the last utterance in the conversation is by the agent
	 */	
	@Test
	void testIllegalConversation2(){
		Conversation convo = new Conversation();
		Utterance utt = new Utterance("agent1");
		convo.addUtterance(utt);
		
		Assertions.assertThrows(IllegalStateException.class, ()->{states.getStateId(convo);});		

		utt = new Utterance("human1");
		convo.addUtterance(utt);
		
		utt = new Utterance("agent2");
		convo.addUtterance(utt);
		
		Assertions.assertThrows(IllegalStateException.class, ()->{states.getStateId(convo);});		
		
		utt = new Utterance("human2");
		convo.addUtterance(utt);
		
		utt = new Utterance("agent3");
		convo.addUtterance(utt);
		
		Assertions.assertThrows(IllegalStateException.class, ()->{states.getStateId(convo);});
	}
			
	
	/*
	 * Checks for the correct state when exactly 2 utterances have been made (agent, human)
	 */
	@Test
	void testStateBuilder() {
		int numActions = actions.length;
		int expected_size = (numActions)*(numActions);
		assertEquals(states.id_to_state.size(), expected_size);
		assertEquals(states.state_to_id.size(), expected_size);

		// Randomly generate two dialogue act tags
		// Create a conversation with these two tags
		// Get the state corresponding to this conversation 
		// Ensure the state matches the two dialogue act tags
		int numTrials = 100;
		for(int i = 0; i < numTrials; i++) {
			DialogueActTag tag1 = actions[rng.nextInt(actions.length)];
			DialogueActTag tag2 = actions[rng.nextInt(actions.length)];
			
			Conversation convo = new Conversation();	
			
			Utterance utt = new Utterance("agent1");
			utt.daTag = tag1;
			convo.addUtterance(utt);
			
			utt = new Utterance("human1");
			utt.daTag = tag2;
			convo.addUtterance(utt);
			
			int id = states.getStateId(convo);
			String state = states.idToState(id);
			
			assertEquals(state.toString(), "[" + tag1.toString() + ", " + tag2.toString() + "]");			
		}
	}


	@Test
	void testNumStates() {
		int numActions = actions.length;
		int expected_size = (numActions)*(numActions);		
		assertEquals(states.numStates(), expected_size);
	}

}
