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
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;

import uk.ac.ed.ph.ballviewer.Ball;
import uk.ac.ed.ph.ballviewer.BallViewer;
import uk.ac.ed.ph.ballviewer.BallViewerFramework;

import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;

import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachListener;

public class SystemObjectsPanel extends JPanel implements AttributeAttachListener
{
	class AttributeHandler implements ActionListener
	{
		private final 	BallViewerFramework		framework;
		private final 	SysObjAttribute			attribute;
		private final 	JPopupMenu				attributePopup;
		private final 	Hashtable< JRadioButtonMenuItem, AnalyserOutput >	menuItemMap =
			new Hashtable< JRadioButtonMenuItem, AnalyserOutput >();
		private			AnalyserOutput			currentOutput;		// The output the attribute is currently attached to

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
			final ButtonGroup group = new ButtonGroup();
			for( AnalyserOutput output : framework.getAnalysisManager().getSupportedOutputs( attribute ) )
			{
				final JRadioButtonMenuItem outputItem = new JRadioButtonMenuItem( output.toString() );
				outputItem.addActionListener( this );
				attributePopup.add( outputItem );
				menuItemMap.put( outputItem, output );
				group.add( outputItem );
			}
			if( attributePopup.getComponentCount() == 0 )
			{
				final JMenuItem noOutputs = new JMenuItem( "<No Supported Outputs>" );
				noOutputs.setEnabled( false );
				attributePopup.add( noOutputs );
			}
		}
		
		public void
		actionPerformed( ActionEvent evt )
		{
			final AnalyserOutput output = menuItemMap.get( evt.getSource() );
			if( output != null )
			{
				// Detach the attribute from the current output if attached
				if( currentOutput != null )
				{
					currentOutput.detachAttribute( attribute );
					currentOutput	= null;
				}
				
				output.attachAttribute( attribute );
				currentOutput	= output;
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
	
	// Maps attributes to their corresponding handler which will deal with popup menus
	// and attaching the attribute to an output etc.
	private final HashMap< SysObjAttribute, AttributeHandler >	handlerMap =
		new HashMap< SysObjAttribute, AttributeHandler >( 5 );
	
	public SystemObjectsPanel(
		final BallViewerFramework	framework
	)
	{
		this.framework		= framework;
		
		// Register ourselves to receive attribute attach messages
		framework.getEventDispatcher().listen( AttributeAttachEvent.class, this );
		
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
	
	// INTERFACES ///////////////////////////////////////////////////
	
    // From AttributeAttachListener
	public void
	attributeAttached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	)
	{
		final TreePath			pathToAttribNode = findPathFromFromAttribute( attribute );
		if( pathToAttribNode != null )
		{
			final DefaultMutableTreeNode attribNode = ( DefaultMutableTreeNode )pathToAttribNode.getLastPathComponent();
			final DefaultMutableTreeNode outputNode	= new DefaultMutableTreeNode( output.getName(), false );
			attribNode.add( outputNode );
			// Now expand the tree
			tree.expandPath( pathToAttribNode );
			tree.updateUI();
		}
	}
	
	public void
	attributeDetached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	)
	{
		final TreePath			pathToAttribNode = findPathFromFromAttribute( attribute );
		if( pathToAttribNode != null )
		{
			final DefaultMutableTreeNode attribNode = ( DefaultMutableTreeNode )pathToAttribNode.getLastPathComponent();
			attribNode.removeAllChildren();
			
			tree.updateUI();
		}
	}
	
	// END INTERFACES /////////////////////////////////////////////////
	
    private void
    createNodes()
    {
		final DefaultMutableTreeNode ball = new DefaultMutableTreeNode( "Ball" );
		top.add( ball );
		
		ArrayList< SysObjAttribute > ballAttributes = getBallAttributes();
		
		for( SysObjAttribute attrib : ballAttributes )
		{
			final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( attrib );
			handlerMap.put( attrib, new AttributeHandler( framework, attrib ) );
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
			ballAttributes.add( new SysObjAttribute( Double.class, Ball.class.getMethod( "setDiameterOffset", double.class ), "Size" ) );
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
    	final DefaultMutableTreeNode	selectedNode	= ( DefaultMutableTreeNode )selPath.getLastPathComponent();
    	final AttributeHandler			attribHandler	= handlerMap.get( selectedNode.getUserObject() );


		// TODO: BAD use of instanceof!  Could maybe use a hashtable to get around this...
    	if( attribHandler != null )
    	{
    		attribHandler.showPopup( component, point );
    	}
    }
    
    private TreePath
    findPathFromFromAttribute(
    	final SysObjAttribute	attribute
    )
    {
    	// This could be improved by only searching a depth of 3 but it's not terribly important as the tree is always small
    	return tree.getNextMatch( attribute.toString(), 0, javax.swing.text.Position.Bias.Forward );
    }
}