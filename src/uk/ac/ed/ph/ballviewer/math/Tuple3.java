package uk.ac.ed.ph.ballviewer.math;


public abstract class Tuple3
{
	public double x;
	public double y;
	public double z;
	
	public Tuple3() {}	// Zero tuple
	
	public Tuple3(
		final Tuple3 tup
	)
	{
		set( tup );
	}
	
	public Tuple3
	subtract(
		final Tuple3 tup
	)
	{
		x -= tup.x;
		y -= tup.y;
		z -= tup.z;
		return this;
	}
	
	
	public void
	set(
		final Tuple3	tup 
	)
	{
		x = tup.x;
		y = tup.y;
		z = tup.z;
	}
}