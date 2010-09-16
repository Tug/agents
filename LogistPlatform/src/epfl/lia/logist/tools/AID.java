package epfl.lia.logist.tools;


/**
 * 
 * @author malves
 *
 */
public class AID {
	
	/**
	 * Address used for broadcasts
	 */
	public static final AID BROADCAST_ADDRESS = new AID();
	
	/**
	 * The current ID of the AID class
	 */
	private static int mClassID=0;
	
	/**
	 * The current ID of this AID object
	 */
	protected final int mObjectID;
	
	
	/**
	 * Default constructor of the class
	 */
	public AID() {
		mObjectID = mClassID++;
	}
	
	
	/**
	 * Indicates if boths AID's are equal or not
	 */
	public boolean equals( Object id ) {
		if ( id instanceof AID ) {
			 return ((AID)id).mObjectID == mObjectID;
		}
		return false;
	}
	
	public String toString() {
		return "ID:" + mObjectID;
	}
}