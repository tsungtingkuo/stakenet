
import java.util.Set;

import stock.undirected.StockUndirectedGraph;
import stock.undirected.StockUndirectedGraphStatistics;
import stock.vertex.StockVertex;

public class Main_APL {

	public static void main(String[] args) throws Exception {		

		// Load graph
		StockUndirectedGraph g = StockUndirectedGraph.load("vertex.txt", "edge.txt", 1);

		// Giant component size
		Set<StockVertex> giant = StockUndirectedGraphStatistics.getGiantConnectedComponent(g);
		System.out.println("GCC = " + giant.size());
		
		// Average path length
		double apl = StockUndirectedGraphStatistics.getAPL(g, giant);
		System.out.println("APL = " + apl);
	}
}
