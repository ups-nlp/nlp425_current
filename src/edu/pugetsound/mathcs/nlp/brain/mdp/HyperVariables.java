package edu.pugetsound.mathcs.nlp.brain.mdp;


/**
 *
 * @author Zachary Cohan
 */
 public class HyperVariables {

	 /**
	  * Gamma is the discount factor that weights immediate versus future rewards
	  * If gamma is close to 1, then future rewards are equal to immediate rewards
	  * If gamma is close to 0, then future rewards are insignificant compared to immediate rewards
	  */
	 protected double GAMMA;
    
    
    /**
     * Explore is the number of iterations of Q-learning that will happen. After this many
     * iterations, we assume that we have converged
     */
    protected int EXPLORE;
    protected int remaining_iters;
    protected double anneal;
    
    /**
     * Bundles together the hyper-parameters for the Markov Decision Process
     * 
     * @param gamma The discount for future rewards
     * @param explore The number of iterations to convergence. Larger values
     * 					correspond to a longer explore/expolit phase. 
     */
    public HyperVariables(double gamma, int explore){
        GAMMA = gamma;
        EXPLORE = explore;
        remaining_iters = explore;
        anneal = (double)remaining_iters/EXPLORE;
    }
    
    /**
     * Decrements the number of remaining iterations and updates
     * the parameters accordingly
     */
    public void decrement(){
    	remaining_iters--;
    	anneal = (double) remaining_iters/EXPLORE;
    }
}
