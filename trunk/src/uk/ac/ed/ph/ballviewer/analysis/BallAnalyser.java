package uk.ac.ed.ph.ballviewer.analysis;

import uk.ac.ed.ph.ballviewer.util.Options;
import uk.ac.ed.ph.ballviewer.StaticSystem;

public abstract class BallAnalyser< T extends Options > extends Analyser< T >
{
	abstract void
	analyseBalls( StaticSystem system );
}