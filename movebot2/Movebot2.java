package movebot2;
import robocode.*;

import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*; 
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * EmmaBrennanW3 - a robot by Emma Brennan
 * February 18th battler.
 */


	/* to-do: implement smart wall choice based on sentry bot position
	 * 	identify sitting ducks and change bullet power
	 *  change dodge amount based on distance to opponent
	 * 	add linear targeting/other targeting methods based on distance to opponent
	 */
public class Movebot2 extends Robot
{
	int i = 0; // a behaviour switch
	boolean scanNow; // Don't turn if there's a robot there
	double moveAmount; // Distance to move
	double dodgeAmount; // The most important value for this strategy
	boolean goNorth;
	boolean goSouth;
	boolean goEast;
	boolean goWest;	
	double absFace;
	boolean firstMove;
	boolean sentryIsScanned;
	double sentryX;
	double sentryY;
	int modValue;
	int recentHit;
	double sentryBearing;
	double enemyBearing;

	// Initialise robot
	public void run() 
	{
		sentryIsScanned = false;
		absFace = 0;
		firstMove = false;
		goNorth = false;
		goSouth = false;
		goEast = false;
		goWest = false;
		recentHit = 0;
		
		// Math.max will return the largest of the given values, which should be 800.
		moveAmount = 300;
		dodgeAmount = 100;
		scanNow = false; // Initialize scanNow to false

		// Absolute angle robot is facing (0=North, 90=East, 180=South, 270=West)
		double myHeading = getHeading();
		double myHeadingDelta;
		
		turnRadarRight(360);
	
		// This while loop will be the movement of the bot
		while (true) 
		{
		
			while (firstMove)
			{
		
				if (goNorth && goEast)
				{
					absFace = 45;
				}
				else if (goNorth && goWest)
				{
					absFace = 315;
				}
				else if (goNorth)
				{
					absFace = 0;
				}
				else if (goEast)
				{
					absFace = 90;
				}
				else if (goWest)
				{
					absFace = 270;
				}				

				if (goSouth && goEast)
				{
					absFace = 135;
				}
				else if (goSouth && goWest)
				{
					absFace = 225;
				}
				else if (goSouth)
				{
					absFace = 180;
				}
				
				
				
				myHeadingDelta = absFace - getHeading();
				if (myHeadingDelta > 180)
				{
					myHeadingDelta = -(360 - myHeadingDelta);
				}
				else if (myHeadingDelta < -180)
				{
					myHeadingDelta = 360 + myHeadingDelta;
				}
				
				turnRight(myHeadingDelta);
				firstMove = false;
				ahead(300);
				turnRadarRight(360);
				// turn perpendicular to sentry and enemy
			//	turnRight(((enemyBearing + sentryBearing)/2)  90);
				if (enemyBearing <  0)
				{
					turnRight(enemyBearing + 90);
				}
				else	
				{
					turnRight(enemyBearing - 90);
				}
				turnRadarRight(360);
			}
			
				

			// Look before we turn when ahead() completes.
			scanNow = true;
			// Move up the wall
			modValue = i % 2;	// This will allow the robot to vary his behaviour
			if(modValue == 0) 	// This if statement will offset where the robot goes, to make it less predictable.
			{	
				ahead(125); // Move 1 8th of the arena then spin
				turnGunRight(45);
			}
			else
			{
				back(125); // Move 1 8th of the arena then spin in the other direction
				turnGunRight(-45);
			}
		
			scanNow= false; // Don't look now
			// Turn to the next wall
			i++;
			if (i == 7){ // every time i reaches 7, rotate to appear random
				turnRight(45);
				i = 0; // reset i to 0
			}
		}
	}

	// If we hit a robot
	public void onHitRobot(HitRobotEvent e) 
	{
		int multiplyTurn = 1;
	
		// If he's in front of us, go backwards to a wall.
		if (e.getBearing() > -90 && e.getBearing() < 90) 
		{
			back(70);
			if (recentHit > 0)
			{
				multiplyTurn = -1;
			}
			turnRight(30 * multiplyTurn);
			ahead(400);
			
		} // else if he is behind us, go forwards to a wall.
		else 
		{
			ahead(400);
		}
		recentHit++;
	}

	// If we see a robot that is NOT the sentry, fire
	public void onScannedRobot(ScannedRobotEvent event) 
	{
		if (event.isSentryRobot() && sentryIsScanned == false) // On first scan of the sentry, save its x and y location
		{
			sentryBearing = event.getBearing();
			sentryX = getSentryX(event.getDistance(), event.getBearing()); // calculate sentry X location
			sentryY = getSentryY(event.getDistance(), event.getBearing()); // calculate sentry Y locationsentryIsScanned = true;
			sentryIsScanned = true;
			
			
			setSentryCardinals(sentryX, sentryY);
			firstMove = true;
		}
		if (!event.isSentryRobot())
		{
			enemyBearing = event.getBearing();
		}
	}
		
	public void setSentryCardinals(double sentryX, double sentryY)
	{
		if (sentryX > 500)
		{
			goWest = true;
		}
		if (sentryX < 300)
		{
			goEast = true;
		}
		if (sentryY < 300)
		{
			goNorth = true;
		}
		if(sentryY > 500)
		{
			goSouth = true;
		}
		
	}

	public double getSentryX (double theDistance, double theBearing) // return the X position of the sentry
	{
		sentryX = getX() + theDistance * Math.sin(Math.toRadians(getHeading() + theBearing));	// sin gives us X component of Distance (we love Sin) 
		return sentryX;
	}
	
	public double getSentryY (double theDistance, double theBearing) // return the Y position of the Sentry
	{
		sentryY = getY() + theDistance * Math.cos(Math.toRadians(getHeading() + theBearing));	// cos gives us X component of Distance
		return sentryY;
	}
}
