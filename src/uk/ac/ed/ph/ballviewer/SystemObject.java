package uk.ac.ed.ph.ballviewer;

public abstract class SystemObject
{
	private int	id;

	void setId( final int id )
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}