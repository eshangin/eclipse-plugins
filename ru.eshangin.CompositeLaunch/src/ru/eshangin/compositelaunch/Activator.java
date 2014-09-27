package ru.eshangin.compositelaunch;

import java.util.ArrayList;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ru.eshangin.CompositeLaunch"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private String launchMode;
	
	private ArrayList<ILaunchConfiguration> selectedConfigurations = new ArrayList<ILaunchConfiguration>();
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void setCurrentMode(String launchMode) {
		this.launchMode = launchMode;		
	}
	
	public String getCurrentMode() {
		return launchMode;
	}

	public void addConfigurationToLaunch(ILaunchConfiguration configuration) {
		selectedConfigurations.add(configuration);
	}
	
	public void removeConfigurationToLaunch(ILaunchConfiguration configuration) {
		selectedConfigurations.remove(configuration);
	}

	public void clearLaunchConfigurations() {
		selectedConfigurations.clear();;
	}
	
	public ArrayList<ILaunchConfiguration> getSelectedConfigurations() {
		return selectedConfigurations;
	}
}
