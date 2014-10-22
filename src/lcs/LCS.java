package lcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LCS {

	private Random randomGenerator;
	private ArrayList<Classifier> classifiers;
	private ArrayList<Action> actions;
	private ArrayList<Classifier> prevActionSet;
	private String curInput = "";
	private Action curAction = null;

	boolean alternate = true; // Whether to alternate between exploring and
								// exploring or not. If not, sticks to
								// exploiting
	int alternateTurn = 0; // What to start with in the alternation, 0 =
							// explore, 1 = exploit
	double wildcardProb = 0.05; // Probability that a symbol will be replaced
									// by a wildcard
	int maxPopulation = 100; // Maximum size of the population. If this is
								// exceeded, classifiers will be removed using
								// roulette wheel selection based on their
								// fitness
	double tau = 0.3; // Multiplication factor to reduce rules in match set but
						// not in action set, [0,1)
	double beta = 0.3; // Multiplication factor to reduce rules in action set,
						// [0,1). Same factor used to reduce reward
	double gamma = 0.5; // Discount factor for distribution of fitness
						// subtracted to previous action set
	int matingPoolSize = 2; // Size of pool of parents for genetic algorithm to
								// run on. Setting it to 0 or 1 effectively disables mating

	public LCS(ArrayList<Action> actions) {
		this.classifiers = new ArrayList<Classifier>();
		this.actions = actions;
		this.randomGenerator = new Random();

	}

	public Action input(String input) throws Exception {
		if(this.curAction != null) {
			throw new Exception("Inputting new data without updating fitness!");
		}
		Action action;
		ArrayList<Classifier> matchSet = new ArrayList<Classifier>();
		for (Classifier classifier : classifiers) {
			if (classifier.isMatching(input)) {
				matchSet.add(classifier);
			}
		}
		if (matchSet.size() > 0) { // Do action selection (exploit and explore)
			action = actionSelection(matchSet);
		} else {
			action = cover(input);
		}
		this.curAction = action;
		this.curInput = input;
		return action;
	}

	private Action cover(String input) {
		// Adding wildcards
		String condition = mutate(input);
		Action randomAction = getRandomAction();
		Classifier classifier = new Classifier(condition, randomAction);
		addClassifier(classifier);
		return randomAction;
	}

	private void addClassifier(Classifier newClassifier) {
		//Check if the same classifier already exists in the population. If so, do not add it
		for(Classifier c : classifiers) {
			if(c.getCondition().equals(newClassifier.getCondition()) && c.getAction().getBitRepresentation().equals(newClassifier.getAction().getBitRepresentation())) {
				return;
			}
		}
		
		if (classifiers.size() >= maxPopulation) {
			Classifier toDelete = rouletteSelection(1, true).get(0);
			classifiers.remove(toDelete);
		}
		classifiers.add(newClassifier);
	}

	private Action actionSelection(ArrayList<Classifier> matchSet) {
		// Explore
		if (alternate && alternateTurn == 0) {
			int rndMatchIndex = randomGenerator.nextInt(matchSet.size());
			Classifier rndMatch = matchSet.get(rndMatchIndex);
			alternateTurn = 1;
			return rndMatch.getAction();
		}

		// Exploit
		HashMap<Action, Double> prediction = new HashMap<Action, Double>();
		// Generating prediction array
		for (Classifier match : matchSet) {
			Action action = match.getAction();
			Double sum = 0.0;
			if (prediction.containsKey(action)) {
				sum = prediction.get(action);
			}
			sum += match.getFitness();
			prediction.put(action, sum);
		}

		// Finding largest value
		Map.Entry<Action, Double> maxEntry = null;
		for (Map.Entry<Action, Double> entry : prediction.entrySet()) {
			if (maxEntry == null
					|| entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}

		alternateTurn = 0;
		return maxEntry.getKey();
	}

	private Action getRandomAction() {
		int rndActionIndex = randomGenerator.nextInt(actions.size());
		return actions.get(rndActionIndex);
	}

	// Using implicit bucket brigade (See
	// http://www.victormontielargaiz.net/Projects/EvolutionaryComputing/Learning_Classifier_Systems.pdf)
	public void updateFitness(double reward) throws Exception {
		if(curAction == null) {
			throw new Exception("Update fitness run without running LCS");
		}
		ArrayList<Classifier> actionSet = new ArrayList<Classifier>();
		double redistFit = 0.0;
		// Collect action set and reduce fitness for current match set
		for (Classifier classifier : classifiers) {
			if(classifier.isMatching(curInput)) {
				double fitness = classifier.getFitness();
				if (classifier.getAction().equals(curAction)) {
					redistFit += beta * fitness;
					fitness = (1 - beta) * fitness;
					actionSet.add(classifier);
				} else {
					fitness = tau * fitness;
				}
				classifier.setFitness(fitness);
			}
		}

		// Update previous action set fitness
		if(prevActionSet != null) {
			redistFit = gamma * (redistFit / prevActionSet.size());
			for (Classifier classifier : prevActionSet) {
				classifier.setFitness(classifier.getFitness() + redistFit);
			}
		}
		
		// Reward current action set
		double individualReward = beta * (reward / actionSet.size());
		for (Classifier classifier : actionSet) {
			classifier.setFitness(classifier.getFitness() + individualReward);
		}
		
		prevActionSet = actionSet;
		curAction = null;
		curInput = "";
	}

	// Genetic algorithm follows 3.2 and 3.3 here:
	// ftp://ftp.dca.fee.unicamp.br/pub/docs/ea072/classifier.pdf
	public void runGA() {
		geneticAlgorithm();
	}
	
	private void geneticAlgorithm() {
		ArrayList<Classifier> matingPool = rouletteSelection(Math.min(matingPoolSize, classifiers.size()),
				false);
		ArrayList<Classifier> children = new ArrayList<Classifier>();
		while (matingPool.size() > 1) {
			int index1 = randomGenerator.nextInt(matingPool.size());
			int index2 = randomGenerator.nextInt(matingPool.size());
			while (index1 == index2) {
				index2 = randomGenerator.nextInt(matingPool.size());
			}
			Classifier mate1 = matingPool.get(index1);
			Classifier mate2 = matingPool.get(index2);
			Classifier child = mate(mate1, mate2);
			children.add(child);
			matingPool.remove(mate1);
			matingPool.remove(mate2);
		}
		
		for(Classifier classifier : children) {
			addClassifier(classifier);
		}

	}

	Classifier mate(Classifier c1, Classifier c2) {
		String condition = crossover(c1.getCondition(), c2.getCondition());
		condition = mutate(condition);
		String bitAction = crossover(c1.getAction().getBitRepresentation(), c2
				.getAction().getBitRepresentation());
		Action selectedAction = null;
		for (Action a : actions) {
			if (a.getBitRepresentation().equals(bitAction)) {
				selectedAction = a;
				break;
			}
		}
		if (selectedAction == null) { // Choose random action between the two
										// mates
			int rnd = randomGenerator.nextInt(2);
			if (rnd == 0) {
				selectedAction = c1.getAction();
			} else {
				selectedAction = c2.getAction();
			}
		}
		return new Classifier(condition, selectedAction);

	}

	private String crossover(String s1, String s2) {
		int i = randomGenerator.nextInt(Math.max(s1.length(),2));
		return s1.substring(0, i) + s2.substring(i, s2.length());
	}

	private String mutate(String input) {
		StringBuffer buffer = new StringBuffer(input.length());
		for (int i = 0; i < input.length(); i++) {
			double rndDouble = randomGenerator.nextDouble();
			if (input.charAt(i) != ',' && rndDouble < wildcardProb) {
				buffer.append(".");
			} else {
				buffer.append(input.charAt(i));
			}
		}
		return buffer.toString();
	}

	private ArrayList<Classifier> rouletteSelection(int numSel, boolean inverse) {

		/*
		 * Code based on
		 * https://github.com/dwdyer/watchmaker/blob/master/framework/src/java/main/org/uncommons/watchmaker/framework/selection/RouletteWheelSelection.java 
		 * Copyright 2006-2010 Daniel W. Dyer 
		 * 
		 * Licensed under the Apache License, Version 2.0 (the "License");
		 * 
		 * Altered to fit our usage
		 */
		double[] cumFit = new double[classifiers.size()];
		if (inverse) {
			cumFit[0] = 1 - classifiers.get(0).getFitness();
		} else {
			cumFit[0] = classifiers.get(0).getFitness();
		}
		for (int i = 1; i < classifiers.size(); i++) {
			double fitness;
			if (inverse) {
				fitness = 1 - classifiers.get(i).getFitness();
			} else {
				fitness = classifiers.get(i).getFitness();
			}
			cumFit[i] = cumFit[i - 1] + fitness;
		}

		ArrayList<Classifier> selection = new ArrayList<Classifier>(numSel);
		for (int i = 0; i < numSel; i++) {
			double randomFitness = randomGenerator.nextDouble()
					* cumFit[cumFit.length - 1];
			int index = Arrays.binarySearch(cumFit, randomFitness);
			if (index < 0) {
				index = Math.abs(index + 1);
			}
			selection.add(classifiers.get(index));
		}
		return selection;

	}
	
	public ArrayList<Classifier> getClassifiers() {
		return this.classifiers;
	}

}
