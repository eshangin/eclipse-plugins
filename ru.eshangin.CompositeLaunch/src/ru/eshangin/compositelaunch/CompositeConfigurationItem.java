package ru.eshangin.compositelaunch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

/**
 * Composite configuration item is an item of composite launch configuration
 * which will be saved in configuration attributes
 */
class CompositeConfigurationItem {
	
	private String fLaunchConfigurationName;
	private String fLaunchConfigurationTypeId;
	
	public CompositeConfigurationItem(String launchConfigurationName, String launchConfigurationTypeId) {
		setfLaunchConfigurationName(launchConfigurationName);
		setfLaunchConfigurationTypeId(launchConfigurationTypeId);
	}
	
	public CompositeConfigurationItem(ILaunchConfiguration launchConfiguration) {
		setfLaunchConfigurationName(launchConfiguration.getName());
		try {
			setfLaunchConfigurationTypeId(launchConfiguration.getType().getIdentifier());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getfLaunchConfigurationName() {
		return fLaunchConfigurationName;
	}

	public void setfLaunchConfigurationName(String fLaunchConfigurationName) {
		this.fLaunchConfigurationName = fLaunchConfigurationName;
	}

	public String getfLaunchConfigurationTypeId() {
		return fLaunchConfigurationTypeId;
	}

	public void setfLaunchConfigurationTypeId(String fLaunchConfigurationTypeId) {
		this.fLaunchConfigurationTypeId = fLaunchConfigurationTypeId;
	}
	
	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();		
	}
	
	public ILaunchConfiguration toLaunchConfiguration() {
		ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(fLaunchConfigurationTypeId);
	    
	    try {
			ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
			
			for (ILaunchConfiguration config : configs) {
				if (config.getName().equals(fLaunchConfigurationName)) {
					return config;
				}
			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return null;
	}
}
