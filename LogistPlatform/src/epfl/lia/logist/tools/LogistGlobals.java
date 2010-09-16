package epfl.lia.logist.tools;

/* import table */
import java.awt.Color;


/**
 * This class holds the globals that are set in the simulation property zone.
 * These properties can only be set from the simulation file.
 *
 */
public class LogistGlobals {

	/* The size of the world on the X-axis */
	public int WorldXSize = 640;
	
	/* The size of the world on the Y-axis */
	public int WorldYSize = 480;
	
	/* The number of rounds in simulation */
	public int Rounds = 1;
	
	/* The time allocated to an agent to initialize */
	public long InitTimeout = 10000;
	
	/* The time allocated to an agent to reset */
	public long ResetTimeout = 30000;
	
	/* The time allocated to an agent to kill */
	public long KillTimeout = 1000;
	
	/* The time allocated to an agent to setup */
	public long SetupTimeout = 30000;
	
	/* The timeout value for auctions */
	public long AuctionTimeout = 300000;
	
	/* The color of the background */
	public Color Backcolor = Color.WHITE;
	
	/* The color of the text */
	public Color Forecolor = Color.BLACK;
	
	/* The color of the city */
	public Color CityColor = Color.RED;
	
	/* The color of the perimeter */
	public Color CityPerimColor = Color.BLACK;
	
	/* The color of the route */
	public Color RouteColor = Color.GRAY;
	
	/* The width of the route */
	public float RouteSize = 3.0f;
	
	/* The color of city name */
	public Color CityNameColor = Color.BLACK;
	
	/* The color of a task to deliver */
	public Color TaskToDeliverColor = Color.RED;
	
	/* The color of a task to pickup */
	public Color TaskToPickupColor = Color.BLUE;
	
	/* The color of task indicateors */
	public Color TaskIndicatorColor = Color.BLACK;
	
	/* Indicates if the text should be antialiased */
	public boolean TextAntialias = true;
	
	/* Indicates if the scene should be antialiased */
	public boolean Antialias = true;
	
	/* The radius of the city */
	public int CityRadius = 8;
	
	/* Indicates whether the legend should be created */
	public boolean ShowLegend = true;
	
	/* Indicates whether the gui should be created */
	public boolean ShowGui = true;
	
	/* Indicates the class-path */
	public String ClassPath = System.getenv("classpath");
	
	/* The path to the history */
	public String HistoryPath = "";
	
	/* The path to the logs */
	public String LogPath = "";
	
	/* Indicates the task threshold that must be attained before creating
	   new tasks. This is useful for infinite tasks */
	public int TaskThreshold = 0;
}
