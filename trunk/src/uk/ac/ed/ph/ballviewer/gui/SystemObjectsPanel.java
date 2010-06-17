package uk.ac.ed.ph.ballviewer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;


import uk.ac.ed.ph.ballviewer.*;
import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;

public class SystemObjectsPanel extends JPanel
{
	class AttributeHandler implements ActionListener
	{
		private final BallViewerFramework		framework;
		private final SysObjAttribute			attribute;
		private final JPopupMenu				attributePopup;
		private final Hashtable< JMenuItem, AnalyserOutput >	menuItemMap =
			new Hashtable< JMenuItem, AnalyserOutput >();

		AttributeHandler(
			final BallViewerFramework	framework,
			final SysObjAttribute		attribute
		)
		{
			this.framework		= framework;
			this.attribute		= attribute;
			
			attributePopup		= new JPopupMenu( "Attach Analyser Output" );
			
			generatePopupMenu();
			
		}
		
		private void
		generatePopupMenu()
		{
			for( AnalyserOutput output : framework.getAnalysisManager().getSupportedOutputs( attribute ) )
			{
				final JMenuItem outputItem = new JMenuItem( output.toString() );
				outputItem.addActionListener( this );
				attributePopup.add( outputItem );
				menuItemMap.put( outputItem, output );
			}
			if( attributePopup.getComponentCount() == 0 )
			{
				final JMenuItem noOutputs = new JMenuItem( "<No Supported Outputs>" );
				noOutputs.setEnabled( false );
				attributePopup.add( noOutputs );
			}
		}
		
		public String
		toString()
		{
			return attribute.getName();
		}
		
		public void
		actionPerformed( ActionEvent evt )
		{
			final AnalyserOutput output = menuItemMap.get( evt.getSource() );
			if( output != null )
			{
				output.attachAttribute( attribute );
				framework.getAnalysisManager().update( framework.getSystem() );
			}
		}
		
		void
		showPopup(
			final Component		comp,
			final Point			point
		)
		{
			attributePopup.show( comp, ( int )point.getX(), ( int )point.getY() );
		}
	}
	
	private final BallViewerFramework		framework;
	
	private final JTree						tree;
	private final DefaultMutableTreeNode	top		= new DefaultMutableTreeNode( "System" );

	
	public SystemObjectsPanel(
		final BallViewerFramework	framework
	)
	{
		this.framework		= framework;
		
		tree = new JTree( top );
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

		final MouseListener ml = new MouseAdapter()
		{
			public void mousePressed( MouseEvent e )
			{
				if( e.isPopupTrigger() )
				{
					final int		selRow	= tree.getRowForLocation(e.getX(), e.getY());
					final TreePath	selPath = tree.getPathForLocation(e.getX(), e.getY());
					if( selRow != -1 )
					{
						mouseNodeRightClick( e.getComponent(), e.getPoint(), selPath, selRow );
					}
				}
			}
			
			public void mouseReleased( MouseEvent e )
			{
				if( e.isPopupTrigger() )
				{
					final int		selRow	= tree.getRowForLocation(e.getX(), e.getY());
					final TreePath	selPath = tree.getPathForLocation(e.getX(), e.getY());
					if( selRow != -1 )
					{
						mouseNodeRightClick( e.getComponent(), e.getPoint(), selPath, selRow );
					}
				}
			}
		};
		
		tree.addMouseListener( ml );

		
		final JScrollPane treeView = new JScrollPane( tree );
		treeView.setPreferredSize( new Dimension( 200, 200 ) );

		this.add( treeView );		
	}
	
	public void
	update()
	{
		top.removeAllChildren();
		createNodes();
		tree.updateUI();
	}
	
    private void
    createNodes()
    {
		final DefaultMutableTreeNode ball = new DefaultMutableTreeNode( "Ball" );
		top.add( ball );
		
		ArrayList< SysObjAttribute > ballAttributes = getBallAttributes();
		
		for( SysObjAttribute attrib : ballAttributes )
		{
			final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( new AttributeHandler( framework, attrib ) );
			ball.add( treeNode );
		}
    }
    
    private ArrayList< SysObjAttribute >
    getBallAttributes()
    {
    	final ArrayList< SysObjAttribute > ballAttributes = new ArrayList< SysObjAttribute >();
    	// Cheat and create the system object attributes manually
    	try
    	{
			ballAttributes.add( new SysObjAttribute( java.awt.Color.class, Ball.class.getMethod( "setColour", java.awt.Color.class ), "Colour" ) );
    	}
    	catch( Exception e )
    	{
    		System.out.println( "Failed to get attribute setter method for ball " + e );
    		e.printStackTrace();
    	}
		
		return ballAttributes;
    }
    
    private void
    mouseNodeRightClick(
    	final Component		component,
    	final Point			point,
    	final TreePath		selPath,
    	final int			selRow
    )
    {
    	System.out.println( "Path: " + selPath + " row: " + selRow );
    	final DefaultMutableTreeNode	selectedNode	= ( DefaultMutableTreeNode )selPath.getLastPathComponent();
    	final Object					myObj			= selectedNode.getUserObject();

		// TODO: BAD use of instanceof!  Could maybe use a hashtable to get around this...
    	if( myObj instanceof AttributeHandler )
    	{
    		( ( AttributeHandler )myObj ).showPopup( component, point );
    	}
    }
}