package epfl.lia.logist.testing.messaging;

import epfl.lia.logist.agent.entity.*;
import epfl.lia.logist.logging.*;
import epfl.lia.logist.messaging.signal.SignalTypeEnum;
import epfl.lia.logist.messaging.MessageDispatcher;


/**
 * This sample test creates two different agents. Both agents
 * are standalone entities that are not controlled by a state.
 * @author malves
 *
 */
public class MessagingSystemTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		boolean bActive = true;
		
		LogManager mgr = new LogManager();
		try { mgr.init(); } catch( Exception e ) {
			e.printStackTrace();
		}
		
		// the dispatcher
		MessageDispatcher mdisp = new MessageDispatcher();
		mdisp.init();
		
		// log the event
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Starting the application..." );
		
		// the sample state handler
		MessagingTestSender testState = new MessagingTestSender();
		
		// creates the state handler
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Created the state handler" );
		
		// creates both agents
		Agent agent1 = new VehicleAgent( testState.getObjectID() );
		Agent agent2 = new VehicleAgent( testState.getObjectID() );
		Agent1Behavior myBehavior1 = new Agent1Behavior();
		Agent2Behavior myBehavior2 = new Agent2Behavior();
		agent1.registerBehavior( SignalTypeEnum.SMT_INIT, myBehavior1 );
		agent1.registerBehavior( SignalTypeEnum.SMT_TEXT, myBehavior1 );
		agent1.registerBehavior( SignalTypeEnum.SMT_KILL, myBehavior1 );
		agent2.registerBehavior( SignalTypeEnum.SMT_INIT, myBehavior2 );
		agent2.registerBehavior( SignalTypeEnum.SMT_TEXT, myBehavior2 );
		agent2.registerBehavior( SignalTypeEnum.SMT_KILL, myBehavior2 );
		
		// register entities
		mdisp.register( agent1 );
		mdisp.register( agent2 );
		mdisp.register( testState );
		
		// indicates that agents were created
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Agents were successfully created !" );
		
		// sets both ids
		testState.setAgent1( agent1.getObjectID() );
		testState.setAgent2( agent2.getObjectID() );
		
		// debug message
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Started both agents" );
		
		// starts both agents
		new Thread( agent1 ).start();
		new Thread( agent2 ).start();

		// debug message
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Entering active loop..." );
		
		// initializes all agents
		testState.initAgents();
		
		// launches the application while
		while( bActive ) {			
			
			// tests if something should be done
			if ( testState.hasSmthToDo() )
				testState.step();
			
			// is everything finished ?
			if ( testState.isFinished() )
				bActive = false;
			
			// yields some processor time
			Thread.yield();
		}
		
		// logs the event
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Application exiting successfull" );
		
		// end here
		mdisp.shutdown();
		mgr.shutdown();
	}

}
