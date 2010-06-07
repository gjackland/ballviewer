package uk.ac.ed.ph.ballviewer.gui.editors;

import javax.swing.*;

public class IntegerTextField implements ReflectionType< Integer >
{
	private static final	String		DEFAULT_VALUE	= "0";
	private JTextField		textField;
	
	public IntegerTextField()
	{
		textField = new JTextField( DEFAULT_VALUE, 10 );
	}
	
	public JComponent
	getComponent()
	{
		return textField;
	}
	
	public void
	setValue( final Integer value )
	{
		textField.setText( value.toString() );
	}
	
	public Integer
	getValue() throws NumberFormatException
	{
		return Integer.valueOf( textField.getText() );
	}
}