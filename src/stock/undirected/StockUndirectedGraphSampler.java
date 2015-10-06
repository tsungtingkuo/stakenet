package stock.undirected;
import java.util.*;

import stock.vertex.StockVertex;



public class StockUndirectedGraphSampler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		StockUndirectedGraph graph = StockUndirectedGraph.load("vertex.txt", "edge.txt");
		System.out.println("Vertices = " + graph.getVertexCount());
		System.out.println("Edges = " + graph.getEdgeCount());
		StockUndirectedGraphSampler.sample(graph, 4);
		System.out.println("Vertices = " + graph.getVertexCount());
		System.out.println("Edges = " + graph.getEdgeCount());
	}

	// Remove low degree nodes
	// minDegree is inclusive
	public static StockUndirectedGraph sample(StockUndirectedGraph graph, int minDegree) {

		StockUndirectedGraph g = graph.clone();

		HashSet<StockVertex> remove = new HashSet<StockVertex>();
	    
		for(StockVertex sv : g.getVertices()) {
	    	if(g.getNeighborCount(sv) < minDegree) {
	    		remove.add(sv);
	    	}
	    }

		for(StockVertex sv : remove) {
	    	g.removeVertex(sv);
		}
		
		return g;
	}
	
	public static StockUndirectedGraph sampleRandomly(StockUndirectedGraph graph, int level) {
		
		StockUndirectedGraph g = graph.clone();
		
		HashSet<StockVertex> remain = sampleRecursively(g, g.getRandomVertex(), level);
		HashSet<StockVertex> remove = new HashSet<StockVertex>();
	    
		for(StockVertex sv : g.getVertices()) {
	    	if(remain.contains(sv) == false) {
	    		remove.add(sv);
	    	}
	    }
		
		for(StockVertex sv : remove) {
	    	g.removeVertex(sv);
		}

		return g;
	}

	public static StockUndirectedGraph sample(StockUndirectedGraph graph, String code, int level) {
		
		StockUndirectedGraph g = graph.clone();
		
		HashSet<StockVertex> remain = sampleRecursively(g, g.getVertexByCode(code), level);
		HashSet<StockVertex> remove = new HashSet<StockVertex>();
	    
		for(StockVertex sv : g.getVertices()) {
	    	if(remain.contains(sv) == false) {
	    		remove.add(sv);
	    	}
	    }
		
		for(StockVertex sv : remove) {
	    	g.removeVertex(sv);
		}

		return g;
	}

	public static StockUndirectedGraph sampleByName(StockUndirectedGraph graph, String name, int level) {
		
		StockUndirectedGraph g = graph.clone();
		
		HashSet<StockVertex> remain = sampleRecursively(g, g.getVertexByName(name), level);
		HashSet<StockVertex> remove = new HashSet<StockVertex>();
	    
		for(StockVertex sv : g.getVertices()) {
	    	if(remain.contains(sv) == false) {
	    		remove.add(sv);
	    	}
	    }
		
		for(StockVertex sv : remove) {
	    	g.removeVertex(sv);
		}

		return g;
	}

	public static HashSet<StockVertex> sampleRecursively(StockUndirectedGraph graph, StockVertex target, int level) {
		HashSet<StockVertex> remain = new HashSet<StockVertex>();
		remain.add(target);
		if (level > 0) {
			for(StockVertex sv : graph.getNeighbors(target)) {
				remain.addAll(sampleRecursively(graph, sv, level - 1));
			}
		}
		return remain;
	}
	
	// After sampling, clear single nodes (degree = 0)
	public static StockUndirectedGraph sampleForViewer(StockUndirectedGraph graph, int minDegree) {
		StockUndirectedGraph g = sample(graph, minDegree);		
		return sample(g, 1);
	}
}
