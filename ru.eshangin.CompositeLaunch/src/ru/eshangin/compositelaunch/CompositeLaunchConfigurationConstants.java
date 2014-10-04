package ru.eshangin.compositelaunch;

class CompositeLaunchConfigurationConstants {
	
	private CompositeLaunchConfigurationConstants() {
		
	}
	
	public static final String ATTR_SELECTED_CONFIGURATION_LIST = "selectedConfigurations";
	
	public static final String MSG_TMPL_PREVIOUSLY_SELECTED_CONFS_DELETED = "Next previously selected launch configurations were renamed or deleted: %1s";
	
	public static final String MSG_FYI = "FYI";
	
	public static final String LABEL_TMPL_TOTAL_COUNT_OF = "%1S out of %2S selected";
	
	public static final String MSG_TMPL_LAUNCH_CONFIG_WAS_DELETED_OR_REMOVED_COMPOSITE_LAUNCH_CANNOT_BE_CONNTINUED = "Launch configuration $1s was deleted or removed. Composite launch can not be continued.";
	
	public static final String LAUNCH_CONFIGURATION_TYPE_WAS_DELETED_CONFIG_CANNOT_BE_LAUNCHED = "Launch configuration type 1%s was deleted. %2s can't be launched.";
	
	public static final String MSG_PROBLEM = "Problem";
	
	public static final String COMPOSITE_LAUNCH_CONFIG_TYPE_ID = "ru.eshangin.CompositeLaunch";
	
	public static final int STATUSCODE_PRE_LAUNCH_CHECK_NO_CONFIG = 1;
	
	public static final int STATUSCODE_PRE_LAUNCH_CHECK_NO_CONFIG_TYPE = 2;
	
	public static final int STATUSCODE_UPDATE_COMPOSITE_VIEWER_MISSED_CONFIG = 3;
	
	public static final String LAUNCHERSTAB_NAME = "Select Launchers";
	
	public static final String LAUNCHERSTAB_NO_CONFIGS_SELECTED_MSG = "No any launch configuration slected";
}
