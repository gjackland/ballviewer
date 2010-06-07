package uk.ac.ed.ph.ballviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.text.*;
import java.util.ArrayList;

/** 
 * This is a viewer for 3d configurations of objects, especially balls. <br>
 * Gets input from mouse dragging and from input components
 * (buttons, textfields, checkboxes) in order to adjust the display.
 */
public class SmileyViewer extends BallViewer
{
	/** 
	 * Creates a new SmileyViewer. <br>
	 * Assigns title. Displays contents of <code>cellsIn</code>. <br>
	 * Sets the dimensions of the projection screen, as if it was at z=0.0 relative to camera.
	 */
	public SmileyViewer(
		final double		xmn,
		final double		ymn,
		final double		xmx,
		final double		ymx
	)
	{
		super( xmn,ymn, xmx,ymx );
	}

	/** Draws the balls in the given graphics context */
	public void drawBalls(Graphics2D g)
	{   // used to display and also to "Save Image")
		if( cells != null )
		{
			final Ball pinkMarker = new Ball(.01,Color.pink);
			final int n = 5;
			int ix,iy,iz,i;
			Object obj;
			
			
			g.clearRect(0,0,frw,frh);					// wipe it clean
			g.translate(-fr0x,-fr0y);
			
			SortTree tr = new SortTree();	
			tr.addNode(pinkMarker,0.0);		      // adds a pink marker always at origin
			
			// Get whichever set of objects you wish, and add them to SortTree //
	//		for (ix=lc.x-n;ix<lc.x+n;ix++) for (iy=lc.y-n;iy<lc.y+n;iy++) for (iz=lc.z-n;iz<lc.z+n;iz++) {                  	
			for (ix=0;ix<=cells.X()+1;ix++) for (iy=1;iy<=cells.Y();iy++) for (iz=1;iz<=cells.Z();iz++) { 
				ArrayList< Positionable > temp = cells.getCellObjectList(ix,iy,iz); 
				for( Positionable p : temp )
				{
					if( p instanceof Ball )
					{   // we transform Balls and Arrows differently
						Ball b = ( Ball )p;
						b = new Ball (transform.appliedTo(b.pos), b.getColour() ); 
						tr.addNode(b,b.pos.z); 			// and then put them into the SortTree
					}
					else if( p instanceof Arrow )
					{
						Arrow a = ( Arrow )p;
						a = new Arrow (transform.appliedTo(a.v1),transform.appliedTo(a.v2));
						tr.addNode(a,a.ctr.z-0.001*cells.cellSize());	
					}		// ditto, but place arrow cameraside of the ball
				}
			}
			// Alternatively can input a set of balls and just do tr.addNode(b[i],b[i].pos.z) //
			tr.resetTraverser();
			final double camdepth = -30.0;
			
			for(i=0;i<tr.total;i++){
				tr.findNextLargest();
				obj=tr.object();		
				double z=tr.value();
				
				if (obj.getClass()==Ball.class) {
					Ball b = (Ball)obj;
					double zsc;
					if (perspective) {
						if (z<0.5*camdepth) break;   		// we can dump the remainder (depthSort)
						zsc = -camdepth/(z-camdepth); 	// < this is such that zsc = 1.0 at z=0;
					}
					else zsc = 1.0;
				
					double d=b.diameter*zsc;
					double r=d/2;
					double cx = b.pos.x*zsc*xsc;
					double cy = b.pos.y*zsc*ysc;
					g.setColor( b.getColour() );
				
					if (!sliceOn || (z>fslice && z<bslice)) {
						g.fillOval((int)(cx-r),(int)(cy-r),(int)d,(int)d);
						g.setColor(Color.black);
						g.drawOval((int)(cx-r),(int)(cy-r),(int)(d-1),(int)(d-1));
						if (d>=10) { 
							final double s = 0.7; // smile //
							final double ep = 0.5;
							final double ew = 0.2; // smile
							g.drawArc((int)(cx-r*s),(int)(cy-r*s),(int)(d*s-1),(int)(d*s-1),180,181); 
							g.fillOval((int)(cx-r*ep),(int)(cy-r*ep),(int)(r*ew),(int)(r*ew)); 
							g.fillOval((int)(cx+r*(ep-ew)),(int)(cy-r*ep),(int)(r*ew),(int)(r*ew)); 
						}
					}
					else if ( (ffadeOn && z>ffade && z<fslice) || (bfadeOn && z>bslice && z<bfade) ) {
						g.drawOval((int)(cx-d/2)+1,(int)(cy-d/2)+1,(int)d-3,(int)d-3);
						g.setColor(Color.black);
						g.drawOval((int)(cx-d/2),(int)(cy-d/2),(int)d-1,(int)d-1);
					}
				}
				else if (obj.getClass()==Arrow.class) {
					Arrow a = (Arrow)obj;
					if (perspective) { // ends could be at different depths: should be done separately
						if (a.v1.z < 0.2*camdepth || a.v2.z < 0.2*camdepth) continue;   
						a.v1.perspectivise(camdepth); // we can merrily ruin these 
						a.v2.perspectivise(camdepth); //  temporary arrows
					}
					if (!sliceOn || (z>fslice && z<bslice)) {
						int x1 = (int)(a.v1.x*xsc);
						int y1 = (int)(a.v1.y*ysc);
						int x2 = (int)(a.v2.x*xsc);
						int y2 = (int)(a.v2.y*ysc);
						g.setColor(Color.yellow);
						g.drawLine(x1,y1,x2,y2);
					}
				}				
			}
			g.setColor(labelColor); g.translate(fr0x,fr0y); // sets translation back to (0,0)
		}
		g.drawRect(0,0,frw-1,frh-1);
		
		g.dispose();
	}

}
