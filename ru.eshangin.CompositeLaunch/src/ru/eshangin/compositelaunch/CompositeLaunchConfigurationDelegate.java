package ru.eshangin.compositelaunch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class CompositeLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		System.out.println("Configurations to launch:");
		
		for (ILaunchConfiguration conf : Activator.getDefault().getSelectedConfigurations()) {
			
			System.out.println(conf.getName());
			
			conf.launch(mode, monitor);
			
		}		
	}

}
