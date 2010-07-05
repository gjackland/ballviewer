package uk.ac.ed.ph.ballviewer;

import java.lang.*;
import java.awt.*;

import uk.ac.ed.ph.ballviewer.math.*;

import uk.ac.ed.ph.ballviewer.analysis.AttributeDefault;
import uk.ac.ed.ph.ballviewer.analysis.AttributeMethod;

/** 
 * Represents a ball for 2d or 3d drawing. <p>
 * Ball does NOT extend Particle and has none of its functionality. 
 * It is specified by a position (Vector3), a diameter and a colour. 
 */
public class Ball extends SystemObject implements Positionable
{
	public static final double	DEFAULT_DIAMETER = 50.0d;
	@AttributeDefault( name = "Colour" )
	public static final Color	DEFAULT_COLOUR			= Color.gray;
	@AttributeDefault( name = "Size" )
	public static final double 	DEFAULT_DIAMETER_OFFSET	= 0d;
	@AttributeDefault( name = "Transparency" )
	public static final double	DEFAULT_ALPHA			= 10d;
	
	public static double	newDiameter = DEFAULT_DIAMETER;

	Vector3			pos;
	private double			diameter	= DEFAULT_DIAMETER_OFFSET;
	private Color 			colour		= DEFAULT_COLOUR;
	private double 			alpha		= DEFAULT_ALPHA;
	                    	
	private double			diameterOffset;		// Tmp
	
	Ball ball;   // this is for drawing plane arrows; there shouldn't be more than 1 arrow per Ball
	
	public Ball() {
		this.pos		= new Vector3();
		this.diameter	= newDiameter;
		this.colour		= Color.gray;
	}
	public Ball(Color c)
	{
		this.pos		= new Vector3();
		this.diameter	= newDiameter;
		this.colour		= c;
	}
	public Ball(Vector3 posin, Color c) {
		this.pos		= posin;
		this.diameter	= newDiameter;
		this.colour		= c;
	}
	public Ball(double d, Color c) {
		this.pos = new Vector3();
		this.diameter = d;
		this.colour= c;
	}
	public Ball(
		final Vector3	posin,
		final double	d,
		final Color		col
	)
	{
  		this.pos		= posin; 
		this.diameter	= d; 
		this.colour		= col; 
	}
	
	// Copy constructor
	public Ball( final Ball bToCopy )
	{
		pos				= new Vector3( bToCopy.pos );
		colour			= bToCopy.colour;
		diameter		= bToCopy.diameter;
		diameterOffset	= bToCopy.diameterOffset;
	}
	
	public void resetBall(Vector3 posin, double d, Color col) {
		this.pos 		= posin;
		this.diameter	= d;
		this.colour		= col;
	}
	
	public Vector3
	getPosition()
	{
		return ( Vector3 )pos.clone();
	}
	
	@AttributeMethod( name	= "Colour" )
	public void
	setColour( Color newColour )
	{
		colour = newColour;
	}
	
	@AttributeMethod( name = "Transparency" )
	public void
	setAlpha( final double alpha )
	{
		this.alpha	= alpha;
	}
	
	public Color
	getColour()
	{
		return colour;
	}
	
	public double
	getAlpha()
	{
		return alpha;
	}
	
	public double
	getDiameter()
	{
		return diameter;
	}
	
	@AttributeMethod( name = "Size" )
	public void
	setDiameterOffset( final double offset )
	{
		diameterOffset = offset;
	}
	
	public double
	getDiameterOffset()
	{
		return diameterOffset;
	}
	
	public Vector3 pos() { return pos; }
	public void move(double x, double y, double z) { pos.x+=x; pos.y+=y; pos.z+=z; }
	public void move(Vector3 v) { pos.x+=v.x; pos.y+=v.y; pos.z+=v.z; }
	public Object copy() { return new Ball((Vector3)this.pos.clone(),this.diameter,this.colour); }

}

interface Positionable {
	Vector3 pos();
	void move(Vector3 v);
	void move(double x,double y,double z);
	Object copy();
}