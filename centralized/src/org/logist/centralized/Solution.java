package org.logist.centralized;

import java.util.Arrays;


public class Solution implements Cloneable
{
	public static final int NULL = -1;
	
	public int[] nextTask;
	public int[] vehicule;
	public int[] time;
	
	public Solution()
	{
		World theWorld = World.getInstance();
		int Nt = theWorld.Nt;
		int Nv = theWorld.Nv;
		nextTask = new int[Nt + Nv];
		Arrays.fill(nextTask, NULL);
		//for(int i=0; i<nextTask.length; i++) nextTask[i] = NULL;
		time = new int[Nt];
		Arrays.fill(time, NULL);
		//for(int i=0; i<time.length; i++) time[i] = NULL;
		vehicule = new int[Nt];
	}
	
	public Solution(Solution A1)
	{
		this.nextTask = A1.nextTask.clone();
		this.vehicule = A1.vehicule.clone();
		this.time = A1.time.clone();
	}
	
	public void ChangingVehicule(int v1 ,int v2)
	{
		// first task of v1
		int t = nextTask[v1];
		// first task of v1 is now its second task
		nextTask[v1] = nextTask[t];
		// the second task of v1 is now the first task of v2
		nextTask[t] = nextTask[v2];
		// the first task of v2 is now the first task of v1
		nextTask[v2] = t;
		UpdateTime(v1);
		UpdateTime(v2);
		vehicule[t] = v2;
	}
	
	public void ChangingTaskOrder(int vi, int tIdx1, int tIdx2)
	{
		int tPre1 = vi; // previous task of task1
		int t1 = nextTask[tPre1]; // task1
		int count = 1;
		while(count < tIdx1)
		{
			tPre1 = t1;
			t1 = nextTask[t1];
			count++;
		}
		int tPost1 = nextTask[t1]; // the task delivered after t1
		int tPre2 = t1; // previous task of task2
		int t2 = nextTask[tPre2]; // task2
		count++;
		while(count < tIdx2)
		{
			tPre2 = t2;
			t2 = nextTask[t2];
			count++;
		}
		int tPost2 = nextTask[t2]; // the task delivered after t2
		// exchanging two tasks
		if(tPost1 == t2)
		{
			// the task t2 is delivered immediately after t1
			nextTask[tPre1] = t2;
			nextTask[t2] = t1;
			nextTask[t1] = tPost2;
		}
		else
		{
			nextTask[tPre1] = t2;
			nextTask[tPre2] = t1;
			nextTask[t2] = tPost1;
			nextTask[t1] = tPost2;
		}
		UpdateTime(vi);
	}
	
	public void UpdateTime(int vi)
	{
		int ti = nextTask[vi];
		if(ti != NULL)
		{
			time[ti] = 1;
			int tj = NULL;
			do {
				tj = nextTask[ti];
				if(tj != NULL)
				{
					time[tj] = time[ti] + 1;
					ti = tj;
				}
			} while(tj != NULL);
		}
	}
	
	protected Object clone() throws CloneNotSupportedException
	{
		Solution S = new Solution(this);
		return S;
	}
	
	
}
