package edu.pugetsound.mathcs.nlp.architecture_nlp.brain;

/**
 * The set of dialogue act tags from the Switchboard Parser with associated
 * labels.
 * 
 * @author Creavesjohnson
 * @version 05/13/2016
 */
public enum DialogueActTag {
	
	/**
	 * about-communication
	 */
	ABOUT_COMMUNICATION("^c"),
	
	
	/**
	 * about-task
	 */
	ABOUT_TASK("^t"),
	
	
	/**
	 * Accept: "ok" , "i agree"
	 */
	ACCEPT("aa"),

	
	/**
	 * Accept-part
	 */
	ACCEPT_PART("aap"),
	
	
	/**
	 * ACKNOWLEDGE-ANSWER "Oh, okay"
	 */
	ACKNOWLEDGE_ANSWER("bk"),
	
	
	/**
	 * Action-directive: "Go ahead", "We could go back to television shows"
	 */
	ACTION_DIRECTIVE("ad"),

	
	/**
	 * Agreements
	 */
	AGREEMENTS("a"),
	
	
	/**
	 * Answer Dispreferred (Well...)
	 */
	ANSWER_DISPREFERRED("nd"),
	
	
	/**
	 * Apology "Apologies" (this is not the "I'm sorry" of sympathy which is
	 * "by")
	 */
	APOLOGY("fa"),
	
	
	/**
	 * assessment/appreciation ("I can imagine")
	 */
	ASSESSMENT_APPRECIATION("ba"),
	
	
	/**
	 * Backchannel/Backwards-Looking
	 */
	BACKCHANNEL("b"),
	
	
	/**
	 * collaborative completion
	 */
	COLLABORATIVE_COMPLETION("^2"),

	
	/**
	 * Commit
	 */
	COMMIT("cc"),

	
	/**
	 * Conventional-closing
	 */
	CONVENTIONAL_CLOSING("fc"),

	
	/**
	 * Conventional-opening
	 */
	CONVENTIONAL_OPENING("fp"),
	
	
	/**
	 * comment (followed by "*[[comment...]]" after transcription to explain)
	 */
	COMMENT("*"),
	
	
	/**
	 * continued from previous by same speaker
	 */
	CONTINUED_FROM_PREVIOUS("+"),

	
	/**
	 * default agreement or continuer (uh-huh, right, yeah)
	 */
	CONTINUER("b"),

	
	/**
	 * declarative question (question asked like a structural statement)
	 */
	DECLARATIVE_QUESTION("^d"),	
	

	/**
	 * a descriptive/narrative statement which acts as an affirmative answer to
	 * a question
	 */
	DESCRIPTIVE_AFFIRMATIVE_ANSWER("na"),


	/**
	 * a descriptive/narrative statement which acts as a negative answer to a
	 * question
	 */
	DESCRIPTIVE_NEGATIVE_ANSWER("ng"),

	
	/**
	 * Downplaying-reponse-to-sympathy/compliments
	 * ("That's all right","that happens")
	 */
	DOWNPLAYING_SYMPATHY("bd"),

	
	/**
	 * [on statements] elaborated reply to y/n question
	 */
	ELABORATED_REPLY_Y_N_QUESTION("^e"),

	
	/**
	 * Exclamation "Ouch"
	 */
	EXCLAMATION("fe"),
	
	
	/**
	 * Explicit-performative ("you're filed" )
	 */
	EXPLICIT_PERFORMATIVE("fx"),
	
	
	/**
	 * Forward-Looking
	 */
	FORWARD_LOOKING("f"),
	
	
	/**
	 * hold (often but not always after a question) ('let me think'; question in
	 * response to a question)
	 */
	HOLD("^h"),

	
	/**
	 * a response to a question that is neither affirmative nor negative (often
	 * "I don't know")
	 */
	INDETERMINATE_RESPONSE("no"),
	
	
	/**
	 * mimic the previous utterance
	 */
	MIMIC_OTHER("^m"),
	

	/**
	 * Maybe
	 */
	MAYBE("am"),

	
	/**
	 * descriptive and/or narrative (listener has no basis to dispute)
	 */
	NARRATIVE_DESCRIPTIVE("sd"),
	

	/**
	 * no or variations (only)
	 */
	NO("nn"),
	
	
	/**
	 * NULL
	 */
	NULL(""),
	
	
	/**
	 * Offer
	 */
	OFFER("co"),

	
	/**
	 * Open-option "We could have lamb or chicken"
	 */
	OPEN_OPTION("oo"),
	

	/**
	 * other
	 */
	OTHER("o"),


	/**
	 * Question
	 */
	QUESTION("q"),

	
	/**
	 * alternative (`or') question
	 */
	QUESTION_ALTERNATIVE("qr"),
	

	/**
	 * open ended question
	 */
	QUESTION_OPEN_ENDED("qo"),

	
	/**
	 * rhetorical question
	 */
	QUESTION_RHETORICAL("qh"),

	
	/**
	 * wh-question
	 */
	QUESTION_WH("qw"),

	
	/**
	 * yes/no question
	 */
	QUESTION_YES_NO("qy"),
	
	
	/**
	 * an or-question clause tacked onto a yes-no question
	 */
	QUESTION_YES_NO_OR("qrr"),

	
	/**
	 * quotation
	 */
	QUOTATION("^q"),


	/**
	 * reFormulate/summarize; paraphrase/summary of other's utterance (as
	 * opposed to a mimic)
	 */
	REFORMULATE_SUMMARIZE("bf"),


	/**
	 * Reject
	 */
	REJECT("ar"),

	
	/**
	 * Reject-part
	 */
	REJECT_PART("arp"),

	
	/**
	 * Statement
	 */
	STATEMENT("s"),

	
	/**
	 * Signal-non-understanding (request for repeat)
	 */
	SIGNAL_NON_UNDERSTANDING("br"),

	
	/**
	 * Sympathetic comment ("I'm sorry to hear about that")
	 */
	SYMPATHETIC_COMMENT("by"),
	
	
	/**
	 * tag question (question asked like a structural statement with a question
	 * tag at end)
	 */
	TAG_QUESTION("^g"),

	
	/**
	 * Thanks "Thank you"
	 */
	THANKS("ft"),
	
	
	/**
	 * viewpoint, from personal opinions to proposed general facts (listener
	 * could have basis to dispute)
	 */
	VIEWPOINT("sv"),

	
	/**
	 * Welcome "You're welcome"
	 */
	WELCOME("fw"),


	/**
	 * yes or variations (only)
	 */
	YES("ny");


	
	

	// The label which appears in the data set
	private String label;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            The label which is associated with this act in the Switchboard
	 *            data
	 */
	DialogueActTag(String label) {
		this.label = label;
	}

	/**
	 * Finds a member of DialogueActTag by its switchboard label
	 * 
	 * @param label
	 *            A switchboard label
	 * @return The DialogueActTag member associated with the switchboard label
	 * @throws IllegalArgumentException
	 *             if label is not represented in the enum
	 */
	public static DialogueActTag fromLabel(String label) throws IllegalArgumentException {		
		for (DialogueActTag tag : DialogueActTag.values()){
			if (tag.label.equalsIgnoreCase(label)){
				return tag;
			}
		}

		throw new IllegalArgumentException("\"" + label
				+ "\" is not a valid dialogue act tag label.");
	}

	@Override
	/**
	 * Creates a String representation containing both the name of the
	 * enum element and its associated Switchboard shorthand label
	 */
	public String toString() {
		return String.format("(%s,  %s)", this.name(), this.label);
	}

}