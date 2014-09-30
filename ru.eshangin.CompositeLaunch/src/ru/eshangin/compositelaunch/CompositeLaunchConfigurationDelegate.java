package ru.eshangin.compositelaunch;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class CompositeLaunchConfigurationDelegate implements ILaunchConfigurationDelegate2 {
		
	private String fErrorMessage;
	
	/**
	 * Launch selected launch configuration
	 * @throws CoreException if unable to launch
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		ArrayList<CompositeConfigurationItem> deserizliedConfigs = JsonConfigurationHelper.fromJson(
				configuration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, ""));
		
		for (CompositeConfigurationItem conf : deserizliedConfigs) {
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
		
		ArrayList<CompositeConfigurationItem> deserizliedConfigs = JsonConfigurationHelper.fromJson(
				configuration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, ""));
		
		fErrorMessage = null;
		
		for (CompositeConfigurationItem configItem : deserizliedConfigs) {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			
			// check that configuration type still exists
			ILaunchConfigurationType lType = manager.getLaunchConfigurationType(configItem.getfLaunchConfigurationTypeId());
			if (lType != null) {
				
				// check that configuration still exists
				boolean configStillExist = false;
				ILaunchConfiguration[] existedConfigs = manager.getLaunchConfigurations(lType);
				for (ILaunchConfiguration config : existedConfigs) {
					if (config.getName().equals(configItem.getfLaunchConfigurationName())) {
						configStillExist = true;
						break;
					}
				}
				if (!configStillExist) {
					fErrorMessage = "Launch configuration " + configItem.getfLaunchConfigurationName() + " was deleted or removed." +
							" Composite launch can not be continued.";
					break;
				}
			}
			else {
				fErrorMessage = "Launch configuration type was deleted. " +
					configItem.getfLaunchConfigurationName() + " can't be launched.";
				break;
			}
		}
		
		if (fErrorMessage != null) {
			Display.getDefault().syncExec(new Runnable() {
			    public void run() {
					MessageDialog.openError(DebugUIPlugin.getShell(), 
						"Problem",
						fErrorMessage);
			    }
			});		
			
			return false;
		}
		else {
			return true;
		}
	}

}
