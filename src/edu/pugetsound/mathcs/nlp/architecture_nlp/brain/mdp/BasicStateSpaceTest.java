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
	 * Checks for the correct state in the initial stages of the conversation:
	 * - There have been no utterances made
	 * - The human has made a single utterance
	 * 
	 *  Checking for the correct state when the human has made 2 or more utterances
	 *  is handled by a different unit test
	 */
	@Test
	void testInitialConversation() {
		Conversation convo = new Conversation();
		int id = states.getStateId(convo);
		String state = states.idToState(id);		
		assertEquals(state.toString(), "[" + DialogueActTag.NULL.toString() + ", " + DialogueActTag.NULL.toString() + "]");
				
		Utterance utt = new Utterance("agent1");
		convo.addUtterance(utt);
		
		utt = new Utterance("human1");
		utt.daTag = DialogueActTag.STATEMENT;
		convo.addUtterance(utt);
		id = states.getStateId(convo);
		state = states.idToState(id);
		assertEquals(state.toString(), "[" + DialogueActTag.NULL.toString() + ", " + DialogueActTag.STATEMENT.toString() + "]");	
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
	 * Ensures that an exception is thrown when the conversation consists of just 3 utterances: agent, human, agent
	 */	
	@Test
	void testIllegalConversation2(){
		Conversation convo = new Conversation();
		Utterance utt = new Utterance("agent1");
		convo.addUtterance(utt);
		
		utt = new Utterance("human1");
		convo.addUtterance(utt);
		
		utt = new Utterance("agent2");
		convo.addUtterance(utt);
		
		Assertions.assertThrows(IllegalStateException.class, ()->{states.getStateId(convo);});	
	}
			
	
	/*
	 * Checks for the correct state when the human has made exactly 2 utterances: agent, human, agent, human
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
			convo.addUtterance(utt);
			
			utt = new Utterance("human1");
			utt.daTag = tag1;
			convo.addUtterance(utt);
			
			utt = new Utterance("agent2");
			convo.addUtterance(utt);
			
			utt = new Utterance("human2");
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
