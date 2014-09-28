package ru.eshangin.compositelaunch;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author 1
 *
 */
class CompositeConfigurationManager {
	
	private static ArrayList<ILaunchConfiguration> selectedConfigurations = new ArrayList<ILaunchConfiguration>();
	
	private CompositeConfigurationManager() {
		
	}
	
	public static void addConfigurationToLaunch(ILaunchConfiguration configuration) {
		if (!selectedConfigurations.contains(configuration)) {
			selectedConfigurations.add(configuration);
		}
	}
	
	public static void removeConfigurationToLaunch(ILaunchConfiguration configuration) {
		selectedConfigurations.remove(configuration);
	}

	public static void clearLaunchConfigurations() {
		selectedConfigurations.clear();;
	}
	
	public static ArrayList<ILaunchConfiguration> getSelectedConfigurations() {
		return selectedConfigurations;
	}
	
	public static String getSerializedConfigurations(ArrayList<ILaunchConfiguration> configs) {
		
		Map<String, String> config2type = new HashMap<String, String>();
		for (ILaunchConfiguration config : configs) {
			try {
				config2type.put(config.getName(), config.getType().getIdentifier());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		Gson gson = new Gson();
		
		String json = gson.toJson(config2type);
		
        return json;
	}
	
	public static String getSerializedSelectedConfigurations() {
		return getSerializedConfigurations(getSelectedConfigurations());
	}
	
	private static ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();		
	}
	
	public static void deserializeSelectedLaunchConfigurations(String serializedConfigurations) {
		Gson gson = new Gson();
		
		Type collectionType = new TypeToken<Map<String, String>>(){}.getType();
		
		Map<String, String> deserizliedConfigs = gson.fromJson(serializedConfigurations, collectionType);

		if (deserizliedConfigs != null) {
			for (Entry<String, String> entry : deserizliedConfigs.entrySet()) {
			    String configName = entry.getKey();
			    String configTypeId = entry.getValue();
	
			    ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(configTypeId);
			    
			    try {
					ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
					
					for (ILaunchConfiguration config : configs) {
						if (config.getName().equals(configName)) {
							addConfigurationToLaunch(config);
						}
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			clearLaunchConfigurations();
		}
	}
}
