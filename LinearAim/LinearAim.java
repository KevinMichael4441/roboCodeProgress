package SETU;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.isNear;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * LinearAim - a robot by Kevin M
 */
public class LinearAim extends Robot
{
	double oppX;					// X coords of opp
	double oppY;					// Y coords of opp
	
	final double robotWidth = 36.0;							// width of robot for (width/2) 
	final double halfRW = robotWidth/2 - 0.0004441;			//  Width/2 for stopping prediction when robot out of bounds 
	// subtracting a tiny amount coz of double rounding issues giving values like 17.99999999 or 782.0000000001
	
	final double arenaSize = 800.0;
	// final is like const

	public void run() {
		setColors(Color.red,Color.yellow,Color.yellow); // body,gun,radar
		
		while(true)
		{
			turnRadarRight(45);
		}
		
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (!e.isSentryRobot())
		{
			oppCalculation(e.getDistance(), e.getBearing());
			//dynamicFiring(e.getHeading(), e.getVelocity(), e.getBearing());
			justFire(e.getBearing());
		}
	}
	
	public void linearFiring(double t_oppHeading, double t_oppVelocity)
	{
		out.println("oppVelocity from robocode: " + t_oppVelocity);
		out.println("oppHeading: " + t_oppHeading);
		double xPos = getX();		// Our bot's X coords
		double yPos = getY();		// Our bot's Y coords
		
		out.println("Our X: " + xPos);
		out.println("Our Y: " + yPos);		

		double timeElapsed = 1;	// since distance cannot be 0, we start with 1	
		// time elapsed (for calculation of prediction) in while loop (PS. NOT real-time)
		
		double bulletPower = 3.0;	
		
		double predictedX = oppX;	// Opp's X that is calculated with time
		double predictedY = oppY; 	// Opp's Y that is calculated with time
		
		out.println("oppX: " + oppX);
		out.println("oppY: " + oppY);
			
		out.println("oppVelocity from calculation: " + t_oppVelocity);
		double gunHeading = getGunHeading();	// where our gun is pointing
		
		double bulletVelocity = 20-3.0*bulletPower;	// speed of bullet based on robocode formula
		
		out.println("Math.sin(t_oppHeading): " + Math.sin(t_oppHeading));
		out.println("Math.cos(t_oppHeading): " + Math.cos(t_oppHeading));
		while((timeElapsed) * bulletVelocity < Point2D.Double.distance(xPos,yPos, predictedX, predictedY))
		// Point2D.Double.distance is used to find the magnitude of distance vector between two points (our bot and opp's predicted location)
		// if velocity is LESS than pred location, bullet will fall short => we run loop till they are equal.
		{

			predictedX += Math.sin(Math.toRadians(t_oppHeading)) * t_oppVelocity;
			predictedY += Math.cos(Math.toRadians(t_oppHeading)) * t_oppVelocity;
			
			out.println("Time: " + timeElapsed);
			out.println("predX = " + predictedX);
			out.println("predY = " + predictedY);

			if(predictedX < halfRW || predictedY < halfRW || predictedX > (arenaSize - halfRW) || predictedY > (arenaSize - halfRW)) 
			{
				//sometimes it becomes 17.9999999 and sometimes 782.000000001 => loop will only run once
        		predictedX = Math.max(Math.min(arenaSize - halfRW, predictedX), halfRW);
        		predictedY = Math.max(Math.min(arenaSize - halfRW, predictedY), halfRW);
				// since tank cannot be at <18 or >782, those values would be invalid
				// they cannot be coz of where center of bot lies and bounding box of bot
			
				break;
			}
			
			timeElapsed++;
		}
		
		double angle = normalAbsoluteAngle(Math.atan2(predictedX - xPos, predictedY-yPos));	
		//^ angle to opp
		// atan2 gives O/P in radians
		// using atan2 and not atan coz it's not limited by range (codomain)
		
		turnGunRight(normalRelativeAngleDegrees(Math.toDegrees(angle - Math.toRadians(gunHeading))));
		// (angle - Math.toRadians(gunHeading)) gives angle to turn gun in Radians (0-2pi)
		// (Math.toDegrees(angle - Math.toRadians(gunHeading))) converts ^ angle to Degrees (0-360)
		// normalRelativeAngleDegrees to normalize relative angles ((-180)-0 or 0-180) for optimization
		
		out.println("-----------------------------------");
		if (getGunHeat() == 0)	// calling fire only when gun heat is 0 so as to not waste turns (calling fire() uses a turn)
		{
			fire(bulletPower);
		}

	}
	
	public void oppCalculation(double t_oppDistance, double t_oppBearing)
	{
		oppX = getX() + t_oppDistance * Math.sin(Math.toRadians(getHeading() + t_oppBearing));
		oppY = getY() + t_oppDistance * Math.cos(Math.toRadians(getHeading() + t_oppBearing));
		// Math.sin and Math.cos expects input in radians
	}
	
	public void dynamicFiring(double t_oppHeading, double t_oppVelocity, double t_oppBearing)
	{
		if (isNear(t_oppVelocity, -8.0) || isNear(t_oppVelocity, 8.0) || isNear(t_oppVelocity, 0))
		{
			linearFiring(t_oppHeading, t_oppVelocity);
		}
		else	
		{
			justFire(t_oppBearing);
		}
	}
	
	public void justFire(double t_oppBearing)
	{
		double absoluteBearing = getHeading() + t_oppBearing;
		turnGunRight(normalRelativeAngleDegrees(absoluteBearing - getGunHeading()));
		
		if (getGunHeat() == 0)	// calling fire only when gun heat is 0 so as to not waste turns (calling fire() uses a turn)
		{
			fire(3);
		}
	}
}
