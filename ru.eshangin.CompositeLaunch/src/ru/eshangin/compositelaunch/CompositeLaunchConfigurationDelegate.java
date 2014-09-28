package ru.eshangin.compositelaunch;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class CompositeLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		ArrayList<CompositeConfigurationItem> deserizliedConfigs = JsonConfigurationHelper.fromJson(
				configuration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, ""));
		
		System.out.println("Configurations to launch:");
		
		for (CompositeConfigurationItem conf : deserizliedConfigs) {
			
			System.out.println(conf.getfLaunchConfigurationName());
			
			conf.toLaunchConfiguration().launch(mode, monitor);
		}		
	}

}
