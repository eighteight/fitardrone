package org.fitardrone.control.wii;

/**
 * @author Hugo Cordier
 */



import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;
import motej.event.ExtensionEvent;
import motej.event.ExtensionListener;
import motej.request.ReportModeRequest;
import motejx.extensions.balanceboard.BalanceBoard;
import motejx.extensions.nunchuk.Nunchuk;

import org.fitardrone.command.ARDrone;
import org.fitardrone.control.wii.board.BoardListener;
import org.fitardrone.control.wii.nunchuk.NunchukListener;
import org.fitardrone.control.wii.wiimote.WiimoteListener;

public class WiimoteManager implements MoteFinderListener,ExtensionListener{

	private Mote wiimote;
	private Nunchuk nunchuk;
	private BalanceBoard balanceboard;
	private MoteFinder finder;
	private Object lock = new Object();
	private ARDrone drone;
	
	/**
	 * Set up connection with the wiimote and the nunchuk.
	 */
	public WiimoteManager(){
	}
	
	public void start()throws Exception {
		/*Searching for a wiimote*/
		System.out.println("Searching for a wiimote..." +
				"\nPress 1 and 2 simultaneously." +
				"\nThis wil take about 10s");
		this.finder = MoteFinder.getMoteFinder();
		this.finder.addMoteFinderListener(this);
		this.finder.startDiscovery();
		try {
			synchronized(lock) {
				lock.wait();
			}
		} catch (InterruptedException ex) {
			System.err.println("Can't find wiimote");
		}
		/*Setting up wiimote config*/
		try {
			this.drone = new ARDrone();
			System.out.println("Let's fly !!");
			this.wiimote.addCoreButtonListener(new WiimoteListener(this.drone));
			this.drone.start();
			System.out.println("Please connect a nunchuk to your wiimote")	;
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Can't initiate drone");
		}
	}

	
	public void stop() throws Exception{
		// TODO remove listeners and others
		this.drone.stop();
	}
	/**
	 * Function called when a mote has been found.
	 * @param mote The wiimote which have just be found
	 */
	public void moteFound(Mote mote) {
		this.finder.stopDiscovery();
		this.wiimote = mote;
		this.wiimote.setPlayerLeds(new boolean[] {true, false, false, true} );
		mote.addExtensionListener(this);
		synchronized(lock) {
			lock.notifyAll();
		}
	}

	/**
	 * Method called when an extension is connected to the wiimote.
	 * If this is a nunchuk, user is informed and ardrone is allowed to take off.
	 * @param evt The connection event
	 */
	public void extensionConnected(ExtensionEvent evt) {
		if (evt.getExtension() instanceof Nunchuk) {
			System.out.println("Nunchuk found");
			this.wiimote.setPlayerLeds(new boolean[] {true, true, false, true} );
			this.nunchuk = (Nunchuk) evt.getExtension();
			this.nunchuk.addAnalogStickListener(new NunchukListener(this.drone));
			this.wiimote.setReportMode(ReportModeRequest.DATA_REPORT_0x32);
			drone.setTakeoffAllowed(true);
		}else
		if (evt.getExtension() instanceof BalanceBoard) {
			System.out.println("BalanceBoard found");
			this.wiimote.setPlayerLeds(new boolean[] {true, true, true, true} );
			this.balanceboard = (BalanceBoard) evt.getExtension();
			this.balanceboard.addBalanceBoardListener(new BoardListener(this.drone));
			this.wiimote.setReportMode(ReportModeRequest.DATA_REPORT_0x32);
			drone.setTakeoffAllowed(true);
		}
	}

	/**
	 * Method called when an extension is disconnected.
	 * Ardrone is stopped and will not be allowed to take off until a new nunchuk is connected
	 * @param evt The disconnection event
	 */
	public void extensionDisconnected(ExtensionEvent evt) {
		if (evt.getExtension() instanceof BalanceBoard) {
			System.out.println("BalanceBoard disconnected");
		}else if (evt.getExtension() instanceof Nunchuk) {
			System.out.println("Nunchuk disconnected");
		}
		this.wiimote.setPlayerLeds(new boolean[] {true, false, false, true} );
		drone.land();
		drone.setTakeoffAllowed(false);
	}
}
