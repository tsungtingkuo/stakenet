import java.io.*;
import java.util.*;
import stock.directed.StockGraph;
import stock.directed.StockGraphStatistics;
import stock.undirected.StockUndirectedGraph;
import stock.undirected.StockUndirectedGraphStatistics;
import stock.util.*;
import stock.vertex.StockVertex;
import utility.*;

public class Main_Dynamic {

	public static void main(String[] args) throws Exception {		

		// Parameters
		int highYear = 98;
		int lowYear = 90;
		int removeSingleNodes = 1;

		
		// Create directory
		String dir = "statistics";
		File f = new File(dir);
		f.mkdir();

		// Dynamic statistics
		Vector<String> v = new Vector<String>();
		StockGraph g = null;
		StockUndirectedGraph ug = null;
		
		for(int i=lowYear; i<highYear; i++) {
			
			for(int j=1; j<=12; j++) {
				
				System.out.print("Processing year = " + (i+1) + ", month = " + j + " ... ");
				
				String vertexFileName = "graph/vertex_" + (i+1) + "_" + j + ".txt";		
				String edgeFileName = "graph/edge_" + (i+1) + "_" + j + ".txt";

				String s = (i+1) + "," + j;
				
				// Load stock data
				StockData sd = StockData.load("exception.csv", "company.csv", "english.csv");

				// Load graph (directed)
				g = StockGraph.load(vertexFileName, edgeFileName, removeSingleNodes);
				g.setVertexEnglishs(sd);

				// Load graph (undirected)
				ug = StockUndirectedGraph.load(vertexFileName, edgeFileName, removeSingleNodes);

				// Vertex count
				int vertexCount = g.getVertexCount();
				int companyVertexCount = g.getCompanyVertexCount();
				int personVertexCount = g.getPersonVertexCount();
				s += "," + vertexCount;
				s += "," + companyVertexCount;
				s += "," + personVertexCount;
				
				// Edge count
				int edgeCount = g.getEdgeCount();
				int holdEdgeCount = g.getHoldEdgeCount();
				int manageEdgeCount = g.getManageEdgeCount();
				int transferEdgeCount = g.getTransferEdgeCount();
				s += "," + edgeCount;
				s += "," + holdEdgeCount;
				s += "," + manageEdgeCount;
				s += "," + transferEdgeCount;
				
				// Giant component size
				Set<StockVertex> giant = StockGraphStatistics.getGiantConnectedComponent(g);
				s += "," + giant.size();
				
				// Average degree (Z1 and Z2)
				TreeMap<Integer, Integer> df = StockGraphStatistics.getDegreeFrequency(g, StockGraphStatistics.DEGREE_BOTH, StockGraphStatistics.VERTEX_BOTH);
				double z1 = StockGraphStatistics.getZ1(df, vertexCount);
				double z2 = StockGraphStatistics.getZ2(df, vertexCount, z1);
				s += "," + z1;
				s += "," + z2;
				
				// Clustering coefficients (directed)
				double cc = StockGraphStatistics.getCC(g);
				s += "," + cc;
						
				// Clustering coefficients (undirected)
				double ucc = StockUndirectedGraphStatistics.getCC(ug);
				s += "," + ucc;
				
				v.add(s);
				
				System.out.println("done.");
			}
		}
		
		Utility.saveVector(dir + "/dynamic.csv", v);
	}
}
