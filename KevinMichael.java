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
		// Problem with this is losing in Semi-Finals :(
		// We'll cross that bridge then
		if (!e.isSentryRobot())
		{
			fire(4);
		}	
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
		
	int sentrySize = getSentryBorderSize();				// = 300
	int fieldHeight = (int)getBattleFieldHeight();		// = 800
	int fieldWidth = (int)getBattleFieldWidth();		// = 800	
	
		if (getX() < sentrySize)
		{
			while (getHeading() >= 87.5 && getHeading() <= 92.5)
			{
				turnRight(5);
			}	 
			ahead(52);
		}
		if (getX() > fieldWidth - sentrySize)
		{
			while (getHeading() >= 267.5 && getHeading() <= 272.5)
			{
				turnLeft(5);
			}
			ahead(52);	 
		}
		if (getY() > fieldHeight - sentrySize)
		{
			while (getHeading() >= 177.5 && getHeading() <= 182.5)
			{
				turnLeft(5);
			}	 
			ahead(52);
		}
		if (getY() < sentrySize)
		{
			while (getHeading() >= -2.5 && getHeading() <= 2.5)
			{
				turnRight(5);
			}
			ahead(52);	 
		}
	}
}
