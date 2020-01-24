package edu.pugetsound.mathcs.nlp.architecture_nlp.brain;


/**
 * Possible actions for the system to take. These actions are a subset of the
 * Dialogue act tags (i.e. a subset of the enum DialogueActTag)
 * 
 * @author alchambers
 * @author Thomas Gagne & Jon Sims
 * @version 04/26/16
 */
public enum Action {   

	APOLOGY("fa"),
	BACKCHANNEL("b"),
	CONVENTIONAL_CLOSING("fc"),
	CONVENTIONAL_OPENING("fp"),
	QUESTION_YES_NO("qy"),
	QUESTION_WH("qw"),
	SIGNAL_NON_UNDERSTANDING("br"),
	STATEMENT("s"),
	SYMPATHETIC_COMMENT("by"),
	THANKS("ft"),
	WELCOME("fw"),
	YES_NO_ANSWER("ayn"),
	NULL("null");

	private String label;

	/**
	 * Constructor
	 * @param label 
	 * 				The label associated with this tag in the Switchboard data set
	 */
	Action(String label) {
		this.label = label;
	}

	/**
	 * Creates a String representation containing both the name of the
	 * enum element and its associated Switchboard shorthand label
	 */
	@Override
	public String toString() {
		return String.format("(%s,  %s)", this.name(), this.label);
	}
}
