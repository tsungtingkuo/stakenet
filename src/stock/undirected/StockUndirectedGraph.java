package stock.undirected;

import java.io.*;
import java.util.*;
import stock.edge.StockEdge;
import stock.util.StockData;
import stock.util.StockGroundTruth;
import stock.vertex.StockVertex;
import utility.*;
import edu.uci.ics.jung.graph.*;

public class StockUndirectedGraph extends UndirectedSparseGraph<StockVertex, StockEdge> implements Serializable {

	private static final long serialVersionUID = 5507466311745266148L;
	
	// For speedup
	HashMap<String, StockVertex> codeToVertex = new HashMap<String, StockVertex>();
	HashMap<String, StockVertex> nameToVertex = new HashMap<String, StockVertex>();
	

	public StockVertex getVertexByCode(String code) {
		return codeToVertex.get(code);
	}
	
	public StockVertex getVertexByName(String name) {
		return nameToVertex.get(name);
	}
	
	public boolean addVertex(StockVertex v) {
		if(!v.getCode().equalsIgnoreCase("")) {
			codeToVertex.put(v.getCode(), v);
		}
		nameToVertex.put(v.getName(), v);
		return super.addVertex(v);
	}
		
	public StockVertex addNewVertex(String name) {
		StockVertex sv = getVertexByName(name);
		if(sv == null) {
			sv = StockVertex.generateVertex("", name, 0, StockVertex.TYPE_UNKNOWN);
			addVertex(sv);
		}
		return sv;
	}
	
	public StockVertex addNewVertex(String name, long stock) {
		StockVertex sv = getVertexByName(name);
		if(sv == null) {
			sv = StockVertex.generateVertex("", name, stock, StockVertex.TYPE_UNKNOWN);
			addVertex(sv);
		}
		else {
			sv.setStock(sv.getStock() + stock);
		}
		return sv;
	}
	
	public StockEdge addNewEdge(StockVertex source, StockVertex destination, long stock, int type) {
		StockEdge se = StockEdge.generateEdge(stock, source.getName(), destination.getName(), type);
		boolean added = addEdge(se, source, destination);
		if(added == true) {
			return se;
		}
		else {
			return null;
		}
			
	}
	
	public boolean removeVertex(StockVertex v) {
		codeToVertex.remove(v.getCode());
		nameToVertex.remove(v.getName());
		return super.removeVertex(v);
	}
	
	public int getPublicCount() {
		return codeToVertex.size();
	}
	
	public static StockUndirectedGraph load(String vertexFileName, String edgeFileName, int minDegree) throws Exception {
		StockUndirectedGraph graph = load(vertexFileName, edgeFileName);
		StockUndirectedGraphSampler.sample(graph, minDegree);
		return graph;
	}

	public static StockUndirectedGraph load(String vertexFileName, String edgeFileName) throws Exception {
		StockUndirectedGraph graph = new StockUndirectedGraph();
		
		// Vertex
		FileReader frv = new FileReader(vertexFileName);
		LineNumberReader lnrv = new LineNumberReader(frv);
		String sv = null;
		while ((sv=lnrv.readLine()) != null) {
			StockVertex v = StockVertex.generateVertex(sv);
			graph.addVertex(v);
		}				
		lnrv.close();
		frv.close();	  
		
		// Edge
		FileReader fre = new FileReader(edgeFileName);
		LineNumberReader lnre = new LineNumberReader(fre);
		String se = null;
		while ((se=lnre.readLine()) != null) {
			StockEdge e = StockEdge.generateEdge(se);
			StockVertex source = graph.getVertexByName(e.getSourceName());
			StockVertex destination = graph.getVertexByName(e.getDestinationName());
			graph.addEdge(e, source, destination);
		}				
		lnre.close();
		fre.close();	    

		return graph;
	}
	
	public StockUndirectedGraph clone() {
		StockUndirectedGraph g = new StockUndirectedGraph();
		
		// Vertex
		for(Iterator<StockVertex> iv = getVertices().iterator(); iv.hasNext(); ) {
			StockVertex sv = iv.next();
			StockVertex v = StockVertex.generateVertex(sv.toFileString());
			v.setGround(sv.getGround());
			v.setEnglish(sv.getEnglish());
			g.addVertex(v);
		}
		
		// Edge
		for(Iterator<StockEdge> ie = getEdges().iterator(); ie.hasNext(); ) {
			StockEdge se = ie.next();
			StockEdge e = StockEdge.generateEdge(se.toFileString());
			e.setValue(se.getValue());
			StockVertex source = g.getVertexByName(e.getSourceName());
			StockVertex destination = g.getVertexByName(e.getDestinationName());
			g.addEdge(e, source, destination);
		}
		
		return g;
	}
	
	public void labelVerticesUsingGroundTruth(StockGroundTruth ground) {
		for(Iterator<String> it = ground.getCodeToRevenueMap().keySet().iterator(); it.hasNext(); ) {
			String code = it.next();
			int result = ground.getCodeToRevenueMap().get(code);
			StockVertex sv = getVertexByCode(code);
			if(sv != null) {
				sv.setGround(result);
			}
		}
	}
	
	public HashSet<String> getPublicVertexCodes() {
		HashSet<String> publicVertexCodes = new HashSet<String>();
		for(StockVertex sv : codeToVertex.values()) {
			publicVertexCodes.add(sv.getCode());
		}
		return publicVertexCodes;
	}

	public double getCC(StockVertex v) {
		int triangles = 0;
		int triples = 0;		
		Collection<StockVertex> neighbors = getNeighbors(v);
		for(StockVertex vi : neighbors) {
			for(StockVertex vj : neighbors) {
				if(findEdge(vi, vj) != null) {
					triangles++;
				}
				triples++;
			}
		}
		if(triples == 0) {
			return 0;
		}
		double cc = (double)triangles/(double)triples;		
		return cc;
	}
	
	public void setVertexEnglishs(StockData sd) {
		for(StockVertex v : getVertices()) {
			String english = sd.getEnglish(v.getCode());
			if(english != null) {
				v.setEnglish(english);
			}
		}
	}
	
	public StockVertex getRandomVertex() {
		Random random = new Random();
		int number = random.nextInt(getVertexCount());
		int count = 0;
		StockVertex result = null;
		for(StockVertex v : getVertices()) {
			result = v;
			if(count == number) {
				break;
			}
			count++;
		}
		return result;
	}
	
	public void removeBanks(String fileName) throws Exception {
		Vector<String> banks = Utility.loadVector("bank.txt");
		TreeSet<StockVertex> removeSet = new TreeSet<StockVertex>();
		
		for (StockVertex v : getVertices()) {
			if (banks.contains(v.getName())) {
				removeSet.add(v);			
			}
		}
		
		for (StockVertex v: removeSet) {
			removeVertex(v);		
		}
	}
}
