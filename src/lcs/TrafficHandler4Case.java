package lcs;

import java.util.Arrays;
import java.util.Random;

/**
 * Class to handle the traffic for the case of 4 intersections by combining 4 TrafficHandlers
 * @author Dom
 *
 */
public class TrafficHandler4Case {
	public TrafficHandler4Case(){}
	
	LaneIncrement1 lInc1 = new LaneIncrement1();
	LaneIncrement2 lInc2 = new LaneIncrement2();
	LaneIncrement3 lInc3 = new LaneIncrement3();
	LaneIncrement4 lInc4 = new LaneIncrement4();
	
	LaneDecrement1 lDec1 = new LaneDecrement1();
	LaneDecrement2 lDec2 = new LaneDecrement2();
	LaneDecrement3 lDec3 = new LaneDecrement3();
	LaneDecrement4 lDec4 = new LaneDecrement4();
	
	boolean runningInc1 = true;
	boolean runningInc2 = true;
	boolean runningInc3 = true;
	boolean runningInc4 = true;
	
	boolean runningDec1 = true;
	boolean runningDec2 = true;
	boolean runningDec3 = true;
	boolean runningDec4 = true;
	
	int laneIncSleep = 250;
	int laneDecSleep = 15000;
	int timeThresh;
	
	long startTime;
	
	public int prob;
	
	int[] laneNumbers = new int[16];
	
	int state1;
	int state2;
	int state3;
	int state4;
	
	//8 sources of randomness
		//N&W for traffic1
		//N&E for traffic2
		//S&W for traffic3
		//S&E for traffic4
	//all other sources of traffic at intersections come from other intersections
	//cars only exit the system at these same points

	public void runTraffic(){
		
		startTime = System.currentTimeMillis();
		
		lInc1.start();
		lInc2.start();
		lInc3.start();
		lInc4.start();
		
		lDec1.start();
		lDec2.start();
		lDec3.start();
		lDec4.start();
		
		if(!runningInc1) lInc1.interrupt();
		if(!runningInc2) lInc2.interrupt();
		if(!runningInc3) lInc3.interrupt();
		if(!runningInc4) lInc4.interrupt();
		
		if(!runningDec1) lDec1.interrupt();
		if(!runningDec2) lDec2.interrupt();
		if(!runningDec3) lDec3.interrupt();
		if(!runningDec4) lDec4.interrupt();
		
		
	}
	
	public void highTraffic(int lane){
		prob = 10;
	}
	
	public void medTraffic(int lane){
		prob = 30;
	}
	
	public void lowTraffic(int lane){
		prob = 50;
	}
	
	public int getLaneNumbers(int light, int lane){
		int position = light*4 + lane;
		return laneNumbers[position];
	}
	
