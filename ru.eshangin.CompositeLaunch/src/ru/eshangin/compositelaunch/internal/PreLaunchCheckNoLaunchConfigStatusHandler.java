/**
 * 
 */
package ru.eshangin.compositelaunch.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Handles statuses of preLaunchCheck of Launch Delegate
 */
public class PreLaunchCheckNoLaunchConfigStatusHandler implements IStatusHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IStatusHandler#handleStatus(org.eclipse.core.runtime.IStatus, java.lang.Object)
	 */
	@Override
	public Object handleStatus(IStatus status, Object source)
			throws CoreException {
		
		String errorMessage = null;
		CompositeConfigurationItem failedConfigItem = (CompositeConfigurationItem) source;
		
		switch (status.getCode()) {
		
		case CompositeLaunchConfigurationConstants.STATUSCODE_PRE_LAUNCH_CHECK_NO_CONFIG:
			errorMessage = String.format(CompositeLaunchConfigurationConstants.MSG_TMPL_LAUNCH_CONFIG_WAS_DELETED_OR_REMOVED_COMPOSITE_LAUNCH_CANNOT_BE_CONNTINUED, 
					failedConfigItem.getLaunchConfigurationName());
			break;
			
		case CompositeLaunchConfigurationConstants.STATUSCODE_PRE_LAUNCH_CHECK_NO_CONFIG_TYPE:
			errorMessage = String.format(CompositeLaunchConfigurationConstants.LAUNCH_CONFIGURATION_TYPE_WAS_DELETED_CONFIG_CANNOT_BE_LAUNCHED, 
					failedConfigItem.getLaunchConfigurationTypeName(), failedConfigItem.getLaunchConfigurationName());
			break;
			
		default:
			errorMessage = "Invalid status code";
			break;
		}
		
		MessageDialog.openError(null, "Problem", errorMessage);
		
		return null;
	}

}
