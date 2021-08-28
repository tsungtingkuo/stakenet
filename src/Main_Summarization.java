
import stock.cluster.*;
import stock.directed.StockGraph;
import stock.undirected.StockUndirectedGraph;
import stock.undirected.StockUndirectedGraphSatelliteViewer;
import stock.util.*;
import stock.vertex.StockVertex;

public class Main_Summarization {

	public static void main(String[] args) throws Exception
	{		

		// Parameters
		int targetYear = 98;
		int targetMonth = 10;
		
		int removeSingleNodes = 1;
		int minDegree = 10;
			
		boolean removeOneDegreeVertex = true;
		boolean removeBankVertex = true;
		boolean countDirectOwnCompany = true;
		int alpha = 3;
		int beta = 20;
		
		boolean viewSocioGraph = true;
		boolean clearSocioGraph = true;
		
		boolean viewEgoGraph = false;
		int levelOfEgoGraph = 2;
		
		boolean displayFullName = false;
		boolean displayEdgeWeight = false;		

		boolean stakeCompanyNet = false;	// True=company, false=person
		
		String nameOfEgoGraph = null;

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

		//////////////////////////////////////////////////////
		//
		// Summarization
		//
		//////////////////////////////////////////////////////

		StockCluster sc = new StockCluster(false ,g, removeOneDegreeVertex, removeBankVertex, countDirectOwnCompany, alpha, beta);		
		
		StockUndirectedGraph ug = null;
		
		if(stakeCompanyNet == true) {
			ug = sc.getStakeCompanyNet();
			nameOfEgoGraph = "µØºÓ¹q¸£"; 
			System.out.println("StakeCompanyNet");
		}
		else {
			ug = sc.getStakePersonNet();
			nameOfEgoGraph = "¬I±R´Å";
			System.out.println("StakePersonNet");
		}

		System.out.println("Vertex (summarized) = " + ug.getVertexCount());
		System.out.println("Edge (summarized) = " + ug.getEdgeCount());
		
		//////////////////////////////////////////////////////
		//
		// Visualization
		//
		//////////////////////////////////////////////////////

		// Initialize
		StockVertex.setFullName(displayFullName);
		
		// View graph (socio)
		if(viewSocioGraph == true) {
			StockUndirectedGraphSatelliteViewer.viewSocio(ug, minDegree, clearSocioGraph, displayEdgeWeight);
		}
		
		// View graph (ego)
		if(viewEgoGraph == true) {
			StockUndirectedGraphSatelliteViewer.viewEgoByName(ug, nameOfEgoGraph, levelOfEgoGraph, displayEdgeWeight);
		}
	}
}
