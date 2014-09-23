package ru.eshangin.compositelaunch;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

@SuppressWarnings("restriction")
public class SelectLaunchersTab extends AbstractLaunchConfigurationTab {
	
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
		
		try {
			
			// comparator to order launch configuration types by name
			Comparator<ILaunchConfigurationType> ltComparator = new Comparator<ILaunchConfigurationType>() {				
				@Override
				public int compare(ILaunchConfigurationType arg0, ILaunchConfigurationType arg1) {
					return arg0.getName().compareTo(arg1.getName());
				}
			};
			
			// comparator to order launch configurations by name
			Comparator<ILaunchConfiguration> lcComparator = new Comparator<ILaunchConfiguration>() {				
				@Override
				public int compare(ILaunchConfiguration arg0, ILaunchConfiguration arg1) {
					return arg0.getName().compareTo(arg1.getName());
				}
			};
			
			SelectLaunchersTreeContentProvider provider = new SelectLaunchersTreeContentProvider(
					Activator.getDefault().getCurrentMode(), configuration.getType());
			
			// get filtered types to view in the tree
			List<ILaunchConfigurationType> launchTypes = provider.getElements(); 
			
			// order types
			Collections.sort(launchTypes, ltComparator);
			
			for (ILaunchConfigurationType configurationType : launchTypes) {
						        
				// view current type
				TreeItem treeItem0 = new TreeItem(tree, 0);
		        treeItem0.setText(configurationType.getName());
		        
        		Image confTypeImage = DebugPluginImages.getImage(configurationType.getIdentifier());
        		
        		treeItem0.setImage(confTypeImage);
        		
        		// get all launch configurations of specified type
        		List<ILaunchConfiguration> launchConfigurations = Arrays.asList(manager.getLaunchConfigurations(configurationType));
		        
		        // order configurations
		        Collections.sort(launchConfigurations, lcComparator);
		        
				for (ILaunchConfiguration launchConf : launchConfigurations) {
					
					if (launchConf.getName() != configuration.getName()) {

						// view current configuration
						TreeItem treeItem1 = new TreeItem(treeItem0, 0);
				        treeItem1.setText(launchConf.getName());
				        treeItem1.setImage(confTypeImage);
				        
				        treeItem1.setData(launchConf);
					}
				}	
				
				// expand node
				treeItem0.setExpanded(true);
			}	
		    
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
