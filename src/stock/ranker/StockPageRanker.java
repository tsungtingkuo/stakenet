package stock.ranker;

import java.io.*;
import java.util.*;

import stock.edge.StockEdge;
import stock.edge.StockEdgeValueTransformer;
import stock.undirected.StockUndirectedGraph;
import stock.vertex.StockVertex;

import edu.uci.ics.jung.algorithms.scoring.*;

public class StockPageRanker extends PageRank<StockVertex, StockEdge> {

	public StockPageRanker(StockUndirectedGraph ug, StockEdgeValueTransformer edgeWeight, double alpha) {
		super(ug, edgeWeight, alpha);
	}
	
	public void saveResult(String fileName, StockUndirectedGraph ug, Vector<String> evaluable) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
		for(String code : evaluable) {
			StockVertex v = ug.getVertexByCode(code);
			pw.println(getVertexScore(v));
		}
		pw.close();
	}
}
