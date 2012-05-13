package org.logist.deliberative;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class TreeSearchStrategy implements Comparator<Node>
{
	abstract public Node search(Node start, Node goal) throws FailureException;
	
	public int compare(Node n1, Node n2)
	{
		double v1 = n1.getTotalCost();
		double v2 = n2.getTotalCost();
		if(v1 > v2) return 1;
		else if(v1 < v2) return -1;
		else return 0;
	}
	
	public void sort(List<Node> list, Heuristic heuristic)
	{
		if(heuristic != null)
			for(Node n : list) n.applyHeuristic(heuristic);
		Collections.sort(list,this);
	}
	
	public void merge(List<Node> list1, List<Node> list2, Heuristic heuristic)
	{
		list1.addAll(list2);
		sort(list1, heuristic);
	}
	
	public void sort(List<Node> list)
	{
		sort(list, null);
	}
	
	public void merge(List<Node> list1, List<Node> list2)
	{
		merge(list1, list2, null);
	}
	
}

