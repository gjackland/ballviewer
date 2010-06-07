//package uk.ac.ed.ph.ballviewer.gui;
//
//import java.awt.*;
//import javax.swing.*;
//import java.beans.*;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;;
//import java.util.ArrayList;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
//
//
//import uk.ac.ed.ph.ballviewer.analysis.Analyser;
//import uk.ac.ed.ph.ballviewer.gui.editors.*;
//
//class ClassMemberEditDialog extends JDialog implements ActionListener
//{
//	public static final		EditorManager	editorManager		= new EditorManager();
//	static
//	{
//		// Tell the property editor manager where to look to find custom editors
//		//propertyEditorManager.setEditorSearchPath( new String[]{ "uk.ac.ed.ph.ballviewer.gui.editors" } );
//		
//		// Try manually registering the boolean type
//		try
//		{
//			editorManager.registerEditor( boolean.class, uk.ac.ed.ph.ballviewer.gui.editors.BooleanEditor.class );
//			editorManager.registerEditor( Boolean.class, BooleanCheckBox.class );
//			editorManager.registerEditor( Double.class, DoubleTextField.class );
//		}
//		catch( Exception e)
//		{
//			System.out.println( "Exception occured trying to register boolean editor" );
//			e.printStackTrace();
//		}
//		
//	}
//	
//	
//	private final			Object								obj;
//	private final			Field[]								objFields;
//	private final			ReflectionType[]					objFieldReflectors;
//	
//	// GUI Components
//	private final			JButton								bOK		= new JButton( "OK" );
//	private final			JButton								bCancel	= new JButton( "Cancel" );
//	
//	ClassMemberEditDialog(
//		final Frame			owner,
//		final Analyser		analyser
//	)
//	{
//		super( owner, true );
//		this.analyser			= analyser;
//		
//		
//		
//		
//		// OK and Cancel buttons
//		bOK.addActionListener( this );
//		bCancel.addActionListener( this );
//		final JPanel pButtonPanel	= new JPanel();
//		pButtonPanel.add( bOK );
//		pButtonPanel.add( bCancel );
//		this.add( pButtonPanel, BorderLayout.SOUTH );
//		
//		this.pack();
//	}
//	
//	private void
//	addProperty(
//		final PropertyDescriptor	descriptor,
//		final PropertyEditor		editor
//	)
//	{
//		propertyDescriptors.add( descriptor );
//		propertyEditors.add( editor );
//	}
//	
//	private boolean
//	setProperties()
//	{
//		try
//		{
//			for( int i = 0; i < propertyDescriptors.size(); ++i )
//			{
//				propertyDescriptors.get( i ).getWriteMethod().invoke( analyser, propertyEditors.get( i ).getValue() );
//			}
//			return true;
//		}
//		catch( Exception e )
//		{
//			System.out.println( "Exception in calling set method" );
//		}
//		return false;
//	}
//	
//	
//	// Deal with the ok and cancel button events
//	public void actionPerformed( ActionEvent evt )
//	{
//		Object source = evt.getSource();
//		
//		if( source == bOK )
//		{
//			if( setProperties() )
//			{
//				this.setVisible( false );
//			}
//		}
//		else if( source == bCancel )
//		{
//			this.setVisible( false );
//		}
//	}
//}