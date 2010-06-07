package uk.ac.ed.ph.ballviewer.analysis;

import java.awt.Color;

import uk.ac.ed.ph.ballviewer.util.Options;


public class SingleValueBallAnalyserOptions extends Options
{
	public		Boolean		useLogscale	= false;
	public 		Color		minColour	= Color.blue;
	public		Color		maxColour	= Color.red;
	
	SingleValueBallAnalyserOptions() {}
	
	public Object
	clone()
	{
		SingleValueBallAnalyserOptions clone = new SingleValueBallAnalyserOptions();
		
		clone.useLogscale	= useLogscale;
		clone.minColour		= minColour;
		clone.maxColour		= maxColour;
		
		return clone;
	}
}