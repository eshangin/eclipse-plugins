package ru.eshangin.compositelaunch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

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
	
	private String getConfigurationTypeImagePath(ILaunchConfigurationType configType) throws IOException {
		
		IConfigurationElement[] cel = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.debug.ui.launchConfigurationTypeImages"); 
        
        for (IConfigurationElement iConfigurationElement : cel) {
        	        	
        	String configTypeIDAttr = iConfigurationElement.getAttribute("configTypeID");
        	        	
        	if (configType.getIdentifier().equals(configTypeIDAttr)) {
        		
        		String iconAttr = iConfigurationElement.getAttribute("icon");
        		
        		System.out.println(configType.getIdentifier() + " " + configType.getName() + " " + configTypeIDAttr);
        		
	        	URL[] entries = FileLocator.findEntries(Activator.getDefault().getBundle(), new Path(iconAttr));
	        					        					        
	        	if (entries.length > 0) {
	        		URL iconUrl = FileLocator.resolve(entries[0]);
	        		//System.out.println(iconUrl);
	        		
	        		//treeItem0.setImage(new Image(null, new File(iconUrl.getPath()).getAbsolutePath()));
	        		return new File(iconUrl.getPath()).getAbsolutePath();
	        	}
	        	
	        	return null;
        	}
		}
        
        return null;
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
		
		ArrayList<ILaunchConfiguration> launchConfigurations;
		
		try {
						
			// get all launch configuration types
			ArrayList<ILaunchConfigurationType> launchTypes = new ArrayList<ILaunchConfigurationType>(Arrays.asList(manager.getLaunchConfigurationTypes()));
			
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
			
			// order types
			Collections.sort(launchTypes, ltComparator);
			
			for (ILaunchConfigurationType configurationType : launchTypes) {
				
				String currentMode = Activator.getDefault().getCurrentMode();
				
				// filter types by Composite type, ability to launch with current launch mode and isPublic flag
				if (configurationType != configuration.getType() && configurationType.supportsMode(currentMode) && 
						configurationType.isPublic()) {
					
			        // get all launch configurations of specified type
			        launchConfigurations = new ArrayList<ILaunchConfiguration>(Arrays.asList(manager.getLaunchConfigurations(configurationType)));
			        
			        // we will show only configuration types which contain configurations
			        if (!launchConfigurations.isEmpty()) {
			        
						// view current type
						TreeItem treeItem0 = new TreeItem(tree, 0);
				        treeItem0.setText(configurationType.getName());
				        
				        IConfigurationElement[] cel = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.debug.ui.launchConfigurationTypeImages"); 
				        
				        for (IConfigurationElement iConfigurationElement : cel) {
				        	        	
				        	String configTypeIDAttr = iConfigurationElement.getAttribute("configTypeID");
				        	
				        	String iconAttr = iConfigurationElement.getAttribute("icon");
				        	System.out.println(configurationType.getIdentifier() + " " + configurationType.getName() + " " + iconAttr);
				        	        	
				        	if (configurationType.getIdentifier().equals(configTypeIDAttr)) {
				        						        		
				        		//URL uf = FileLocator.find(new URL(iconAttr));
				        		//System.out.println(configurationType.getIdentifier() + " " + configurationType.getName() + " " + uf.toString());
				        		
				        		//BundleUtility.
				        		//System.out.println(FileLocator.toFileURL(url));
				        		
				        		//Bundle b = FrameworkUtil.getBundle(configurationType.getClass());

				        		//FileLocator.findEntries(Platform.getBundle("org.eclipse.jdt.junit"), new Path(iconAttr))
				        		// TODO :: fix calculation of symbolic name
				        		Bundle b = Platform.getBundle(configurationType.getIdentifier().substring(0, configurationType.getIdentifier().lastIndexOf(".")));
				        		
				        		if (b != null) {
					        		System.out.println(configurationType.getClass().getName());
					        		
						        	URL[] entries = FileLocator.findEntries(b, new Path(iconAttr));
						        						        					        					       
						        	if (entries.length > 0) {
						        		URL iconUrl = FileLocator.resolve(entries[0]);
						        		
						        		//System.out.println(iconUrl);
						        		
						        		//treeItem0.setImage(new Image(null, new File(iconUrl.getPath()).getAbsolutePath()));
						        		//return new File(iconUrl.getPath()).getAbsolutePath();
						        		treeItem0.setImage(new Image(null, new File(FileLocator.toFileURL(entries[0]).getPath()).getAbsolutePath()));
						        	}
						        	
						        	//return null;
				        		}
				        	}
						}
				        
//				        String iconPath = getConfigurationTypeImagePath(configurationType);
//				        
//				        if (iconPath != null) {
//				        	treeItem0.setImage(new Image(null, iconPath));
//				        }
				        
				        //String iconPath = cel[0].getAttribute("icon");
				        
				        //treeItem0.setImage(new Image(null, iconPath));
				        //treeItem0.setImage(configurationType.getAttribute(""));
				        			        				        
				        // order configurations
				        Collections.sort(launchConfigurations, lcComparator);
				        
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
						
						// expand node
						treeItem0.setExpanded(true);
			        }
				}
			}	
		    
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (InvalidRegistryObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
