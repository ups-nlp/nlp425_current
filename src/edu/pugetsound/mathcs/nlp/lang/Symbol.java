package edu.pugetsound.mathcs.nlp.lang;

import java.util.regex.Pattern;

public class Symbol {
	private Symbol() {
		
	}
	
	/**
	 * Matches any of the following 32 symbol characters:
	 *
	 * 			!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
	 *
	 * @param token a string
	 * @return true if string is a symbol and false otherwise
	 */
	public static boolean isSymbol(String token){
		return Pattern.matches("\\p{Punct}", token);
	}
}
