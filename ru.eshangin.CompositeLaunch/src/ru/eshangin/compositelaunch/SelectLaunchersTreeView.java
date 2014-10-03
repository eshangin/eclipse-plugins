package ru.eshangin.compositelaunch;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * This tree view helps select launch configs to launch in composite
 */
class SelectLaunchersTreeView extends CheckboxTreeViewer {
	
	private SelectLaunchersContentProvider fContentProvider;

	/**
	 * @wbp.parser.entryPoint
	 */
	public SelectLaunchersTreeView(Composite parent, int style) {
		super(parent, style);
		
		fContentProvider = new SelectLaunchersContentProvider();
		
		// Set content provider
		setContentProvider(fContentProvider);

		// set label provider
		setLabelProvider(new DecoratingLabelProvider(DebugUITools.newDebugModelPresentation(), 
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		
		// set sorter
		setSorter(new SelectLaunchersTreeViewerSorter());
		
		addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {				
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
	public boolean setChecked(Object element, boolean state) {
		
		boolean canCheck = super.setChecked(element, state);
		
		if (canCheck) {
			checkBranchItems(element, state);
		}
		
		return canCheck;
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
