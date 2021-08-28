package stock.directed;

import java.io.*;
import java.util.*;
import stock.edge.StockEdge;
import stock.undirected.StockUndirectedGraph;
import stock.util.StockData;
import stock.util.StockGroundTruth;
import stock.util.StockRevenue;
import stock.vertex.StockVertex;
import utility.*;
import edu.uci.ics.jung.graph.*;

public class StockGraph extends DirectedSparseGraph<StockVertex, StockEdge> implements Serializable {

	public static final int CRITERION_MIN_DEGREE = 0;
	public static final int CRITERION_MAX_DEGREE = 1;
	public static final int CRITERION_MIN_IN_DEGREE = 2;
	public static final int CRITERION_MAX_IN_DEGREE = 3;
	public static final int CRITERION_MIN_OUT_DEGREE = 4;
	public static final int CRITERION_MAX_OUT_DEGREE = 5;
	public static final int CRITERION_MIN_NODE_WEIGHT = 6;
	public static final int CRITERION_MAX_NODE_WEIGHT = 7;
	public static final int CRITERION_MIN_CC = 8;
	public static final int CRITERION_MAX_CC = 9;
	
	private static final long serialVersionUID = -5412372221359694220L;

	// For speedup
	HashMap<String, StockVertex> codeToVertex = new HashMap<String, StockVertex>();
	HashMap<String, StockVertex> nameToVertex = new HashMap<String, StockVertex>();

	public static void main(String[] args) throws Exception {		
	}
	
	public void findDuplicateName() {		
		HashSet<String> hs = new HashSet<String>();
		for(StockVertex sv : codeToVertex.values()) {
			if(hs.contains(sv.getName())) {
				System.out.println(sv.toFileString());
			}
			else {
				hs.add(sv.getName());
			}
		}
	}

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
	
	public StockVertex addNewVertex(String code, String name, long stock) {
		if(code.equalsIgnoreCase("")) {
			return addNewVertex(name, stock);
		}
		else {
			StockVertex sv = getVertexByCode(code);
			if(sv == null) {
				sv = StockVertex.generateVertex(code, name, stock, StockVertex.TYPE_PUBLIC);
				addVertex(sv);
			}
			else {
				sv.setStock(sv.getStock() + stock);
			}
			return sv;
		}
	}
	
