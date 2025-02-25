package SETU;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * LinearAim - a robot by Kevin M
 */
public class LinearAim extends Robot
{
	
	double oppX;
	double oppY;
	
	boolean oppSeen = false;
	
	final double robotWidth = 36.0;		// width of robot for (width/2) 
	final double halfRW = robotWidth/2;	//  Width/2 for stopping prediction when robot out of bounds
	final double arenaSize = 800.0;		// final is like const

	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		
		while(!oppSeen)
		{
			turnGunRight(20);
		}
		
		while(true) {
			
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (!e.isSentryRobot())
		{
			oppSeen = true;
			oppCalculation(e.getDistance(), e.getBearing());
			linearFiring(e.getHeading(), e.getVelocity());
		}
	}
	
	public void linearFiring(double t_oppHeading, double t_oppVelocity)
	{
		double xPos = getX();		// Our bot's X coords
		double yPos = getY();		// Our bot's Y coords
		
		double timeElapsed = 1;	// since distance cannot be 0, we start with 1	
		// time elapsed (for calculation of prediction) in while loop (PS. NOT real-time)
		
		double bulletPower = 3.0;	
		
		double predictedX = oppX;	// Opp's X that is calculated with time
		double predictedY = oppY; 	// Opp's Y that is calculated with time
		
		double placeHolderX;
		double placeHolderY;
		
		double gunHeading = getGunHeading();	// where our gun is pointing
		
		double bulletVelocity = 20-3.0*bulletPower;	// speed of bullet based on robocode formula
		
		while((timeElapsed) * bulletVelocity < Point2D.Double.distance(xPos,yPos, predictedX, predictedY))
		// Point2D.Double.distance is used to find the magnitude of distance vector between two points (our bot and opp's predicted location)
		// if velocity is LESS than pred location, bullet will fall short => we run loop till they are equal.
		{
			predictedX += Math.sin(t_oppHeading) * t_oppVelocity;
			predictedY += Math.cos(t_oppHeading) * t_oppVelocity;
			
			if(predictedX < halfRW || predictedY < halfRW || predictedX > arenaSize - halfRW || predictedY > arenaSize - halfRW) 
			{
        		predictedX = Math.min(Math.max(halfRW, predictedX), arenaSize - halfRW);	 
        		predictedY = Math.min(Math.max(halfRW, predictedY), arenaSize - halfRW);
				// ^ making sure the predicted values are valid
				break;
			}
			
			timeElapsed++;
		}
		
		double angle = normalAbsoluteAngle(Math.atan2(predictedX - xPos, predictedY-yPos));	
		// atan2 gives O/P in radians
		// using atan2 and not atan coz it's not limited by range (codomain)
		
		turnGunRight(normalRelativeAngleDegrees(Math.toDegrees(angle - Math.toRadians(gunHeading))));
		// (angle - Math.toRadians(gunHeading)) gives angle to turn gun in Radians (0-2pi)
		// (Math.toDegrees(angle - Math.toRadians(gunHeading))) converts ^ angle to Degrees (0-360)
		// normalRelativeAngleDegrees to normalize relative angles ((-180)-0 or 0-180) for optimization
		
		oppSeen = false;
		fire(bulletPower);

	}
	
	public void oppCalculation(double t_distance, double t_bearing)
	{
		oppX = getX() + t_distance * Math.sin(Math.toRadians(getHeading() + t_bearing));
		oppY = getY() + t_distance * Math.cos(Math.toRadians(getHeading() + t_bearing));
	}
}
