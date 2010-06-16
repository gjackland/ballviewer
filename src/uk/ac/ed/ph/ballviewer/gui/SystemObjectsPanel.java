package uk.ac.ed.ph.ballviewer.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;

import java.awt.Dimension;


import uk.ac.ed.ph.ballviewer.*;

public class SystemObjectsPanel extends JScrollPane
{
	private final BallViewerFramework		framework;
	
	private final JTree						tree;
	
	public SystemObjectsPanel(
		final BallViewerFramework	framework
	)
	{
		this.framework		= framework;
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode( "System" );
		tree = new JTree( top );
		
		
		this.add( tree );
		
		this.setMinimumSize( new Dimension( 100, 100 ) );
		
		System.out.println( "Scroll pane constructed" );
		
	}
	
}