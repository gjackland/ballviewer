package uk.ac.ed.ph.ballviewer.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;

import java.lang.Class;

import uk.ac.ed.ph.ballviewer.StaticSystem;
import uk.ac.ed.ph.ballviewer.BallViewerFramework;

import uk.ac.ed.ph.ballviewer.event.AnalyserChangeEvent;
import uk.ac.ed.ph.ballviewer.event.AnalyserChangeListener;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachListener;

public final class AnalysisManager implements AnalyserChangeListener, AttributeAttachListener
{
	private static final ArrayList< Class< ? extends Analyser > > defaultAnalyserRegistry	=
		new ArrayList< Class< ? extends Analyser > >();
	
	static
	{
		// Add all the analysers that are available to all system types here
		registerDefaultAnalyser( CrystalAnalyser.class );
		//ballAnalyserRegistry.add( TestAnalyser.class );
	}
	
	private static boolean
	registerDefaultAnalyser( final Class< ? extends Analyser > analyserCls )
	{
		// Check that the class has the required constructor i.e. default zero argumenet constructor
		try
		{
			// This will throw exception if it can't find the method
			analyserCls.getDeclaredConstructor();
			defaultAnalyserRegistry.add( analyserCls );
			return true;
		}
		catch( NoSuchMethodException e )
		{
			return false;
		}
	}
	
	private final		BallViewerFramework			framework;
	
	
	// A list of all the analysers being managed
	private final 		ArrayList< Analyser >		analysers		= new ArrayList< Analyser >();
	
	// A list of all the current ball analysers
	private final		ArrayList< BallAnalyser >	ballAnalysers	= new ArrayList< BallAnalyser >();
	
	// A hashtable that maps from a class type to a list of all the analyser outputs that can be attached
	// to a system object attribute of that type
	private final		Hashtable< Class, HashSet< AnalyserOutput > >	outputTypeMap =
		new Hashtable< Class, HashSet< AnalyserOutput > >();
	
	public AnalysisManager( BallViewerFramework framework )
	{
		this.framework		= framework;
		
		// Register ourselves to receive analyser change events
		BallViewerFramework.eventDispatcher.listen( AnalyserChangeEvent.class, this );
		BallViewerFramework.eventDispatcher.listen( AttributeAttachEvent.class, this );
		
		reset();
	}
	
	public void
	reset()
	{
		ballAnalysers.clear();
		
		// Let's create instances of classes from the ball analyser registry
		for( Class< ? extends Analyser > analyserClass  : defaultAnalyserRegistry )
		{
			System.out.print( "Adding analyser: " + analyserClass );
			try
			{
				final Analyser newAnalyser = analyserClass.newInstance();
				addAnalyser( newAnalyser );
			}
			catch( Exception e )
			{
				System.out.println( "Failed to create new instance of " + analyserClass );
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList< Analyser >
	getAllAnalysers()
	{
		return analysers;
	}
	
	public ArrayList< BallAnalyser >
	getBallAnalysers()
	{
		return ballAnalysers;
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

		System.out.println( "Adding analyser: " + newAnalyser.getName() );
		analysers.add( newAnalyser );
		newAnalyser.initialise( this );
	}
	
	
	public Set< AnalyserOutput >
	getSupportedOutputs(
		final SysObjAttribute		attribute
	)
	{
		final HashSet< AnalyserOutput > outputs = outputTypeMap.get( attribute.getAttributeClassType() );
		return outputs != null ? outputs : new HashSet< AnalyserOutput >();
	}
	
	private void
	update(
		final StaticSystem		system
	)
	{
		updateBallAnalysers( system );
	}
	
	private void
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
	
	
	// TODO: Change this method name to registerBallAnalyser
	public void
	attachBallAnalyser(
		final		BallAnalyser		newAnalyser
	)
	{
		//System.out.println( "Attaching ball analyser: " + newAnalyser.getName() );
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
	
	// INTERFACES //////////////////////////////////////////////////////////
	
	public void
	analyserStateChanged( Analyser source )
	{
		final int indexOfAnalyser = ballAnalysers.indexOf( source );
		if( indexOfAnalyser != -1 )
		{
			ballAnalysers.get( indexOfAnalyser ).updateAttributes( framework.getSystem() );
		}
	}
	
	public void
	attributeAttached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	)
	{
		// Tell the analyser to update the attributes attached to each output
		// TODO: Could make this more specific so analyser only updates a particular output
		// or even a particular attribute of a particular output
		output.getParentAnalyser().updateAttributes( framework.getSystem() );
	}
	
	public void
	attributeDetached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	)
	{
		// Don't need to do anything
	}
	
	
	// END INTERFACES //////////////////////////////////////////////////////
}