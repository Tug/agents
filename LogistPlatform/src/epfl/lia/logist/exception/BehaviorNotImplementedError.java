package epfl.lia.logist.exception;

import epfl.lia.logist.messaging.signal.SignalTypeEnum;

public class BehaviorNotImplementedError extends LogistException {
	public BehaviorNotImplementedError( SignalTypeEnum type ) {
		super( "The behavior for signal " + type + " was not implemented !" );
	}
}
