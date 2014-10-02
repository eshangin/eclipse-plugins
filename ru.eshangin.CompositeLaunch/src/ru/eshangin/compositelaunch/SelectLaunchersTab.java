package ru.eshangin.compositelaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * This tab helps to select configurations to launch in composite
 */
@SuppressWarnings("restriction")
public class SelectLaunchersTab extends AbstractLaunchConfigurationTab {
	
	private Button btnCheckButton;
	private SelectLaunchersTreeView checkboxTreeViewer;
	private Button btnSelectAll;
	private ViewerFilter fOnlySelectedFilter;
	private FormData fd_filteredTree;
	private FormData fd_btnCheckButton;
	private FormData fd_btnSelectAll;
	private FormData fd_btnDeselectAll;
	private String fDefaultSerializedConfigs;
	private Label fLblXOf;
	private Button btnDeselectAll;
	private FilteredSelectLaunchersTreeView filteredTree;

	
	@Override
	public void createControl(Composite parent) {
		
		Font font = parent.getFont();		
		
		Composite comp = new Composite(parent, SWT.BORDER);
		setControl(comp);
		comp.setFont(font);
		comp.setLayout(new FormLayout());
		
		createSelectAllButton(comp);
			
		createDeselectAllButton(comp);
		
		createTreeViewer(comp);
		
		createTotalSelectedLabel(comp);	
	}
	
	private void createTotalSelectedLabel(Composite parent) {
		fLblXOf = new Label(parent, SWT.NONE);
		fd_btnCheckButton.top = new FormAttachment(fLblXOf, -25, SWT.TOP);
		fd_btnCheckButton.bottom = new FormAttachment(fLblXOf, -6);
		FormData fd_lblXOf = new FormData();
		fd_lblXOf.bottom = new FormAttachment(100, -10);
		fd_lblXOf.left = new FormAttachment(btnSelectAll, 0, SWT.LEFT);
		fLblXOf.setLayoutData(fd_lblXOf);
		fLblXOf.setText("0 out of 0 selected");
	}
	
