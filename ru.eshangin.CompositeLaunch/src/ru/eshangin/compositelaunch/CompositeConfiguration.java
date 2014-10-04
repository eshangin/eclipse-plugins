package ru.eshangin.compositelaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

class CompositeConfiguration {
	
	private static ILaunchConfiguration fConfiguration;
	
	public CompositeConfiguration(ILaunchConfiguration configuration) {
		fConfiguration = configuration;
	}
	
	/**
	 * Returns list of current Launch Configurations selected to run in composite including removed items if any
	 */
	public List<CompositeConfigurationItem> getCurrentItems() throws CoreException {
		return JsonConfigurationHelper.fromJson(
				fConfiguration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, ""));
	}

	public List<CompositeConfigurationItem> getRemovedItems() throws CoreException {
		
		List<CompositeConfigurationItem> removedItems = new ArrayList<CompositeConfigurationItem>();
		
		List<CompositeConfigurationItem> currentItems = getCurrentItems();

		for (CompositeConfigurationItem configItem : currentItems) {
			ILaunchConfiguration convertedConfig = configItem.toLaunchConfiguration();
			if (convertedConfig == null) {
				removedItems.add(configItem);
			}
		}
		
		return removedItems;
	}
	
}
