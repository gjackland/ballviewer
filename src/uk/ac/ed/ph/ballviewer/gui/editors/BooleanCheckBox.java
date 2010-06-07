package uk.ac.ed.ph.ballviewer.gui.editors;

import javax.swing.JComponent;
import javax.swing.JCheckBox;

public class BooleanCheckBox implements ReflectionType< Boolean >
{
	private JCheckBox		checkBox;
	
	public BooleanCheckBox()
	{
		checkBox = new JCheckBox();
	}
	
	public JComponent
	getComponent()
	{
		return checkBox;
	}
	
	public void
	setValue( Boolean value )
	{
		checkBox.setSelected( value );
	}
	
	public Boolean
	getValue() throws NumberFormatException
	{
		return Boolean.valueOf( checkBox.isSelected() );
	}
}