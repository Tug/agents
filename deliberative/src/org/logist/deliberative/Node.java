package org.logist.deliberative;

import java.util.ArrayList;

public abstract class Node
{
	private String id;
	private double totalCost;
	
	private ArrayList<Node> siblings;
	private Node parent;
	
	public Node()
	{
		this.id = "0";
		this.parent = null;
		this.siblings = new ArrayList<Node>();
		this.totalCost = 0;
	}
	
	public Node(Node parent)
	{
		int localId = parent.getNumberOfSiblings();
		this.id = parent.getId()+"n"+localId;
		this.parent = parent;
		this.siblings = new ArrayList<Node>();
	}
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public ArrayList<Node> getSiblings() { return siblings; }
	public void addSibling(Node n) { siblings.add(n); }
	public int getNumberOfSiblings() { return siblings.size(); }
	public Node getParent() { return parent; }
	public double getTotalCost() { return totalCost; }
	public void setTotalCost(double newCost) { totalCost = newCost; }
	public void applyHeuristic(Heuristic heu) { totalCost = heu.f(this); }
	
	abstract public void createSiblings();
	abstract public double getCost();
}

/*
public interface Node
{
	int getId();
	Node getSibling(int idSibling);
	int getNumberOfSiblings();
	Node getParent();
	boolean equals(Object o);
	
	double getCost();
	double getTotalCost();
	void setTotalCost(double newCost);
}
*/
