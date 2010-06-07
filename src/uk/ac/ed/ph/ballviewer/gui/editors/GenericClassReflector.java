//package uk.ac.ed.ph.ballviewer.gui.editors;
//
//import java.lang.reflect.InvocationTargetException;
//import javax.swing.*;
//
//public class GenericClassReflector implements ReflectionType
//{
//	private ClassReflectionPanel	reflectionPanel;
//	
//	public GenericClassReflector( Class classToReflect, final int constructorNo )
//	{
//		reflectionPanel = new ClassReflectionPanel( classToReflect, constructorNo );
//	}
//	
//	public JComponent
//	getComponent()
//	{
//		return reflectionPanel;
//	}
//	
//	public Object
//	getValue()
//	throws InstantiationException,
//	     IllegalAccessException,
//	     IllegalArgumentException,
//	     InvocationTargetException
//	{
//		return reflectionPanel.getReflectedClassInstance();
//	}
//}