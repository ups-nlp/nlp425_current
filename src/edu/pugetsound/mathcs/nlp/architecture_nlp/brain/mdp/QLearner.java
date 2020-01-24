package edu.pugetsound.mathcs.nlp.architecture_nlp.brain.mdp;

import edu.pugetsound.mathcs.nlp.architecture_nlp.brain.*;
import edu.pugetsound.mathcs.nlp.lang.Conversation;
import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.mathcs.nlp.util.Logger;
import edu.pugetsound.mathcs.nlp.util.PathFormat;

import java.io.*;
import java.util.*;

/**
 * TODO: The state is off sync
 * TODO: Sometimes the assertion that choice is valid is false. Choice is -1 still????
 * @author Zachary Cohan, Damon Williams
 * @version 05/12/16
 * 
 * @author alchambers
 * @version 05/30/18
 */
public class QLearner implements DecisionMaker {
	protected HyperVariables params;

	protected Action[] actions;
	protected StateSpace states;
	protected double[][] q_table;

	protected int lastState;
	protected int lastAction;
	protected int lastReward;

	protected Random rng;
	protected Scanner in;

	protected static final String INPUT_PATH = PathFormat.absolutePathFromRoot("models/qlearner/qlearner");


	/**
	 * Constructs the original QLearner: defines the states and actions that are
	 * possible, and initializes the QTable based on those states and actions.
	 * Will construct the QTable, either by reading the file, or counting the
	 * number of possible states and actions.
	 *
	 * @param hyperVariables
	 * 						The {@link HyperVariables} class is the class
	 * 						which configures the variables for the QLearner.
	 *
	 */
	public QLearner(HyperVariables hyperVariables) {
		params = hyperVariables;

		//create states and actions
		actions = Action.values(); // TODO: Do we need a deep copy here? Are we ever changing actions?
		states = new BasicStateSpace();
		q_table = new double[states.numStates()][actions.length];		

		lastState = states.getStateId(null);
		lastAction = Action.NULL.ordinal();
		lastReward = -1;

		//TODO: Refactor this so that we can unit test it???
		in = new Scanner(System.in);
		rng = new Random();
	}



	/**
	 *	This runs one iteration of the Q-learning algorithm. 
	 *
	 * @param conversation The conversation up to the given point in time
	 * @return The recommended action to take
	 */
	public Action getAction(Conversation conversation) {
		if(conversation.size() == 0){
			return Action.CONVENTIONAL_OPENING;
		}
		else if(conversation.size() % 2 == 1) {
			// The last utterance in the conversation was made by the agent...so this method shouldn't have been called
			throw new IllegalStateException();
		}
		else if(conversation.getLastUtterance().daTag == DialogueActTag.CONVENTIONAL_CLOSING) {
			// The user has said goodbye. It would be rude to continue trying to prolong the conversation. Just say goodbye back.
			return Action.CONVENTIONAL_CLOSING;
		}

		// We know that the conversation has length of at least 2, that is the conversation consists of at least:
		// Agent's first utterance
		// Human's first utterance

		if(Logger.debug()) {
			System.out.println("lastState was: " + lastState);
			System.out.println("last action was: " + lastAction);
			System.out.println("last reward was: " + lastReward);
		}

		// Compute the updated annealing parameter which ranges 
		// from 1 to 0 over the course of EXPLORE iterations
		params.anneal = (double) params.remaining_iters/params.EXPLORE;		
		if (Logger.debug()) {
			System.out.println("Remaining Iterations: " + params.remaining_iters);
			System.out.println("Explore: " + params.EXPLORE);
			System.out.println("anneal = remain/explore: " + params.anneal);
		}


		// Get the id of the current state
		int stateIndex = states.getStateId(conversation);
		if (Logger.debug()) {
			System.out.println("current state index: " + stateIndex);
			System.out.println("current state: " + states.idToState(states.getStateId(conversation)));
			System.out.println("Updating previous states");
		}


		// Updates the Q-table given the sample (s, a, r, s') where
		// s is the state from *last* time
		// a is the action taken from *last* time
		// r is the reward given for that action
		// s' is the result of taking action a in state s
		updateQTable(lastState, lastAction, lastReward, stateIndex);


		// TODO: They are using the same annealed parameter for (1) deciding between exploring/exploiting
		// and (2) the \alpha value in the Q learning update equation. Are these the same value? Can they be?


		// Choose a valid action by either exploring or exploiting
		int choice = -1;
		do {
			if (rng.nextInt(params.EXPLORE) < params.remaining_iters) {		
				choice = explore(); // explore
				
			} else {
				choice = exploit(stateIndex); // exploit
			}			
		}while(actions[choice] == Action.NULL);
		
		if(Logger.debug()){
			System.out.println("Chosen action:");
			System.out.println(choice);
			System.out.println(actions[choice]);
		}
		assert(choice >= 0 && choice < actions.length);

		

		// Present the action to the user and get the reward
		lastReward = rateActionChoice(stateIndex, choice);

		// The next state is determined by the user (i.e. the dialogue act tag of whatever response they
		// give to our action). As such, we cannot immediately update the Q-table. We must wait until the
		// user types the next utterance. Therefore, we store all information necessary to update the
		// Q-table the *next* time this method is called.
		lastAction = choice;
		lastState = stateIndex;
		params.decrement();

		//return the action that we decided to take to the processing actions team.
		return actions[choice];
	}


