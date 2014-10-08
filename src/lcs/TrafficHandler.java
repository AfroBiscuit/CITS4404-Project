package lcs;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TrafficHandler {
	
	public TrafficHandler(){}
	
	public int laneNorth;
	public int laneSouth;
	public int laneEast;
	public int laneWest;
	
	public int[] laneNumbers = new int[4];
	
	LaneIncrement laneInc = new LaneIncrement();
	LaneDecrement laneDec = new LaneDecrement();
	
	int laneIncSleep = 250;
	int laneDecSleep = 5000;
	
	long startTime;
	
	boolean runningInc;
	boolean runningDec;
	
	public int prob;
	
	public void trafficLoop(){
		//implement main algorithm here
		//set traffic mode
		highTraffic(1);
		//runs for 1 minute
		startTime = System.currentTimeMillis();
		laneInc.start();
		laneDec.start();
		
		if(!runningInc) laneInc.interrupt();
		if(!runningDec) laneDec.interrupt();
		
	}
	
	//threads needed for good simulation
	
	public void highTraffic(int lane){
		prob = 10;
	}
	
	public void medTraffic(int lane){
		prob = 30;
	}
	
	public void lowTraffic(int lane){
		prob = 50;
	}
	
	public int getNorth(){
		return laneNorth;
	}
	
	public int getNSouth(){
		return laneSouth;
	}
	
	public int getEast(){
		return laneEast;
	}
	
	public int getWest(){
		return laneWest;
	}
	
	private class LaneIncrement extends Thread{
		//variables
		@Override
		public void run(){
			Random rand = new Random();
			runningInc = true;
			
			while(System.currentTimeMillis() - startTime < 60000){
				//calculate whether a car joins a lane, and pick which lane
				int lanePick = rand.nextInt((3) + 1);
				int probToJoin = rand.nextInt((100) + 1);
				
				if (probToJoin >= prob){
					laneNumbers[lanePick]++;
					
				}
				if((System.currentTimeMillis() - startTime)%500 <= 10) System.out.println("N: " + laneNumbers[0] + " S: " + laneNumbers[1] + " E: " + laneNumbers[2] + " W: " + laneNumbers[3] + " " + (System.currentTimeMillis() - startTime));
				
				try {
					Thread.sleep(laneIncSleep);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			runningInc = false;
		}
		
	}
	
	private class LaneDecrement extends Thread{
		//variables
		@Override
		public void run(){
			runningDec = true;
			
			
			while(System.currentTimeMillis() - startTime < 60000){
				long loopStart = System.currentTimeMillis();
				while(System.currentTimeMillis() - loopStart < laneDecSleep){
					if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
					if(laneNumbers[1]>0) laneNumbers[1]--; //South Lane
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				loopStart = System.currentTimeMillis();
				
				while(System.currentTimeMillis() - loopStart < laneDecSleep){
					if(laneNumbers[2]>0) laneNumbers[2]--; //East Lane
					if(laneNumbers[3]>0) laneNumbers[3]--; //West Lane
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			runningDec = false;
		}
	}
	
}

