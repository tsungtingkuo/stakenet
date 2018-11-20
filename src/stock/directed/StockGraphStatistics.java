package stock.directed;

import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.algorithms.filters.*;
import edu.uci.ics.jung.algorithms.shortestpath.*;
import java.util.*;
import java.io.*;

import stock.edge.StockEdge;
import stock.vertex.StockVertex;
import utility.*;

public class StockGraphStatistics {

	public static final int DEGREE_BOTH = 0;
	public static final int DEGREE_IN = 1;
	public static final int DEGREE_OUT = 2;
	
	public static final int VERTEX_BOTH = 0;
	public static final int VERTEX_PUBLIC = 1;
	public static final int VERTEX_NON_PUBLIC = 2;

    StockGraph graph = new StockGraph();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		// Load graph (with min degree = 1)
		StockGraph g = StockGraph.load("vertex.txt", "edge.txt", 1);
		
		// Vertex count
		int vertexCount = g.getVertexCount();
		int pulicVertexCount = g.getPublicVertices().size();
		System.out.println("Vertex = " + vertexCount);
		System.out.println("Public vertex= " + pulicVertexCount);

		// Edge count
		int edgeCount = g.getEdgeCount();
		System.out.println("Edge = " + edgeCount);
		
		// Degree frequency
		TreeMap<Integer, Integer> df = StockGraphStatistics.getDegreeFrequency(g, StockGraphStatistics.DEGREE_BOTH, StockGraphStatistics.VERTEX_BOTH);
		StockGraphStatistics.saveDegreeFrequency(df, "graph_frequency.csv");
		System.out.println("DF = " + df.size());
		TreeMap<Integer, String> ud = StockGraphStatistics.getUniqueDegree(g, df, StockGraphStatistics.DEGREE_BOTH);
		StockGraphStatistics.saveDegreeFrequencyWithUniqueNote(df, ud, "graph_frequency_note.csv");		

		// In-Degree frequency
		TreeMap<Integer, Integer> idf = StockGraphStatistics.getDegreeFrequency(g, StockGraphStatistics.DEGREE_IN, StockGraphStatistics.VERTEX_BOTH);
		StockGraphStatistics.saveDegreeFrequency(idf, "graph_in_frequency.csv");
		System.out.println("IDF = " + idf.size());
		TreeMap<Integer, String> iud = StockGraphStatistics.getUniqueDegree(g, idf, StockGraphStatistics.DEGREE_IN);
		StockGraphStatistics.saveDegreeFrequencyWithUniqueNote(idf, iud, "graph_in_frequency_note.csv");		

		// Out-Degree frequency
		TreeMap<Integer, Integer> odf = StockGraphStatistics.getDegreeFrequency(g, StockGraphStatistics.DEGREE_OUT, StockGraphStatistics.VERTEX_BOTH);
		StockGraphStatistics.saveDegreeFrequency(odf, "graph_out_frequency.csv");
		System.out.println("ODF = " + odf.size());
		TreeMap<Integer, String> oud = StockGraphStatistics.getUniqueDegree(g, odf, StockGraphStatistics.DEGREE_OUT);
		StockGraphStatistics.saveDegreeFrequencyWithUniqueNote(odf, oud, "graph_out_frequency_note.csv");		

		// Degree distribution
		TreeMap<Integer, Double> dd = StockGraphStatistics.getDegreeDistribution(df, vertexCount);
		StockGraphStatistics.saveDegreeDistribution(dd, "graph_distribution.csv");

		// Vertex weight frequency
		TreeMap<Long, Integer> vwf = StockGraphStatistics.getVertexWeightFrequency(g);
		StockGraphStatistics.saveWeightFrequency(vwf, "vertex_weight_frequency.csv");
		System.out.println("VWF = " + vwf.size());
		TreeMap<Long, String> uwv = StockGraphStatistics.getUniqueWeightVertex(g, vwf);
		StockGraphStatistics.saveWeightFrequencyWithUniqueNote(vwf, uwv, "vertex_weight_frequency_note.csv");		

