package uk.ac.ed.ph.ballviewer.gui.editors;

import java.awt.*;
import javax.swing.*;

import uk.ac.ed.ph.ballviewer.math.Vector3;

public class Vector3TextFields implements ReflectionType< Vector3 >
{
	private static final String DEFAULT_VALUE	= "0.0";
	private static final int	FIELD_LENGTH	= 10;
	private JPanel				vectorPanel;
	private JTextField			x, y, z;
	
	public Vector3TextFields()
	{
		vectorPanel			= new JPanel();
		BoxLayout layout	= new BoxLayout( vectorPanel, BoxLayout.X_AXIS );
		vectorPanel.setLayout( layout );
		
		x = new JTextField( DEFAULT_VALUE, FIELD_LENGTH );
		y = new JTextField( DEFAULT_VALUE, FIELD_LENGTH );
		z = new JTextField( DEFAULT_VALUE, FIELD_LENGTH );
	
		
		JPanel xPanel	= new JPanel();		// Flow layout
		JPanel yPanel	= new JPanel();		// Flow layout
		JPanel zPanel	= new JPanel();		// Flow layout
		
		xPanel.add( new JLabel( "x" ), BorderLayout.WEST );
		xPanel.add( x );
		yPanel.add( new JLabel( "y" ) );
		yPanel.add( y );
		zPanel.add( new JLabel( "z" ) );
		zPanel.add( z );
		
		vectorPanel.add( xPanel );
		vectorPanel.add( yPanel );
		vectorPanel.add( zPanel );
	}
	
	public JComponent
	getComponent()
	{
		return vectorPanel;
	}
	
	public void
	setValue( final Vector3 value )
	{
		x.setText( Double.toString( value.x ) );
		y.setText( Double.toString( value.y ) );
		z.setText( Double.toString( value.z ) );
	}
	
	public Vector3
	getValue()
	{
		return new Vector3(
			Double.valueOf( x.getText() ),
			Double.valueOf( y.getText() ),
			Double.valueOf( z.getText() )
			);
	}
}