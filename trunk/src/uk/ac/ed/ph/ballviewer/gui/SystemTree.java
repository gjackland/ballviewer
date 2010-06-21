package uk.ac.ed.ph.ballviewer.gui;

import java.awt.Component;
import java.awt.Point;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import uk.ac.ed.ph.ballviewer.Ball;
import uk.ac.ed.ph.ballviewer.BallViewerFramework;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;
import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.BallAnalyser;
import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;

import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachListener;

class SystemTree extends JTree implements AttributeAttachListener
{
	class AttributeHandler implements ActionListener, AttributeAttachListener
	{
		private final 	AnalysisManager			analysisManager;
		private final 	SysObjAttribute			attribute;
		private final 	JPopupMenu				attributePopup;
		private final 	HashMap< JCheckBoxMenuItem, AnalyserOutput >	menuItemMap =
			new HashMap< JCheckBoxMenuItem, AnalyserOutput >();
		private			AnalyserOutput			currentOutput;		// The output the attribute is currently attached to

		AttributeHandler(
			final AnalysisManager		analysisManager,
			final SysObjAttribute		attribute
		)
		{
			this.analysisManager	= analysisManager;
			this.attribute			= attribute;
									
			// Register ourselves to receive attribute attach messages
			BallViewerFramework.eventDispatcher.listen( AttributeAttachEvent.class, this );
			
			attributePopup			= new JPopupMenu( "Attach Analyser Output" );
			
			generatePopupMenu();
			
		}
		
		private void
		generatePopupMenu()
		{
			final ButtonGroup group = new ButtonGroup();
			for( AnalyserOutput output : analysisManager.getSupportedOutputs( attribute ) )
			{
				final JCheckBoxMenuItem outputItem = new JCheckBoxMenuItem( output.toString() );
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
				else
				{
					output.attachAttribute( attribute );
					currentOutput	= output;
				}
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
		
		// INTERFACES //////////////////////////////////////////////////
		
		public void
		attributeAttached(
			final AnalyserOutput	output,
			final SysObjAttribute	attribute
		)
		{
			if( attribute == this.attribute )
			{
				for( Map.Entry< JCheckBoxMenuItem, AnalyserOutput > entry : menuItemMap.entrySet() )
				{
					if( entry.getValue() == output )
					{
						entry.getKey().setState( true );
					}		
				}
			}
		}
		
		public void
		attributeDetached(
			final AnalyserOutput	output,
			final SysObjAttribute	attribute
		)
		{
			if( attribute == this.attribute )
			{
				for( Map.Entry< JCheckBoxMenuItem, AnalyserOutput > entry : menuItemMap.entrySet() )
				{
					if( entry.getValue() == output )
					{
						entry.getKey().setState( false );
					}		
				}
			}
		}
		
		// END INTERFACES //////////////////////////////////////////////
	}
	
	private final AnalysisManager			analysisManager;
	private final DefaultMutableTreeNode	rootNode		= new DefaultMutableTreeNode( "System" );
	private final DefaultTreeModel			model			= new DefaultTreeModel( rootNode );
	
	// Maps attributes to their corresponding handler which will deal with popup menus
	// and attaching the attribute to an output etc.
	private final HashMap< SysObjAttribute, AttributeHandler >	handlerMap =
		new HashMap< SysObjAttribute, AttributeHandler >( 5 );
	
	public SystemTree(
		final AnalysisManager		analysisManager
	)
	{
		super();
		
		this.analysisManager	= analysisManager;
		
		// Register ourselves to receive attribute attach messages
		BallViewerFramework.eventDispatcher.listen( AttributeAttachEvent.class, this );
		
		this.setModel( model );
		this.setEditable( true );
		this.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		
		final MouseListener ml = new MouseAdapter()
		{
			public void mousePressed( MouseEvent e )
			{
				if( e.isPopupTrigger() )
				{
					final int		selRow	= getRowForLocation(e.getX(), e.getY());
					final TreePath	selPath = getPathForLocation(e.getX(), e.getY());
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
					final int		selRow	= getRowForLocation(e.getX(), e.getY());
					final TreePath	selPath = getPathForLocation(e.getX(), e.getY());
					if( selRow != -1 )
					{
						mouseNodeRightClick( e.getComponent(), e.getPoint(), selPath, selRow );
					}
				}
			}
		};
		this.addMouseListener( ml );

	}
	
    private void
    createNodes()
    {
		final DefaultMutableTreeNode ball = new DefaultMutableTreeNode( "Ball" );
		model.insertNodeInto( ball, rootNode, rootNode.getChildCount() );
		
		ArrayList< SysObjAttribute > ballAttributes = getBallAttributes();
		
		for( SysObjAttribute attrib : ballAttributes )
		{
			final DefaultMutableTreeNode attribNode = new DefaultMutableTreeNode( attrib );
			handlerMap.put( attrib, new AttributeHandler( analysisManager, attrib ) );
			model.insertNodeInto( attribNode, ball, ball.getChildCount() );
		}
    }
    
    private ArrayList< SysObjAttribute >
    getBallAttributes()
    {
    	final ArrayList< SysObjAttribute > ballAttributes = new ArrayList< SysObjAttribute >();
    	// Cheat and create the system object attributes manually
    	try
    	{
			ballAttributes.add( new SysObjAttribute( java.awt.Color.class, Ball.class.getMethod( "setColour", java.awt.Color.class ), java.awt.Color.gray, "Colour" ) );
			ballAttributes.add( new SysObjAttribute( Double.class, Ball.class.getMethod( "setDiameterOffset", double.class ), Ball.DEFAULT_DIAMETER, "Size" ) );
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
    	return getNextMatch( attribute.toString(), 0, javax.swing.text.Position.Bias.Forward );
    }
    
	public void
	update()
	{
		rootNode.removeAllChildren();
		model.nodeStructureChanged( rootNode );	// Tell the model that we've made majour changes
		createNodes();
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
			model.insertNodeInto( outputNode, attribNode, attribNode.getChildCount() );
			// Now expand the tree
			expandPath( pathToAttribNode );
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
			model.removeNodeFromParent( ( DefaultMutableTreeNode )attribNode.getChildAt( 0 ) );
		}
	}
	
	// END INTERFACES /////////////////////////////////////////////////
}