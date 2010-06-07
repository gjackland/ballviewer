//package uk.ac.ed.ph.ballviewer.gui.editors;
//
//import java.lang.annotation.Annotation;
//import javax.swing.JPanel;
//
//import dust.experiments.frontend.Reflect;
//
//public class ReflectionPanelFactory
//{
//	// Can't construct
//	private ReflectionPanelFactory() {}
//	
//	
//	public static ReflectionType generatePanel( final Class parameter, final Reflect reflect )
//	{
//		ReflectionType reflector = null;
//		
//		// Determine what type of parameter it is here
//		String paramTypeName = parameter.getName();
//		
//		if( parameter.isPrimitive() )	// Primitives
//		{
//			if( paramTypeName.equals( "int" ) )
//			{
//				reflector		= new IntegerTextField( reflect.defaultValue().isEmpty() ? "0" : reflect.defaultValue() );
//			}
//			else if( paramTypeName.equals( "double" ) )
//			{
//				reflector		= new DoubleTextField( reflect.defaultValue().isEmpty() ? "0.0" : reflect.defaultValue() );
//			}
//			else if( paramTypeName.equals( "boolean" ) )
//			{
//				reflector		= new BooleanCheckBox( Boolean.valueOf( reflect.defaultValue() ) );
//			}
//		}
//		else if( parameter.isEnum() )	// Enums
//		{
//			reflector		= new EnumComboBox( parameter, reflect );
//		}
//		else if( parameter.isArray() )	// Arrays
//		{
//			Class componentType = parameter.getComponentType();
//			if( componentType.equals( java.io.File.class ) )
//			{
//				reflector		= new FileTextFieldWithFileChooser( reflect.defaultValue(), true );
//			}
//			if( componentType.equals( javax.vecmath.Vector3d.class ) )
//			{
//				reflector		= new Vector3dArrayTextFields( reflect.defaultValue() );
//			}
//		}
//		else
//		{
//			// It is not a primitive, check if we know what to do with a class of this type
//			if( parameter.equals( javax.vecmath.Vector3d.class ) )
//			{
//				reflector		= new Vector3dTextFields( reflect.defaultValue() );
//			}
//			else if( parameter.equals( dust.experiments.Experiment.class ) )
//			{
//				reflector		= new ExperimentReflector();
//			}
//			else if( parameter.equals( dust.experiments.ExperimentBatch.class ) )
//			{
//				reflector		= new ExperimentBatchReflector();
//			}
//			else if( parameter.equals( java.io.File.class ) )
//			{
//				reflector		= new FileTextFieldWithFileChooser( reflect.defaultValue(), false );
//			}
//			else
//			{
//				reflector		= new GenericClassReflector( parameter, 0 );
//			}
//		}
//		
//		return reflector;
//	}
//}