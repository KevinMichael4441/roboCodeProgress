package SETU;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 * KevinMichael - a robot by Kevin Michael
 */

public class KM extends Robot
{
	boolean shot = false;
	int location = 0;	
	int moveAmnt = 52;	
	
	boolean sentryScanned = false;
	double sentryX = 0.0;
	double sentryY = 0.0;
	double middle = 400.0;				// middle o'Board
	
	public void run() {
		setColors(Color.green,Color.pink,Color.green); // body,gun,radar
		
		
		while (true)
		{
			turnGunLeft(360);
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
	
		// Since I am now dealing with sentry, Prepping for Semi won't be too hard
		// But again, we'll cross that bridge then
		
		out.println(getHeading());
		if (e.isSentryRobot())
			sentryCalculation(e.getDistance(), e.getBearing());
		
		if (!e.isSentryRobot() && !shot && sentryScanned)
		{
			double absoluteBearing = getHeading() + e.getBearing();
			double gunBearing = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

			if (Math.abs(gunBearing) < 3) // If opp is close, fire 
			// (3 is just a small enough arbitrary angle)
			{
				turnGunRight(gunBearing);
				// Calling fire() only if gun is cooled so that we don't waste a turn (calling fire() uses up a turn)
				if (getGunHeat() == 0) 
				{
					if (e.getEnergy() < 10 || e.getDistance() > 400)		// Making the last bullet fast and also if opp is far
					{
						fire(2);
						shot = true;
					}
					else
					{
						fire(3);
						shot = true;
					}
				}
			} 
			else		// Else turn gun to face opp
			{	
				turnGunRight(gunBearing);
			}
					
			if (gunBearing == 0) 
			{
				scan();		// Scanning again to re-call the onScannedRobot function (this function)
			}
		}	
		else if (!e.isSentryRobot() && shot)
		{	
			movement();
		}
	}
	
	public void movement()
	// Movement that (kind of) uses the opp as shield from sentry and gets out into sentry territory
	{
		double heading = getHeading();
		if (sentryX < middle && sentryY > middle)
		{
			if (heading < 225 && heading > 45)
			{
				ahead(moveAmnt);
				out.println("Q1 ahead");
			}
			else
			{
				back(moveAmnt);
				out.println("Q1 back");
			}
			location = 1;
			
		}
		else if (sentryX > middle && sentryY > middle)
		{
			if (heading > 135 && heading < 315)
			{
				ahead(moveAmnt);
				out.println("Q2 ahead");
			}
			else
			{
				back(moveAmnt);
				out.println("Q2 back");
			}
			location = 2;
		}
	    else if (sentryX > middle && sentryY < middle)
		{
			if (heading > 225 || heading < 45)
			{
				ahead(moveAmnt);
				out.println("Q3 ahead");
			}
			else
			{
				back(moveAmnt);
				out.println("Q3 back");
			}
			location = 3;
		}
		else if (sentryX < middle && sentryY < middle)
		{
			if (heading > 315 || heading < 135)
			{
				ahead(moveAmnt);
				out.println("Q4 ahead");
			}
			else
			{
				back(moveAmnt);
				out.println("Q4 back");
			}
			location = 4;
		}

		shot = false;
	}
	
	public void sentryCalculation(double t_distance, double t_bearing)
	{
		// sentry location is our location plus respective component of distance vector
       sentryX = getX() + t_distance * Math.sin(Math.toRadians(getHeading() + t_bearing));	// sin gives us X component of Distance
       sentryY = getY() + t_distance * Math.cos(Math.toRadians(getHeading() + t_bearing));	// cos gives us Y component of Distance
	   
		sentryScanned = true;
	}
	
	public void onHitWall(HitWallEvent e)
	{
		double heading = getHeading();
		if (location == 1)
		{
			if (heading < 225 && heading > 45)
			{
				back(moveAmnt * 2);
			}
			else
			{
				ahead(moveAmnt * 2);
			}
		}
		else if (location == 2)
		{
			if (heading > 135 && heading < 315)
			{
				back(moveAmnt * 2);
			}
			else
			{
				ahead(moveAmnt * 2);
			}
		}
		else if (location == 3)
		{
			if (heading > 225 || heading < 45)
			{
				back(moveAmnt * 2);
			}
			else
			{
				ahead(moveAmnt * 2);
			}
		}
		else if (location == 4)
		{
			if (heading > 315 || heading < 135)
			{
				back(moveAmnt * 2);
			}
			else
			{
				ahead(moveAmnt * 2);
			}
		}
	}
}