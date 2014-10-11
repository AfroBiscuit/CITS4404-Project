package lcs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class LCS {

	private Random randomGenerator;
	private ArrayList<Classifier> classifiers;
	private ArrayList<Action> actions;
	
	boolean alternate = true; //Whether to alternate between exploring and exploring or not. If not, sticks to exploiting
	int alternateTurn = 0; //What to start with in the alternation, 0 = explore, 1 = exploit
	int wildcardProb = 30; //Probability that a symbol will be replaced by a wildcard
	int maxPopulation = 100; //Maximum size of the population. If this is exceeded, classifiers will be removed using roulette wheel selection based on their fitness
	
	public LCS(ArrayList<Action> actions) {
		this.classifiers = new ArrayList<Classifier>();
		this.actions = actions;
		this.randomGenerator = new Random();
		
	}
	
	public Action input(String input) {
		ArrayList<Classifier> matchSet = new ArrayList<Classifier>();
		for(Classifier classifier : classifiers) {
			if(classifier.isMatching(input)) {
				matchSet.add(classifier);
			}
		}
	
		if(matchSet.size() > 0) { //Do action selection (exploit and explore)
			return actionSelection(matchSet);
		}
		else {
			return cover(input);
		}
	}
	
	private Action cover(String input) {
		//Adding wildcards
		StringBuffer buffer = new StringBuffer(input.length());
	    for (int i = 0; i < input.length(); i++) {
	    	int rndInt = randomGenerator.nextInt(100);
	    	if(rndInt < wildcardProb) {
	    		buffer.append(".");
	    	}
	    	else {
	    		buffer.append(input.charAt(i));
	    	}
	    }
		Action randomAction = getRandomAction();
		Classifier classifier = new Classifier(buffer.toString(), randomAction);
		addClassifier(classifier);
		return randomAction;
	}
	
	
	private void addClassifier(Classifier newClassifier) {
		if(classifiers.size() >= maxPopulation) {
			double sumFit = 0.0;
			for(Classifier classifier : classifiers) {
				sumFit += classifier.getFitness();
			}
			
			//Roulette wheel selection for inverse fitness. Could probably be done a lot better
			int probSize = maxPopulation*100;
			Classifier[] probArray = new Classifier[probSize];
			int start = 0;
			int end = -1;
			for(Classifier classifier : classifiers) {
				double invFit = 1/classifier.getFitness();
				double prob = invFit/sumFit;
				start = end+1;
				end = (int) (start+prob*probSize)-1;
				for(int i = start; i<=end; i++) {
					probArray[i] = classifier;
				}
			}
			int rndClassifierIndex = randomGenerator.nextInt(end+1);
			Classifier toDelete = probArray[rndClassifierIndex];			
			classifiers.remove(toDelete);
			
			
		}
		classifiers.add(newClassifier);
	}
	
	private Action actionSelection(ArrayList<Classifier> matchSet) {
		//Explore
		if(alternate && alternateTurn == 0) { 
			int rndMatchIndex = randomGenerator.nextInt(matchSet.size());
			Classifier rndMatch = matchSet.get(rndMatchIndex);
			alternateTurn = 1;
			return rndMatch.getAction();
		}
		
		//Exploit
		HashMap<Action, Double> prediction = new HashMap<Action, Double>();
		//Generating prediction array
		for(Classifier match : matchSet) {
			Action action = match.getAction();
			Double sum = 0.0;
			if(prediction.containsKey(action)) {
				sum = prediction.get(action);
			}
			sum += match.getFitness();
			prediction.put(action, sum);
		}
		
		//Finding largest value
		Map.Entry<Action, Double> maxEntry = null;
		for (Map.Entry<Action, Double> entry : prediction.entrySet())
		{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
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
	
	public void updateFitness(String input, Action action, double reward) {
		ArrayList<Classifier> actionSet = new ArrayList<Classifier>();
		for(Classifier classifier : classifiers) {
			if(classifier.isMatching(input) && classifier.getAction().equals(action)) {
				actionSet.add(classifier);
			}
		}		
		
		//Do some fitness updates here
	
		
		
	}
	
	
}
