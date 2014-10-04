package ru.eshangin.compositelaunch.ui;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Select launchers tree view extended by filter
 */
public class FilteredSelectLaunchersTreeView extends FilteredTree {

	public FilteredSelectLaunchersTreeView(Composite parent) {
		super(parent, SWT.BORDER | SWT.FULL_SELECTION, new PatternFilter(), true);
	}

	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		return new SelectLaunchersTreeView(parent, style);
	}
}
