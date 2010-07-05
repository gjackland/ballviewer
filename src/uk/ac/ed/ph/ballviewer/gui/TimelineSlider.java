package uk.ac.ed.ph.ballviewer.gui;

import javax.swing.JSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.ed.ph.ballviewer.BallViewerFramework;
import uk.ac.ed.ph.ballviewer.ExperimentRecord;

import uk.ac.ed.ph.ballviewer.event.NewExperimentEvent;
import uk.ac.ed.ph.ballviewer.event.NewExperimentListener;

class TimelineSlider extends JSlider implements ChangeListener, NewExperimentListener
{
	private static final int 	DEFAULT_MIN		= 0;
	private static final int 	DEFAULT_MAX		= 1;
	private static final int 	DEFAULT_VAL		= 0;
	
	private final				BallViewerFramework				framework;
	
	TimelineSlider(
		final BallViewerFramework	framework
	)
	{
		super( JSlider.HORIZONTAL, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_VAL );
		this.framework	= framework;
		
		setEnabled( false );
		setMajorTickSpacing( 1 );
		setSnapToTicks( true );
		setPaintLabels( true );
		addChangeListener( this );
		
		// Register to be notified when a new experiment is loaded
		framework.eventDispatcher.listen( NewExperimentEvent.class, this );
	}
	
	
	// INTERFACES /////////////////////////////////////////////////////////////
	@Override
	public void
	newExperiment( final ExperimentRecord newExperiment )
	{
		setValue( DEFAULT_VAL );
		if( newExperiment == null )
		{

			setMaximum( DEFAULT_MAX );
			setEnabled( false );
		}
		else
		{
			setMaximum( newExperiment.getNumerOfSamples() - 1 );
			setEnabled( true );
		}
	}
	
	@Override
	public void
	stateChanged( final ChangeEvent e )
	{
		// The slider has changed value		    
		framework.tmpSetCurrentSample( getValue() );
	}
}