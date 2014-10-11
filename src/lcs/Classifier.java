package lcs;


public class Classifier {
	
	private double fitness;
	private String condition;
	private Action action;
	
	public Classifier(String condition, Action action) {
		this.condition = condition;
		this.action = action;
		this.fitness = 0.0;
	}

	public Action getAction() {
		return action;
	}

	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public boolean isMatching(String input) {
		return input.matches(condition);
	}

}
