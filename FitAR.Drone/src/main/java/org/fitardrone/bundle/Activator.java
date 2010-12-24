/**
 * 
 */
package org.fitardrone.bundle;

import org.fitardrone.control.wii.WiimoteManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Didier Donsez
 *
 */
public class Activator implements BundleActivator {

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		new WiimoteManager();
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
