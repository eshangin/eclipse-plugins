package ru.eshangin.compositelaunch;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

class SelectLaunchersTreeView extends CheckboxTreeViewer {
	
	private SelectLaunchersContentProvider fContentProvider;

	public SelectLaunchersTreeView(Composite parent, int style) {
		super(parent, style);

		// set label provider
		setLabelProvider(new DecoratingLabelProvider(DebugUITools.newDebugModelPresentation(), 
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		
		// set sorter
		setSorter(new SelectLaunchersTreeViewerSorter());
		
		addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				
				// add/remove selected configuration into/from list of Selected Configurations which will be used
			  	// if user will launch this configuration.
				if (event.getElement() instanceof ILaunchConfiguration) {
					saveCompositeLaunchItem((ILaunchConfiguration)event.getElement(), event.getChecked());
				}
				
				checkBranchItems(event.getElement(), event.getChecked());				
			}
		});
		
		// check/uncheck on doubleclick
		addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeSelection selection = (TreeSelection)event.getSelection();
				Object clickedElement = selection.getFirstElement();
				boolean newState = !getChecked(clickedElement);
				setChecked(clickedElement, newState);
				checkBranchItems(clickedElement, newState);
			}
		});
	}
	
	public boolean anyLaunchConfigurationSelected(ILaunchConfigurationType confType) {
		Object[] configs = fContentProvider.getChildren(confType);
		
		boolean anySelected = false;
		
		for (Object config : configs) {
			if (getChecked(config)) {
				return true;
			}
		}
		
		return anySelected;
	}

	@Override
	public void setContentProvider(IContentProvider provider) {
		
		if (provider instanceof SelectLaunchersContentProvider) {
			fContentProvider = (SelectLaunchersContentProvider) provider;
		}
		else {
			throw new Error("Provider must be of type SelectLaunchersContentProvider");
		}
		
		super.setContentProvider(provider);
	}
	
	@Override
	public boolean setChecked(Object element, boolean state) {
		// add/remove selected configuration into/from list of Selected Configurations which will be used
	  	// if user will launch this configuration.
		if (element instanceof ILaunchConfiguration) {
			saveCompositeLaunchItem((ILaunchConfiguration)element, state);
		}
		
		return super.setChecked(element, state);
	}
	
	@Override
	public boolean setSubtreeChecked(Object element, boolean state) {
		
		if (element instanceof ILaunchConfigurationType) {
			Object[] childConfigurations = fContentProvider.getChildren(element);
			for (Object config : childConfigurations) {
				saveCompositeLaunchItem((ILaunchConfiguration)config, state);
			}
		}
		
		return super.setSubtreeChecked(element, state);
	}
	
	private void saveCompositeLaunchItem(ILaunchConfiguration configuration, boolean add) {
		if (add) {
			// add configuration to launch in composite
			Activator.getDefault().addConfigurationToLaunch(configuration);
		}
		else {
			// remove configuration to launch in composite
			Activator.getDefault().removeConfigurationToLaunch(configuration);
		}
	}
	
	private void checkBranchItems(Object element, boolean isChecked) {
		
		// get parent of checked element
		Object parentOfChecked = fContentProvider.getParent(element);
		if (parentOfChecked instanceof ILaunchConfigurationType) {
			if (isChecked) {
				// check if all children are checked or unchecked
				Object[] confTypeChildrens = fContentProvider.getChildren(parentOfChecked);
				boolean allChecked = true;
				for (int i = 0; i < confTypeChildrens.length; i++) {
					if (!getChecked(confTypeChildrens[i])) {
						allChecked = false;
						break;
					}
				}
				
				if (allChecked) {
					// set checked state for the parent
					setChecked(parentOfChecked, true);
				}
			}
			else {
				if (getChecked(parentOfChecked)) {
					// uncheck parent
					setChecked(parentOfChecked, false);
				}
			}
			
		} else if (parentOfChecked instanceof IWorkspaceRoot) {
			setSubtreeChecked(element, isChecked);
		}
	}

}
