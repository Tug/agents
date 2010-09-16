package epfl.lia.logist.testing.messaging;

/* importation table */
import epfl.lia.logist.messaging.*;
import epfl.lia.logist.tools.AID;
import epfl.lia.logist.messaging.signal.*;
import epfl.lia.logist.messaging.action.*;
import epfl.lia.logist.logging.*;


/**
 * A simple test sender that sends a message and then
 * gets a response from an activated agent.
 */
public class MessagingTestSender extends MessageHandler {

	/* the first child */
	private AID mChildID1 = null;
	
	/* the second child */
	private AID mChildID2 = null;
	
	/* the internal state machine */
	public enum InternalStateEnum {
		CREATED, INITIALIZED, IDLE, SENT, KILL, WAIT_KILLING, EXIT
	}
	
	/* the current state of the state machine */
	private InternalStateEnum meState = InternalStateEnum.CREATED;
	
	/* the number of active agents */
	private int mbActiveCount = 2;
	private int mbInitCount = 2;
	
	/**
	 * 
	 * @param c1
	 * @param c2
	 */
	public MessagingTestSender() {
	}
	
	
	public void setAgent1( AID c ) {
		mChildID1 = c;
	}
	
	
	public void setAgent2( AID c ) {
		mChildID2 = c;
	}
	
	
	/**
	 * 
	 */
	public void step() {

		// retrieves a messsage
		Message<?> msg = MessageDispatcher.getInstance().retrieve(this);
		
		// only accept actions
		if ( msg==null || msg.getMsgType() != MessageTypeEnum.MGT_ACTION ) {
			LogManager.getInstance().log( "stdout", 
					LogSeverityEnum.LSV_WARNING, 
					"Received an illegal message typ" );
			return;
		}
		
		// transform message into an action
		Action<?> action = (Action<?>)msg;
		
		// acts upon message reception
		switch( meState ) {
		
			// first, we are in created state, and we wait for
			// the child to respond with a READY message
			case CREATED:
				LogManager.getInstance().log( "stdout", 
						LogSeverityEnum.LSV_INFO, 
						"state == CREATED" );
				if ( action.getType() == ActionTypeEnum.AMT_READY )  {
					mbInitCount--;
					if ( mbInitCount == 0 ) {
						sendMessage1();
						sendMessage2();
						mbInitCount=2;
						meState = InternalStateEnum.IDLE;
					}
				}
				
				break;
				
				
			// the agent is idle waiting for the response
			// of the messages
			case IDLE:
				LogManager.getInstance().log( "stdout", 
						LogSeverityEnum.LSV_INFO, 
						"state == IDLE" );
				if ( action.getType() == ActionTypeEnum.AMT_READY ) {
					meState = InternalStateEnum.KILL;
				}
				break;
				

			// kills the agents
			case KILL:
				LogManager.getInstance().log( "stdout", 
						LogSeverityEnum.LSV_INFO, 
						"state == KILL" );
				if ( action.getType() == ActionTypeEnum.AMT_READY ) {
					killThem();
					meState = InternalStateEnum.WAIT_KILLING; 
				}

				break;
				
			// waits for every agent to respond
			case WAIT_KILLING:
				LogManager.getInstance().log( "stdout", 
						LogSeverityEnum.LSV_INFO, 
						"state == WAIT_KILLING" );
				if ( action.getType() == ActionTypeEnum.AMT_READY ) {
					mbActiveCount--;
					if ( mbActiveCount == 0)
						meState = InternalStateEnum.EXIT;
				}
				break;
			
			case EXIT:
				break;
		}
	}
	
	public void initAgents() {
		//	LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Initializing both agents..." );
		//	MessageDispatcher.getInstance().post( new InitSignal(this.getObjectID(), mChildID1) );
		//	MessageDispatcher.getInstance().post( new InitSignal(this.getObjectID(), mChildID2) );
	}
	
	
	/**
	 * This has something to do, while there are messages
	 * @return
	 */
	public boolean hasSmthToDo() {
		return MessageDispatcher.getInstance().check( this );
	}
	
	
	/**
	 * indicates if the 
	 * @return
	 */
	public boolean isFinished() {
		return mbActiveCount == 0;	
	}
	
	
	/**
	 * 
	 *
	 */
	public void sendMessage1() {
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Sending first message..." );
		TextMessage msg = new TextMessage(
				this.getObjectID(), mChildID1, 
				"Hello from Test sender !!!" );
		MessageDispatcher.getInstance().post( msg );
	}
	
	
	/**
	 * 
	 *
	 */
	public void sendMessage2() {
		LogManager.getInstance().log( "stdout", LogSeverityEnum.LSV_INFO, "Sending second message..." );
		TextMessage msg = new TextMessage(
				this.getObjectID(), mChildID2, 
				"Hello from Test sender !!!" );
		MessageDispatcher.getInstance().post( msg );
	}
	
	
	/**
	 * 
	 *
	 */
	public void killThem() {
		MessageDispatcher.getInstance().post( 
			new KillSignal(this.getObjectID(), mChildID1,null) );
		MessageDispatcher.getInstance().post( 
			new KillSignal(this.getObjectID(), mChildID2,null) );
	}
}
