package uk.ac.ed.ph.ballviewer.gui;

import java.awt.Dimension;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;


import uk.ac.ed.ph.ballviewer.BallViewerFramework;


public class SystemObjectsPanel extends JTabbedPane
{	
	private final BallViewerFramework		framework;
	private final SystemTree 				systemTree;
	
	public SystemObjectsPanel(
		final BallViewerFramework	framework
	)
	{
		this.framework		= framework;

		systemTree			= new SystemTree( framework.getAnalysisManager() );
		final JScrollPane systemTreeView	= new JScrollPane( systemTree );
		final JScrollPane analysisTreeView	= new JScrollPane( new AnalysisTree( framework.getAnalysisManager() ) );

		addTab( "System", systemTreeView );
		addTab( "Analysis", analysisTreeView );
		
		setPreferredSize( new Dimension( 200, 400 ) );
	}
	
	
	// TODO: Get rid of this, will be done by messages
	public void
	update()
	{
		systemTree.update();
	}
}