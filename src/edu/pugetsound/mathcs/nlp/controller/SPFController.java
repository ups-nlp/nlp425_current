package edu.pugetsound.mathcs.nlp.controller;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * This class contains the main input/output loop of the conversational agent
 * @author alchambers, kstern, jpolonitza
 */

public class SPFController {
	protected static PrintStream out;
	protected static Scanner input;
	protected static SPFModel model;
		
	
	/**
	 * Sets up the necessary tools for the conversational agent
	 */
	protected static void setup(InputStream in, PrintStream outStream){
		out = outStream;		
		input = new Scanner(in);
		model = new SPFModel();
	}

	
	/**
	 * Main controller for the conversational agent. 
	 * TODO: Add description of any command line arguments
	 */
	public static void main(String[] args){	
		setup(System.in, System.out);
		model.loadState();
		
		// Provide an initial greeting
		out.print("Agent: " + model.initialResponse() + "\n");
		
		while(!model.conversationIsOver()){
			// Read the human's typed input
			out.print("> ");
			String line = input.nextLine();

			out.println();
			out.println();

			// Respond with the conversational agent's response
			String response = model.getResponse(line);
			out.println("Agent: " + response);
		}
		model.saveState();
	}
}
