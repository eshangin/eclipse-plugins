package ru.eshangin.compositelaunch;

import java.util.ArrayList;

import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @author 1
 *
 */
class CompositeConfigurationManager {
	
	private static ArrayList<ILaunchConfiguration> selectedConfigurations = new ArrayList<ILaunchConfiguration>();
	
	private CompositeConfigurationManager() {
		
	}
	
	public static void addConfigurationToLaunch(ILaunchConfiguration configuration) {
		selectedConfigurations.add(configuration);
	}
	
	public static void removeConfigurationToLaunch(ILaunchConfiguration configuration) {
		selectedConfigurations.remove(configuration);
	}

	public static void clearLaunchConfigurations() {
		selectedConfigurations.clear();;
	}
	
	public static ArrayList<ILaunchConfiguration> getSelectedConfigurations() {
		return selectedConfigurations;
	}
}
