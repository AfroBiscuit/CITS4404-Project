package lcs;

/**
 * Class to handle the traffic for the case of 4 intersections by combining 4 TrafficHandlers
 * @author Dom
 *
 */
public class TrafficHandler4Case {
	public TrafficHandler4Case(){}
	
	TrafficHandler traffic1 = new TrafficHandler(); //top left
	TrafficHandler traffic2 = new TrafficHandler(); //top right
	TrafficHandler traffic3 = new TrafficHandler(); //bottom left
	TrafficHandler traffic4 = new TrafficHandler(); //bottom right
	
	Traffic1Handle t1Handle = new Traffic1Handle();
	Traffic2Handle t2Handle = new Traffic2Handle();
	Traffic3Handle t3Handle = new Traffic3Handle();
	Traffic4Handle t4Handle = new Traffic4Handle();
	
	//8 sources of randomness
		//N&W for traffic1
		//N&E for traffic2
		//S&W for traffic3
		//S&E for traffic4
	//all other sources of traffic at intersections come from other intersections
	//cars only exit the system at these same points
	public void setup(){
		traffic1.ignore(1, 2);
		traffic2.ignore(1, 3);
		traffic3.ignore(0, 2);
		traffic4.ignore(0, 3);
	}
	
	public void runTraffic(){
		t1Handle.start();
		t2Handle.start();
		t3Handle.start();
		t4Handle.start();
		
		
		
	}
	
	private class Traffic1Handle extends Thread{
		//variables
		@Override
		public void run(){
			traffic1.trafficLoop();
		}
	}
	private class Traffic2Handle extends Thread{
		//variables
		@Override
		public void run(){
			traffic2.trafficLoop();
		}
	}
	private class Traffic3Handle extends Thread{
		//variables
		@Override
		public void run(){
			traffic3.trafficLoop();
		}
	}
	private class Traffic4Handle extends Thread{
		//variables
		@Override
		public void run(){
			traffic4.trafficLoop();
		}
	}
}
