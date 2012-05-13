package centralized;

import java.util.ArrayList;
import java.util.List;

public class Util
{
	
	public static int randomInt(int min, int max)
	{
		return min + (int)(Math.random() * (max+1-min));
	}
	
	public static int randomInt(int max)
	{
		return randomInt(0, max);
	}
	
	public static <T> T getRandomElement(ArrayList<T> list)
	{
		if(list.isEmpty()) return null;
		return list.get(randomInt(list.size()-1));
	}
	
	public static <T> void insertTaskRandomly(List<T> list, T el)
	{
		int rand = randomInt(list.size()-1);
		list.add(rand, el);
		list.add(rand+1, el);
	}
	
	public static <T> T pollTaskRandomely(List<T> list)
	{
		int rand = (int)(Math.random() * (list.size()-1));
		T t = list.get(rand);
		list.remove(rand);
		list.remove(t);
		return t;
	}
	
	public static <T> void swap(List<T> list, int ida, int idb)
	{
		T temp = list.get(ida);
		list.set(ida, list.get(idb));
		list.set(idb, temp);
	}
	
	/*
	 * to perform tests
	 */
	public static void main(String[] args)
	{
		for(int i=0; i<100; i++)
			System.out.println(Math.random());
	}
	
	public static int normalizeInt(int nb, int nbmax, int maxmorm)
	{
		if(nb == 0) return nb;
		else return maxmorm * nb / nbmax;
	}
	
	public static double normalizeFloat(double nb, double nbmax, double maxnorm)
	{
		if(nb == 0) return nb;
		else return nb * maxnorm / nbmax;
	}
	
}