	public StockEdge addNewEdge(StockVertex source, StockVertex destination, long stock, int type) {
		StockEdge se = StockEdge.generateEdge(stock, source.getName(), destination.getName(), type);
		boolean added = super.addEdge(se, source, destination);
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
	
	public int getTotalPublicVertexCount() {
		return codeToVertex.size();
	}

	public int getCompanyVertexCount() {
		return getPublicVertices().size();
	}

	public int getPersonVertexCount() {
		return (getVertexCount() - getCompanyVertexCount());
	}

	public HashSet<StockVertex> getPublicVertices() {
		HashSet<StockVertex> publicVertices = new HashSet<StockVertex>();
		for(StockVertex sv: getVertices()) {
			//if(codeToVertex.containsKey(sv.getCode()) == true) {
			if(sv.getCode().equalsIgnoreCase("") == false) {
				publicVertices.add(sv);
			}
		}
		return publicVertices;
	}
	
	public HashSet<StockVertex> getNonPublicVertices() {
		HashSet<StockVertex> nonPublicVertices = new HashSet<StockVertex>();
		for(StockVertex sv: getVertices()) {
			//if(codeToVertex.containsKey(sv.getCode()) == false) {
			if(sv.getCode().equalsIgnoreCase("") == true) {
				nonPublicVertices.add(sv);
			}
		}
		return nonPublicVertices;
	}
	
	public HashSet<String> getPublicVertexCodes() {
		HashSet<String> publicVertexCodes = new HashSet<String>();
		for(StockVertex sv : codeToVertex.values()) {
			publicVertexCodes.add(sv.getCode());
		}
		return publicVertexCodes;
	}

	public HashSet<String> getPublicVertexNames() {
		HashSet<String> publicVertexNames = new HashSet<String>();
		for(StockVertex sv : codeToVertex.values()) {
			publicVertexNames.add(sv.getName());
		}
		return publicVertexNames;
	}
		
	public int getHoldManageEdgeCount() {
		int count = 0;
		for(StockEdge se : getEdges()) {
			if(se.getType() == StockEdge.TYPE_HOLD) {
				count++;
			}
		}
		return count;
	}

	public int getHoldEdgeCount() {
		int count = 0;
		for(StockEdge se : getEdges()) {
			if(se.getType()==StockEdge.TYPE_HOLD && se.getStock()>0) {
				count++;
			}
		}
		return count;
	}

	public int getManageEdgeCount() {
		int count = 0;
		for(StockEdge se : getEdges()) {
			if(se.getType()==StockEdge.TYPE_HOLD && se.getStock()==0) {
				count++;
			}
		}
		return count;
	}

	public int getTransferEdgeCount() {
		int count = 0;
		for(StockEdge se : getEdges()) {
			if(se.getType() == StockEdge.TYPE_TRANSFER) {
				count++;
			}
		}
		return count;
	}

	public static void save(StockGraph graph, String vertexFileName, String edgeFileName) throws Exception {
	    
		// Vertex
		PrintWriter pwv = new PrintWriter(vertexFileName);
	    for(StockVertex sv : graph.getVertices()) {
	    	pwv.println(sv.toFileString());
		}					
	    pwv.close();

	    // Edge
	    PrintWriter pwe = new PrintWriter(edgeFileName);
		for(StockEdge se : graph.getEdges()) {
	    	pwe.println(se.toFileString());
		}					
	    pwe.close();
	}
	
	public static StockGraph load(String vertexFileName, String edgeFileName, int minDegree) throws Exception {
		StockGraph graph = load(vertexFileName, edgeFileName);
		StockGraphSampler.sample(graph, minDegree);
		return graph;
	}

	public static StockGraph load(String vertexFileName, String edgeFileName) throws Exception {
		StockGraph graph = new StockGraph();
		
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
	
	public StockGraph clone() {
		StockGraph g = new StockGraph();
		
		// Vertex
		for(StockVertex sv : getVertices()) {
			StockVertex v = StockVertex.generateVertex(sv.toFileString());
			v.setGround(sv.getGround());
			v.setEnglish(sv.getEnglish());
			g.addVertex(v);
		}
		
		// Edge
		for(StockEdge se : getEdges()) {
			StockEdge e = StockEdge.generateEdge(se.toFileString());
			e.setValue(se.getValue());
			StockVertex source = g.getVertexByName(e.getSourceName());
			StockVertex destination = g.getVertexByName(e.getDestinationName());
			g.addEdge(e, source, destination);
		}
		
		return g;
	}
	
	public StockEdge getEdgeByVertexNames(String sourceName, String destinationName) {
		StockVertex source = getVertexByName(sourceName);
		StockVertex destination = getVertexByName(destinationName);
		return findEdge(source, destination);
	}
	
	public void labelVerticesUsingGroundTruth(StockGroundTruth ground) {
		for(String code : ground.getCodeToRevenueMap().keySet()) {
			int result = ground.getCodeToRevenueMap().get(code);
			StockVertex sv = getVertexByCode(code);
			if(sv != null) {
				sv.setGround(result);
			}
		}
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
	
	public int getInNeighborCount(StockVertex sv, int vertexType, int edgeType) {
		int count = 0;
		for(StockVertex v : getNeighbors(sv)) {
			if(v.getType() == vertexType) {
				StockEdge se = findEdge(v, sv);
				if(se!=null && se.getType()==edgeType) {
					count++;
				}
			}
		}
		return count;
	}

	public HashSet<StockVertex> getInNeighbors(StockVertex sv) {
		HashSet<StockVertex> result = new HashSet<StockVertex>();
		for(StockVertex v : getNeighbors(sv)) {
			StockEdge se = findEdge(v, sv);
			if(se!=null) {
				result.add(v);
			}
		}
		return result;
	}
	
	public int getOutNeighborCount(StockVertex sv, int vertexType, int edgeType) {
		int count = 0;
		for(StockVertex v : getNeighbors(sv)) {
			if(v.getType() == vertexType) {
				StockEdge se = findEdge(sv, v);
				if(se!=null && se.getType()==edgeType) {
					count++;
				}
			}
		}
		return count;
	}
	
	public HashSet<StockVertex> getOutNeighbors(StockVertex sv) {
		HashSet<StockVertex> result = new HashSet<StockVertex>();
		for(StockVertex v : getNeighbors(sv)) {
			StockEdge se = findEdge(sv, v);
			if(se!=null) {
				result.add(v);
			}
		}
		return result;
	}
	
	public long getInNeighborWeight(StockVertex sv, int vertexType, int edgeType) {
		long weight = 0;
		for(StockVertex v : getNeighbors(sv)) {
			if(v.getType() == vertexType) {
				StockEdge se = findEdge(v, sv);
				if(se!=null && se.getType()==edgeType) {
					weight += se.getStock();
				}
			}
		}
		return weight;
	}

	public long getOutNeighborWeight(StockVertex sv, int vertexType, int edgeType) {
		long weight = 0;
		for(StockVertex v : getNeighbors(sv)) {
			if(v.getType() == vertexType) {
				StockEdge se = findEdge(sv, v);
				if(se!=null && se.getType()==edgeType) {
					weight += se.getStock();
				}
			}
		}
		return weight;
	}
	
	public int getNeighborCount(StockVertex sv, int vertexType, int edgeType) {
		return getInNeighborCount(sv, vertexType, edgeType) + getOutNeighborCount(sv, vertexType, edgeType);
	}
	
	public Vector<StockVertex> getPublicKNNByCode(String code, int K, int criterion) {
		return getPublicKNN(getVertexByCode(code), K, criterion);
	}
	
	public Vector<StockVertex> getPublicKNN(StockVertex sv, int K, int criterion) {
		Vector<StockVertex> knn = new Vector<StockVertex>();
		if(K == 0) {
			return knn;
		}
		TreeSet<StockVertex> checked = new TreeSet<StockVertex>();
		TreeSet<StockVertex> target = new TreeSet<StockVertex>();
		setVertexValue(sv, criterion);
		knn.add(sv);
		target.add(sv);
		getPublicKNN(knn, checked, target, K+1, criterion);
		knn.remove(0);
		return knn;		
	}
	
	public void getPublicKNN(Vector<StockVertex> knn, TreeSet<StockVertex> checked, TreeSet<StockVertex> target, int K, int criterion) {

		//System.out.println("knn = " + knn + ", checked = " + checked + ", target = " + target);

		TreeSet<StockVertex> pub = new TreeSet<StockVertex>();
		
		// Set value of v for sorting
		for(StockVertex vi : target) {
			for(StockVertex vj : getNeighbors(vi)) {
				if(checked.contains(vj) == false) {
					if(vj.getType() == StockVertex.TYPE_PUBLIC) {
						setVertexValue(vj, criterion);
						pub.add(vj);
					}
				}
			}
		}
		
		// Decide priority
		Iterator<StockVertex> it = null;
		if(criterion%2 == 0) {			// MIN
			it = pub.iterator();
		}
		else {							// MAX
			it = pub.descendingIterator();
		}
		
		//System.out.println("pub = " + pub);
		
		// Add pub to knn
		for( ; it.hasNext(); ) {
			StockVertex v = it.next();
			knn.add(v);
			if(knn.size() == K) {								// Done
				return;
			}
		}

		// Compute new target
		checked.addAll(target);
		//System.out.println("checked = " + checked);
		if(target.size() == 0) {
			return;
		}
		
		// Recursively find KNN
		TreeSet<StockVertex> newTarget = new TreeSet<StockVertex>();
		for(StockVertex vi : target) {
			//System.out.println(vi.getName() + ", neighbor = " +  getNeighbors(vi));
			for(StockVertex vj : getNeighbors(vi)) {
				if(checked.contains(vj) == false) {
					//System.out.println(vj.getName() + " = " + false);
					newTarget.add(vj);
				}
				else {
					//System.out.println(vj.getName() + " = " + true);					
				}
			}
		}
		
		//System.out.println("newTarget = " + newTarget);
		//System.out.println();
		
		getPublicKNN(knn, checked, newTarget, K, criterion);

		return;
	}
	
	public void setVertexValue(StockVertex sv, int criterion) {
		switch(criterion) {
		
		case CRITERION_MIN_DEGREE:
		case CRITERION_MAX_DEGREE:
			sv.setValue(degree(sv));
			break;

		case CRITERION_MIN_IN_DEGREE:
		case CRITERION_MAX_IN_DEGREE:
			sv.setValue(inDegree(sv));
			break;
		
		case CRITERION_MIN_OUT_DEGREE:
		case CRITERION_MAX_OUT_DEGREE:
			sv.setValue(outDegree(sv));
			break;
		
		case CRITERION_MIN_NODE_WEIGHT:
		case CRITERION_MAX_NODE_WEIGHT:
			sv.setValue(sv.getStock());
			break;
		
		case CRITERION_MIN_CC:
		case CRITERION_MAX_CC:
			sv.setValue(getCC(sv));		
			break;
		}
	}
	
	public TreeSet<StockVertex> filtPublicVertices(TreeSet<StockVertex> vertices) {
		TreeSet<StockVertex> pub = new TreeSet<StockVertex>();
		for(StockVertex v : vertices) {
			if(v.getType() == StockVertex.TYPE_PUBLIC) {
				pub.add(v);
			}
		}
		return pub;	
	}
	
	public TreeSet<StockVertex> getNeighborsContainKeyword(StockVertex sv, String keyword) {
		TreeSet<StockVertex> neighbors = new TreeSet<StockVertex>();
		for(StockVertex v : getNeighbors(sv)) {
			if(v.getName().contains(keyword)) {
				neighbors.add(v);
			}
		}
		return neighbors;		
	}

	public boolean isNeighborContainMarket(StockVertex sv) {
		for(StockVertex v : getNeighbors(sv)) {
			if(v.getName().equalsIgnoreCase("")) {
				return true;
			}
		}
		return false;		
	}

	public TreeSet<StockVertex> getMarketRelatedNeighbors(StockVertex sv) {
		TreeSet<StockVertex> neighbors = new TreeSet<StockVertex>();
		for(StockVertex v : getNeighbors(sv)) {
			if(isNeighborContainMarket(v) == true) {
				neighbors.add(v);
			}
		}
		return neighbors;		
	}
	
	public void setVertexEnglishs(StockData sd) {
		for(StockVertex v : getVertices()) {
			String english = sd.getEnglish(v.getCode());
			if(english != null) {
				v.setEnglish(english);
			}
		}
	}
	
	public void setEdgeValues(StockRevenue price, int year, int month) {
		
		// Find max value
		double maxValue = 0;
		for(StockVertex v : getVertices()) {
			Long p = price.getRevenue(v.getCode(), year, month);
			if(p != null) {
				Collection<StockEdge> es = this.getInEdges(v);
				for(StockEdge e : es) {
					double value = Math.log10((e.getStock()/1000)*p);
					if(maxValue < value) {
						maxValue = value;
					}
				}
			}
		}
		System.out.println(maxValue);
		
		// Set values
		int count = 0;
		for(StockVertex v : getVertices()) {
			Long p = price.getRevenue(v.getCode(), year, month);
			if(p != null) {
				Collection<StockEdge> es = this.getInEdges(v);
				for(StockEdge e : es) {
					double normalizedValue = Math.log10((e.getStock()/1000)*p)/maxValue;
					e.setValue(normalizedValue);
					count++;
				}
			}
		}
		System.out.println(count);
		
		// Assign small probability to zero valued edges
		double minValue = 0.000001;
		for(StockEdge e : getEdges()) {
			if(e.getValue() < minValue) {
				e.setValue(minValue);
			}
		}
	}
	
	public StockUndirectedGraph toUndirected() {
		StockUndirectedGraph ug = new StockUndirectedGraph();
		
		// Vertex
		for(StockVertex sv : getVertices()) {
			StockVertex v = StockVertex.generateVertex(sv.toFileString());
			v.setGround(sv.getGround());
			v.setEnglish(sv.getEnglish());
			ug.addVertex(v);
		}
		
		// Edge
		for(StockEdge se : getEdges()) {
			StockEdge e = StockEdge.generateEdge(se.toFileString());
			e.setValue(se.getValue());
			StockVertex source = ug.getVertexByName(e.getSourceName());
			StockVertex destination = ug.getVertexByName(e.getDestinationName());
			ug.addEdge(e, source, destination);
		}
		
		return ug;
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
