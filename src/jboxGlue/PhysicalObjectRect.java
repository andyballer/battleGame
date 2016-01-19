package jboxGlue;

import jgame.JGColor;

import org.jbox2d.collision.PolygonDef;

public class PhysicalObjectRect extends PhysicalObject
{
	private double myWidth;
	private double myHeight;
	private double[] myPolyx;
	private double[] myPolyy;
	
	public PhysicalObjectRect( String id, int collisionId, JGColor color, double width, double height )
	{
		this( id, collisionId, color, width, height, 0 );
	}
	
	public PhysicalObjectRect( String id, int collisionId, JGColor color, double width, double height, double mass )
	{
		super( id, collisionId, color );
		init( width, height, mass );
	}
	
	public PhysicalObjectRect( String id, int collisionId, String gfxname, double width, double height )
	{
		this( id, collisionId, gfxname, width, height, 0 );
	}
	
	public PhysicalObjectRect( String id, int collisionId, String gfxname, double width, double height, double mass )
	{
		super( id, collisionId, gfxname );
		init( width, height, mass );
	}
	
	public void init( double width, double height, double mass )
	{
		// save arguments
		myWidth = width;
		myHeight = height;
		
		// init defaults
		myPolyx = null;
		myPolyy = null;
		
		// make it a rect
		PolygonDef shape = new PolygonDef();
		shape.density = (float)mass;
		shape.setAsBox( (float)width, (float)height );
		createBody( shape );
		setBBox( -(int)width/2, -(int)height/2, (int)width, (int)height );
	}
	
	@Override
	public void paintShape( )
	{
		if( myPolyx == null || myPolyy == null )
		{
			// allocate memory for the polygon
			myPolyx = new double[4];
			myPolyy = new double[4];
		}
		
		// draw a rotated polygon
		myEngine.setColor( myColor );
		double cos = Math.cos( myRotation );
		double sin = Math.sin( myRotation );
		double halfWidth = myWidth/2;
		double halfHeight = myHeight/2;
		myPolyx[0] = (int)( x - halfWidth * cos - halfHeight * sin );
		myPolyy[0] = (int)( y + halfWidth * sin - halfHeight * cos );
		myPolyx[1] = (int)( x + halfWidth * cos - halfHeight * sin );
		myPolyy[1] = (int)( y - halfWidth * sin - halfHeight * cos );
		myPolyx[2] = (int)( x + halfWidth * cos + halfHeight * sin );
		myPolyy[2] = (int)( y - halfWidth * sin + halfHeight * cos );
		myPolyx[3] = (int)( x - halfWidth * cos + halfHeight * sin );
		myPolyy[3] = (int)( y + halfWidth * sin + halfHeight * cos );
		myEngine.drawPolygon( myPolyx, myPolyy, null, 4, true, true );
	}
}