package ru.eshangin.compositelaunch.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ui.statushandlers.StatusManager;

public class CompositeConfiguration {
	
	// related composite ILaunchConfiguration
	private static ILaunchConfiguration fConfiguration;
	
	public CompositeConfiguration(ILaunchConfiguration configuration) {
		fConfiguration = configuration;
	}
	
	/**
	 * Applies changed config items to Launch Config Working Copy
	 */
	public static void applyChanges(List<CompositeConfigurationItem> newConfItems, 
			ILaunchConfigurationWorkingCopy configWorkingCopy) throws CoreException {
		
		// convert config items to JSON
		String newConfListAttrValue = JsonConfigurationHelper.toJson(newConfItems);
		
		try {
			// get currently saved list of configurations in launch config attributes
			String currentSavedConfigs = configWorkingCopy.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, "");
			
			// check that selected config list were changed
			if (!currentSavedConfigs.equals(newConfListAttrValue)) {
				configWorkingCopy.setAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, newConfListAttrValue);
			}
		} catch (CoreException e) {
			e.printStackTrace();
			
			StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
		}
	}
	
	/**
	 * Returns list of current Launch Configurations selected to run in composite including removed items if any
	 */
	public List<CompositeConfigurationItem> getCurrentItems() throws CoreException {
		return JsonConfigurationHelper.fromJson(
				fConfiguration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, ""));
	}

	/**
	 * Returns list of Launch Configurations which were removed/renamed since last setup of Composite Launch Configuration.
	 */
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
