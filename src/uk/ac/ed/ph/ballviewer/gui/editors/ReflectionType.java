package uk.ac.ed.ph.ballviewer.gui.editors;

import javax.swing.*;

public interface ReflectionType< T >
{
	public JComponent
	getComponent();
	     
	public T
	getValue();
	
	public void
	setValue( final T value );
//	throws InstantiationException,
//	     IllegalAccessException,
//	     IllegalArgumentException,
//	     InvocationTargetException;
}