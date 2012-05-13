import epfl.lia.logist.*;

public class MainDeliberative {

    public static void main(String[] args)
    {
		if( args.length != 0 
			&& ( args[0].equals("ex2-deliberative1")
				 || args[0].equals("ex2-deliberative2") ) )
		{
			String[] arguments = {"config/default.xml", args[0]};
			LogistPlatform.main(arguments);
		}
		else
		{
		    String[] arguments = {"config/default.xml", "ex2-deliberative1"};
		    LogistPlatform.main(arguments);
		}

    }

}
