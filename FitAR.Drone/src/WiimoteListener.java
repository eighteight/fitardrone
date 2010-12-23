/**
 * @author Hugo Cordier
 */

import motej.event.CoreButtonEvent;
import motej.event.CoreButtonListener;


public class WiimoteListener implements CoreButtonListener {
	private Boolean aBtn = false;
	private Boolean oneBtn = false;
	private Boolean twoBtn = false;
	private Boolean upBtn = false;
	private Boolean downBtn = false;
	private Boolean leftBtn = false;
	private Boolean rightBtn = false;
	private Boolean noBtn = true;
	private Boolean homeBtn = false;
	
	private ARDrone drone;
	private Thread threadDrone;
	
	public WiimoteListener(ARDrone drone){
		this.drone=drone;
	}

	/**
	 * Method called when a button is pressed
	 * @param evt the button event
	 */
	public void buttonPressed(CoreButtonEvent evt) {
		aBtn = evt.isButtonAPressed();
		oneBtn = evt.isButtonOnePressed();
		twoBtn = evt.isButtonTwoPressed();
		upBtn = evt.isDPadUpPressed();
		downBtn = evt.isDPadDownPressed();
		leftBtn = evt.isDPadLeftPressed();
		rightBtn = evt.isDPadRightPressed();
		homeBtn = evt.isButtonHomePressed();
		if(evt.isNoButtonPressed()){
			drone.stopMovment();
		}
		if(!aBtn){
			if(noBtn){
				if(homeBtn){drone.emergency();}
				if(oneBtn){drone.takeoff();}else if(twoBtn){drone.land();}
				if(upBtn){drone.gazUp();
				}else if(downBtn){drone.gazDown();
				}if(leftBtn){drone.left();
				}else if(rightBtn){drone.right();}
			}
		}else{
			this.stopDrone();
		}
		noBtn=evt.isNoButtonPressed();
	}
	
	/**
	 * Make the drone land and the system exit
	 */
	private void stopDrone(){
		drone.land();
		System.out.println("Good bye !!");
		try{
		threadDrone.interrupt();
		}catch(Exception e){
			System.exit(2);
		}
		System.exit(1);
	}
	
}


