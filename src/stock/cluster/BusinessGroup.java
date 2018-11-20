package stock.cluster;

import stock.vertex.StockVertex;
import java.util.HashSet;
import java.util.Set;

public class BusinessGroup {

	StockVertex core;
	Set<StockVertex> triangelSet,directLinkOwnSet,indirectLinkSet;
	
	public BusinessGroup(StockVertex vertex)
	{
		core = vertex;
		triangelSet = new HashSet<StockVertex>();
		directLinkOwnSet = new HashSet<StockVertex>();
		indirectLinkSet = new HashSet<StockVertex>();
	}
	
	public Set<StockVertex> getAll()
	{
		Set<StockVertex> wholeSet = new HashSet<StockVertex>();
		wholeSet.add(core);
		wholeSet.addAll(directLinkOwnSet);
		wholeSet.addAll(triangelSet);
		wholeSet.addAll(indirectLinkSet);
		return wholeSet;
	}
	
	public int size()
	{
		return directLinkOwnSet.size()+triangelSet.size()+indirectLinkSet.size()+1;
	}
	
	public int getMergeScore(Set<StockVertex> compareSet)
	{
		int score = 0;
		for (StockVertex v:directLinkOwnSet) // 3
		{
			if (compareSet.contains(v))
				score += 3;
		}
		for (StockVertex v:triangelSet) // 2
		{
			if (compareSet.contains(v))
				score += 2;
		}
		for (StockVertex v:indirectLinkSet) // 1
		{
			if (compareSet.contains(v))
				score += 1;
		}
		return score;
	}
	   
	
}
