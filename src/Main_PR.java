import java.util.*;
import stock.directed.StockGraph;
import stock.edge.StockEdgeValueTransformer;
import stock.ranker.StockPageRanker;
import stock.undirected.StockUndirectedGraph;
import stock.util.*;
import utility.*;

public class Main_PR {

	public static void main(String[] args) throws Exception {
				
		// Market value
		boolean marketValue = true;
		
		// PR parameter
		double alpha = 0.15;
		
		// Time
		int targetYear = 98;
		int targetMonth = 10;
		
		// Parameters
		int removeSingleNodes = 1;		
		
		// Graph name
		String mv = "";
		if(marketValue == true) {
			mv = "_MV";
		}
		String vertexFileName = "graph/vertex_" + targetYear + "_" + targetMonth + mv + ".txt";		
		String edgeFileName = "graph/edge_" + targetYear + "_" + targetMonth + mv + ".txt";

		// Load evaluable companies
		Vector<String> evaluable = Utility.loadVector("evaluable.txt");
		
		// Load price
		StockRevenue price = StockRevenue.load("price.csv");

		// Load stock data
		StockData sd = StockData.load("exception.csv", "company.csv", "english.csv");

		// Load graph (directed)
		StockGraph g = StockGraph.load(vertexFileName, edgeFileName, removeSingleNodes);
		g.setVertexEnglishs(sd);
		
		// Compute edge value
		g.setEdgeValues(price, targetYear, targetMonth);
		
		// Convert to undirected
		StockUndirectedGraph ug = g.toUndirected();
		
		// Page Rank
		StockEdgeValueTransformer t = new StockEdgeValueTransformer();
		StockPageRanker spr = new StockPageRanker(ug, t, alpha);
		
		System.out.print("Starting Page Rank ... ");
		spr.evaluate();
		System.out.println("done!");
		
		spr.saveResult("pr.txt", ug, evaluable);
	}
}
