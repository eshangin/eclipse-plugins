package ru.eshangin.compositelaunch.ui;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Select launchers tree view extended by filter
 */
class FilteredSelectLaunchersTreeView extends FilteredTree {

	public FilteredSelectLaunchersTreeView(Composite parent) {
		super(parent, SWT.BORDER | SWT.FULL_SELECTION, new PatternFilter(), true);
	}
	
	/**
	 * For some reason filtered tree looses checked state after it has been filtered.
	 * We will restore checked states manually.
	 */
	@Override
	protected void updateToolbar(boolean visible) {
		// TODO Auto-generated method stub
		super.updateToolbar(visible);
		
		SelectLaunchersTreeView tView = (SelectLaunchersTreeView) treeViewer;
		
		// restore checked states
		tView.updateCheckedItems();
		
		// expand all items
		tView.expandAll();
	}

	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		return new SelectLaunchersTreeView(parent, style);
	}
}
