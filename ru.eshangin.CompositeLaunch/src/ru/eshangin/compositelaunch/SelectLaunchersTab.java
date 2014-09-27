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
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;

// TODO :: add commentes to the class and it's methods
//
public class SelectLaunchersTab extends AbstractLaunchConfigurationTab {
	
	//private Composite comp;
	private Button btnCheckButton;
	private SelectLaunchersTreeView checkboxTreeViewer;
	private Button btnSelectAll;
	private ViewerFilter fOnlySelectedFilter;
	private FormData fd_tree;
	
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
		
		btnSelectAll = new Button(comp, SWT.NONE);
		FormData fd_btnSelectAll = new FormData();
		fd_btnSelectAll.left = new FormAttachment(100, -133);
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
			        	break;
		        	}
		      }
	    });
			
		Button btnDeselectAll = new Button(comp, SWT.NONE);
		FormData fd_btnDeselectAll = new FormData();
		fd_btnDeselectAll.left = new FormAttachment(btnSelectAll, 0, SWT.LEFT);
		fd_btnDeselectAll.right = new FormAttachment(100, -10);
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
			        	break;
		        	}
		      }
	    });		
		
		createTreeViewer(comp);
		
		System.out.println("createControl");		
	}
	
	private void createTreeViewerFilters(Composite parent) {
		btnCheckButton = new Button(parent, SWT.CHECK);
		fd_tree.bottom = new FormAttachment(btnCheckButton, 0, SWT.BOTTOM);
		FormData fd_btnCheckButton = new FormData();
		fd_btnCheckButton.bottom = new FormAttachment(100, -10);
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
		checkboxTreeViewer = new SelectLaunchersTreeView(parent, SWT.BORDER);
		Tree tree_1 = checkboxTreeViewer.getTree();
		fd_tree = new FormData();
		fd_tree.right = new FormAttachment(btnSelectAll, -6);
		fd_tree.top = new FormAttachment(btnSelectAll, 0, SWT.TOP);
		fd_tree.left = new FormAttachment(0, 10);
		tree_1.setLayoutData(fd_tree);
		
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
		Activator.getDefault().clearLaunchConfigurations();
		
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
}
