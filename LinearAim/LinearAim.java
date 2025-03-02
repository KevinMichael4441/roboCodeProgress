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
	// Miscellaneous Variables
	double oppX;					// X coords of opp
	double oppY;					// Y coords of opp


	// Radar Variables by Marie-Elise
	double radarTurnDirection = 1;			// 1 or -1 for left or right of bot (or right or left)
	double opponentAngle;					// Angle to opponent
		
	boolean initialSentryFound = false;		// check for first time seeing sentry
	boolean initialOpponentFound = false;	// check for first time seeing opponent		
	boolean initialSentrySecond = false;	// check for whether sentry was found (first or) second


	// Targeting Variables by Kevim	
	double gunTurnAngle;
	int fireTurn = 0;				// How many shots fired
	int fireMode = 0;				// shooting mode is linear in the beginning (Refer to finals below for each value)
	int bulletsMissed = 0;			
	int turnForAverage = 0;		// no. of turns for average aim calculation
	
	final int LINEAR = 0;		// int repr for linear targeting 
	final int AVERAGE = 1;		// int repr for targeting average position of bot (against oscillators)
	final int HEAD_ON = 2;		// int repr for simply shooting where opp is

	final int MAX_VALUES = 20;	// Accuracy for our average prediction
	double oppXArray[] = new double[MAX_VALUES];
	double oppYArray[] = new double[MAX_VALUES];
	
	final double robotWidth = 36.0;							// width of robot for (width/2) 
	final double halfRW = robotWidth/2 - 0.0004441;			//  Width/2 for stopping prediction when robot out of bounds 
	// subtracting a tiny amount coz of double rounding issues giving values like 17.99999999 or 782.0000000001
	
	final double arenaSize = 800.0;
	// final is like const		

	public void run() {
		setColors(Color.red,Color.yellow,Color.yellow); // body,gun,radar
		
		//Radar Conditions by Marie-Elise
		while(initialSentryFound == false || initialOpponentFound == false)
		{
			turnRadarRight(45);
			// keep sweeping till both are found
		}
		
		while(true)
		{
			turnRadarRight(90 * radarTurnDirection);
			// for this to run, radar must have lost opp. so we scan again in same radar direction once more to find
		}

	}

	public void onScannedRobot(ScannedRobotEvent e) {
	
		// Radar conditions by Marie-Elise
		if (e.isSentryRobot() && initialOpponentFound == true && initialSentryFound == false)
		// If Sentry is found AFTER opponent, go back to opponent (in the shortest route)
		{
			turnRadarRight(opponentAngle);
		}
		
		if(e.isSentryRobot() && initialSentryFound == false)
		// seeing sentry for the first time
		{
			initialSentryFound = true;
		}
		else if(initialOpponentFound == false && !e.isSentryRobot())
		// seeing opponent for the first time
		{
			initialOpponentFound = true;
			oppCalculation(e.getDistance(), e.getBearing());
		}

		if (initialOpponentFound && initialSentryFound && !e.isSentryRobot())
		// if both are found and right now scanning opponent
		{
			oppCalculation(e.getDistance(), e.getBearing());
			opponentScan();
			
			// Once Locked On, Targeting by Kevin
			updateAverage();
			if (getGunHeat() == 0)	// calling fire only when gun heat is 0 so as to not waste turns (calling fire() uses a turn)
			{
				setAdjustRadarForGunTurn(true);				//
				radarTurnDirection = -radarTurnDirection;	// Alternate between left and right
				dynamicFiring(e.getHeading(), e.getVelocity(), e.getBearing());
			}
		}
	}
	
	
	// Targeting Functions by Kevin
	public void linearFiring(double t_oppHeading, double t_oppVelocity)
	{
		double xPos = getX();		// Our bot's X coords
		double yPos = getY();		// Our bot's Y coords

		double timeElapsed = 1;	// since distance cannot be 0, we start with 1	
		// time elapsed (for calculation of prediction) in while loop (PS. NOT real-time)
		
		double bulletPower = 3.0;	
		
		double predictedX = oppX;	// Opp's X that is calculated with time
		double predictedY = oppY; 	// Opp's Y that is calculated with time

		double gunHeading = getGunHeading();	// where our gun is pointing
		
		double bulletVelocity = 20-3.0*bulletPower;	// speed of bullet based on robocode formula
		
		while((timeElapsed) * bulletVelocity < Point2D.Double.distance(xPos,yPos, predictedX, predictedY))
		// Point2D.Double.distance is used to find the magnitude of distance vector between two points (our bot and opp's predicted location)
		// if velocity is LESS than pred location, bullet will fall short => we run loop till they are equal.
		{

			predictedX += Math.sin(Math.toRadians(t_oppHeading)) * t_oppVelocity;
			predictedY += Math.cos(Math.toRadians(t_oppHeading)) * t_oppVelocity;
			
			if(predictedX < halfRW || predictedY < halfRW || predictedX > (arenaSize - halfRW) || predictedY > (arenaSize - halfRW)) 
			{
				//sometimes it becomes 17.9999999 and sometimes 782.000000001 => loop will only run once which is fixed in initializing halfRW value
        		predictedX = Math.max(Math.min(arenaSize - halfRW, predictedX), halfRW);
        		predictedY = Math.max(Math.min(arenaSize - halfRW, predictedY), halfRW);
				// since tank cannot be at <18 or >782, those values would be invalid
				// they cannot be coz of where center of bot lies and bounding box of bot
			
				break;
			}
			
			timeElapsed++;
		}
		
		double angleToOpp = normalAbsoluteAngle(Math.atan2(predictedX - xPos, predictedY-yPos));	
		//^ angle to opp
		// atan2 gives O/P in radians
		// using atan2 and not atan coz it's not limited by range (codomain)
		
	
		gunTurnAngle = normalRelativeAngleDegrees(Math.toDegrees(angleToOpp - Math.toRadians(gunHeading)));
		
		turnGunRight(gunTurnAngle);
		// (angle - Math.toRadians(gunHeading)) gives angle to turn gun in Radians (0-2pi)
		// (Math.toDegrees(angle - Math.toRadians(gunHeading))) converts ^ angle to Degrees (0-360)
		// normalRelativeAngleDegrees to normalize relative angles ((-180)-0 or 0-180) for optimization
		
		fire(bulletPower);
	}
	
	public void dynamicFiring(double t_oppHeading, double t_oppVelocity, double t_oppBearing)
	{
		fireTurn++;
		if (fireTurn % 6 == 0 )		// checking how many bullets Hit every 5 turns
		// 7 up and 2 below are arbitrary values and needs more testing
		{
			if (bulletsMissed > 2)	// changing shoot mode when more than 3 bullets miss
			{
				fireMode++;
			}
			fireTurn = 0;
			bulletsMissed = 0;
		}
		out.println("fireTurn: " + fireTurn);
		out.println("BulletsMissed: " + bulletsMissed);
		
		if (fireMode % 3 == LINEAR)
		{
			out.println("Linear targeting");
			linearFiring(t_oppHeading, t_oppVelocity);
		}
		else if (fireMode % 3 == AVERAGE && turnForAverage > MAX_VALUES)
		{
			averageFire();
			out.println("Average targeting");
		}
		else
		{
			justFire(t_oppBearing);
			out.println("Head-On targeting");
		}
	}
	
	public void justFire(double t_oppBearing)
	{
		double absoluteBearing = getHeading() + t_oppBearing;
		
		gunTurnAngle = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		turnGunRight(gunTurnAngle);

		fire(3);
	}
	
	public void averageFire()
	{
		double sumX = 0;
		double sumY = 0;
		
		for (int i = 0; i < MAX_VALUES; i++)
		{
			sumX += oppXArray[i];
			sumY += oppYArray[i];
		}
		
		double avgX = sumX / MAX_VALUES;
		double avgY = sumY / MAX_VALUES;
		

		out.println("Avg X: " + avgX);
		out.println("Avg Y: " + avgY);
		double angleToOpp = normalAbsoluteAngle(Math.atan2(avgX - getX(), avgY - getY()));
		
		gunTurnAngle = normalRelativeAngleDegrees(Math.toDegrees(angleToOpp - Math.toRadians(getGunHeading())));
		
		turnGunRight(gunTurnAngle);
		
		fire(3);
	}
		
	public void onBulletMissed(BulletMissedEvent e)
	{
		bulletsMissed++;
	}
	
	public void updateAverage()
	{
		turnForAverage++;
		oppXArray[turnForAverage % MAX_VALUES] = oppX;
		oppYArray[turnForAverage % MAX_VALUES] = oppY;
	}


	// Radar wobble function by Marie-Elise
	public void opponentScan()
	{
    	radarTurnDirection = -radarTurnDirection;	 // Alternate between left and right
    	turnRadarRight(90 * radarTurnDirection);	 // Apply wobble to radar
	}

	public void oppCalculation(double t_oppDistance, double t_oppBearing)
	{		
		oppX = getX() + t_oppDistance * Math.sin(Math.toRadians(getHeading() + t_oppBearing));
		oppY = getY() + t_oppDistance * Math.cos(Math.toRadians(getHeading() + t_oppBearing));
		// Math.sin and Math.cos expects input in radians
		
		opponentAngle = normalAbsoluteAngle(Math.atan2(oppY-getY(), oppX-getX()));
