package uk.ac.ed.ph.ballviewer.event;


import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;

public class AttributeAttachEvent implements BallViewerEvent< AttributeAttachListener >
{
	private final boolean 				attached;
	private final AnalyserOutput		output;
	private final SysObjAttribute		attribute;
	
	/**
	 *	Construct a new attribute attach event.
	 *
	 *	@param attached 	true if the attribute was attached, false if detached
	 *	@param output		The analyser output that the attribute was attached to
	 *	@param attribute	The attribute that was attached
	 */
	public AttributeAttachEvent(
		final boolean			attached,
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
		)
	{
		this.attached	= attached;
		this.output		= output;
		this.attribute	= attribute;
	}
	
	
	public void
	notify(
		final AttributeAttachListener listener
	)
	{
		if( attached )
		{
			listener.attributeAttached( output, attribute );
		}
		else
		{
			listener.attributeDetached( output, attribute );
		}
	}
}