package uk.ac.ed.ph.ballviewer.gui.editors;

import java.awt.Color;
import javax.swing.*;

public class ColorReflector implements ReflectionType< Color >
{
	private static final	Color				DEFAULT_VALUE	= Color.red;
	private					JColorChooser		colorChooser;
	
	public ColorReflector()
	{
		colorChooser = new JColorChooser( DEFAULT_VALUE );
	}
	
	public JComponent
	getComponent()
	{
		return colorChooser;
	}
	
	public void
	setValue( final Color value )
	{
		colorChooser.setColor( value );
	}
	
	public Color
	getValue()
	{
		return colorChooser.getColor();
	}
}