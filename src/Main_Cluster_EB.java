
import java.util.*;
import edu.uci.ics.jung.algorithms.cluster.*;
import stock.cluster.*;
import stock.directed.StockGraph;
import stock.edge.StockEdge;
import stock.undirected.StockUndirectedGraph;
import stock.util.*;
import stock.vertex.StockVertex;

public class Main_Cluster_EB {

	public static void main(String[] args) throws Exception
	{		
		// Parameters
		int numEdgesToRemove = 2100;
		
		int targetYear = 98;
		int targetMonth = 10;
		
		int removeSingleNodes = 1;
		
		boolean removeOneDegreeVertex = true;
		boolean removeBankVertex = true;
		boolean countDirectOwnCompany = true;
		int alpha = 3;
		int beta = 20;
		
		// Graph name
		String vertexFileName = "graph/vertex_" + targetYear + "_" + targetMonth + ".txt";		
		String edgeFileName = "graph/edge_" + targetYear + "_" + targetMonth + ".txt";

		// Load stock data
		StockData sd = StockData.load("exception.csv", "company.csv", "english.csv");

		// Load graph (directed)
		StockGraph g = StockGraph.load(vertexFileName, edgeFileName, removeSingleNodes);
		g.setVertexEnglishs(sd);

		// Load clustering answer
		HashSet<String> bgc = StockUtil.loadBussinessGroupCompanies("bussiness_group.txt");
		System.out.println("Answer companies = " + bgc.size());
		
		// Baseline
		//g.removeBanks("bank.txt");		
		StockCluster sc = new StockCluster(false ,g, removeOneDegreeVertex, removeBankVertex, countDirectOwnCompany, alpha, beta);
		StockUndirectedGraph ug = sc.getSummarizedGraph();
		EdgeBetweennessClusterer<StockVertex, StockEdge> ec = new EdgeBetweennessClusterer<StockVertex, StockEdge>(numEdgesToRemove);
		Set<Set<StockVertex>> eb_clusters = ec.transform(ug);
		Set<Set<StockVertex>> eb_clusters_filtered = StockUtil.filterClusteringResult(eb_clusters, bgc);
		System.out.println("NMI = " + sc.getNMI(eb_clusters_filtered));
		
		// Save result
		StockUtil.saveClusters(eb_clusters, "eb_result_" + numEdgesToRemove + ".bin");
	}
}