	/**
	 * Iterates through the QTable and prints the best decision determined for each state.
	 */
	protected void printPolicy() {
		for (int i = 0; i < q_table.length; i++) {
			int act = 0;
			for (int j = 0; j < q_table[i].length; j++) {
				if (q_table[i][j] > q_table[i][act]) {
					act = j;
				}
			}
			System.out.println("For STATE " + i + ", "+ states.idToState(i)+", we will respond with " + actions[act]);
		}
		System.out.println();
	}


	/**
	 * Randomly chooses an action to take
	 * 
	 * @param stateIndex
	 * 						The index of the current state
	 * @return choice
	 * 						The index of a randomly chosen action
	 */
	protected int explore() {
		int choice = rng.nextInt(actions.length);
		return choice;
	}

	/**
	 * Chooses the action with the highest q-value
	 * 
	 * @param stateIndex
	 * 						The index of the current state
	 * @return choice
	 * 						The index of the action with the highest q-value
	 */
	protected int exploit(int stateIndex) {
		int choice = 0;
		for (int i = 1; i < q_table[stateIndex].length; i++) {
			if (q_table[stateIndex][i] > q_table[stateIndex][choice]) {
				choice = i;
			}
		}
		return choice;
	}

	/**
	 *	Asks the user to rate the chosen action
	 *
	 * @param state
	 * 				The index of the current state
	 * @param choice
	 * 				The index of the chosen action
	 * 
	 * @return
	 * 				A numerical reward for the chosen action (higher is better) 
	 */
	protected int rateActionChoice(int state, int choice) {
		final int MAX = 5;
		final int MIN = 1;
		int reward = -1;
		System.out.println("===================================================");
		System.out.println("I am in state " + states.idToState(state));
		System.out.println("I will respond with a " + actions[choice]);
		System.out.println("On a scale of " + MAX + "-" + MIN + ", how accurate is this response?");

		while(reward < MIN || reward > MAX){
			try {
				reward = in.nextInt();
				in.nextLine();
			}	
			catch (InputMismatchException e) {	
				in.nextLine();
				System.out.println("Error: Please enter an integer between " + MIN + " and " + MAX);				
			}
		}
		System.out.println("===================================================");

		return reward;
	}


	/**
	 *
	 * @param nextState - the state that follows the current state
	 * @return The highest possible reward, for taking the best action, from the
	 * future state.
	 */
	protected double bestResponseValue(int nextState) {
		double maxAPrime = q_table[nextState][0];
		for (int i = 1; i < q_table[nextState].length; i++) {
			if (q_table[nextState][i] > maxAPrime) {
				maxAPrime = q_table[nextState][i];
			}
		}
		return maxAPrime;
	}


	/**
	 * Updates the Q table given the new sample (state, action, reward) 
	 * 
	 * @param lastState The last state of the MDP
	 * @param lastAction The last action taken by the agent
	 * @param lastReward The reward given for taking lastAction in lastState
	 * @param newState The resulting state after taking lastAction in lastState
	 */
	protected void updateQTable(int lastState, int lastAction, int lastReward, int newState) {
		double aPrime = bestResponseValue(newState);
		q_table[lastState][lastAction]
				= q_table[lastState][lastAction]
						+ (params.anneal) * (((double) lastReward
								+ (params.GAMMA * aPrime)) - q_table[lastState][lastAction]);
	}

	/**
	 * A method which saves the current QLearner to file, so that it
	 *
	 * @return - true if operation was successful, false otherwise
	 */
	public boolean saveToFile() {
		File file = new File(INPUT_PATH);
		FileWriter writer = null;

		try {
			writer = new FileWriter(file);
			writer.append(String.valueOf(params.GAMMA)
					+ "," + String.valueOf(params.remaining_iters)
					+ "," + String.valueOf(params.EXPLORE));

			for (int i = 0; i < q_table.length; i++) {
				writer.append("\n");
				for (int j = 0; j < q_table[i].length; j++) {
					if (j == q_table[i].length - 1) {
						writer.append(String.valueOf(q_table[i][j]));
					} else {
						writer.append(String.valueOf(q_table[i][j]) + ",");
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Couldn't write to file");
			e.printStackTrace();
			return false;
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("error in QLearner - saveToFile()");
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * A method which reads the current QLearner from file, so that it
	 *
	 * @return - true if operation was successful, false otherwise
	 */
	public boolean readFromFile() {
		final String DELIMITER = ",";

		try {
			BufferedReader fileReader;
			fileReader = new BufferedReader(new FileReader(INPUT_PATH));

			//Read first line, which contains instance variables: GAMMA, ANNEAL, and EXPLORE
			String line = fileReader.readLine();
			String[] tokens = line.split(DELIMITER);
			params.GAMMA = Double.parseDouble(tokens[0]);
			params.remaining_iters = Integer.parseInt(tokens[1]);
			params.EXPLORE = Integer.parseInt(tokens[2]);

			for (int i = 0; i < q_table.length; i++) {
				line = fileReader.readLine();
				tokens = line.split(DELIMITER);
				for (int j = 0; j < q_table[i].length; j++) {					
					q_table[i][j] = Double.parseDouble(tokens[j]);
				}
			}
			fileReader.close();
		} catch (IOException e) {
			System.err.println("Couldn't read from file");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
