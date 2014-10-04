package ru.eshangin.compositelaunch.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import ru.eshangin.compositelaunch.Activator;

/**
 * Tab group for composite launch configuration
 */
public class CompositeLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new CommonTab() }; 
		setTabs(tabs);
		
		Activator.getDefault().setCurrentMode(mode);
	}

}
