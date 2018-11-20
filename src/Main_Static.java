import java.io.*;
import java.util.*;
import stock.directed.StockGraph;
import stock.directed.StockGraphSatelliteViewer;
import stock.directed.StockGraphStatistics;
import stock.undirected.StockUndirectedGraph;
import stock.undirected.StockUndirectedGraphStatistics;
import stock.util.*;
import stock.vertex.StockVertex;

public class Main_Static {

	public static void main(String[] args) throws Exception {		

		//////////////////////////////////////////////////////
		//
		// Parameters
		//
		//////////////////////////////////////////////////////
		
		// Market value
		boolean marketValue = true;
		
		int targetYear = 98;
		int targetMonth = 10;
		
		int removeSingleNodes = 1;
		int minDegree = 30;
		
		int discreteLevel = 5;
		int maxDegree = 1000;
		long maxWeight = 10000000000000000l;
		
		boolean regenerateGroundTruth = true;

		boolean viewSocioGraph = true;
		boolean clearSocioGraph = true;
		
		boolean viewEgoGraph = false;
		String codeOfEgoGraph = "2357";
		int levelOfEgoGraph = 2;
		
		boolean displayEdgeWeight = true;
		
		
		//////////////////////////////////////////////////////
		//
		// Graph
		//
		//////////////////////////////////////////////////////
		
		// Graph name
		// Graph name
		String mv = "";
		if(marketValue == true) {
			mv = "_MV";
		}
		String vertexFileName = "graph/vertex_" + targetYear + "_" + targetMonth + mv + ".txt";		
		String edgeFileName = "graph/edge_" + targetYear + "_" + targetMonth + mv + ".txt";

		// Load stock data
		StockData sd = StockData.load("exception.csv", "company.csv", "english.csv");

		// Load graph (directed)
		StockGraph g = StockGraph.load(vertexFileName, edgeFileName, removeSingleNodes);
		g.setVertexEnglishs(sd);

		// Load graph (undirected)
		StockUndirectedGraph ug = StockUndirectedGraph.load(vertexFileName, edgeFileName);

		//////////////////////////////////////////////////////
		//
		// Statistics
		//
		//////////////////////////////////////////////////////
		
		// Create directory
		String dir = "statistics";
		File f = new File(dir);
		f.mkdir();

		// Vertex count
		int vertexCount = g.getVertexCount();
		int companyVertexCount = g.getCompanyVertexCount();
		int personVertexCount = g.getPersonVertexCount();
		System.out.println("Vertex = " + vertexCount);
		System.out.println("Vertex (company) = " + companyVertexCount);
		System.out.println("Vertex (person) = " + personVertexCount);

		// Edge count
		int edgeCount = g.getEdgeCount();
		int holdEdgeCount = g.getHoldEdgeCount();
		int manageEdgeCount = g.getManageEdgeCount();
		int transferEdgeCount = g.getTransferEdgeCount();
		System.out.println("Edge = " + edgeCount);
		System.out.println("Edge (hold) = " + holdEdgeCount);
		System.out.println("Edge (manage) = " + manageEdgeCount);
		System.out.println("Edge (transfer) = " + transferEdgeCount);
		
		// Degree frequencies
		StockGraphStatistics.getAndSaveLoggedDegreeFrequencies(g, dir + "/degree_frequencies.csv", discreteLevel, maxDegree);

		// Weight frequencies
		StockGraphStatistics.getAndSaveLoggedWeightFrequencies(g, dir + "/weight_frequencies.csv", discreteLevel, maxWeight);

		// Non-logged edge weight frequency
		StockGraphStatistics.getAndSaveEdgeWeightFrequency(g, dir + "/edge_weight_frequency.csv");
		
		// Giant component size
		Set<StockVertex> giant = StockGraphStatistics.getGiantConnectedComponent(g);
		System.out.println("GCC = " + giant.size());
		
		// Average degree (Z1 and Z2)
		TreeMap<Integer, Integer> df = StockGraphStatistics.getDegreeFrequency(g, StockGraphStatistics.DEGREE_BOTH, StockGraphStatistics.VERTEX_BOTH);
		double z1 = StockGraphStatistics.getZ1(df, vertexCount);
		double z2 = StockGraphStatistics.getZ2(df, vertexCount, z1);
		System.out.println("Z1 = " + z1);
		System.out.println("Z2 = " + z2);
		
		// Clustering coefficients (directed)
		double cc = StockGraphStatistics.getCC(g);
		System.out.println("CC (directed) = " + cc);
				
		// Clustering coefficients (undirected)
		double ucc = StockUndirectedGraphStatistics.getCC(ug);
		System.out.println("CC (undirected) = " + ucc);

		//////////////////////////////////////////////////////
		//
		// Ground truth
		//
		//////////////////////////////////////////////////////

		// Generate ground truth
		if(regenerateGroundTruth == true) {
			StockGroundTruth.generate(g, targetYear, targetMonth, targetYear-1, targetMonth, "revenue.csv", "ground.csv");
		}

		// Load ground truth
		StockGroundTruth ground = StockGroundTruth.load("ground.csv");
		System.out.println("Evaluable companies = " + ground.size());
		
		//////////////////////////////////////////////////////
		//
		// Visualization
		//
		//////////////////////////////////////////////////////

		// View graph (socio)
		if(viewSocioGraph == true) {
			g.labelVerticesUsingGroundTruth(ground);
			StockGraphSatelliteViewer.viewSocio(g, minDegree, clearSocioGraph, displayEdgeWeight);
		}
		
		// View graph (ego)
		if(viewEgoGraph == true) {
			g.labelVerticesUsingGroundTruth(ground);
			StockGraphSatelliteViewer.viewEgo(g, codeOfEgoGraph, levelOfEgoGraph, displayEdgeWeight);
		}


	}
}
