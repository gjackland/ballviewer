package uk.ac.ed.ph.ballviewer.util;

public interface Optionable< T extends Options >
{
	public T
	getOptions();
	
	public void
	setOptions(
		final T		newOptions
	) throws IllegalOptionsException;
}