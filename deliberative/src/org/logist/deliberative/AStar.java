package org.logist.deliberative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class AStar extends TreeSearchStrategy
{
	private Heuristic heuristic;
	
	public AStar(Heuristic heuristic)
	{
		super();
		this.heuristic = heuristic;
	}
	
	public AStar()
	{
		this(new AStarHeuristic());
	}
	
	public Node search(Node start, Node goal) throws FailureException
	{
		int nbState = 1;
		LinkedList<Node> Q = new LinkedList<Node>();
		Q.add(start);
		HashMap<String,Node> C = new HashMap<String,Node>();
		while(true)
		{
			if(Q.isEmpty()) throw new FailureException();
			Node n = Q.poll();
			if(n.equals(goal)) {
				System.out.println("A* nb state explored = "+nbState);
				return n;
			}
			Node n2 = C.get(n.getId());
			nbState++;
			if(n2 == null ||  n.getTotalCost() < n2.getTotalCost())
			{
				C.put(n.getId(),n);
				n.createSiblings();
				ArrayList<Node> S = n.getSiblings();
				sort(S,heuristic); //(sort in the merge)
				merge(Q,S,heuristic);
			}
		}
	}	
	
}
