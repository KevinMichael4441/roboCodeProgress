package KM;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

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
			turnGunLeft(360);
			ahead(52);
			turnGunRight(360);
			back(52);
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
			double absoluteBearing = getHeading() + e.getBearing();
			double gunBearing = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

			if (Math.abs(gunBearing) <= 3) 
			{
				turnGunRight(gunBearing);
				// Calling fire() only if gun is cooled so that we don't waste a turn (calling fire() uses up a turn)
				if (getGunHeat() == 0) 
				{
					fire(Math.min(3,getEnergy()-1));	//Fires if more than 1 energy 
				}
			} 
			else
			{
				turnGunRight(gunBearing);
			}

			if (gunBearing == 0) //Makig sure opponent didn't move away by scanning again
			{
				scan();
			}

		}	
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
