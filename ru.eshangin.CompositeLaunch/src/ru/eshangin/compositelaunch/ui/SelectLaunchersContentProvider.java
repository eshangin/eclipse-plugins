package ru.eshangin.compositelaunch.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.statushandlers.StatusManager;

import ru.eshangin.compositelaunch.Activator;
import ru.eshangin.compositelaunch.internal.CompositeLaunchConfigurationConstants;

/**
 * This content provider populates SelectLaunchersTreeView with items
 */
class SelectLaunchersContentProvider implements ITreeContentProvider {
	
	private static Object[] EMPTY_ARRAY = new Object[0];
	
	@Override
	public void dispose() {
		// nothing to dispose		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing		
	}
	
	private String getCurrentMode() {
		return Activator.getDefault().getCurrentMode();
	}
	
	private ILaunchConfigurationType getCompositeType() {
		return DebugPlugin.getDefault().getLaunchManager()
				.getLaunchConfigurationType(CompositeLaunchConfigurationConstants.COMPOSITE_LAUNCH_CONFIG_TYPE_ID);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		ILaunchConfigurationType[] allTypes = getLaunchManager().getLaunchConfigurationTypes();
		
		List<ILaunchConfigurationType> filteredTypes = new ArrayList<ILaunchConfigurationType>();
		
		for (ILaunchConfigurationType configurationType : allTypes) {
			
			// filter types by Composite type, ability to launch with current launch mode and isPublic flag
			if (configurationType != getCompositeType() && configurationType.supportsMode(getCurrentMode()) && 
					configurationType.isPublic()) {
		        
		        // we will return only configuration types which contain configurations
		        try {
					if (!Arrays.asList(getLaunchManager().getLaunchConfigurations(configurationType)).isEmpty()) {
						
						filteredTypes.add(configurationType);
					}
				} catch (CoreException e) {
					e.printStackTrace();
					
					StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
				}
			}
						
		}

		return filteredTypes.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof ILaunchConfiguration) {
			return EMPTY_ARRAY;
		} else if (parentElement instanceof ILaunchConfigurationType) {
			ILaunchConfigurationType type = (ILaunchConfigurationType)parentElement;
			try {
				return getLaunchManager().getLaunchConfigurations(type);
			} catch (CoreException e) {
				e.printStackTrace();
				
				StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
			}
		} else {
			return getLaunchManager().getLaunchConfigurationTypes();
		}
		
		return EMPTY_ARRAY;
	}
	
	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public Object getParent(Object element) {

		if (element instanceof ILaunchConfiguration) {
			if (!((ILaunchConfiguration)element).exists()) {
				return null;
			}
			try {
				return ((ILaunchConfiguration)element).getType();
			} catch (CoreException e) {
				e.printStackTrace();
				
				StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW);
			}
		} else if (element instanceof ILaunchConfigurationType) {			
			return ResourcesPlugin.getWorkspace().getRoot();
		}
		
		return null;		
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ILaunchConfiguration) {
			return false;
		} 
		return getChildren(element).length > 0;
	}

}
