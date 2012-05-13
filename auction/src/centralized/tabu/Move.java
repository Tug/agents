package centralized.tabu;

import centralized.Solution;

public abstract class Move
{
	
	@Override public abstract int hashCode();
	@Override public abstract boolean equals(Object o);
	
	public abstract Solution createSolution(Solution s);
	public abstract Move getOppositeMove();
	
}
