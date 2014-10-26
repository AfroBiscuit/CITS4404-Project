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
		actions.add(new TestActionC());
		actions.add(new TestActionD());
		
		TrafficHandler tf = new TrafficHandler();
		runTF tfr = new runTF(tf);
		runLCS rlcs = new runLCS(actions, tf);
		
		tfr.start();
		rlcs.start();



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

	
	//turn on state 0
	public static class TestActionA implements Action {
		
		@Override
		public String getBitRepresentation() {
			return "A";
		}

		@Override
		public void performAction(Object o) {
			((TrafficHandler)o).state = 0;

		}

	}

	
	//turn on state 1
	public static class TestActionB implements Action {

		@Override
		public String getBitRepresentation() {
			return "B";
		}

		@Override
		public void performAction(Object o) {
			((TrafficHandler)o).state = 1;
		}

	}

	
	//turn on state 2
	public static class TestActionC implements Action {

		@Override
		public String getBitRepresentation() {
			return "C";
		}

		@Override
		public void performAction(Object o) {
			((TrafficHandler)o).state = 2;
		}

	}
	
	
	//turn on state 3
	public static class TestActionD implements Action {
		
		@Override
		public String getBitRepresentation() {
			return "C";
		}

		@Override
		public void performAction(Object o) {
			((TrafficHandler)o).state = 3;
		}
		
	}
	
	private static class runTF extends Thread{
		
		TrafficHandler tf;
		
		private runTF(TrafficHandler tfIN){
			tf = tfIN;
		}
		
		@Override
		public void run(){
			tf.trafficLoop();
		}
		
	}
	
	private static class runLCS extends Thread{
		
		ArrayList<Action> actions;
		TrafficHandler tf;
		
		private runLCS(ArrayList<Action> actionsIN, TrafficHandler tfIN){
			actions = actionsIN;
			tf = tfIN;
		}
		
		@Override
		public void run(){
			//consider moving the below to an inner class for threading
			try {
				int rounds = 1000;
				for (int k = 0; k < rounds; k++) {
					LCS lcs = new LCS(actions);
						String input = tf.queueLength();
						String[] queuesStr = input.split(",");
						//calculate a reward
						double totalQueue = Double.parseDouble(queuesStr[0]) + Double.parseDouble(queuesStr[1]) 
								+ Double.parseDouble(queuesStr[2]) + Double.parseDouble(queuesStr[3]); 
						double aveQueue = totalQueue/4.0;
						if (aveQueue < 1.0) aveQueue = 1.0;
							Action a = lcs.input(input);
							System.out.println(a.getBitRepresentation());							
							double fitness = 1.0/aveQueue;
							lcs.updateFitness(fitness);
							lcs.runGA();
						for (Classifier c : lcs.getClassifiers()) {
							System.out.println(c.getCondition() + ": "
									+ c.getAction().getBitRepresentation() + "= "
									+ c.getFitness());
						}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
