package jboxGlue;

import jgame.JGColor;

import org.jbox2d.collision.CircleDef;

public class PhysicalObjectCircle extends PhysicalObject
{
	private double myRadius;
	
	public PhysicalObjectCircle( String id, int collisionId, JGColor color, double radius )
	{
		this( id, collisionId, color, radius, 0 );
	}
	
	public PhysicalObjectCircle( String id, int collisionId, JGColor color, double radius, double mass )
	{
		super( id, collisionId, color );
		init( radius, mass );
	}
	
	public PhysicalObjectCircle( String id, int collisionId, String gfxname, double radius )
	{
		this( id, collisionId, gfxname, radius, 0 );
	}
	
	public PhysicalObjectCircle( String id, int collisionId, String gfxname, double radius, double mass )
	{
		super( id, collisionId, gfxname );
		init( radius, mass );
	}
	
	private void init( double radius, double mass )
	{
		// save arguments
		myRadius = radius;
		
		// make it a circle
		CircleDef shape = new CircleDef();
		shape.radius = (float)radius;
		shape.density = (float)mass;
		createBody( shape );
		setBBox( -(int)radius, -(int)radius, 2*(int)radius, 2*(int)radius );
	}
	
	@Override
	public void paintShape( )
	{
		myEngine.setColor( myColor );
		myEngine.drawOval( x, y, (float)myRadius*2, (float)myRadius*2, true, true );
	}
}
