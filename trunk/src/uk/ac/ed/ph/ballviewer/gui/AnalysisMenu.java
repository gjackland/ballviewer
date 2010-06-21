package uk.ac.ed.ph.ballviewer.gui;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;
import uk.ac.ed.ph.ballviewer.analysis.AnalysisManager;

public class AnalysisMenu extends JMenu
{
	/*
	 *	Class to represent a menu for an individual analyser
	 *
	 */
	private class AnalyserMenu extends JMenu implements ActionListener
	{
		private final		JFrame				owner;
		private final		AnalysisManager		analysisManager;	// The manager responsible for managing analysis operations
		private final		Analyser			analyser;			// The analyser that this class represents the menu for

		// Menu item to bring up analyser options if it has any
		private final		JMenuItem			miOptions;
		
		AnalyserMenu(
			final JFrame			owner,
			final AnalysisManager	analysisManager,
			final Analyser			analyser
		)
		{
			super();
			if( analysisManager == null )
			{
				throw new IllegalArgumentException( "Cannot instantiate AnalyserMenu with null analysis manager" );
			}
			if( analyser == null )
			{
				throw new IllegalArgumentException( "Cannot instantiate AnalyserMenu with null analyser" );
			}
			
			this.owner				= owner;
			this.analysisManager	= analysisManager;
			this.analyser			= analyser;
			
			this.setText( analyser.getName() );
			
			// Allow menus to overlap over lightweight JOGL canvas
			JPopupMenu.setDefaultLightWeightPopupEnabled( false );
			
			
			// Set up the options menu item if necessary
			if( analyser.getOptions() != null )
			{
				miOptions	= new JMenuItem( "Options" );
				miOptions.addActionListener( this );
				this.add( miOptions );
			}
			else
			{
				miOptions	= null;
			}
		}
		
		public void
		actionPerformed( ActionEvent e )
		{
			// Find out which operation has been selected
			Object evtSource = e.getSource();
			if( evtSource == miOptions )
			{
				AnalyserOptionsDialog dialog = new AnalyserOptionsDialog( owner, analyser );
				dialog.setVisible( true );
			}
		}
	}
	
	private final 	JFrame											owner;				// The frame that this menu is being displayed in
	private final 	AnalysisManager									analysisManager;	// The analysis manager
		          	
	private final 	JMenu											mAnalysisBalls	=
		new JMenu( "Ball Analysers" );
	// Group all the ball analysers together as only one can be selected at a time
	private 		ButtonGroup										bgBallAnalysers;
	
	
	public
	AnalysisMenu(
		final JFrame											owner,
		final AnalysisManager									analysisManager
	)	
	{
		super( "Analysis" );
		this.owner				= owner;
		this.analysisManager	= analysisManager;
		
		// Allow menus to overlap over lightweight JOGL canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled( false );	
		
		// Populate all the menus with the current analysers
		updateAnalysers();
		
		this.add( mAnalysisBalls );
	}
	
	public void
	updateAnalysers()
	{
		mAnalysisBalls.removeAll();
		
		// Now fill up the menu with all the analysers from the manager
		ButtonGroup bgBallAnalysers = new ButtonGroup();
		for( Analyser analyser : analysisManager.getAllAnalysers() )
		{
			final AnalyserMenu analyserMenu = new AnalyserMenu( owner, analysisManager, analyser );
			mAnalysisBalls.add( analyserMenu );
		}
	}
}