	public void changeState(int light, int state){
		if(light == 0) state1 = state;
		else if(light == 1) state2 = state;
		else if (light == 2) state3 = state;
		else if (light ==3) state4 = state;
		else{
			//chuck an error
		}
	}
	//PRIVATE CLASSES BELOW FOR EACH TRAFFIC LIGHT
	private class LaneIncrement1 extends Thread{
		//variables
		int[] ignoreLanes = {1,2};
		@Override
		public void run(){
			runningInc1 = true;
			
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
			
			runningInc1 = false;
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
	
	private class LaneDecrement1 extends Thread{
		//variables
		@Override
		public void run(){
			runningDec1 = true;
			
			loop(720000);
			
			
			runningDec1 = false;
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
				
				if(state1 == 0){
					//North & South go, East & West stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
						if(laneNumbers[1]>0){
							laneNumbers[1]--; //South Lane
							laneNumbers[8]++;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(state1 == 1){
					//East & West go, North & South stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[2]>0){
							laneNumbers[2]--; //East Lane
							laneNumbers[7]++;
						}
						if(laneNumbers[3]>0) laneNumbers[3]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state1 == 2){
					//North & South turn right, East & West turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[0]>0) laneNumbers[0]--; //North Lane
						if(laneNumbers[1]>0){
							laneNumbers[1]--; //South Lane
							laneNumbers[7]++;
						}
						if(laneNumbers[2]>0){
							laneNumbers[2]--; //East Lane
							laneNumbers[8]++;
						}
						if(laneNumbers[3]>0) laneNumbers[3]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state1 == 3){
					//East & West turn right, North & South turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[0]>0){
							laneNumbers[0]--; //North Lane
							laneNumbers[7]++;
						}
						if(laneNumbers[1]>0) laneNumbers[1]--; //South Lane
						if(laneNumbers[2]>0) laneNumbers[2]--; //East Lane
						if(laneNumbers[3]>0){
							laneNumbers[3]--; //West Lane
							laneNumbers[8]++;
						}
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
	private class LaneIncrement2 extends Thread{
		//variables
		int[] ignoreLanes = {1,3};
		@Override
		public void run(){
			runningInc2 = true;
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
			
			runningInc2 = false;
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
					laneNumbers[4 + lanePick]++;
					
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
	
	private class LaneDecrement2 extends Thread{
		//variables
		@Override
		public void run(){
			runningDec2 = true;
			
			loop(720000);
			
			
			runningDec2 = false;
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
				
				if(state2 == 0){
					//North & South go, East & West stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[4]>0) laneNumbers[4]--; //North Lane
						if(laneNumbers[5]>0){
							laneNumbers[5]--; //South Lane
							laneNumbers[12]++;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(state2 == 1){
					//East & West go, North & South stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[6]>0) laneNumbers[6]--; //East Lane
						if(laneNumbers[7]>0){
							laneNumbers[7]--; //West Lane
							laneNumbers[2]++;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state2 == 2){
					//North & South turn right, East & West turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[4]>0){
							laneNumbers[4]--; //North Lane
							laneNumbers[2]++;
						}
						if(laneNumbers[5]>0) laneNumbers[5]--; //South Lane
						if(laneNumbers[6]>0){
							laneNumbers[6]--; //East Lane
							laneNumbers[12]++;
						}
						if(laneNumbers[7]>0) laneNumbers[7]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state2 == 3){
					//East & West turn right, North & South turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[4]>0) laneNumbers[4]--; //North Lane
						if(laneNumbers[5]>0){
							laneNumbers[5]--; //South Lane
							laneNumbers[2]++;
						}
						if(laneNumbers[6]>0) laneNumbers[6]--; //East Lane
						if(laneNumbers[7]>0){
							laneNumbers[7]--; //West Lane
							laneNumbers[12]++;
						}
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
	private class LaneIncrement3 extends Thread{
		//variables
		int[] ignoreLanes = {0,2};
		@Override
		public void run(){
			runningInc3 = true;
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
			
			runningInc3 = false;
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
					laneNumbers[8 + lanePick]++;
					
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
	
	private class LaneDecrement3 extends Thread{
		//variables
		@Override
		public void run(){
			runningDec3 = true;
			
			loop(720000);
			
			
			runningDec3 = false;
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
				
				if(state3 == 0){
					//North & South go, East & West stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[8]>0){
							laneNumbers[8]--; //North Lane
							laneNumbers[1]++;
						}
						if(laneNumbers[9]>0) laneNumbers[9]--; //South Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(state3 == 1){
					//East & West go, North & South stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[10]>0){
							laneNumbers[10]--; //East Lane
							laneNumbers[15]++;
						}
						if(laneNumbers[11]>0) laneNumbers[11]--; //West Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state3 == 2){
					//North & South turn right, East & West turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[8]>0) laneNumbers[8]--; //North Lane
						if(laneNumbers[9]>0){
							laneNumbers[9]--; //South Lane
							laneNumbers[15]++;
						}
						if(laneNumbers[10]>0) laneNumbers[10]--; //East Lane
						if(laneNumbers[11]>0){
							laneNumbers[11]--; //West Lane
							laneNumbers[1]++;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state3 == 3){
					//East & West turn right, North & South turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[8]>0){
							laneNumbers[8]--; //North Lane
							laneNumbers[15]++;
						}
						if(laneNumbers[9]>0) laneNumbers[9]--; //South Lane
						if(laneNumbers[10]>0){
							laneNumbers[10]--; //East Lane
							laneNumbers[1]++;
						}
						if(laneNumbers[11]>0) laneNumbers[11]--; //West Lane
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
	private class LaneIncrement4 extends Thread{
		//variables
		int[] ignoreLanes = {0,3};
		@Override
		public void run(){
			runningInc4 = true;
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
			
			runningInc4 = false;
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
					laneNumbers[12 + lanePick]++;
					
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
	
	private class LaneDecrement4 extends Thread{
		//variables
		@Override
		public void run(){
			runningDec4 = true;
			
			loop(720000);
			
			
			runningDec4 = false;
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
				
				if(state4 == 0){
					//North & South go, East & West stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[12]>0){
							laneNumbers[12]--; //North Lane
							laneNumbers[5]++;
						}
						if(laneNumbers[13]>0) laneNumbers[13]--; //South Lane
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(state4 == 1){
					//East & West go, North & South stop
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[14]>0) laneNumbers[14]--; //East Lane
						if(laneNumbers[15]>0){
							laneNumbers[15]--; //West Lane
							laneNumbers[10]++;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state4 == 2){
					//North & South turn right, East & West turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[12]>0){
							laneNumbers[12]--; //North Lane
							laneNumbers[10]++;
						}
						if(laneNumbers[13]>0) laneNumbers[13]--; //South Lane
						if(laneNumbers[14]>0) laneNumbers[14]--; //East Lane
						if(laneNumbers[15]>0){
							laneNumbers[15]--; //West Lane
							laneNumbers[5]++;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if (state4 == 3){
					//East & West turn right, North & South turn left
					while(System.currentTimeMillis() - loopStart < laneDecSleep){
						if(laneNumbers[12]>0) laneNumbers[12]--; //North Lane
						if(laneNumbers[13]>0){
							laneNumbers[13]--; //South Lane
							laneNumbers[10]++;
						}
						if(laneNumbers[14]>0){
							laneNumbers[14]--; //East Lane
							laneNumbers[5]++;
						}
						if(laneNumbers[15]>0) laneNumbers[15]--; //West Lane
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
