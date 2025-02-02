package KM;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * KevinMichael - a robot by Kevin Michael
 */
public class KevinMichael extends Robot
{
	/**
	 * run: KevinMichael's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.green,Color.pink,Color.green); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like	
			ahead(52);
			turnGunRight(360);
			back(52);
			turnGunRight(360);
			stayInBounds();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		fire(3);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(15);
		stayInBounds();
	}
	
	public void stayInBounds() 
	{
		
	//int sentrySize = getSentryBorderSize();
	// We know sentrySize will be 300 so hardcoding	
	
		if (getX() < 300)
		{
			while (getHeading() >= 87.5 && getHeading() <= 92.5)
			{
				turnRight(5);
			}	 
		}
		if (getX() > 500)
		{
			while (getHeading() >= 267.5 && getHeading() <= 272.5)
			{
				turnLeft(5);
			}	 
		}
		if (getY() > 500)
		{
			while (getHeading() >= 177.5 && getHeading() <= 182.5)
			{
				turnLeft(5);
			}	 
		}
		if (getY() < 300)
		{
			while (getHeading() >= -2.5 && getHeading() <= 2.5)
			{
				turnRight(5);
			}	 
		}
	}
}
