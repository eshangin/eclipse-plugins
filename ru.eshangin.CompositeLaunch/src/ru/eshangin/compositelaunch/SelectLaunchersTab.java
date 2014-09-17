package ru.eshangin.compositelaunch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SelectLaunchersTab extends AbstractLaunchConfigurationTab {
	
	private Text fProgramText;
	private Button fProgramButton;
	private Tree tree;
	
	@Override
	public void createControl(Composite parent) {
		//Font font = parent.getFont();		
		
		Composite comp = new Composite(parent, SWT.NONE);
		
		tree = new Tree(comp, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    tree.setSize(290, 290);
	    
	    tree.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
	          if (event.detail == SWT.CHECK) {
	        	  System.out.println("c");
	        	  TreeItem item = (TreeItem)event.item;
	        	  
	        	  // add selected configuration into array of Selected Configurations which will be used
	        	  // if user will launch this configuration.
	        	  Activator.getDefault().addConfigurationToLaunch((ILaunchConfiguration)item.getData());
	          } else {
	        	  System.out.println("u");
	          }
	        }
	      });
		
		setControl(comp);
		
		System.out.println("createControl");
		
//		GridLayout topLayout = new GridLayout();
//		topLayout.verticalSpacing = 0;
//		topLayout.numColumns = 3;
//		comp.setLayout(topLayout);
//		//comp.setFont(font);
//		
//		createVerticalSpacer(comp, 3);
//		
//		Label programLabel = new Label(comp, SWT.NONE);
//		programLabel.setText("&Program:");
//		//GridData gd = new GridData(GridData.BEGINNING);
//		//programLabel.setLayoutData(gd);
//		//programLabel.setFont(font);
//		
//		fProgramText = new Text(comp, SWT.SINGLE | SWT.BORDER);
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		fProgramText.setLayoutData(gd);
//		//fProgramText.setFont(font);
//		
//		
//		fProgramButton = createPushButton(comp, "&Browse...", null); //$NON-NLS-1$
		
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
		tree.removeAll();
		
		// clear selected configurations
		Activator.getDefault().clearLaunchConfigurations();
		
		// get launch manager
		final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
		ILaunchConfiguration[] launchConfigurations;
		try {
						
			// get all launch configuration types
			ILaunchConfigurationType[] launchTypes = manager.getLaunchConfigurationTypes();
			
			for (ILaunchConfigurationType configurationType : launchTypes) {
				
				String currentMode = Activator.getDefault().getCurrentMode();
				
				// filter types by Composite type and ability to launch with current launch mode
				if (configurationType != configuration.getType() && configurationType.supportsMode(currentMode)) {
					
					// view current type
					TreeItem treeItem0 = new TreeItem(tree, 0);
			        treeItem0.setText(configurationType.getName());
			        
			        // get all launch configurations of specified type
			        launchConfigurations = manager.getLaunchConfigurations(configurationType);
			        
					for (ILaunchConfiguration launchConf : launchConfigurations) {
						
						if (launchConf.getName() != configuration.getName()) {
	
							// view current configuration
							TreeItem treeItem1 = new TreeItem(treeItem0, 0);
					        treeItem1.setText(launchConf.getName());
					        
					        treeItem1.setData(launchConf);
					        
							//if (launchConf.supportsMode(configuration.getm())) {				
								//launchConf.launch("run", monitor);
							//}
							//else {
								//System.out.println(launchConf.getName() + " not supports '" + configuration.getName() + "' mode");
							//}
							//Map<String, Object> attrs = launchConf.getAttributes();
							//attrs.
						}
					}		
				}
			}	
		    
		} catch (CoreException e) {
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
