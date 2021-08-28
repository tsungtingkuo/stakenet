package stock.undirected;
import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.algorithms.filters.*;
import edu.uci.ics.jung.algorithms.shortestpath.*;
import java.util.*;
import java.io.*;

import stock.edge.StockEdge;
import stock.vertex.StockVertex;


public class StockUndirectedGraphStatistics {

	public static final int DEGREE_BOTH = 0;
	public static final int DEGREE_IN = 1;
	public static final int DEGREE_OUT = 2;
	
    StockUndirectedGraph graph = new StockUndirectedGraph();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		// Generate graph
		StockUndirectedGraph g = StockUndirectedGraph.load("vertex.txt", "edge.txt");
		
		// Remove zero-degree nodes
		StockUndirectedGraphSampler.sample(g, 1);

		// Basic count
		int vertexCount = g.getVertexCount();
		int edgeCount = g.getEdgeCount();
		int pulicCount = g.getPublicCount();
		System.out.println("Vertex = " + vertexCount);
		System.out.println("Edge = " + edgeCount);
		System.out.println("Public = " + pulicCount);
		
		// Degree frequency
		Hashtable<Integer, Integer> df = StockUndirectedGraphStatistics.getDegreeFrequency(g, StockUndirectedGraphStatistics.DEGREE_BOTH);
		StockUndirectedGraphStatistics.saveDegreeFrequency(df, "graph_frequency.csv");
		System.out.println("DF = " + df.size());
		Hashtable<Integer, String> ud = StockUndirectedGraphStatistics.getUniqueDegree(g, df, StockUndirectedGraphStatistics.DEGREE_BOTH);
		StockUndirectedGraphStatistics.saveDegreeFrequencyWithUniqueNote(df, ud, "graph_frequency_note.csv");		

		// In-Degree frequency
		Hashtable<Integer, Integer> idf = StockUndirectedGraphStatistics.getDegreeFrequency(g, StockUndirectedGraphStatistics.DEGREE_IN);
		StockUndirectedGraphStatistics.saveDegreeFrequency(idf, "graph_in_frequency.csv");
		System.out.println("IDF = " + idf.size());
		Hashtable<Integer, String> iud = StockUndirectedGraphStatistics.getUniqueDegree(g, idf, StockUndirectedGraphStatistics.DEGREE_IN);
		StockUndirectedGraphStatistics.saveDegreeFrequencyWithUniqueNote(idf, iud, "graph_in_frequency_note.csv");		

		// Out-Degree frequency
		Hashtable<Integer, Integer> odf = StockUndirectedGraphStatistics.getDegreeFrequency(g, StockUndirectedGraphStatistics.DEGREE_OUT);
		StockUndirectedGraphStatistics.saveDegreeFrequency(odf, "graph_out_frequency.csv");
		System.out.println("ODF = " + odf.size());
		Hashtable<Integer, String> oud = StockUndirectedGraphStatistics.getUniqueDegree(g, odf, StockUndirectedGraphStatistics.DEGREE_OUT);
		StockUndirectedGraphStatistics.saveDegreeFrequencyWithUniqueNote(odf, oud, "graph_out_frequency_note.csv");		

		// Degree distribution
		Hashtable<Integer, Double> dd = StockUndirectedGraphStatistics.getDegreeDistribution(df, vertexCount);
		StockUndirectedGraphStatistics.saveDegreeDistribution(dd, "graph_distribution.csv");

		// Vertex weight frequency
		Hashtable<Long, Integer> vwf = StockUndirectedGraphStatistics.getVertexWeightFrequency(g);
		StockUndirectedGraphStatistics.saveWeightFrequency(vwf, "vertex_weight_frequency.csv");
		System.out.println("VWF = " + vwf.size());
		Hashtable<Long, String> uwv = StockUndirectedGraphStatistics.getUniqueWeightVertex(g, vwf);
		StockUndirectedGraphStatistics.saveWeightFrequencyWithUniqueNote(vwf, uwv, "vertex_weight_frequency_note.csv");		

		// Edge weight frequency
		Hashtable<Long, Integer> ewf = StockUndirectedGraphStatistics.getEdgeWeightFrequency(g);
		StockUndirectedGraphStatistics.saveWeightFrequency(ewf, "edge_weight_frequency.csv");
		System.out.println("EWF = " + ewf.size());
		Hashtable<Long, String> uwe = StockUndirectedGraphStatistics.getUniqueWeightEdge(g, ewf);
		StockUndirectedGraphStatistics.saveWeightFrequencyWithUniqueNote(ewf, uwe, "edge_weight_frequency_note.csv");		

