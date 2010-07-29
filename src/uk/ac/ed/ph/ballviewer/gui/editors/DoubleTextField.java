package uk.ac.ed.ph.ballviewer.gui.editors;

import javax.swing.*;

public class DoubleTextField implements ReflectionType< Double >
{
	private static final String	DEFAULT_VALUE	= "0.0";
	private JTextField			textField;

	public DoubleTextField()
	{
		textField = new JTextField( DEFAULT_VALUE, 10 );
	}

	@Override
	public JComponent getComponent()
	{
		return textField;
	}

	@Override
	public void setValue( final Double value )
	{
		textField.setText( value.toString() );
	}

	@Override
	public Double getValue() throws NumberFormatException
	{
		return Double.valueOf( textField.getText() );
	}
}