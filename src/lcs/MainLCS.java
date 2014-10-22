package lcs;

import java.util.ArrayList;
import java.util.Random;

public class MainLCS {
	public static void main(String args[]) {
		
		/**
		 * The following code is a simple testcase for the LCS.
		 * 
		 * We attempt to get the LCS to distinguish between numbers >= 100 and < 100 without actually telling it that is what it is doing.
		 * 
		 * Choosing action A will give a reward of 1 when the number is below 100 and 0 otherwise and vice versa for action B.
		 * 
		 * We run @rounds rounds, where each round consists of inputting the numbers 0 to 199 to the LCS in a random order. The numbers have all been padded to the length of 4 as the LCS assumes input have a constant length
		 * 
		 * With the settings {alternate=true,alternateTurn=0,wildcardProb=0.3,maxPopulation=100,tau=0.2,beta=0.3,gamma=0.5,matingPoolSize=2,minFit=0.05} and 1000 rounds, a result of about 150 correct answers is obtained, i.e. 75% correct answers
		 */
		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(new TestActionA());
		actions.add(new TestActionB());

		try {
			int rounds = 1000;
			int minCorrect = 200;
			int maxCorrect = 0;
			int sumCorrect = 0;
			for (int k = 0; k < rounds; k++) {
				LCS lcs = new LCS(actions);
				int correct = 0;
				/*
				 * lcs.input("000."); lcs.updateFitness(0.5); lcs.input("00.0");
				 * lcs.updateFitness(0.5); lcs.runGA();
				 */
				int[] numbers = new int[200];
				for (int i = 0; i < 200; i++) {
					numbers[i] = i;
				}

				shuffleArray(numbers);

				for (int i = 0; i < 200; i++) {
					String iStr = prefixZeroes(numbers[i], 4);
					String input = iStr;
					for (int j = 0; j < 1; j++) {

						// System.out.println(input);
						Action a = lcs.input(input);
						System.out.println(a.getBitRepresentation());
						double fitness;
						if (a.getBitRepresentation().equals("A")) {
							if (i < 100) {
								correct++;
								fitness = 1;
							} else {
								fitness = 0;
							}
						} else {
							if (i < 100) {
								fitness = 0;
							} else {
								correct++;
								fitness = 1;
							}
						}
						lcs.updateFitness(fitness);
						lcs.runGA();
					}
					System.out.println("Round " + iStr);
					for (Classifier c : lcs.getClassifiers()) {
						System.out.println(c.getCondition() + ": "
								+ c.getAction().getBitRepresentation() + "= "
								+ c.getFitness());
					}

				}
				System.out.println("Correct: " + correct);
				minCorrect = Math.min(minCorrect, correct);
				maxCorrect = Math.max(maxCorrect, correct);
				sumCorrect += correct;
			}
			System.out.println("Average number of correct actions over "
					+ rounds + " rounds:");
			System.out.println(sumCorrect / rounds);
			System.out.println("Min. correct: "+minCorrect);
			System.out.println("Max. correct: "+maxCorrect);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String prefixZeroes(int i, int length) {
		String iStr = Integer.toString(i);
		if (iStr.length() >= length) {
			return iStr.substring(iStr.length() - length);
		} else {
			int numZeroes = length - iStr.length();
			for (int j = 0; j < numZeroes; j++) {
				iStr = "0" + iStr;
			}
			return iStr;
		}

	}

	public static class TestActionA implements Action {

		@Override
		public String getBitRepresentation() {
			return "A";
		}

		@Override
		public void performAction(Object o) {
			// TODO Auto-generated method stub

		}

	}

	public static class TestActionB implements Action {

		@Override
		public String getBitRepresentation() {
			return "B";
		}

		@Override
		public void performAction(Object o) {
			// TODO Auto-generated method stub

		}

	}

	public static class TestActionC implements Action {

		@Override
		public String getBitRepresentation() {
			return "C";
		}

		@Override
		public void performAction(Object o) {
			// TODO Auto-generated method stub

		}

	}

	public static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

}
