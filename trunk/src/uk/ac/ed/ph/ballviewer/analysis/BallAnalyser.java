package uk.ac.ed.ph.ballviewer.analysis;

import uk.ac.ed.ph.ballviewer.util.Options;
import uk.ac.ed.ph.ballviewer.StaticSystem;

public abstract class BallAnalyser< T extends Options > extends Analyser< T >
{
	abstract void
	analyseBalls( StaticSystem system );
	
	/**
	 *
	 *	Called by the Analysis Manager to tell the analyser to update 
	 *
	 *
	 */
	abstract void
	updateAttributes( StaticSystem system );
	
	/**
	 *	The analyser should provide an array of outputs that can be mapped onto
	 *	attributes of a ball e.g. ball energy can be mapped onto the ball colour
	 *
	 */
	abstract AnalyserOutput[]
	getOutputs();
}