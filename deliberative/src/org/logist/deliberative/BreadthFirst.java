package org.logist.deliberative;

import java.util.ArrayList;


public class BreadthFirst extends TreeSearchStrategy
{
	
	public Node search(Node start, Node goal) throws FailureException
	{
		int nbState = 1;
		if(start.equals(goal)) return start;
		start.createSiblings();
		ArrayList<Node> level = start.getSiblings();
		Node best = null;
		while(!level.isEmpty())
		{
			ArrayList<Node> nextLevel = new ArrayList();
			for(Node n : level)
			{
				nbState++;
				if(n.equals(goal))
				{
					if(best == null || n.getCost() < best.getCost())
						best = n;
				}
				n.createSiblings();
				nextLevel.addAll(n.getSiblings());
			}
			level = nextLevel;
		}
		System.out.println("BFS nb state explored = "+nbState);
		if(best == null) throw new FailureException();
		return best;
	}
	
	/*
	public Node search(Node start, Node goal) throws FailureException
	{
		ArrayList<Node> level = start.getSiblings();
		Node bestLevelNode = start;
		while(!bestLevelNode.equals(goal))
		{
			bestLevelNode.createSiblings();
			level = bestLevelNode.getSiblings();
			bestLevelNode = level.get(0);
			// get the minimum cost of the current level
			for(Node n : level)
			{
				if(n.getCost() < bestLevelNode.getCost())
					bestLevelNode = n;
			}
		}
		return bestLevelNode;
	}
	*/
	
}
