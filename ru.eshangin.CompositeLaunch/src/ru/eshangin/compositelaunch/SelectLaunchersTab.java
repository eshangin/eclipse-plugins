package ru.eshangin.compositelaunch;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Label;

// TODO :: add commentes to the class and it's methods
//
public class SelectLaunchersTab extends AbstractLaunchConfigurationTab {
	
	//private Composite comp;
	private Button btnCheckButton;
	private SelectLaunchersTreeView checkboxTreeViewer;
	private Button btnSelectAll;
	private ViewerFilter fOnlySelectedFilter;
	private FormData fd_tree;
	private Tree tree;
	private FormData fd_btnCheckButton;
	private FormData fd_btnSelectAll;
	private FormData fd_btnDeselectAll;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		
		Font font = parent.getFont();		
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		comp.setFont(font);
		comp.setLayout(new FormLayout());
		
		createSelectAllButton(comp);
			
		createDeselectAllButton(comp);
		
		createTreeViewer(comp);
		
		createTotalSelectedLabel(comp);
		
		System.out.println("createControl");		
	}
	
	private void createTotalSelectedLabel(Composite parent) {
		Label lblXOf = new Label(parent, SWT.NONE);
		fd_btnSelectAll.right = new FormAttachment(lblXOf, 0, SWT.RIGHT);
		fd_btnCheckButton.bottom = new FormAttachment(lblXOf, -6);
		fd_tree.right = new FormAttachment(lblXOf, -6);
		FormData fd_lblXOf = new FormData();
		fd_lblXOf.right = new FormAttachment(100, -10);
		fd_lblXOf.bottom = new FormAttachment(100, -10);
		lblXOf.setLayoutData(fd_lblXOf);
		lblXOf.setText("1000 out of 1000 selected");
	}
	
	private void createSelectAllButton(Composite parent) {
		btnSelectAll = new Button(parent, SWT.NONE);
		fd_btnSelectAll = new FormData();
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
		Button btnDeselectAll = new Button(parent, SWT.NONE);
		fd_btnDeselectAll = new FormData();
		fd_btnDeselectAll.right = new FormAttachment(btnSelectAll, 0, SWT.RIGHT);
		fd_btnDeselectAll.top = new FormAttachment(btnSelectAll, 6);
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
		btnCheckButton = new Button(parent, SWT.CHECK);
		fd_btnCheckButton = new FormData();
		fd_btnCheckButton.left = new FormAttachment(tree, 6);
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
		checkboxTreeViewer = new SelectLaunchersTreeView(parent, SWT.BORDER);
		tree = checkboxTreeViewer.getTree();
		fd_btnDeselectAll.left = new FormAttachment(tree, 6);
		fd_btnSelectAll.left = new FormAttachment(tree, 6);
		fd_tree = new FormData();
		fd_tree.top = new FormAttachment(0, 5);
		fd_tree.left = new FormAttachment(0, 10);
		fd_tree.bottom = new FormAttachment(100, -10);
		tree.setLayoutData(fd_tree);
		
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
		// TODO Auto-generated method stub
		System.out.println("setDefaults");
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		
		System.out.println("initializeFrom");
		
		// remove all tree nodes created before
		//tree.removeAll();
		
		// clear selected configurations
		CompositeConfigurationManager.clearLaunchConfigurations();
		
		try {
			
			checkboxTreeViewer.setContentProvider(new SelectLaunchersContentProvider(
					Activator.getDefault().getCurrentMode(), configuration.getType()));
			checkboxTreeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
			checkboxTreeViewer.expandAll();	
		    
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (InvalidRegistryObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
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
