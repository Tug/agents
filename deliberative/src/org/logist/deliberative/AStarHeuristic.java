package org.logist.deliberative;

public class AStarHeuristic implements Heuristic
{
	public double f(Node n)
	{
		return g(n) + h(n);
	}
	
	public double g(Node n)
	{
		return n.getCost();
	}
	
	public double h(Node n)
	{
		return n.getParent().getCost();
	}
}
