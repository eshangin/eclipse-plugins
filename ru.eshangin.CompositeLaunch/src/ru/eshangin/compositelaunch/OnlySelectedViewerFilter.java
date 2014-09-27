package ru.eshangin.compositelaunch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * This viewer filter will filter Select Launchers tree
 * and will show/hide nodes depending on current checked state of nodes
 */
class OnlySelectedViewerFilter extends ViewerFilter {
	
	private SelectLaunchersTreeView fTreeView;
	
	public OnlySelectedViewerFilter(SelectLaunchersTreeView treeView) {
		fTreeView = treeView;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ILaunchConfiguration) {
			// show/hide ILaunchConfiguration node depending on it's checked state
			return fTreeView.getChecked(element);
		}
		else if (element instanceof ILaunchConfigurationType) {
			// show/hide ILaunchConfigurationType node depending on it's children checked state
			return fTreeView.anyLaunchConfigurationSelected((ILaunchConfigurationType) element);
		}
		
		return true;
	}

}