		// Giant component size
		Set<StockVertex> giant = StockUndirectedGraphStatistics.getGiantConnectedComponent(g);
		System.out.println("GCC = " + giant.size());
		
		// Average degree (Z1 and Z2)
		double z1 = StockUndirectedGraphStatistics.getZ1(df, vertexCount);
		double z2 = StockUndirectedGraphStatistics.getZ2(df, vertexCount, z1);
		System.out.println("Z1 = " + z1);
		System.out.println("Z2 = " + z2);
		
		// Clustering coefficients
		double cc = StockUndirectedGraphStatistics.getCC(g);
		System.out.println("CC = " + cc);

		// Average path length
		//double apl = StockUndirectedGraphStatistics.getAPL(g, giant);
		//System.out.println("APL = " + apl);
	}
	
	public StockUndirectedGraphStatistics(StockUndirectedGraph g) {
		this.graph = g;
	}

	public static Hashtable<Long, Integer> getVertexWeightFrequency(StockUndirectedGraph g) {
		Hashtable<Long, Integer> vertexWeightFrequency = new Hashtable<Long, Integer>();
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
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
	
	public static Hashtable<Long, Integer> getEdgeWeightFrequency(StockUndirectedGraph g) {
		Hashtable<Long, Integer> edgeWeightFrequency = new Hashtable<Long, Integer>();
		for(Iterator<StockEdge> it = g.getEdges().iterator(); it.hasNext(); ) {
			StockEdge e = it.next();
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
	
	public static Hashtable<Long, String> getUniqueWeightVertex(StockUndirectedGraph g, Hashtable<Long, Integer> vwf) {
		Hashtable<Long, String> uniqueWeightVertex = new Hashtable<Long, String>();
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
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
	
	public static Hashtable<Long, String> getUniqueWeightEdge(StockUndirectedGraph g, Hashtable<Long, Integer> ewf) {
		Hashtable<Long, String> uniqueWeightEdge = new Hashtable<Long, String>();
		for(Iterator<StockEdge> it = g.getEdges().iterator(); it.hasNext(); ) {
			StockEdge e = it.next();
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
	
	public static Hashtable<Integer, String> getUniqueDegree(StockUndirectedGraph g, Hashtable<Integer, Integer> df, int type) {
		Hashtable<Integer, String> uniqueDegree = new Hashtable<Integer, String>();
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
			Integer d = 0;
			switch(type) {
			case StockUndirectedGraphStatistics.DEGREE_BOTH:
				d = g.degree(v);
				break;
			case StockUndirectedGraphStatistics.DEGREE_IN:
				d = g.inDegree(v);
				break;
			case StockUndirectedGraphStatistics.DEGREE_OUT:
				d = g.outDegree(v);
				break;			
			}
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
	
	public static Hashtable<Integer, Integer> getDegreeFrequency(StockUndirectedGraph g, int type) {
		Hashtable<Integer, Integer> degreeFrequency = new Hashtable<Integer, Integer>();
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
			Integer d = 0;
			switch(type) {
			case StockUndirectedGraphStatistics.DEGREE_BOTH:
				d = g.degree(v);
				break;
			case StockUndirectedGraphStatistics.DEGREE_IN:
				d = g.inDegree(v);
				break;
			case StockUndirectedGraphStatistics.DEGREE_OUT:
				d = g.outDegree(v);
				break;			
			}
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
	
	public static Hashtable<Integer, Double> getDegreeDistribution(Hashtable<Integer, Integer> degreeFrequency, int vertexCount) {
		Hashtable<Integer, Double> degreeDistribution = new Hashtable<Integer, Double>();
		for(Enumeration<Integer> e = degreeFrequency.keys(); e.hasMoreElements(); ) {
			Integer k = e.nextElement();
			Integer v = degreeFrequency.get(k);
			degreeDistribution.put(k, (double)v/(double)vertexCount);
		}
		return degreeDistribution;
	}
	
	public static void saveDegreeFrequency(Hashtable<Integer, Integer> degreeFrequency, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Enumeration<Integer> e = degreeFrequency.keys(); e.hasMoreElements(); ) {
			Integer k = e.nextElement();
			Integer v = degreeFrequency.get(k);
			pw.println(k + "," + v);
		}
		pw.close();
	}
	
	public static void saveWeightFrequency(Hashtable<Long, Integer> weightFrequency, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Enumeration<Long> e = weightFrequency.keys(); e.hasMoreElements(); ) {
			Long k = e.nextElement();
			Integer v = weightFrequency.get(k);
			pw.println(k + "," + v);
		}
		pw.close();
	}
	
	public static void saveDegreeFrequencyWithUniqueNote(Hashtable<Integer, Integer> df, Hashtable<Integer, String> ud, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Enumeration<Integer> e = df.keys(); e.hasMoreElements(); ) {
			Integer k = e.nextElement();
			Integer v = df.get(k);
			String s = ud.get(k);
			pw.println(k + "," + v + "," + s);
		}
		pw.close();
	}

	public static void saveWeightFrequencyWithUniqueNote(Hashtable<Long, Integer> wf, Hashtable<Long, String> uw, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Enumeration<Long> e = wf.keys(); e.hasMoreElements(); ) {
			Long k = e.nextElement();
			Integer v = wf.get(k);
			String s = uw.get(k);
			pw.println(k + "," + v + "," + s);
		}
		pw.close();
	}
	
	public static void saveDegreeDistribution(Hashtable<Integer, Double> degreeDistribution, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Enumeration<Integer> e = degreeDistribution.keys(); e.hasMoreElements(); ) {
			Integer k = e.nextElement();
			Double v = degreeDistribution.get(k);
			pw.println(k + "," + v);
		}
		pw.close();
	}
	
	public static void saveConnectedComponentSizes(Set<Set<StockVertex>> components, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Iterator<Set<StockVertex>> it = components.iterator(); it.hasNext(); ) {
			Set<StockVertex> component = it.next();
			pw.println(component.size());
		}
		pw.close();
	}
	
	public static Set<Set<StockVertex>> getConnectedComponent(StockUndirectedGraph g) {
		WeakComponentClusterer<StockVertex, StockEdge> wcc = new WeakComponentClusterer<StockVertex, StockEdge>();
		Set<Set<StockVertex>> components = wcc.transform(g);
		return components;
	}
	
	public static Set<StockVertex> getGiantConnectedComponent(StockUndirectedGraph g) {
		Set<Set<StockVertex>> components = getConnectedComponent(g);
		Set<StockVertex> giant = null;
		int size = 0;
		for(Iterator<Set<StockVertex>> it = components.iterator(); it.hasNext(); ) {
			Set<StockVertex> component = it.next();
			if(size < component.size()) {
				size = component.size();
				giant = component;
			}
		}
		return giant;
	}
	
	public static double getZ1(Hashtable<Integer, Integer> degreeFrequency, int vertexCount) {
		double z1 = 0;
		for(Enumeration<Integer> e = degreeFrequency.keys(); e.hasMoreElements(); ) {
			Integer k = e.nextElement();
			Integer v = degreeFrequency.get(k);
			z1 += k*v;
		}
		z1 /= vertexCount; 
		return z1;
	}
	
	public static double getZ2(Hashtable<Integer, Integer> degreeFrequency, int vertexCount, double z1) {
		double z2 = 0;
		for(Enumeration<Integer> e = degreeFrequency.keys(); e.hasMoreElements(); ) {
			Integer k = e.nextElement();
			Integer v = degreeFrequency.get(k);
			z2 += k*k*v;
		}
		z2 /= vertexCount;
		z2 -= z1;
		return z2;
	}
	
	public static double getCC(StockUndirectedGraph g) {
		int triangles = 0;
		int triples = 0;		
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
			Collection<StockVertex> neighbors = g.getNeighbors(v);
			for(Iterator<StockVertex> i = neighbors.iterator(); i.hasNext(); ) {
				StockVertex vi = i.next();
				for(Iterator<StockVertex> j = neighbors.iterator(); j.hasNext(); ) {
					StockVertex vj = j.next();
					if(g.findEdge(vi, vj)!=null) {
						triangles++;
					}
					triples++;
				}
			}
		}
		if(triples == 0) {
			return 0;
		}		double cc = (double)triangles/(double)triples;		
		return cc;
	}
	
	public static boolean getNodesWithZeroCC(StockUndirectedGraph g, StockVertex v, int lower, int upper) {
		Collection<StockVertex> neighbors = g.getNeighbors(v);
		if(neighbors.size() >= lower && neighbors.size() <= upper) {
			for(Iterator<StockVertex> i = neighbors.iterator(); i.hasNext(); ) {
				StockVertex vi = i.next();
				for(Iterator<StockVertex> j = neighbors.iterator(); j.hasNext(); ) {
					StockVertex vj = j.next();
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
	public static HashSet<StockVertex> getNodesWithZeroCC(StockUndirectedGraph g, int lower, int upper) {
		HashSet<StockVertex> results = new HashSet<StockVertex>();
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
			if(getNodesWithZeroCC(g, v, lower, upper) == true) {
				results.add(v);
			}
		}
		return results;
	}
	
	// The bounds are inclusive
	public static HashSet<StockVertex> getNodesWithZeroCCLevel2(StockUndirectedGraph g, int lower, int upper, int l2Lower, int l2Upper, double l2RatioLower) {
		int count = 0;
		HashSet<StockVertex> results = new HashSet<StockVertex>();
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			
			StockVertex v = it.next();
			Collection<StockVertex> neighbors = g.getNeighbors(v);

			if(neighbors.size() >= lower && neighbors.size() <= upper) {
				boolean isZeroCC = true;
				for(Iterator<StockVertex> i = neighbors.iterator(); i.hasNext(); ) {
					
					StockVertex vi = i.next();
					
					for(Iterator<StockVertex> j = neighbors.iterator(); j.hasNext(); ) {
						StockVertex vj = j.next();
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
	public static HashSet<StockVertex> getNodesWithZeroCCLevel2(StockUndirectedGraph g, StockVertex v, StockVertex parent, int lower, int upper) {
		HashSet<StockVertex> results = new HashSet<StockVertex>();
		Collection<StockVertex> neighbors = g.getNeighbors(v);
		boolean isZeroCC = true;
		
		if(neighbors.size() >= lower && neighbors.size() <= upper) {
					
			for(Iterator<StockVertex> i = neighbors.iterator(); i.hasNext(); ) {
				StockVertex vi = i.next();
				
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
	
	public static void saveDegreeFrequencies(StockUndirectedGraph g, HashSet<StockVertex> hs, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(Iterator<StockVertex> it = hs.iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
			pw.println(v + "," + g.getNeighborCount(v));
		}
		pw.close();
	}
	
	public static double getAPL(StockUndirectedGraph g, Set<StockVertex> giant) {
		double path = 0;
		double count = 0;
		int c = 0;
		StockUndirectedGraph gg = g;
		if(giant.size() < g.getVertexCount()) {
			gg = FilterUtils.createInducedSubgraph(giant, g);
		}
		for(Iterator<StockVertex> i = gg.getVertices().iterator(); i.hasNext(); ) {
			double tempPath = 0;
			double tempCount = 0;
			StockVertex vi = i.next();
			BFSDistanceLabeler<StockVertex, StockEdge> dl = new BFSDistanceLabeler<StockVertex, StockEdge>();
			dl.labelDistances(gg, vi);
			Map<StockVertex, Number> m = dl.getDistanceDecorator();
			for(Iterator<StockVertex> j = gg.getVertices().iterator(); j.hasNext(); ) {
				StockVertex vj = j.next();
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
	
	public static Vector<StockVertex> getVerticesSortedByDegree(StockUndirectedGraph g) {
		HashMap<StockVertex, Integer> hm = new HashMap<StockVertex, Integer>();
		TreeSet<Integer> ts = new TreeSet<Integer>();
		
		for(Iterator<StockVertex> it = g.getVertices().iterator(); it.hasNext(); ) {
			StockVertex v = it.next();
			int n = g.getNeighborCount(v);
			hm.put(v, n);
			ts.add(n);
		}
		
		Vector<StockVertex> vertices = new Vector<StockVertex>();
		for(Iterator<Integer> dit = ts.descendingIterator(); dit.hasNext(); ) {
			Integer currentDegree = dit.next();
			for(Iterator<StockVertex> hmit = hm.keySet().iterator(); hmit.hasNext(); ) {
				StockVertex currentVertex = hmit.next();
				if(hm.get(currentVertex).equals(currentDegree)) {
					vertices.add(currentVertex);
				}
			}
		}
		
		return vertices;
	}
}
