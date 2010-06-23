package uk.ac.ed.ph.ballviewer.event;

public class TimelineEvent implements BallViewerEvent< TimelineListener >
{
	final int currentSample;
	
	public
	TimelineEvent( final int currentSample )
	{
		this.currentSample = currentSample;
	}
	
	@Override
	public void
	notify( final TimelineListener listener )
	{
		listener.timelineChanged( currentSample );
	}
}