package uk.ac.ed.ph.ballviewer;

/**
 * 
 * A class to store the physical properties of the system.
 * 
 * 
 */
public class SystemProperties
{
	private SystemCell	supercell;

	public boolean		periodic	= false;

	public SystemProperties()
	{
		supercell = new CellOrdinary();
	}

	public void setSupercell( final SystemCell supercell )
	{
		this.supercell = supercell;
	}

	public SystemCell getSupercell()
	{
		return supercell;
	}

}