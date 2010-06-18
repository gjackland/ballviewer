package uk.ac.ed.ph.ballviewer.event;


public interface BallViewerEvent< T >
{
	public void
	notify( final T listener );
}