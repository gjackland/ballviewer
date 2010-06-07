package uk.ac.ed.ph.ballviewer.analysis;

import uk.ac.ed.ph.ballviewer.StaticSystem;
import uk.ac.ed.ph.ballviewer.util.Options;

public class TestAnalyser extends BallAnalyser
{
	private	String	name		= "TestAnalyser";
	private boolean	useLogscale	= false;
	
	public String
	getName()
	{
		return name;
	}
	
	public void
	setName( String newName )
	{
		name = newName;
	}
	
	public void
	analyseBalls( StaticSystem system )
	{
		
	}
	
	public void
	setUseLogscale( final boolean useLogscale )
	{
		this.useLogscale	= useLogscale;
	}
	
	public boolean
	isUseLogscale()
	{
		return useLogscale;
	}
}
