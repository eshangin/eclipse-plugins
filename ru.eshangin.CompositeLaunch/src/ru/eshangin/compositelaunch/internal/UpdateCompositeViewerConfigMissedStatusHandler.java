package ru.eshangin.compositelaunch.internal;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Handles the case when user selects Composite Configuration on UI but some of it's Launch Configurations
 * were renamed or deleted
 */
public class UpdateCompositeViewerConfigMissedStatusHandler implements IStatusHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IStatusHandler#handleStatus(org.eclipse.core.runtime.IStatus, java.lang.Object)
	 */
	@Override
	public Object handleStatus(IStatus status, Object source)
			throws CoreException {
		
		@SuppressWarnings("unchecked")
		List<CompositeConfigurationItem> removedItems = (List<CompositeConfigurationItem>) source;

		MessageDialog.openWarning(null, 
				CompositeLaunchConfigurationConstants.MSG_FYI, createNotFoundConfigsMessage(removedItems));
		
		return null;
	}
	
	/**
	 * Creates warn message for the case when some user opens composite configuration with
	 * some deleted previously selected launch configurations
	 */
	private String createNotFoundConfigsMessage(List<CompositeConfigurationItem> configItems) {
		String messageTmpl = CompositeLaunchConfigurationConstants.MSG_TMPL_PREVIOUSLY_SELECTED_CONFS_DELETED;
		
		String messageTmplArg = "";
		
		for (CompositeConfigurationItem conf : configItems) {
			messageTmplArg += String.format("%n%1s of type %2s", conf.getLaunchConfigurationName(), conf.getLaunchConfigurationTypeName());
		}
		
		return String.format(messageTmpl, messageTmplArg);
	}

}
