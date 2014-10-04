package ru.eshangin.compositelaunch.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Composite configuration item is an item of composite launch configuration
 * which will be saved in configuration attributes
 */
public class CompositeConfigurationItem {
	
	// The name of Launch Configuration related to this item
	private String fLaunchConfigurationName;
	
	// The Id of Launch Configuration Type related to this item
	private String fLaunchConfigurationTypeId;
	
	// The name of Launch Configuration Type related to this item
	private String fLaunchConfigurationTypeName;
	
	public CompositeConfigurationItem(ILaunchConfiguration launchConfiguration) {
		setLaunchConfigurationName(launchConfiguration.getName());
		try {
			setLaunchConfigurationTypeId(launchConfiguration.getType().getIdentifier());
			setLaunchConfigurationTypeName(launchConfiguration.getType().getName());
		} catch (CoreException e) {
			e.printStackTrace();
			
			StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
		}
	}

	public String getLaunchConfigurationName() {
		return fLaunchConfigurationName;
	}

	public void setLaunchConfigurationName(String fLaunchConfigurationName) {
		this.fLaunchConfigurationName = fLaunchConfigurationName;
	}

	public String getLaunchConfigurationTypeId() {
		return fLaunchConfigurationTypeId;
	}

	public void setLaunchConfigurationTypeId(String fLaunchConfigurationTypeId) {
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

	public String getLaunchConfigurationTypeName() {
		return fLaunchConfigurationTypeName;
	}

	public void setLaunchConfigurationTypeName(
			String fLaunchConfigurationTypeName) {
		this.fLaunchConfigurationTypeName = fLaunchConfigurationTypeName;
	}
}
