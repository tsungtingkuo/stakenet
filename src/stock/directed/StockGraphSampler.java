package stock.directed;
import java.util.*;

import stock.vertex.StockVertex;



public class StockGraphSampler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		StockGraph graph = StockGraph.load("vertex.txt", "edge.txt");
		System.out.println("Vertices = " + graph.getVertexCount());
		System.out.println("Edges = " + graph.getEdgeCount());
		StockGraph g = StockGraphSampler.sample(graph, 4);
		System.out.println("Vertices = " + g.getVertexCount());
		System.out.println("Edges = " + g.getEdgeCount());
	}

	// Remove low degree nodes
	// minDegree is inclusive
	public static StockGraph sample(StockGraph graph, int minDegree) {
		
		StockGraph g = graph.clone();
		
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
	
	// After sampling, clear single nodes (degree = 0)
	public static StockGraph sampleForViewer(StockGraph graph, int minDegree) {
		StockGraph g = sample(graph, minDegree);		
		return sample(g, 1);
	}
	
	public static StockGraph sample(StockGraph graph, String code, int level) {
		
		StockGraph g = graph.clone();
		
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
	
	public static HashSet<StockVertex> sampleRecursively(StockGraph graph, StockVertex target, int level) {
		HashSet<StockVertex> remain = new HashSet<StockVertex>();
		remain.add(target);
		if (level > 0) {
			for(StockVertex sv : graph.getNeighbors(target)) {
				remain.addAll(sampleRecursively(graph, sv, level - 1));
			}
		}
		return remain;
	}

}
