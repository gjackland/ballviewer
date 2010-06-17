package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;

import java.io.*;

import java.lang.Class;
import java.lang.ClassLoader;

import uk.ac.ed.ph.ballviewer.StaticSystem;
import uk.ac.ed.ph.ballviewer.BallViewerFramework;

public class AnalysisManager implements AnalyserChangeListener
{
	private class ClassFilter implements FilenameFilter
	{
		public boolean accept( File dir, String name )
		{
			return ( name.endsWith( ".class" ) );
		}
	}
	
	private static final ArrayList< Class< ? extends BallAnalyser > > ballAnalyserRegistry	=
		new ArrayList< Class< ? extends BallAnalyser > >();
	
	static
	{
		final String 		packagePath		= AnalysisManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();		
		final File			analysersDir	= new File( packagePath );
		final ClassLoader	clsLoader		= AnalysisManager.class.getProtectionDomain().getClassLoader();
		
//		for( File f : analysersDir.listFiles() )
//		{
//			if( f.toString().endsWith( ".class" ) )
//			{
//				try
//				{
//				System.out.println( "Trying to load class " + f.toString() );
//				final Class analyserClass	= clsLoader.loadClass( f.toString() );
//				System.out.println( "Loaded class " + analyserClass );
//				}
//				catch( Exception e )
//				{
//					System.out.println( "shit happened" );
//				}
//			}
//		}

		// TODO: Faking this for now, we'll add proper plugin support later
		ballAnalyserRegistry.add( CrystalAnalyser.class );
		//ballAnalyserRegistry.add( TestAnalyser.class );
	}
	
	private final		BallViewerFramework			framework;
	
	// A list of all the current ball analysers
	private final		ArrayList< BallAnalyser >	ballAnalysers	= new ArrayList< BallAnalyser >();
	
	// A hashtable that maps from a class type to a list of all the analyser outputs that can be attached
	// to a system object attribute of that type
	private final		Hashtable< Class, HashSet< AnalyserOutput > >	outputTypeMap =
		new Hashtable< Class, HashSet< AnalyserOutput > >();
	
	public AnalysisManager( BallViewerFramework framework )
	{
		this.framework		= framework;
		
		reset();
	}
	
	public void
	reset()
	{
		ballAnalysers.clear();
		
		// Let's create instances of classes from the ball analyser registry
		for( Class< ? extends BallAnalyser > bClass  : ballAnalyserRegistry )
		{
			System.out.print( "Adding ball analyser: " + bClass );
			try
			{
				final BallAnalyser newAnalyser = bClass.newInstance();
				addAnalyser( newAnalyser );
			}
			catch( Exception e )
			{
				System.out.println( "Failed to create new instance of " + bClass );
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList< Analyser >
	getBallAnalysers()
	{
		return new ArrayList< Analyser >( ballAnalysers );
	}
	
	public void
	addAnalysers(
		final Collection< Analyser >	newAnalysers
	)
	{
		for( Analyser analyser : newAnalysers )
		{
			addAnalyser( analyser );
		}
	}
	
	public void
	addAnalyser(
		final Analyser				newAnalyser )
	{
		if( newAnalyser instanceof BallAnalyser )
		{
			System.out.println( "Adding ball analyser: " + newAnalyser.getName() );
			ballAnalysers.add( ( BallAnalyser )newAnalyser );
			newAnalyser.addAnalyserChangeListener( this );
			
			attachBallAnalyser( ( BallAnalyser )newAnalyser );
		}
	}
	
	public boolean 
	applyAnalyser(
		final	Analyser		analyser
	)
	{
//		// Make sure we can find the analyser
//		if( ballAnalysers.contains( analyser ) )
//		{
//			( ( BallAnalyser )analyser ).analyseBalls( framework.getSystem() );
//			return true;
//		}
//		return false;
		update( framework.getSystem() );
		return true;
	}
	
	public Set< AnalyserOutput >
	getSupportedOutputs(
		final SysObjAttribute		attribute
	)
	{
		final HashSet< AnalyserOutput > outputs = outputTypeMap.get( attribute.getAttributeClassType() );
		return outputs != null ? outputs : new HashSet< AnalyserOutput >();
	}
	
	public void
	update(
		final StaticSystem		system
	)
	{
		updateBallAnalysers( system );
	}
	
	public void
	updateBallAnalysers(
		final StaticSystem		system
	)
	{
		System.out.println( "Updating output" );
		for( BallAnalyser bAnalyser: ballAnalysers )
		{
			bAnalyser.updateAttributes( system );
		}
	}
	
	public void
	analyserStateChanged( AnalyserChangeEvent e )
	{
		final int indexOfAnalyser = ballAnalysers.indexOf( e.getSource() );
		if( indexOfAnalyser != -1 )
		{
			ballAnalysers.get( indexOfAnalyser ).analyseBalls( framework.getSystem() );
		}
	}
	
	
	// TODO: Change this method name to registerBallAnalyser
	public void
	attachBallAnalyser(
		final		BallAnalyser		newAnalyser
	)
	{
		System.out.println( "Attaching ball analyser: " + newAnalyser.getName() );
		ballAnalysers.add( newAnalyser );
		
		AnalyserOutput[] outputs	= newAnalyser.getOutputs();
		if( outputs != null )
		{
			for( AnalyserOutput output: newAnalyser.getOutputs() )
			{
				updateOutputTypeMap( output );
			}
		}
	}
	
	private void
	updateOutputTypeMap(
		final AnalyserOutput	output
	)
	{
		Set< Class > supportTypeSet = output.getSupportedAttributeTypes();
		for( Class supportedType: supportTypeSet )
		{
			HashSet< AnalyserOutput > outputList = outputTypeMap.get( supportedType );
			if( outputList == null )
			{
				outputList = new HashSet< AnalyserOutput >();
				outputTypeMap.put( supportedType, outputList );
			}
			outputList.add( output );
		}
	}
}