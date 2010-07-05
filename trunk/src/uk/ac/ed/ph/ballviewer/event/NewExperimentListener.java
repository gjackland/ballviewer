package uk.ac.ed.ph.ballviewer.event;

import uk.ac.ed.ph.ballviewer.ExperimentRecord;

public interface NewExperimentListener
{
	public void
	newExperiment( final ExperimentRecord newExperiment );
}