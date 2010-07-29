package uk.ac.ed.ph.ballviewer.util;

import java.io.Serializable;

public abstract class Options implements Cloneable, Serializable
{
	@Override
	public abstract Object clone();
}