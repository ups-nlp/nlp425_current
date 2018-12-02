package edu.pugetsound.mathcs.architecture_nlp.features.spf;


import java.util.HashMap;

/**
 *
 * PreProcessor used to "fix" input sentences so the SPFSemanticAnalyzer properly recognizes all words.
 * Replacements of contractions are not perfect fits, and are only meant to hopefully provide something relatively parsible for the SPF library.
 * @author David S Smith
 * @version 11/28/2018
 *
 */

    public class SPFPreProcessor {

        private HashMap<String, String> expansionMap;

        /**
         * Instantiates a preProcessor, which involves setting up a collection of contractions and expansions.
         */
        public SPFPreProcessor() {
            expansionMap = new HashMap<>();

            //SOMEWHAT COMMON CONTRACTIONS
            //Marked "ambiguity" if there are multiple context-dependent results. I just pick one.
            //Listed alphabetically
            expansionMap.put("aren't", "are not");
            expansionMap.put("can't", "can not");
            expansionMap.put("could've", "could have");
            expansionMap.put("couldn't", "could not");
            expansionMap.put("didn't", "did not");
            expansionMap.put("doesn't", "does not");
            expansionMap.put("don't", "do not"); //ambiguity
            expansionMap.put("hadn't", "had not");
            expansionMap.put("hasn't", "has not");
            expansionMap.put("haven't", "have not");
            expansionMap.put("he'd", "he would");
            expansionMap.put("he'll", "he will");
            expansionMap.put("he'd", "he would"); //ambiguity
            expansionMap.put("he's", "he is"); //ambiguity
            expansionMap.put("here're", "here are"); //ambiguity
            expansionMap.put("here's", "here is"); //ambiguity
            expansionMap.put("how'd", "how did"); //ambiguity
            expansionMap.put("how'll", "how will"); //ambiguity
            expansionMap.put("how're", "how are");
            expansionMap.put("how's", "how is"); //ambiguity
            expansionMap.put("I'd", "I would"); //ambiguity'
            expansionMap.put("I'll", "I will"); //ambiguity
            expansionMap.put("I'm", "I am");
            expansionMap.put("I've", "I have");
            expansionMap.put("isn't", "is not"); //ambiguity
            expansionMap.put("it'd", "it would");
            expansionMap.put("it'll", "it will"); //ambiguity
            expansionMap.put("it's", "it is"); //ambiguity
            expansionMap.put("let's", "let us");
            expansionMap.put("might've", "might have");
            expansionMap.put("must've", "must have"); //ambiguity
            expansionMap.put("she'd", "she would"); //ambiguity
            expansionMap.put("she'll", "she will"); //ambiguity
            expansionMap.put("she's", "she is"); //ambiguity
            expansionMap.put("should've", "should have");
            expansionMap.put("shouldn't", "should not");
            expansionMap.put("shouldn't've", "should not have");
            expansionMap.put("somebody's", "somebody is"); //ambiguity
            expansionMap.put("someone's", "someone is"); //ambiguity
            expansionMap.put("something's", "something is"); //ambiguity
            expansionMap.put("that'll", "that will"); //ambiguity
            expansionMap.put("that's", "that is"); //ambiguity
            expansionMap.put("that'd", "that would"); //ambiguity
            expansionMap.put("there'd", "there would"); //ambiguity
            expansionMap.put("there'll", "there will"); //ambiguity
            expansionMap.put("there're", "there are");
            expansionMap.put("there's", "there is"); //ambiguity
            expansionMap.put("these're", "these are"); //ambiguity
            expansionMap.put("they'd", "they would"); //ambiguity
            expansionMap.put("they'll", "they will"); //ambiguity
            expansionMap.put("they're", "they are"); //ambiguity
            expansionMap.put("they've", "they have");
            expansionMap.put("those're", "those are");
            expansionMap.put("wasn't", "was not");
            expansionMap.put("we'd", "we had"); //ambiguity
            expansionMap.put("we'd've", "we would have");
            expansionMap.put("we'll", "we will"); //ambiguity
            expansionMap.put("we're", "we are");
            expansionMap.put("we've", "we have");
            expansionMap.put("weren't", "were not");
            expansionMap.put("what'd", "what did");
            expansionMap.put("what'll", "what will");
            expansionMap.put("what're", "what are");
            expansionMap.put("what's", "what is"); //ambiguity
            expansionMap.put("what've", "what have");
            expansionMap.put("when's", "when is"); //ambiguity
            expansionMap.put("where'd", "where did");
            expansionMap.put("where's", "where is"); //ambiguity
            expansionMap.put("where've", "where have");
            expansionMap.put("who'd", "who did"); //ambiguity
            expansionMap.put("who'd've", "who would have");
            expansionMap.put("who'll", "who will"); //ambiguity
            expansionMap.put("who're", "who are");
            expansionMap.put("who's", "who is"); //ambiguity
            expansionMap.put("who've", "who have");
            expansionMap.put("why'd", "why did");
            expansionMap.put("why're", "why are");
            expansionMap.put("why's", "why is"); //ambiguity
            expansionMap.put("won't", "will not");
            expansionMap.put("would've", "would have");
            expansionMap.put("wouldn't", "would not");
            expansionMap.put("you'd", "you would"); //ambiguity
            expansionMap.put("you'll", "you will"); //ambiguity
            expansionMap.put("you're", "you are");
            expansionMap.put("you've", "you have");


        }

        /**
         * Processes a sentence to be compatible with the SPFSemanticAnalyzer.
         * Processed sentence must:
         * 1. Be lower case
         * 2. Have no contractions (for step 3 to work smoothly)
         * 3. Have no punctuation
         *
         * @param sentence An input sentence to be processed.
         * @return A sentence properly altered to be used by the SPFSemantic Analyzer
         */
        public String process(String sentence) {

            String altered = sentence.toLowerCase(); //set sentence to lowercase
            for (String contraction : expansionMap.keySet()) {
                altered = altered.replaceAll(contraction, expansionMap.get(contraction)); //retrieve expansion from map, replace all occurrences of contraction with expansion
            }
            altered = altered.replaceAll("[^0-9a-z ]", ""); //remove all remaining punctuation (NOTE: APOSTROPHES EXPRESSING POSSESSION WILL NO LONGER MAKE SENSE)
            return altered;
        }

    }
