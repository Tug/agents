package epfl.lia.logist.tools;

/**
 * This simple class represents a pair of objects sharing the same type. The 
 * class provided accessors to access the state of the pair.
 */
public class Pair<E> {

	/* A reference to first object */
	private E mObject1 = null;
	
	/* A reference to second object */
	private E mObject2 = null;
	
	
	/**
	 * Constructor of the class
	 * 
	 * This constructor initializes the internal state of the Pair by
	 * storing the value of the objects.
	 * 
	 * @param obj1 A reference to first object
	 * @param obj2 A referece to second object
	 */
	public Pair( E obj1, E obj2 ) {
		mObject1 = obj1;
		mObject2 = obj2;
	}
	
	
	/**
	 * Return the first object in pair
	 */
	public E getFirst() {
		return mObject1;
	}
	
	
	/**
	 * Return the second object in pair
	 */
	public E getSecond() {
		return mObject2;
	}
	
	public boolean equals( Object obj ) {
		if ( obj==null ) return false;
		if ( !(obj instanceof Pair) )
			return false;
		Pair<E> p = (Pair<E>)obj;
		return mObject1.equals(p.getFirst()) && mObject2.equals(p.getSecond());
	}
	
	public int hashCode() {
		return mObject1.hashCode() + mObject2.hashCode();
	}
	
	public String toString() {
		return "pair<" + mObject1 + ", " + mObject2 + ">";
	}
}
