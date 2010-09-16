package centralized.tabu;

import centralized.Solution;

public class ChangeVehicleMove extends Move
{
	public int vid1;
	public int vid2;
	
	public ChangeVehicleMove(int vid1, int vid2)
	{
		this.vid1 = vid1;
		this.vid2 = vid2;
	}
	
	public Solution createSolution(Solution s)
	{
		Solution s2 = new Solution(s);
		s2.ChangingVehicle(vid1, vid2);
		return s2;
	}
	
	public Move getOppositeMove()
	{
		return new ChangeVehicleMove(vid2, vid1);
	}
	
	@Override public int hashCode()
	{
		return Integer.parseInt(""+vid1+vid2);
	}
	
	@Override public boolean equals(Object o)
	{
		if(!(o instanceof ChangeVehicleMove))
			return false;
		else
		{
			ChangeVehicleMove cvm = (ChangeVehicleMove)o;
			return cvm.vid1 == vid1
				&& cvm.vid2 == vid2;
		}
	}
	
}