		// Edge weight frequency
		TreeMap<Long, Integer> ewf = StockGraphStatistics.getEdgeWeightFrequency(g);
		StockGraphStatistics.saveWeightFrequency(ewf, "edge_weight_frequency.csv");
		System.out.println("EWF = " + ewf.size());
		TreeMap<Long, String> uwe = StockGraphStatistics.getUniqueWeightEdge(g, ewf);
		StockGraphStatistics.saveWeightFrequencyWithUniqueNote(ewf, uwe, "edge_weight_frequency_note.csv");		

		// Giant component size
		Set<StockVertex> giant = StockGraphStatistics.getGiantConnectedComponent(g);
		System.out.println("GCC = " + giant.size());
		
		// Average degree (Z1 and Z2)
		double z1 = StockGraphStatistics.getZ1(df, vertexCount);
		double z2 = StockGraphStatistics.getZ2(df, vertexCount, z1);
		System.out.println("Z1 = " + z1);
		System.out.println("Z2 = " + z2);
		
		// Clustering coefficients
		double cc = StockGraphStatistics.getCC(g);
		System.out.println("CC = " + cc);

		// Average path length
		//double apl = StockGraphStatistics.getAPL(g, giant);
		//System.out.println("APL = " + apl);
	}
	
	public StockGraphStatistics(StockGraph g) {
		this.graph = g;
	}

	public static void getAndSaveVertexWeightFrequency(StockGraph g, String fileName) throws Exception {
		TreeMap<Long, Integer> wf = getVertexWeightFrequency(g);
		saveWeightFrequency(wf, fileName);
	}

	public static TreeMap<Long, Integer> getVertexWeightFrequency(StockGraph g) {
		TreeMap<Long, Integer> vertexWeightFrequency = new TreeMap<Long, Integer>();
		for(StockVertex v : g.getVertices()) {
			Long s = v.getStock();
			Integer c  = vertexWeightFrequency.get(s);
			if(c == null) {
				vertexWeightFrequency.put(s, 1);
			}
			else {
				vertexWeightFrequency.put(s, c+1);
			}
		}
		return vertexWeightFrequency;
	}
	
	public static void getAndSaveLoggedWeightFrequencies(StockGraph g, String fileName, int discreteLevel, long maxDegree) throws Exception {
		TreeMap<Long, Integer> vwf = getLoggedVertexWeightFrequency(g, StockGraphStatistics.VERTEX_BOTH, discreteLevel, maxDegree);
		TreeMap<Long, Integer> pvwf = getLoggedVertexWeightFrequency(g, StockGraphStatistics.VERTEX_PUBLIC, discreteLevel, maxDegree);
		TreeMap<Long, Integer> npvwf = getLoggedVertexWeightFrequency(g, StockGraphStatistics.VERTEX_NON_PUBLIC, discreteLevel, maxDegree);
		TreeMap<Long, Integer> ewf = getLoggedEdgeWeightFrequency(g, discreteLevel, maxDegree);
		saveWeightFrequencies(vwf, pvwf, npvwf, ewf, fileName);
	}
	
	public static void saveWeightFrequencies(TreeMap<Long, Integer> vwf, TreeMap<Long, Integer> pvwf, TreeMap<Long, Integer> npvwf, TreeMap<Long, Integer> ewf, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Long k : vwf.keySet()) {
			pw.println(k + "," + vwf.get(k) + "," + pvwf.get(k) + "," + npvwf.get(k) + "," + ewf.get(k));
		}
		pw.close();
	}
	
	public static void getAndSaveLoggedVertexWeightFrequency(StockGraph g, String fileName, int vertexType, int discreteLevel, long maxDegree) throws Exception {
		TreeMap<Long, Integer> wf = getLoggedVertexWeightFrequency(g, vertexType, discreteLevel, maxDegree);
		saveWeightFrequency(wf, fileName);
	}
	
	public static TreeMap<Long, Integer> getLoggedVertexWeightFrequency(StockGraph g, int vertexType, int discreteLevel, long maxDegree) {
		
		// Initialize
		TreeMap<Long, Integer> loggedVertexWeightFrequency = new TreeMap<Long, Integer>();
		loggedVertexWeightFrequency.put(0l, 0);
		long key = 1;
		for(long i=1; i<=maxDegree; i+=Utility.discreteByLog(key, 1)) {
			key = Utility.discreteByLog(i, discreteLevel);
			loggedVertexWeightFrequency.put(key, 0);
		}	
		
		// Build
		Collection<StockVertex> vertices = getVertices(g, vertexType);
		for(StockVertex v : vertices) {
			Long s = v.getStock();
			s = Utility.discreteByLog(s, discreteLevel);
			Integer c  = loggedVertexWeightFrequency.get(s);
			loggedVertexWeightFrequency.put(s, c+1);
		}
		
		return loggedVertexWeightFrequency;
	}
	
	public static void getAndSaveEdgeWeightFrequency(StockGraph g, String fileName) throws Exception {
		TreeMap<Long, Integer> wf = getEdgeWeightFrequency(g);
		saveWeightFrequency(wf, fileName);
	}

	public static TreeMap<Long, Integer> getEdgeWeightFrequency(StockGraph g) {
		TreeMap<Long, Integer> edgeWeightFrequency = new TreeMap<Long, Integer>();
		for(StockEdge e : g.getEdges()) {
			Long s = e.getStock();
			Integer c  = edgeWeightFrequency.get(s);
			if(c == null) {
				edgeWeightFrequency.put(s, 1);
			}
			else {
				edgeWeightFrequency.put(s, c+1);
			}
		}
		return edgeWeightFrequency;
	}
	
	public static void getAndSaveLoggedEdgeWeightFrequency(StockGraph g, String fileName, int discreteLevel, long maxDegree) throws Exception {
		TreeMap<Long, Integer> wf = getLoggedEdgeWeightFrequency(g, discreteLevel, maxDegree);
		saveWeightFrequency(wf, fileName);
	}
	
	public static TreeMap<Long, Integer> getLoggedEdgeWeightFrequency(StockGraph g, int discreteLevel, long maxDegree) {
		
		// Initialize
		TreeMap<Long, Integer> loggedEdgeWeightFrequency = new TreeMap<Long, Integer>();
		loggedEdgeWeightFrequency.put(0l, 0);
		long key = 1;
		for(long i=1; i<=maxDegree; i+=Utility.discreteByLog(key, 1)) {
			key = Utility.discreteByLog(i, discreteLevel);
			loggedEdgeWeightFrequency.put(key, 0);
		}	
		
		// Build
		for(StockEdge e : g.getEdges()) {
			Long s = e.getStock();
			s = Utility.discreteByLog(s, discreteLevel);
			Integer c  = loggedEdgeWeightFrequency.get(s);
			loggedEdgeWeightFrequency.put(s, c+1);
		}
		
		return loggedEdgeWeightFrequency;
	}
	
	public static TreeMap<Long, String> getUniqueWeightVertex(StockGraph g, TreeMap<Long, Integer> vwf) {
		TreeMap<Long, String> uniqueWeightVertex = new TreeMap<Long, String>();
		for(StockVertex v : g.getVertices()) {
			Long s = v.getStock();
			String n = v.getName();
			if(vwf.get(s) == 1) {
				uniqueWeightVertex.put(s, n);
			}
			else {
				uniqueWeightVertex.put(s, "");
			}
		}
		return uniqueWeightVertex;
	}
	
	public static TreeMap<Long, String> getUniqueWeightEdge(StockGraph g, TreeMap<Long, Integer> ewf) {
		TreeMap<Long, String> uniqueWeightEdge = new TreeMap<Long, String>();
		for(StockEdge e : g.getEdges()) {
			Long s = e.getStock();
			String n = e.getSourceName() + "," + e.getDestinationName();
			if(ewf.get(s) == 1) {
				uniqueWeightEdge.put(s, n);
			}
			else {
				uniqueWeightEdge.put(s, ",");
			}
		}
		return uniqueWeightEdge;
	}
	
	public static TreeMap<Integer, String> getUniqueDegree(StockGraph g, TreeMap<Integer, Integer> df, int type) {
		TreeMap<Integer, String> uniqueDegree = new TreeMap<Integer, String>();
		for(StockVertex v : g.getVertices()) {
			Integer d  = getDegree(g, v, type);
			String n = v.getName();
			if(df.get(d) == 1) {
				uniqueDegree.put(d, n);
			}
			else {
				uniqueDegree.put(d, "");
			}
		}
		return uniqueDegree;
	}
	
	public static int getDegree(StockGraph g, StockVertex v, int degreeType) {
		int d = 0;
		switch(degreeType) {
		case StockGraphStatistics.DEGREE_BOTH:
			d = g.degree(v);
			break;
		case StockGraphStatistics.DEGREE_IN:
			d = g.inDegree(v);
			break;
		case StockGraphStatistics.DEGREE_OUT:
			d = g.outDegree(v);
			break;			
		}
		return d;
	}

	public static Collection<StockVertex> getVertices(StockGraph g, int vertexType) {
		Collection<StockVertex> c = null;
		switch(vertexType) {
		case StockGraphStatistics.VERTEX_BOTH:
			c = g.getVertices();
			break;
		case StockGraphStatistics.VERTEX_PUBLIC:
			c = g.getPublicVertices();
			break;
		case StockGraphStatistics.VERTEX_NON_PUBLIC:
			c = g.getNonPublicVertices();
			break;			
		}
		return c;
	}
	
	public static void getAndSaveDegreeFrequency(StockGraph g, int degreeType, int vertexType, String fileName) throws Exception {
		TreeMap<Integer, Integer> df = getDegreeFrequency(g, degreeType, vertexType);
		saveDegreeFrequency(df, fileName);
	}
	
	public static TreeMap<Integer, Integer> getDegreeFrequency(StockGraph g, int degreeType, int vertexType) {
		TreeMap<Integer, Integer> degreeFrequency = new TreeMap<Integer, Integer>();
		Collection<StockVertex> vertices = getVertices(g, vertexType);
		for(StockVertex v : vertices) {
			Integer d  = getDegree(g, v, degreeType);
			Integer c  = degreeFrequency.get(d);
			if(c == null) {
				degreeFrequency.put(d, 1);
			}
			else {
				degreeFrequency.put(d, c+1);
			}
		}
		return degreeFrequency;
	}
	
	public static void getAndSaveLoggedDegreeFrequencies(StockGraph g, String fileName, int discreteLevel, int maxDegree) throws Exception {
		Vector<TreeMap<Integer, Integer>> dfs = new Vector<TreeMap<Integer, Integer>>();
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				dfs.add(getLoggedDegreeFrequency(g, i, j, discreteLevel, maxDegree));
			}
		}
		saveDegreeFrequencies(dfs, fileName);
	}

	public static void saveDegreeFrequencies(Vector<TreeMap<Integer, Integer>> dfs, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Integer k : dfs.get(0).keySet()) {
			pw.print(k);
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					pw.print("," + dfs.get(i*3+j).get(k));
				}
			}
			pw.println();
		}
		pw.close();
	}
	public static void getAndSaveLoggedDegreeFrequency(StockGraph g, int degreeType, int vertexType, String fileName, int discreteLevel, int maxDegree) throws Exception {
		TreeMap<Integer, Integer> df = getLoggedDegreeFrequency(g, degreeType, vertexType, discreteLevel, maxDegree);
		saveDegreeFrequency(df, fileName);
	}
	
	public static TreeMap<Integer, Integer> getLoggedDegreeFrequency(StockGraph g, int degreeType, int vertexType, int discreteLevel, int maxDegree) {
		
		// Initialize
		TreeMap<Integer, Integer> loggedDegreeFrequency = new TreeMap<Integer, Integer>();
		loggedDegreeFrequency.put(0, 0);
		int key = 1;
		for(int i=1; i<=maxDegree; i+=Utility.discreteByLog(key, 1)) {
			key = Utility.discreteByLog(i, discreteLevel);
			loggedDegreeFrequency.put(key, 0);
		}		
				
		// Build
		Collection<StockVertex> vertices = getVertices(g, vertexType);
		for(StockVertex v : vertices) {
			Integer d  = getDegree(g, v, degreeType);
			d = Utility.discreteByLog(d, discreteLevel);
			Integer c  = loggedDegreeFrequency.get(d);
			loggedDegreeFrequency.put(d, c+1);
		}
		
		return loggedDegreeFrequency;
	}
		
	public static TreeMap<Integer, Double> getDegreeDistribution(TreeMap<Integer, Integer> degreeFrequency, int vertexCount) {
		TreeMap<Integer, Double> degreeDistribution = new TreeMap<Integer, Double>();
		for(Integer k : degreeFrequency.keySet()) {
			Integer v = degreeFrequency.get(k);
			degreeDistribution.put(k, (double)v/(double)vertexCount);
		}
		return degreeDistribution;
	}
	
	public static void saveDegreeFrequency(TreeMap<Integer, Integer> degreeFrequency, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Integer k : degreeFrequency.keySet()) {
			Integer v = degreeFrequency.get(k);
			pw.println(k + "," + v);
		}
		pw.close();
	}

	public static void saveWeightFrequency(TreeMap<Long, Integer> weightFrequency, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Long k : weightFrequency.keySet()) {
			Integer v = weightFrequency.get(k);
			pw.println(k + "," + v);
		}
		pw.close();
	}
	
	public static void saveDegreeFrequencyWithUniqueNote(TreeMap<Integer, Integer> df, TreeMap<Integer, String> ud, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Integer k : df.keySet()) {
			Integer v = df.get(k);
			String s = ud.get(k);
			pw.println(k + "," + v + "," + s);
		}
		pw.close();
	}

	public static void saveWeightFrequencyWithUniqueNote(TreeMap<Long, Integer> wf, TreeMap<Long, String> uw, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Long k : wf.keySet()) {
			Integer v = wf.get(k);
			String s = uw.get(k);
			pw.println(k + "," + v + "," + s);
		}
		pw.close();
	}
	
	public static void saveDegreeDistribution(TreeMap<Integer, Double> degreeDistribution, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Integer k : degreeDistribution.keySet()) {
			Double v = degreeDistribution.get(k);
			pw.println(k + "," + v);
		}
		pw.close();
	}
	
	public static void saveConnectedComponentSizes(Set<Set<StockVertex>> components, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Set<StockVertex> component : components) {
			pw.println(component.size());
		}
		pw.close();
	}
	
	public static Set<Set<StockVertex>> getConnectedComponent(StockGraph g) {
		WeakComponentClusterer<StockVertex, StockEdge> wcc = new WeakComponentClusterer<StockVertex, StockEdge>();
		Set<Set<StockVertex>> components = wcc.transform(g);
		return components;
	}
	
	public static Set<StockVertex> getGiantConnectedComponent(StockGraph g) {
		Set<Set<StockVertex>> components = getConnectedComponent(g);
		Set<StockVertex> giant = null;
		int size = 0;
		for(Set<StockVertex> component : components) {
			if(size < component.size()) {
				size = component.size();
				giant = component;
			}
		}
		return giant;
	}
	
	public static double getZ1(TreeMap<Integer, Integer> degreeFrequency, int vertexCount) {
		double z1 = 0;
		for(Integer k : degreeFrequency.keySet()) {
			Integer v = degreeFrequency.get(k);
			z1 += k*v;
		}
		z1 /= vertexCount; 
		return z1;
	}
	
	public static double getZ2(TreeMap<Integer, Integer> degreeFrequency, int vertexCount, double z1) {
		double z2 = 0;
		for(Integer k : degreeFrequency.keySet()) {
			Integer v = degreeFrequency.get(k);
			z2 += k*k*v;
		}
		z2 /= vertexCount;
		z2 -= z1;
		return z2;
	}
	
	public static double getCC(StockGraph g) {
		int triangles = 0;
		int triples = 0;		
		for(StockVertex v : g.getVertices()) {
			Collection<StockVertex> neighbors = g.getNeighbors(v);
			for(StockVertex vi : neighbors) {
				for(StockVertex vj : neighbors) {
					if(g.findEdge(vi, vj)!=null) {
						triangles++;
					}
					triples++;
				}
			}
		}
		if(triples == 0) {
			return 0;
		}
		double cc = (double)triangles/(double)triples;		
		return cc;
	}
	
	public static boolean getNodesWithZeroCC(StockGraph g, StockVertex v, int lower, int upper) {
		Collection<StockVertex> neighbors = g.getNeighbors(v);
		if(neighbors.size() >= lower && neighbors.size() <= upper) {
			for(StockVertex vi : neighbors) {
				for(StockVertex vj : neighbors) {
					if(g.findEdge(vi, vj) != null) {
						return false;
					}
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
		
	
	// The bounds are inclusive
	public static HashSet<StockVertex> getNodesWithZeroCC(StockGraph g, int lower, int upper) {
		HashSet<StockVertex> results = new HashSet<StockVertex>();
		for(StockVertex v : g.getVertices()) {
			if(getNodesWithZeroCC(g, v, lower, upper) == true) {
				results.add(v);
			}
		}
		return results;
	}
	
	// The bounds are inclusive
	public static HashSet<StockVertex> getNodesWithZeroCCLevel2(StockGraph g, int lower, int upper, int l2Lower, int l2Upper, double l2RatioLower) {
		int count = 0;
		HashSet<StockVertex> results = new HashSet<StockVertex>();
		for(StockVertex v : g.getVertices()) {
			Collection<StockVertex> neighbors = g.getNeighbors(v);
			if(neighbors.size() >= lower && neighbors.size() <= upper) {
				boolean isZeroCC = true;
				for(StockVertex vi : neighbors) {
					for(StockVertex vj : neighbors) {
						if(g.findEdge(vi, vj)!= null) {
							isZeroCC = false;
							break;
						}
					}
					
					if(isZeroCC == true) {
						// Check 0: the grandchildren should not be the same
						/*
						Collection grand = g.getNeighbors(vi);
						for(Iterator itg = grand.iterator(); itg.hasNext(); ) {
							Integer gc = (Integer)itg.next();
							if(gc.intValue() != v.intValue()) {
								if(grandchildren.contains(gc)) {
									isZeroCC = false;
									break;
								}
								else {
									grandchildren.add(gc);
								}
							}
						}
						*/
						
						//HashSet hs = getNodesWithZeroCCLevel2(g, vi, v, l2Lower, l2Upper);
						
						// Check 1: see if grandchildren are connected to each other
						/*
						if(hs.size() == 0) {
							isZeroCC = false;
						}
						*/
						
						// Check 2: see if ratio is higher enough
						/*
						double l2Ratio = (double)hs.size() / (double)neighbors.size();
						if(l2Ratio < l2RatioLower) {
							isZeroCC = false;
						}
						*/
						
						// Check 3: see if grandchildren are the same
						/*
						for(Iterator iths = hs.iterator(); iths.hasNext(); ) {
							Integer gc = (Integer)iths.next();
							if(grandchildren.contains(gc)) {
								isZeroCC = false;
								break;
							}
							else {
								grandchildren.add(gc);
							}
						}
						*/
						
					}
					
					if(isZeroCC == false) {
						break;
					}
				}
				if(isZeroCC == true) {
					results.add(v);
					count++;
					if(count%50 == 0) {
						System.out.print(".");
					}
				}
			}
		}
		System.out.println();
		return results;
	}
	
	// The bounds are inclusive
	// Return empty set if not zero CC
	// Return grandchildren if zero CC
	public static HashSet<StockVertex> getNodesWithZeroCCLevel2(StockGraph g, StockVertex v, StockVertex parent, int lower, int upper) {
		HashSet<StockVertex> results = new HashSet<StockVertex>();
		Collection<StockVertex> neighbors = g.getNeighbors(v);
		boolean isZeroCC = true;
		
		if(neighbors.size() >= lower && neighbors.size() <= upper) {
					
			for(StockVertex vi : neighbors) {

				// Don't consider parent
				if(!vi.getName().equalsIgnoreCase(parent.getName())) { 
					
					// Check connection with other grandchildren
					/*
					for(Iterator j = neighbors.iterator(); j.hasNext(); ) {
						Integer vj = (Integer)j.next();
						if(g.findEdge(vi, vj)!= null) {
							isZeroCC = false;
							break;
						}
					}
					*/
						
					if(isZeroCC == false) {
						break;
					}
					else {
						results.add(vi);
					}
				}				
			}
		}
		
		if(isZeroCC == true) {
			return results;
		}
		else {
			return new HashSet<StockVertex>();
		}
	}
	
	public static void saveDegreeFrequencies(StockGraph g, HashSet<StockVertex> hs, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(StockVertex v : hs) {
			pw.println(v + "," + g.getNeighborCount(v));
		}
		pw.close();
	}
	
	public static double getAPL(StockGraph g, Set<StockVertex> giant) {
		double path = 0;
		double count = 0;
		int c = 0;
		StockGraph gg = g;
		if(giant.size() < g.getVertexCount()) {
			gg = FilterUtils.createInducedSubgraph(giant, g);
		}
		for(StockVertex vi : gg.getVertices()) {
			double tempPath = 0;
			double tempCount = 0;
			BFSDistanceLabeler<StockVertex, StockEdge> dl = new BFSDistanceLabeler<StockVertex, StockEdge>();
			dl.labelDistances(gg, vi);
			Map<StockVertex, Number> m = dl.getDistanceDecorator();
			for(StockVertex vj : gg.getVertices()) {
				tempPath += (Integer)m.get(vj);
				tempCount++;
			}
			path += tempPath;
			count += tempCount;
			if((c%1000)==0) {
				System.out.print(".");
			}
			c++;
		}
		double apl = (double)path/(double)count;
		System.out.println();
		return apl;
	}
	
	public static Vector<StockVertex> getVerticesSortedByDegree(StockGraph g) {
		HashMap<StockVertex, Integer> hm = new HashMap<StockVertex, Integer>();
		TreeSet<Integer> ts = new TreeSet<Integer>();
		
		for(StockVertex v : g.getVertices()) {
			int n = g.getNeighborCount(v);
			hm.put(v, n);
			ts.add(n);
		}
		
		Vector<StockVertex> vertices = new Vector<StockVertex>();
		for(Iterator<Integer> dit = ts.descendingIterator(); dit.hasNext(); ) {
			Integer currentDegree = dit.next();
			for(StockVertex currentVertex : hm.keySet()) {
				if(hm.get(currentVertex).equals(currentDegree)) {
					vertices.add(currentVertex);
				}
			}
		}
		
		return vertices;
	}
}
