//package uk.ac.ed.ph.ballviewer.gui.editors;
//
//import javax.swing.*;
//
//public class EnumComboBox implements ReflectionType
//{
//	private Object[]		enumConstants;
//	private JComboBox		comboBox;
//
//	
//	public EnumComboBox( Class enumClass, final Reflect reflect )
//	{
//		this.enumConstants	= enumClass.getEnumConstants();
//		comboBox			= new JComboBox( this.enumConstants );
//		
//		for( int i = 0; i < enumConstants.length; ++i )
//		{
//			if( enumConstants[ i ].toString().equals( reflect.defaultValue() ) )
//			{
//				comboBox.setSelectedItem( enumConstants[ i ] );
//				break;
//			}
//		}
//	}
//	
//	public JComponent
//	getComponent()
//	{
//		return comboBox;
//	}
//	
//	public void
//	setValue( final Object value )
//	{
//		
//	}
//	
//	public Object
//	reflectDataFromComponent()
//	{
//		return comboBox.getSelectedItem();
//	}
//}