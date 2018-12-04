package edu.pugetsound.mathcs.nlp.architecture_nlp.generator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class connects the java codebase to a trained python model with a python script intermediary to
 * create a natural language response from an utterance input.
 *
 * @author kmramos
 * @version 12/3/2018
 */
public class BasicTextGenerator implements TextGenerator
{
	/**
	 * Gives the model an utterance and returns the generated response.
	 * 
	 * @param input		an utterance to feed to the model
	 * @return String	a String representation of the response from the model
	 * @throws IOException  if error occurs while sending input to script and receiving response
	 */
	public String generateResponse(String input) throws IOException 
	{
		/*Basically, you use the exec method of the Runtime class to run 
		 * the command as a separate process. Invoking the exec method returns 
		 * a Process object for managing the subprocess.
		 */
		
		/*Then you use the getInputStream() and getErrorStream() methods of the
		 *  Process object to read the normal output of the command, and the 
		 *  error output of the command.
		 */
		final String OS = System.getProperty("os.name");
		String output = null;
		String command = "";
		
		 try 
		 {
			 //Storing project path on current machine
			 Path currentRelativePath = Paths.get("");
			 String path = currentRelativePath.toAbsolutePath().toString();	
			 
			 //Checking OS to determine which slashes to use in file path to script
			 //This file is a test file that only works on my (kmramos) machine right now
			 //This will change once I know where the script will be placed in the project file structure
			 if (OS.contains("Windows"))
			 {
				 command = "python " + "\""+ path + "\\Local Tests\\HelloWorld.py" + "\"";
			 }
			 else
			 {
				 command = "python " + "\""+ path + "/Local Tests/HelloWorld.py" + "\"";
			 }

		     //Run the command in the command line with input as a command line argument
			 Process process = Runtime.getRuntime().exec(command + " " + "\"" + input + "\"");
			 	
			 BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

		     BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		     // read the output from the command line (should only be one line if working correctly)
		     while ((output = stdInput.readLine()) != null) 
		     {
		    	 return output;
		     }
		            
		     // read any errors from the attempted command
		     // prints python errors if there are any
		     while ((output = stdError.readLine()) != null) 
		     {
		    	 System.out.println(output);
		     }
		            			 		
		     }
		 catch (IOException e) 
		 {
		      System.out.println("exception happened - here's what I know: ");
		      e.printStackTrace();
		      System.exit(-1);
		 }
		
		
		return output;
	}
	
	/**
	 * Testing to see if the object works and returns a  string that has been 
	 * manipulated printed to the command line in python
	 * @param args 
	 */
	public static void main(String[] args)
	{
		String response ="Failed";
		BasicTextGenerator gen = new BasicTextGenerator();
		try {
			response = gen.generateResponse("I am a cat.");
			} 
		catch (IOException e) 
			{
			e.printStackTrace();
			}
		
			System.out.println(response);
	}

}

