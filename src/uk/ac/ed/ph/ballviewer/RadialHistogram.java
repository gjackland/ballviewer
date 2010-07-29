package uk.ac.ed.ph.ballviewer;

import java.awt.Color;

public class RadialHistogram extends Histogram
{
	static int	xOff	= 0, yOff = 0, dyOff = 50;

	public RadialHistogram( String n, double mn, double mx, double stp )
	{
		super( n, mn, mx, stp );
		name = "radial " + n;
		W = 450;
	}

	public RadialHistogram( String n, double mn, double mx, int nbars )
	{
		super( n, mn, mx, nbars );
		name = "radial " + n;
		W = 450;
	}

	@Override
	public void draw()
	{ // some reference marks
		super.draw();
		gr.setColor( Color.red );
		gr.drawLine( 200, 0, 200, 4 );
		gr.drawLine( 320, 0, 320, 40 );
		gr.drawLine( 400, 0, 400, 4 );
		gr.setColor( Color.blue );
		gr.drawLine( 267, 0, 267, 4 ); // = 4/3
		gr.drawLine( 533, 0, 533, 4 ); // = 8/3
		gr.drawLine( 733, 0, 733, 4 ); // = 11/3
		int m = ( int )( mean( 0.1, 2.0 ) * 200 );
		gr.setColor( Color.yellow );
		gr.drawLine( m, 0, m, 60 );
		setLocation( xOff, yOff );
		yOff += dyOff;

		canv.update( canv.getGraphics() );
	}
}