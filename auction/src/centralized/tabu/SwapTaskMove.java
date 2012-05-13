package centralized.tabu;

import centralized.Solution;

public class SwapTaskMove extends Move
{
	public int vid;
	public int idTask1;
	public int idTask2;
	
	public SwapTaskMove(int vid, int idTask1, int idTask2)
	{
		this.vid = vid;
		this.idTask1 = idTask1;
		this.idTask2 = idTask2;
	}
	
	public Solution createSolution(Solution s)
	{
		Solution s2 = new Solution(s);
		s2.ChangingTaskOrder(vid, idTask1, idTask2);
		return s2;
	}
	
	public Move getOppositeMove()
	{
		return this;
	}

	@Override public int hashCode()
	{
		return Integer.parseInt(""+vid+idTask1+idTask2);
	}
	
	@Override public boolean equals(Object o)
	{
		if(!(o instanceof SwapTaskMove))
			return false;
		else {
			SwapTaskMove stm = (SwapTaskMove)o;
			return stm.vid == vid
				&& stm.idTask1 == idTask1
				&& stm.idTask2 == idTask2;
		}
	}
	
}

