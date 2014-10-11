package lcs;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles a singular traffic intersection and its lights, using a series of states and a traffic generator. This will then be
 * expanded into a four intersection system for further analysis of the LCS algorithm
 * @author Dom
 *
 */
public class TrafficHandler {
	
	public TrafficHandler(){}
	
	public int laneNorth;
	public int laneSouth;
	public int laneEast;
	public int laneWest;
	
	public int[] laneNumbers = new int[4]; //0 North, 1 South, 2 East, 3 West
	
	public int[] ignoreLanes = {4,5};
	public int[] flagLanes = new int[4];
	
	LaneIncrement laneInc = new LaneIncrement();
	LaneDecrement laneDec = new LaneDecrement();
	
	int laneIncSleep = 250;
	int laneDecSleep = 15000;
	int timeThresh;
	
	long startTime;
	
	boolean runningInc;
	boolean runningDec;
	
	public int prob;
	
	public int state;
	
	public void trafficLoop(){
		//implement main algorithm here
		//set traffic mode
		//highTraffic(1);
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
		laneNorth = laneNumbers[0];
		return laneNorth;
	}
	
	public int getNSouth(){
		laneSouth = laneNumbers[1];
		return laneSouth;
	}
	
	public int getEast(){
		laneEast = laneNumbers[2];
		return laneEast;
	}
	
	public int getWest(){
		laneWest = laneNumbers[3];
		return laneWest;
	}
	
	public void ignore(int first, int second){
		ignoreLanes[0] = first;
		ignoreLanes[1] = second;
	}
	
	
	
	private class LaneIncrement extends Thread{
		//variables
		@Override
		public void run(){
			runningInc = true;
			//loop(60000);
			//start at 12am -- 1 hour to 1 minute mapping
			//12am to 5am - low
			startTime = System.currentTimeMillis();
			lowTraffic(1);
			loop(150000);
			//5am to 7am - medium
			startTime = System.currentTimeMillis();
			medTraffic(1);
			loop(60000);
			//7am to 10am - high
			startTime = System.currentTimeMillis();
			highTraffic(1);
			loop(90000);
			//10am to 4pm - medium
			startTime = System.currentTimeMillis();
			medTraffic(1);
			loop(180000);
			//4pm to 6pm - high
			startTime = System.currentTimeMillis();
			highTraffic(1);
			loop(60000);
			//6pm to 9pm - medium
			startTime = System.currentTimeMillis();
			medTraffic(1);
			loop(90000);
			//9pm to 12am - low
			startTime = System.currentTimeMillis();
			lowTraffic(1);
			loop(90000);
			
			runningInc = false;
		}
		
		public void loop(int time){
			Random rand = new Random();
			while(System.currentTimeMillis() - startTime < time){
				//calculate whether a car joins a lane, and pick which lane
				int lanePick = rand.nextInt((3) + 1);
				while(Arrays.asList(ignoreLanes).contains(lanePick)){ //skip lanes that do not have random cars added to them
					lanePick = rand.nextInt((3) + 1);
				}
				int probToJoin = rand.nextInt((100) + 1);
				
				if (probToJoin >= prob){
					laneNumbers[lanePick]++;
					
				}
				//if((System.currentTimeMillis() - startTime)%500 <= 50) System.out.println("N: " + laneNumbers[0] + " S: " + laneNumbers[1] + " E: " + laneNumbers[2] + " W: " + laneNumbers[3] + " " + (System.currentTimeMillis() - startTime));
				
				try {
					Thread.sleep(laneIncSleep);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class LaneDecrement extends Thread{
		//variables
		@Override
		public void run(){
			runningDec = true;
			
			loop(720000);
			
			
			runningDec = false;
		}
		
		public void loop(int time){
			while(System.currentTimeMillis() - startTime < time){
				long loopStart = System.currentTimeMillis();
				//simple cyclic traffic light loop
				/*
				while(System.currentTimeMillis() - loopStart < laneDecSleep){
					if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
					if(laneNumbers[1]>0) laneNumbers[1]--; //South Lane
					try {
						Thread.sleep(500);
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
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/
				
				if(state == 0){
					//North & South go, East & West stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
						if(laneNumbers[1]>0) laneNumbers[1]--; //South Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(state == 1){
					//East & West go, North & South stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[2]>0) laneNumbers[2]--; //East Lane
						if(laneNumbers[3]>0) laneNumbers[3]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state == 2){
					//North & South turn right, East & West turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
						if(laneNumbers[1]>0) laneNumbers[1]--; //South Lane
						if(laneNumbers[2]>0) laneNumbers[2]--; //East Lane
						if(laneNumbers[3]>0) laneNumbers[3]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state == 3){
					//East & West turn right, North & South turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
						if(laneNumbers[1]>0) laneNumbers[1]--; //South Lane
						if(laneNumbers[2]>0) laneNumbers[2]--; //East Lane
						if(laneNumbers[3]>0) laneNumbers[3]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else{
					//error - no more states to consider
				}
			}
		}
	}
	
}

