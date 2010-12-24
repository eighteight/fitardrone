package org.fitardrone.main;

/**
 * @author Hugo Cordier
 */


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ARDrone implements Runnable {
   
	private final int ALTTITUDE_MAX = 1;
	private final byte[] IP_ADDR={new Integer(192).byteValue(),new Integer(168).byteValue(),new Integer(1).byteValue(),new Integer(1).byteValue()};
	private DatagramSocket socket;
	private InetAddress inet_addr;
	
	private int seq = 1;
	private float pitch = 0, roll = 0, gaz = 0, yaw = 0;
    private float step = (float)0.7;
    
	private boolean flying=false;
	private boolean rollPitchAllowed = true;
	private boolean takeoffAllowed = false;	

	/**
	 * Setting up ARDrone.
	 * ARDronne class will communicate with the drone
	 */
    public ARDrone() throws Exception {
		inet_addr = InetAddress.getByAddress(IP_ADDR);
		socket = new DatagramSocket();
		socket.setSoTimeout(3000);	
		setMaxAltitude(ALTTITUDE_MAX);
    }
    
	/**
	 * Commands are sent every 20ms to the drone (see updateDrone()).
	 */
    public void run(){
    	while(!Thread.interrupted()){
    		try {
				Thread.sleep(20l);
				if(seq > Integer.MAX_VALUE-10)
					seq = 0;
				updateDrone();
			} catch (InterruptedException e) {
			} catch (Exception e) {
			}
    	}
    }
    
    /*Setters*/
	/**
	 * Sets boolean to allow the drone to take off
	 */
    public void setTakeoffAllowed(boolean takeoffAllowed) {
		this.takeoffAllowed = takeoffAllowed;
	}
	/**
	 * Sets boolean to allow roll and pitch movements.
	 * This can be use to set up a security button in order to avoid random movements with accelerometer or analog device
	 */
    public void setRollPitchAllowed(boolean rollPitchAllowed){
    	this.rollPitchAllowed=rollPitchAllowed;
    }

	/**
	 * Makes the drone going up
	 */
	public void gazUp() {this.gaz=step;}
	
	/**
	 * Makes the drone going down
	 */
	public void gazDown() {this.gaz=-step;}
	
	/**
	 * Makes the drone turning left
	 */
	public void left() {	this.yaw=-step;}
	
	/**
	 * Makes the drone turning right
	 */
	public void right() {this.yaw=step;}
	
	/**
	 * Makes the drone going ahead or back.
	 * @param pitch A float between -1 and 1
	 */
	public void setPitch(float pitch){
		if(rollPitchAllowed && pitch>-1 && pitch<1)
			this.pitch = pitch;
	}
	/**
	 * Makes the drone going right or left
	 * @param roll A float between -1 and 1
	 */
	public void setRoll(float roll){
		if(rollPitchAllowed && roll>-1 && roll<1)
			this.roll = roll;
	}
	/**
	 * Stops up/down and turn left/right movements
	 */
	public void stopMovment(){
		this.gaz=0;
		this.yaw=0;
	}
    
	/**
	 * Asks the drone to send video from video port (5555)
	 */
	public void initVideo(){
		try {
			send_to_video("AT*ZAP="+ (seq++) + ",0");
		} catch (Exception e) {
			System.out.println("Can't ask for video stream");
		}
	}
	
	/**
	 * Makes the drone taking off    
	 */
    public void takeoff(){
    	try {
    		if(!flying && takeoffAllowed){
    			send_at_cmd("AT*REF=" + (seq++) + ",290718208");
    			flying=true;
    			System.out.println("Taking off");
    		}
		} catch (Exception e) {
			System.err.println("Can't take off !");
		}
    	
    }
    
    /**
     * Makes the drone landing
     */
    public void land(){
    	try {
    		if(flying){
    			send_at_cmd("AT*REF=" + (seq++) + ",290717696");
    			flying=false;
    			System.out.println("Landing");
    		}
		} catch (Exception e) {
			System.err.println("Can't land !");
		}
    }
    
    /**
     * Setting drone's max altitude for the flight
     * @param maxAlt The maximum altitude
     */
    public void setMaxAltitude(int maxAlt) {
		try {
			send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\""+(maxAlt*1000)+"\"\"");
			System.out.println("Setting max altitude = "+maxAlt);
		} catch (Exception e) {
			System.err.println("Unable to set maximum altitude !");
		}
    }

    /**
     * Send updated information to the drone
     */
    private void updateDrone() {
    	try {
    		send_at_cmd( "AT*PCMD=" + (seq++) + ",1," 
    			+ Float.floatToIntBits(pitch) + "," 
    			+ Float.floatToIntBits(roll) + "," 
    			+ Float.floatToIntBits(gaz) + "," 
    			+ Float.floatToIntBits(yaw));
			send_at_cmd("AT*COMWDG="+(seq++) );
		} catch (Exception e) {
			System.err.println("Unable to send information to drone ! Check connection");
		}
    }
    
    /*UDP connection manager*/
    /**
     * Send the AT command to the drone
     * @param at_cmd the command to send to the drone
     * @throws An exception if packet can't be sent (unable to find a network for exemple)
     */
    private void send_at_cmd(String at_cmd) throws Exception {
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5556);
		socket.send(packet);
    }
    
    /**
     * Send a command to video port 
     * @param at_cmd
     * @throws Exception
     */
    private void send_to_video(String at_cmd) throws Exception {
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5555);
		socket.send(packet);
    }

    /**
     * Sends an emergency packet. 
     * If the drone is already on emergency, it will turn it on the normal mode.
     * If not, it will stop the engines.
     */
	public void emergency() {
		try {
			send_at_cmd("AT*REF=" + (seq++) + ",290717952");
		} catch (Exception e) {
			System.err.println("Can't send emergency command, and that's not cool at all");
		}		
	}
}
