package emmabrennan;
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
public class EmmaBrennanW3 extends Robot
{
	int i = 0; // a behaviour switch
	boolean scanNow; // Don't turn if there's a robot there
	double moveAmount; // Distance to move
	double dodgeAmount; // The most important value for this strategy

	// Initialise robot
	public void run() 
	{
		// Math.max will return the largest of the given values, which should be 800.
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		dodgeAmount = moveAmount / 8;
		scanNow = false; // Initialize scanNow to false
		
		//size of the field
		double arenaHeight;
		double arenaWidth;
		// robot current position
		double myXPos;
		double myYPos;
		//midpoint of the field
		double centreX;
		double centreY;
		// Absolute angle robot is facing (0=North, 90=East, 180=South, 270=West)
		double myHeading = getHeading();
		double myHeadingDelta;
		
		setColors(Color.cyan,Color.cyan,Color.cyan); // body,gun,radar

	// Get the height and width of the arena
		arenaHeight = getBattleFieldHeight();
		arenaWidth = getBattleFieldWidth();
		myXPos = getX();
		myYPos = getY();
		
	//	divide field size by 2 to find midpoint
		centreX = arenaHeight / 2;
		centreY = arenaHeight / 2;
		
		/* find angle robot is facing, eg. 40 dgrees
		myHeading = getHeading();
		// find fastest direction to face North
		if (myHeading > 180)
		{
			turnRight(360-myHeading);
		}
		else
		{
			// turn North (absolute degrees is 0)
			turnLeft(myHeading);
		} */
		
		// Divide our heading by 90, and the remainder is how much to move to face a cardinal direction.
		myHeadingDelta = getHeading() % 90;
		turnLeft(myHeadingDelta);
		ahead(moveAmount); // Move until we hit a wall
		// Turn the gun to turn right 90 degrees.
		scanNow = true;
		turnGunRight(90);
		turnRight(90);
		int modValue;
		i = 0;
		
		// This while loop will be the movement of the bot
		while (true) 
		{
			// Look before we turn when ahead() completes.
			scanNow = true;
			// Move up the wall
			modValue = i % 2;	// This will allow the robot to vary his behaviour
			if(modValue == 0) 	// This if statement will offset where the robot goes, to make it less predictable.
			{	
				ahead(dodgeAmount); // Move 1 8th of the arena then spin
				turnGunRight(360);
			}
			else
			{
				back(dodgeAmount); // Move 1 8th of the arena then spin in the other direction
				turnGunRight(-360);
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
		// If he's in front of us, go backwards to a wall.
		if (e.getBearing() > -90 && e.getBearing() < 90) 
		{
			back(moveAmount);
		} // else if he is behind us, go forwards to a wall.
		else 
		{
			ahead(moveAmount);
		}
	}

	// If we see a robot that is NOT the sentry, fire
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if (!e.isSentryRobot())
		{
			fire(3);
		}
		
	}
}