	private void createSelectAllButton(Composite parent) {
		btnSelectAll = new Button(parent, SWT.NONE);
		fd_btnSelectAll = new FormData();
		fd_btnSelectAll.left = new FormAttachment(100, -134);
		fd_btnSelectAll.right = new FormAttachment(100, -10);
		fd_btnSelectAll.top = new FormAttachment(0, 5);
		btnSelectAll.setLayoutData(fd_btnSelectAll);
		btnSelectAll.setText("Select all");
		btnSelectAll.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
			        switch (e.type) {
			        case SWT.Selection:
			        	Object[] topElements = ((SelectLaunchersContentProvider) checkboxTreeViewer.getContentProvider()).getElements(
			        			ResourcesPlugin.getWorkspace().getRoot());
			        	for (Object element : topElements) {
			        		checkboxTreeViewer.setSubtreeChecked(element, true);
						}
			        	
			        	// update dialog message and buttons
			        	updateLaunchConfigurationDialog();
			        	break;
		        	}
		      }
	    });
	}
	
	private void createDeselectAllButton(Composite parent) {
		btnDeselectAll = new Button(parent, SWT.NONE);
		fd_btnDeselectAll = new FormData();
		fd_btnDeselectAll.top = new FormAttachment(btnSelectAll, 6);
		fd_btnDeselectAll.left = new FormAttachment(100, -134);
		fd_btnDeselectAll.right = new FormAttachment(100, -10);
		btnDeselectAll.setLayoutData(fd_btnDeselectAll);
		btnDeselectAll.setText("Deselect all");
		btnDeselectAll.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
			        switch (e.type) {
			        case SWT.Selection:
			        	Object[] topElements = ((SelectLaunchersContentProvider) checkboxTreeViewer.getContentProvider()).getElements(
			        			ResourcesPlugin.getWorkspace().getRoot());
			        	for (Object element : topElements) {
			        		checkboxTreeViewer.setSubtreeChecked(element, false);
						}
			        	
			        	// update dialog message and buttons
			        	updateLaunchConfigurationDialog();
			        	break;
		        	}
		      }
	    });		
	}
	
	private void createTreeViewerFilters(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_composite = new FormData();
		fd_composite.right = new FormAttachment(btnSelectAll, -6);
		fd_composite.bottom = new FormAttachment(btnSelectAll, 0, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(btnSelectAll, 0, SWT.TOP);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		btnCheckButton = new Button(parent, SWT.CHECK);
		fd_btnCheckButton = new FormData();
		fd_btnCheckButton.left = new FormAttachment(btnSelectAll, -124);
		fd_btnCheckButton.right = new FormAttachment(btnSelectAll, 0, SWT.RIGHT);
		btnCheckButton.setLayoutData(fd_btnCheckButton);
		btnCheckButton.setText("Only show selected");
		
		fOnlySelectedFilter = new OnlySelectedViewerFilter();
		
		btnCheckButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
		        if (button.getSelection()) {
		        	checkboxTreeViewer.addFilter(fOnlySelectedFilter);
		        }
		        else {
		        	checkboxTreeViewer.removeFilter(fOnlySelectedFilter);
		        	checkboxTreeViewer.expandAll();
		        	
		        	// By some reason system removes checked state from
		        	// child elements of parent elements which were collapsed.
		        	// We need restore child states.
		        	Object[] topElements = ((SelectLaunchersContentProvider) checkboxTreeViewer.getContentProvider()).getElements(
		        			ResourcesPlugin.getWorkspace().getRoot());
		        	for (Object element : topElements) {
		        		if (checkboxTreeViewer.getChecked(element)) {
		        			checkboxTreeViewer.setSubtreeChecked(element, true);
		        		}
					}
		        	
		        }
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing				
			}
		});
	}
	
	private void createTreeViewer(Composite parent) {
		PatternFilter filter = new PatternFilter();
		
		filteredTree = new FilteredSelectLaunchersTreeView(parent, SWT.NONE, filter);
		
		checkboxTreeViewer = (SelectLaunchersTreeView) filteredTree.getViewer();
		fd_filteredTree = new FormData();
		fd_filteredTree.right = new FormAttachment(100, -140);
		fd_filteredTree.left = new FormAttachment(0, 10);
		fd_filteredTree.top = new FormAttachment(0, 20);
		fd_filteredTree.bottom = new FormAttachment(100, -10);
		filteredTree.setLayoutData(fd_filteredTree);
		
		// update dialog buttons and message when some configurations
		// will be checked/unchecked
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {	
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateLaunchConfigurationDialog();
			}
		});
		checkboxTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				updateLaunchConfigurationDialog();				
			}
		});
		
		// create viewer filters
		createTreeViewerFilters(parent);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {		
		// do nothing because there is no need to set defaults for newly created configuration
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
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		try {
			// Init list of selected launch configs from composite config attributes
			fDefaultSerializedConfigs = configuration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, "");
			
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			
			// Update list of available launch configurations
			checkboxTreeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
			checkboxTreeViewer.expandAll();

			// set default checked items
			List<CompositeConfigurationItem> removedItems = new ArrayList<CompositeConfigurationItem>();
			checkboxTreeViewer.setCheckedElements(new Object[0]);
			for (CompositeConfigurationItem configItem : JsonConfigurationHelper.fromJson(fDefaultSerializedConfigs)) {
				ILaunchConfiguration convertedConfig = configItem.toLaunchConfiguration();
				if (convertedConfig != null) {
					checkboxTreeViewer.setChecked(convertedConfig, true);
				}		
				else {
					removedItems.add(configItem);
				}
			}
			
			if (!removedItems.isEmpty()) {
				MessageDialog.openWarning(DebugUIPlugin.getShell(), 
						CompositeLaunchConfigurationConstants.MSG_FYI, createNotFoundConfigsMessage(removedItems));
			}
		    
		} catch (InvalidRegistryObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		
		int totalLauchConfsCount = 0;
		int totalSelectedConfigs = 0;
		
		// get all currently selected configurations from tree view
		TreeItem[] treeItems = checkboxTreeViewer.getTree().getItems();
		for (TreeItem confTypeItem : treeItems) {
			for (TreeItem confItem : confTypeItem.getItems()) {
				if (confItem.getChecked()) {
					totalSelectedConfigs++;
				}
			}
			totalLauchConfsCount += confTypeItem.getItemCount();
		}
		
		Object[] checkedElements = checkboxTreeViewer.getCheckedElements();
		
		ArrayList<CompositeConfigurationItem> confItems = new ArrayList<CompositeConfigurationItem>();
		
		for (Object element : checkedElements) {
			if (element instanceof ILaunchConfiguration) {
				confItems.add(new CompositeConfigurationItem((ILaunchConfiguration)element));
			}
		}
		
		fLblXOf.setText(String.format(CompositeLaunchConfigurationConstants.LABEL_TMPL_TOTAL_COUNT_OF, totalSelectedConfigs, totalLauchConfsCount));
		
		String newConfListAttrValue = JsonConfigurationHelper.toJson(confItems);
				
		try {
			// get currently saved list of configurations in launch config attributes
			String currentSavedConfigs = configuration.getAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, "");
			
			// check that selected config list were changed
			if (!currentSavedConfigs.equals(newConfListAttrValue)) {
				configuration.setAttribute(CompositeLaunchConfigurationConstants.ATTR_SELECTED_CONFIGURATION_LIST, newConfListAttrValue);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Select Launchers";
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {		
		
		setErrorMessage(null);
		
		// check if any launch configuration were selected
		if (checkboxTreeViewer.getCheckedElements().length == 0) {
			setErrorMessage("No any launch configuration slected");
			return false;
		}
		
		return true;
	}
}
