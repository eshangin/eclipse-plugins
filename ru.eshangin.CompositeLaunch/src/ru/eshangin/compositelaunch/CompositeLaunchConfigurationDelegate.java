package ru.eshangin.compositelaunch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.ui.statushandlers.StatusManager;

import ru.eshangin.compositelaunch.internal.CompositeConfiguration;
import ru.eshangin.compositelaunch.internal.CompositeConfigurationItem;
import ru.eshangin.compositelaunch.internal.CompositeLaunchConfigurationConstants;

public class CompositeLaunchConfigurationDelegate implements ILaunchConfigurationDelegate2 {
	
	/**
	 * Launch selected launch configuration
	 * @throws CoreException if unable to launch
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		CompositeConfiguration compositeConfig = new CompositeConfiguration(configuration);
		
		for (CompositeConfigurationItem conf : compositeConfig.getCurrentItems()) {
			conf.toLaunchConfiguration().launch(mode, monitor);
		}		
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return false;
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return true;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		
		CompositeConfiguration compositeConfig = new CompositeConfiguration(configuration);
		
		Status errorStatus = null;
		
		for (CompositeConfigurationItem configItem : compositeConfig.getCurrentItems()) {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			
			// check that configuration type still exists
			ILaunchConfigurationType lType = manager.getLaunchConfigurationType(configItem.getLaunchConfigurationTypeId());
			if (lType != null) {
				
				// check that configuration still exists
				boolean configStillExist = false;
				ILaunchConfiguration[] existedConfigs = manager.getLaunchConfigurations(lType);
				for (ILaunchConfiguration config : existedConfigs) {
					if (config.getName().equals(configItem.getLaunchConfigurationName())) {
						configStillExist = true;
						break;
					}
				}
				if (!configStillExist) {
					String errorMessage = String.format(CompositeLaunchConfigurationConstants.MSG_TMPL_LAUNCH_CONFIG_WAS_DELETED_OR_REMOVED_COMPOSITE_LAUNCH_CANNOT_BE_CONNTINUED, 
							configItem.getLaunchConfigurationName());
					
					errorStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							CompositeLaunchConfigurationConstants.STATUSCODE_PRE_LAUNCH_CHECK_NO_CONFIG, errorMessage, null);
					break;
				}
			}
			else {
				String errorMessage = String.format(CompositeLaunchConfigurationConstants.LAUNCH_CONFIGURATION_TYPE_WAS_DELETED_CONFIG_CANNOT_BE_LAUNCHED, 
						configItem.getLaunchConfigurationTypeName(), configItem.getLaunchConfigurationName());
				
				errorStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						CompositeLaunchConfigurationConstants.STATUSCODE_PRE_LAUNCH_CHECK_NO_CONFIG_TYPE, errorMessage, null);
				break;
			}
		}
		
		if (errorStatus != null) {
			StatusManager.getManager().handle(errorStatus, StatusManager.BLOCK);
			
			return false;
		}
		else {
			return true;
		}
	}

}
