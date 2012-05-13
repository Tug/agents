import epfl.lia.logist.*;

public class MainReactive {

    public static void main(String[] args)
    {
    	String[] arguments = {"config/default.xml"};
    	if ( (args.length != 0) && (args[0].equals("ex1-reactive1") ||
				    args[0].equals("ex1-reactive2") ||
				    args[0].equals("ex1-reactive3")) )
    	{
    		arguments = new String[]{"config/default.xml", args[0]};
    	}
    	
	    LogistPlatform.main(arguments);
    }

}
