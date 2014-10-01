package ru.eshangin.compositelaunch;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Select launchers tree view extended by filter
 */
public class FilteredSelectLaunchersTreeView extends FilteredTree {

	public FilteredSelectLaunchersTreeView(Composite parent, int treeStyle,
			PatternFilter filter) {
		super(parent, treeStyle, filter, false);
	}

	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		return new SelectLaunchersTreeView(parent, style);
	}
}
