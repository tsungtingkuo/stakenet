import java.util.*;
import stock.cluster.*;
import stock.directed.StockGraph;
import stock.util.*;
import stock.vertex.StockVertex;

public class Main_Cluster {

	public static void main(String[] args) throws Exception
	{		

		// Parameters
		int targetYear = 98;
		int targetMonth = 10;
		
		int removeSingleNodes = 1;
		
		boolean removeOneDegreeVertex = true;
		boolean removeBankVertex = true;
		boolean countDirectOwnCompany = true;
		int alpha = 3;
		int beta = 20;
		
		
		//////////////////////////////////////////////////////
		//
		// Graph
		//
		//////////////////////////////////////////////////////
		
		
		// Graph name
		String vertexFileName = "graph/vertex_" + targetYear + "_" + targetMonth + ".txt";		
		String edgeFileName = "graph/edge_" + targetYear + "_" + targetMonth + ".txt";

		// Load stock data
		StockData sd = StockData.load("exception.csv", "company.csv", "english.csv");

		// Load graph (directed)
		StockGraph g = StockGraph.load(vertexFileName, edgeFileName, removeSingleNodes);
		g.setVertexEnglishs(sd);

		StockCluster sc = new StockCluster(false ,g, removeOneDegreeVertex, removeBankVertex, countDirectOwnCompany, alpha, beta);		
		Set<StockVertex> cluster = sc.getCompanySoftingGroup("2357");
		System.out.println(cluster);

		Set<Set<StockVertex>> clusters = sc.getBussinessGroup();
		int iGCC = 0;
		int iTotalPublicVertexCount = 0;
		Map<Number,Number> clusterCountMap = new HashMap<Number,Number>();
		for (Set<StockVertex> c : clusters) {
			if(c.size() > 2) {
				System.out.println("Cluster size: " + c.size() + " vertex are: " + c);
			}
			iTotalPublicVertexCount += c.size();
			if (c.size() > iGCC) {
				iGCC = c.size();
			}
			if (clusterCountMap.get(c.size()) != null) {
				clusterCountMap.put(c.size(), clusterCountMap.get(c.size()).intValue() + 1);
			} 
			else {
				clusterCountMap.put(c.size(), 1);
			}
		}
		System.out.println("GCC size " + iGCC+ " clster set number : "+clusters.size()+" public vertex number : "+iTotalPublicVertexCount);
		//System.out.println(clusterCountMap);
		
		HashSet<String> bgc = StockUtil.loadBussinessGroupCompanies("bussiness_group.txt");
		clusters = StockUtil.filterClusteringResult(clusters, bgc);
		System.out.println("Computed companies = " + StockUtil.computeClusterCompanies(clusters));
		System.out.println(sc.getNMI(clusters));
		
		////find repeats!
		/*
		Set<StockVertex> publicVertex = g.getPublicVertices();
		while (!publicVertex.isEmpty())
		{
			StockVertex C1 = publicVertex.iterator().next();
			int iFoundCount = 0;
			for (Set<StockVertex> c:clusters)
	        {
				if (c.contains(C1))
				{
					iFoundCount++;
					publicVertex.remove(C1);
				}
	        }
			if (iFoundCount > 1)
				System.out.println(C1 +" is "+iFoundCount);
		}
		*/
	}
}
