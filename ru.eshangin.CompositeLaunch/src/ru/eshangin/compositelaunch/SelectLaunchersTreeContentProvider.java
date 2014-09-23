package ru.eshangin.compositelaunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

class SelectLaunchersTreeContentProvider {

	ILaunchConfigurationType fCompositeType;
	String fMode;
	
	public SelectLaunchersTreeContentProvider(String mode, ILaunchConfigurationType compositeType) {
		fMode = mode;
		fCompositeType = compositeType;
	}
	
	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * Returns a list containing the given types minus any types that
	 * should not be visible. A type should not be visible if it doesn't match
	 * the current mode, if it equals to Composite Type or it doesn't contain any configurations
	 * 
	 * @return the given types minus any types that should not be visible.
	 * @throws CoreException if an error occurs while retrieving a launch configuration 
	 */
	public List<ILaunchConfigurationType> getElements() throws CoreException {
		
		ILaunchConfigurationType[] allTypes = getLaunchManager().getLaunchConfigurationTypes();
		
		List<ILaunchConfigurationType> filteredTypes = new ArrayList<ILaunchConfigurationType>();
		
		for (ILaunchConfigurationType configurationType : allTypes) {
			
			// filter types by Composite type, ability to launch with current launch mode and isPublic flag
			if (configurationType != fCompositeType && configurationType.supportsMode(fMode) && 
					configurationType.isPublic()) {
		        
		        // we will return only configuration types which contain configurations
		        if (!Arrays.asList(getLaunchManager().getLaunchConfigurations(configurationType)).isEmpty()) {
		        	
		        	filteredTypes.add(configurationType);
		        }
			}
						
		}

		return filteredTypes;
	}

}
