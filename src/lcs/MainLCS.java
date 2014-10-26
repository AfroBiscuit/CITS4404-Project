package lcs;

import java.util.ArrayList;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;

public class MainLCS {
	
	public MainLCS(){}
	
	//static boolean tfrRunning;
	static String fileLoc = "G:\\LCSTestCSV\\lcs.csv";
	
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
		
		try
		{
		    FileWriter writer = new FileWriter(fileLoc);
	 
		    writer.append("North,South,East,West,Run Time,State,Fitness");
		    writer.append('\n');
	 
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		}
		
		TrafficHandler tf = new TrafficHandler();
		runTF tfr = new runTF(tf);
		runLCS rlcs = new runLCS(actions, tf);
		
		tfr.start();
		//System.out.println(tfrRunning);
		rlcs.start();



	}
	
	//following from http://www.mkyong.com/java/how-to-export-data-to-csv-file-java/
	private static void generateCSVFile(String sFileName, String data)
	   {
		try
		{
		    FileWriter writer = new FileWriter(sFileName, true);
	 
		    writer.append(data);
		    writer.append('\n');
	 
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}

	private static String prefixZeroes(String iStr, int length) {
		//String iStr = Integer.toString(i);
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
			return "D";
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
				//int rounds = 1000;
				//for (int k = 0; k < rounds; k++) {
				long startTime = System.currentTimeMillis();
				long prevTime = 0;
				while(System.currentTimeMillis() - startTime <=720000){
					LCS lcs = new LCS(actions);
					String input = tf.queueLength();
					String[] queuesStr = input.split(",");
					String inStr = null;
					for(String i : queuesStr) inStr = inStr + prefixZeroes(i, 4) + ",";
					inStr = inStr.replace("null", "");
					//calculate a reward
					double totalQueue = Double.parseDouble(queuesStr[0]) + Double.parseDouble(queuesStr[1]) 
							+ Double.parseDouble(queuesStr[2]) + Double.parseDouble(queuesStr[3]); 
					double aveQueue = totalQueue/4.0;
					if (aveQueue < 1.0) aveQueue = 1.0;
					Action a = lcs.input(inStr);
					if(System.currentTimeMillis()%500 == 0)System.out.println(a.getBitRepresentation());
					if(System.currentTimeMillis()%500 == 0)System.out.println(input + " " + tf.state + " " + System.currentTimeMillis());
					a.performAction(tf);
					double fitness = 1.0/aveQueue;
					lcs.updateFitness(fitness);
					lcs.runGA();
					
					double outFit = 0.0;
					for (Classifier c : lcs.getClassifiers()) {
						if(System.currentTimeMillis()%500 == 0)System.out.println(c.getCondition() + ": "
								+ c.getAction().getBitRepresentation() + "= "
								+ c.getFitness());
						outFit = c.getFitness();
					}
					input = input + "," + (System.currentTimeMillis() - startTime) + "," + tf.state + "," + outFit;
					if(System.currentTimeMillis()%500 == 0 && (System.currentTimeMillis() - startTime) != prevTime){
						generateCSVFile(fileLoc, input);
						prevTime = (System.currentTimeMillis() - startTime);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
