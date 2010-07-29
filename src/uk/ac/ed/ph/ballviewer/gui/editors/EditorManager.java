package uk.ac.ed.ph.ballviewer.gui.editors;

import java.util.Hashtable;

public class EditorManager
{
	private final Hashtable< Class< ? >, Class< ? extends ReflectionType > >	editorRegistry	= new Hashtable< Class< ? >, Class< ? extends ReflectionType > >();

	public ReflectionType findEditor( final Class< ? > targetType )
	{
		Class< ? extends ReflectionType > editorClass = editorRegistry.get( targetType );
		System.out.println( "EditorManager: being asked for " + targetType + " and got " + editorClass );

		if( editorClass != null )
		{
			try
			{
				return editorClass.newInstance();
			}
			catch( InstantiationException e )
			{
				System.out.println( "Failed to instantiate the registered editor " + editorClass + ", likely it doesn't have a default (null) constructor" );
			}
			catch( IllegalAccessException e )
			{
				System.out.println( "Failed to instantiate the registered editor " + editorClass + ", do not have priviliges necessary to call null constructor" );
			}

		}
		return null;
	}

	public void registerEditor( final Class< ? > targetType, final Class< ? extends ReflectionType > editorClass )
	{
		System.out.println( "EditorManager registering " + targetType + " with " + editorClass );
		editorRegistry.put( targetType, editorClass );
	}

}