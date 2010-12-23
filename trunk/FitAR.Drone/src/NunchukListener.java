/**
 * @author Hugo Cordier
 */

import java.awt.Point;

import motejx.extensions.nunchuk.AnalogStickEvent;
import motejx.extensions.nunchuk.AnalogStickListener;


public class NunchukListener implements AnalogStickListener {

	private ARDrone drone;
	private Point point;
	
	public NunchukListener(ARDrone drone){
		this.drone=drone;
	}

	/**
	 * Method called when stick is moving
	 * @param evt The movement event
	 */
	@Override
	public void analogStickChanged(AnalogStickEvent evt) {
		Point p = evt.getPoint();
		if (this.point==null){
			this.point=p;
		}
		drone.setPitch(-(float)(point.getX()-p.getX())/100);
		drone.setRoll((float)(point.getY()-p.getY())/100);
	}
}