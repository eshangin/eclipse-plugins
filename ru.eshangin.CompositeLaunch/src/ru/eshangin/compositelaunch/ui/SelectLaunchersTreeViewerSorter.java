package ru.eshangin.compositelaunch.ui;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * This sorter helps sort launch configs and launch config types in Select Launchers Tree View
 */
class SelectLaunchersTreeViewerSorter extends ViewerSorter {

	public int compare(Viewer viewer, Object e1, Object e2) {
		   String name1, name2;
		   if (viewer == null || !(viewer instanceof ContentViewer)) {
		       name1 = e1.toString();
		       name2 = e2.toString();
		   } else {
		       IBaseLabelProvider prov = ((ContentViewer)viewer).getLabelProvider();
		       if (prov instanceof ILabelProvider) {
		           ILabelProvider lprov = (ILabelProvider)prov;
		           name1 = lprov.getText(e1);
		           name2 = lprov.getText(e2);
		       } else {
		           name1 = e1.toString();
		           name2 = e2.toString();
		       }
		   }

		   return name1.compareTo(name2);
		}
	
	
}
