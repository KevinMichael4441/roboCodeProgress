package SETU;
import robocode.*;
import static robocode.util.Utils.normalAbsoluteAngle;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * BigMack - a robot by (your name here)
 */
public class BigMack extends Robot
{
	/**
	 * run: BigMack's default behavior
	 */
	
		double radarTurnDirection = 1;		
		boolean initialSentryFound = false;
		boolean initialOpponentFound = false;
		boolean secondOppFound = false;
		//boolean sentryFound = false;
		//boolean opponentFound = false;
		boolean isClose = false;
		boolean initialSentrySecond = false;
		
		//double radarBearing = 45;
		
		double opponentX;
		double opponentY;
		double opponentAngle;
		
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		while(initialSentryFound == false || initialOpponentFound == false)
		{
			initialScan();
		}

		while (secondOppFound == false)
		{
			turnRadarRight(45);
			out.println("secondOppFound == false");
		}
		
		while(true)
		{
			turnRadarRight(90 * radarTurnDirection);
			out.println("turningInMain");
			out.println(radarTurnDirection);
		}
		
	
	}

	public void onScannedRobot(ScannedRobotEvent e) 
	{	
		if (!e.isSentryRobot() && initialSentryFound == true)
		{
			initialSentrySecond = false;
			out.println("Seeing opp AFTER sentry");
		}
		else if (e.isSentryRobot() && initialOpponentFound == true && initialSentryFound == false)
		{
			out.println("Seeing sentry AFTER opp");
			initialSentrySecond = true;
			findOpponentAngle();
			turnRadarRight(opponentAngle);
		}
		
		if(e.isSentryRobot() && initialSentryFound == false)
		{
			initialSentryFound = true;
			out.println("First time seeing sentry");
		}
		else if(initialOpponentFound == false && !e.isSentryRobot())
		{
			initialOpponentFound = true;
			opponentDistanceCalculation(e.getDistance(), e.getBearing());
			out.println("First time seeing opp");
		}

		if (initialOpponentFound && initialSentryFound && !e.isSentryRobot())
		{
			secondOppFound = true;
			out.println("TARGET LOCKED");
			opponentScan();
		}
	}
	
	public void opponentScan()
	{
    	radarTurnDirection = -radarTurnDirection; // Alternate between left and right
    	turnRadarRight(90 * radarTurnDirection); // Apply wobble to radar
		out.println(radarTurnDirection);
	//	turnRadarRight(45);
	}

	public void opponentDistanceCalculation(double t_distance, double t_bearing)
	{
		out.println("DISTANCEScaned");
		// opponent location is our location plus respective component of distance vector
       opponentX = getX() + t_distance * Math.sin(Math.toRadians(getHeading() + t_bearing));	// sin gives us X component of Distance
       opponentY = getY() + t_distance * Math.cos(Math.toRadians(getHeading() + t_bearing));	// cos gives us Y component of Distance
	   
		if((Math.abs(opponentX-getX()) > 50) && (Math.abs(opponentY-getY()) > 50))
			{isClose = false;}
		else
			{isClose = true;}
	}
	
	public void findOpponentAngle()
	{
		opponentAngle = normalAbsoluteAngle(Math.atan2(opponentY-getY(), opponentX-getX()));
	}
/*


	public void radarAim ()
	{
		if (Math.abs(radarBearing) >  45)
		{
			turnRadarRight(radarBearing);
		}
		else
		{
			turnRadarRight(45);
			turnRadarRight(-90);
		}
		out.println("reScaned");
	//	if (isClose == true)
	//		{turnRadarRight(90);
	//		 turnRadarRight(-180);}
	//	if (isClose == false)
	//		{turnRadarRight(45);
	//		 turnRadarRight(-90);}
	}
*/
	public void initialScan()
	{		
		turnRadarRight(45);
		out.println("ini scaned");
	}

}