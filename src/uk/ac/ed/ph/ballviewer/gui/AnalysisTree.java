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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import uk.ac.ed.ph.ballviewer.BallViewerFramework;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;
import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;
import uk.ac.ed.ph.ballviewer.analysis.BallAnalyser;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;

import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachListener;

class AnalysisTree extends JTree
{
	class AttributeHandler implements ActionListener, AttributeAttachListener
	{
		private final 	AnalysisManager			analysisManager;
		private final 	AnalyserOutput			analyserOutput;
		private final 	JPopupMenu				attributePopup;
		private final 	HashMap< JCheckBoxMenuItem, SysObjAttribute >	menuItemMap =
			new HashMap< JCheckBoxMenuItem, SysObjAttribute >();

		AttributeHandler(
			final AnalysisManager		analysisManager,
			final AnalyserOutput		analyserOutput
		)
		{
			this.analysisManager	= analysisManager;
			this.analyserOutput		= analyserOutput;
						
			// Register ourselves to receive attribute attach messages
			BallViewerFramework.eventDispatcher.listen( AttributeAttachEvent.class, this );
			
			attributePopup			= new JPopupMenu( "Attach Attribute" );
			
			generatePopupMenu();
			
		}
		
		private void
		generatePopupMenu()
		{
//			for( AnalyserOutput output : analysisManager.getSupportedOutputs( attribute ) )
//			{
//				final JRadioButtonMenuItem outputItem = new JRadioButtonMenuItem( output.toString() );
//				outputItem.addActionListener( this );
//				attributePopup.add( outputItem );
//				menuItemMap.put( outputItem, output );
//				group.add( outputItem );
//			}
			if( attributePopup.getComponentCount() == 0 )
			{
				final JMenuItem noOutputs = new JMenuItem( "<No Supported Attributes>" );
				noOutputs.setEnabled( false );
				attributePopup.add( noOutputs );
			}
		}
		
		public void
		actionPerformed( ActionEvent evt )
		{
			final SysObjAttribute attribute = menuItemMap.get( evt.getSource() );
			if( attribute != null )
			{
				// Detach the attribute from the current output if attached
				if( analyserOutput.isAttached( attribute ) )
				{
					analyserOutput.detachAttribute( attribute );
				}
				else
				{
					analyserOutput.attachAttribute( attribute );
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
			if( output == analyserOutput )
			{
				for( Map.Entry< JCheckBoxMenuItem, SysObjAttribute > entry : menuItemMap.entrySet() )
				{
					if( entry.getValue() == attribute )
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
			if( output == analyserOutput )
			{
				for( Map.Entry< JCheckBoxMenuItem, SysObjAttribute > entry : menuItemMap.entrySet() )
				{
					if( entry.getValue() == attribute )
					{
						entry.getKey().setState( false );
					}		
				}
			}
		}
		
		// END INTERFACES //////////////////////////////////////////////
	}
	
	private final DefaultMutableTreeNode	rootNode	= new DefaultMutableTreeNode( "Analysis" );
	private final DefaultTreeModel			model		= new DefaultTreeModel( rootNode );
	
	// Maps attributes to their corresponding handler which will deal with popup menus
	// and attaching the attribute to an output etc.
	private final HashMap< AnalyserOutput, AttributeHandler >	handlerMap =
		new HashMap< AnalyserOutput, AttributeHandler >( 5 );
	
	public AnalysisTree(
		final AnalysisManager		analysisManager
	)
	{
		super();
		
		this.setModel( model );
		this.setEditable( false );
		this.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		
		for( Analyser analyser : analysisManager.getAllAnalysers() )
		{
			final DefaultMutableTreeNode analyserNode = new DefaultMutableTreeNode( analyser );
			
			model.insertNodeInto( analyserNode, rootNode, rootNode.getChildCount() );
			
			if( analyser instanceof BallAnalyser )
			{
				for( AnalyserOutput output : ( ( BallAnalyser )analyser ).getOutputs() )
				{
					final DefaultMutableTreeNode outputNode = new DefaultMutableTreeNode( output );
					model.insertNodeInto( outputNode, analyserNode, analyserNode.getChildCount() );
					
					handlerMap.put( output, new AttributeHandler( analysisManager, output ) );
				}
			}
		}
		
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
}