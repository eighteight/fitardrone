package org.fitardrone.main;

import org.fitardrone.control.wii.WiimoteManager;

// TODO add a shutdown hook to stop the drone (emergency for instance)

public class Main {
	public static void main(String[] args) throws Exception {
		WiimoteManager 		wiimoteManager=new WiimoteManager();
		wiimoteManager.start();
	}
}
