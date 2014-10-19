package lcs;


public class TrafficLightAction implements Action {

	public void performAction(Object o) {
		if(o instanceof TrafficHandler) {
			TrafficHandler th = (TrafficHandler) o;
			//Do stuff with the lights here	
		}
		else
		{
			System.out.println("Invalid Object: Object needs to be an instance of TrafficHandler");
		}
		
	}
	
	public String getBitRepresentation() {
		return "11"; //4
	}
	
}
