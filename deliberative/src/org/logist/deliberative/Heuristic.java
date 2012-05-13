package org.logist.deliberative;

public interface Heuristic
{
	double f(Node n);
	double g(Node n);
	double h(Node n);
}
