package uk.ac.ed.ph.ballviewer;

import java.awt.Color;

public class AngularHistogram extends Histogram
{
	static int	xOff	= 700, yOff = 0, dyOff = 50;

	public AngularHistogram( String n, double mn, double mx, double stp )
	{
		super( n, mn, mx, stp );
		name = "angular " + n;
		W = 300;
	}

	public AngularHistogram( String n, double mn, double mx, int nbars )
	{
		super( n, mn, mx, nbars );
		name = "angular " + n;
		W = 300;
	}

	@Override
	public void draw()
	{ // these are some extra reference marks
		super.draw();
		gr.setColor( Color.red );
		gr.drawLine( 50, 0, 50, 4 ); // =-1
		gr.drawLine( 150, 0, 150, 4 ); // =0
		gr.drawLine( 250, 0, 250, 4 ); // =+1
		gr.setColor( Color.blue );
		gr.drawLine( 117, 0, 117, 4 ); // =-1/3 (150-33)
		gr.drawLine( 183, 0, 183, 4 ); // =+1/3 (150+33)
		gr.setColor( Color.green );
		gr.drawLine( 100, 0, 100, 4 ); // =-1/2 (150-50)
		gr.drawLine( 200, 0, 200, 4 ); // =+1/2 (150+50)
		gr.setColor( Color.yellow );
		gr.drawLine( 67, 0, 67, 4 ); // =-5/6
		setLocation( xOff, yOff );
		yOff += dyOff;

		canv.update( canv.getGraphics() );
	}
}
