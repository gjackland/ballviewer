package uk.ac.ed.ph.ballviewer.gui;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import uk.ac.ed.ph.ballviewer.analysis.Analyser;
import uk.ac.ed.ph.ballviewer.gui.editors.*;
import uk.ac.ed.ph.ballviewer.util.Options;

class AnalyserOptionsDialog extends JDialog implements ActionListener
{
	public static final		EditorManager	editorManager		= new EditorManager();
	static
	{
		// Tell the property editor manager where to look to find custom editors
		//propertyEditorManager.setEditorSearchPath( new String[]{ "uk.ac.ed.ph.ballviewer.gui.editors" } );
		
		// Try manually registering the boolean type
		try
		{
			//editorManager.registerEditor( boolean.class, uk.ac.ed.ph.ballviewer.gui.editors.BooleanEditor.class );
			editorManager.registerEditor( Boolean.class, BooleanCheckBox.class );
			editorManager.registerEditor( Double.class, DoubleTextField.class );
			editorManager.registerEditor( Color.class, ColorReflector.class );
		}
		catch( Exception e)
		{
			System.out.println( "Exception occured trying to register boolean editor" );
			e.printStackTrace();
		}
		
	}
	
	private final			Analyser							analyser;
	private 				Options								obj;
	private 				Field[]								objFields;
	private 				ReflectionType[]					objFieldReflectors;
	
	// GUI Components
	private final			JButton								bOK		= new JButton( "OK" );
	private final			JButton								bCancel	= new JButton( "Cancel" );
	
	AnalyserOptionsDialog(
		final Frame			owner,
		final Analyser		analyser
	)
	{
		super( owner, true );
		this.analyser			= analyser;
		
		final JPanel fieldsList = new JPanel();
		final BoxLayout	box		= new BoxLayout( fieldsList, BoxLayout.Y_AXIS );
		fieldsList.setLayout( box );
		
		obj = analyser.getOptions();
		System.out.println( "Analyser: " + analyser + " options " + obj );
		if( obj != null )
		{
			objFields = obj.getClass().getFields();
			objFieldReflectors	= new ReflectionType[ objFields.length ];
			
			System.out.println( "Options have fields " + objFields );
			for( int i = 0; i < objFields.length; ++i )
			{
				objFieldReflectors[ i ] = editorManager.findEditor( objFields[ i ].getType() );
				System.out.println( "Processing " + objFields[ i ] + " have editor " + objFieldReflectors[ i ] );
				try
				{
					objFieldReflectors[ i ].setValue( objFields[ i ].get( obj ) );
				}
				catch( Exception e )
				{
					System.out.println( "Could not set " + objFieldReflectors[ i ] + " with the value of " + objFields[ i ] );
				}
				
				final JPanel	field = new JPanel();	// FlowLayout
				field.add( new JLabel( objFields[ i ].getName() ) );
				field.add( objFieldReflectors[ i ].getComponent() );
				fieldsList.add( field );
			}
		}
		
		this.add( fieldsList );
		
		// OK and Cancel buttons
		bOK.addActionListener( this );
		bCancel.addActionListener( this );
		final JPanel pButtonPanel	= new JPanel();
		pButtonPanel.add( bOK );
		pButtonPanel.add( bCancel );
		this.add( pButtonPanel, BorderLayout.SOUTH );
		
		this.pack();
	}
	
	
	private boolean
	setProperties()
	{
		try
		{
			for( int i = 0; i < objFields.length; ++i )
			{
				objFields[ i ].set( obj, objFieldReflectors[ i ].getValue() );
			}
			analyser.setOptions( obj );
			return true;
		}
		catch( Exception e )
		{
			System.out.println( "Exception in calling set method" );
		}
		return false;
	}
	
	
	// Deal with the ok and cancel button events
	public void actionPerformed( ActionEvent evt )
	{
		Object source = evt.getSource();
		
		if( source == bOK )
		{
			if( setProperties() )
			{
				this.setVisible( false );
			}
		}
		else if( source == bCancel )
		{
			this.setVisible( false );
		}
	}
}