package epfl.lia.logist.core.view;

public abstract class View<E> {

	/**
	 * The space belongging to this view
	 */
	protected E msSpace = null;
	
	
	/** 
	 * Invoked once to create the view
	 */
	public abstract void create();
	
	
	/**
	 * Invoked once to destroy the view
	 */
	public abstract void destroy();
	
	
	/**
	 * Return the space of this object
	 * @return
	 */
	public E space() {
		return msSpace;
	}
}
