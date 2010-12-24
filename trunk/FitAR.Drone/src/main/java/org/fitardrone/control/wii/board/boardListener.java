package org.fitardrone.control.wii.board;
/**
 * @author Hugo Cordier
 */

import org.fitardrone.main.ARDrone;

import motejx.extensions.balanceboard.BalanceBoardEvent;
import motejx.extensions.balanceboard.BalanceBoardListener;


public class boardListener implements BalanceBoardListener{

	private int bl;
	private int br;
	private int tl;
	private int tr;
	private boolean youShouldAjustIt=true;
	private ARDrone drone;
	
	public boardListener(ARDrone drone){
		this.drone=drone;
	}
	
	public void setYouShouldAjustIt(boolean youShouldAjustIt) {
		this.youShouldAjustIt = youShouldAjustIt;
	}
	
	public void balanceBoardChanged(BalanceBoardEvent evt) {
		if(youShouldAjustIt){
			this.adjust(evt);
			youShouldAjustIt=false;
		}

		int bt =evt.getBottomLeft()-bl+evt.getBottomRight()-br-evt.getTopLeft()-tl+	evt.getTopRight()-tr;
		int lr =evt.getBottomLeft()-bl+evt.getTopLeft()-tl-evt.getBottomRight()-br+	evt.getTopRight()-tr;

		drone.setPitch(bt);
		drone.setRoll(lr);
	}

	public void adjust(BalanceBoardEvent evt){
		bl=evt.getBottomLeft();
		br=evt.getBottomRight();
		tl=evt.getTopLeft();
		tr=evt.getTopRight();
	}

}
