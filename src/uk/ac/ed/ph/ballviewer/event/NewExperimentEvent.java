package uk.ac.ed.ph.ballviewer.event;

import uk.ac.ed.ph.ballviewer.ExperimentRecord;

/**
 *
 *	Event fired when a new experiment is loaded.
 *
 */
public class NewExperimentEvent implements BallViewerEvent< NewExperimentListener >
{
	private final ExperimentRecord	newExperiment;
	
	public
	NewExperimentEvent( final ExperimentRecord newExperiment )
	{
		this.newExperiment = newExperiment;
	}
	
	public void
	notify( final NewExperimentListener listener )
	{
		listener.newExperiment( newExperiment );
	}
}