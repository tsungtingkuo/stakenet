package stock.util;
import java.io.*;
import java.util.*;

import stock.directed.StockGraph;
import stock.undirected.StockUndirectedGraph;


public class StockGroundTruth {

	public static final int REVENUE_DECREASE = 0;
	public static final int REVENUE_INCREASE = 1;
	
	HashMap<String, Integer> codeToRevenueMap = new HashMap<String, Integer>();

	public static StockGroundTruth generate(StockGraph graph, int highYear, int highMonth, int lowYear, int lowMonth, String revenueFileName, String groundTruthFileName) throws Exception {
		return generate(graph.getPublicVertexCodes(), highYear, highMonth, lowYear, lowMonth, revenueFileName, groundTruthFileName);
	}

	public static StockGroundTruth generate(StockUndirectedGraph graph, int highYear, int highMonth, int lowYear, int lowMonth, String revenueFileName, String groundTruthFileName) throws Exception {
		return generate(graph.getPublicVertexCodes(), highYear, highMonth, lowYear, lowMonth, revenueFileName, groundTruthFileName);
	}

	public static StockGroundTruth generate(HashSet<String> publicVertexCodes, int highYear, int highMonth, int lowYear, int lowMonth, String revenueFileName, String groundTruthFileName) throws Exception {
		StockGroundTruth sgt = new StockGroundTruth();
		StockRevenue sr = StockRevenue.load(revenueFileName);
		
		for(Iterator<String> it = publicVertexCodes.iterator(); it.hasNext(); ) {
			String code = it.next();
			long highRevenue = 0;
			long lowRevenue = 0;
			boolean dataFound = true;
			
			try {
				highRevenue = sr.codeMap.get(code).get(highYear).get(highMonth);
				lowRevenue = sr.codeMap.get(code).get(lowYear).get(lowMonth);
			}
			catch (NullPointerException npe) {
				dataFound = false;
			}
			
			if(dataFound == true) {
				if(highRevenue >= lowRevenue) {
					sgt.codeToRevenueMap.put(code, StockGroundTruth.REVENUE_INCREASE);
				}
				else {
					sgt.codeToRevenueMap.put(code, StockGroundTruth.REVENUE_DECREASE);				
				}
			}
		}
		StockGroundTruth.save(sgt, groundTruthFileName);
		return sgt;
	}
	
	public static void save(StockGroundTruth sgt, String fileName) throws Exception{
		PrintWriter pw = new PrintWriter(fileName);
		for(Iterator<String> it = sgt.codeToRevenueMap.keySet().iterator(); it.hasNext(); ) {
			String code = it.next();
			Integer result = sgt.codeToRevenueMap.get(code);
			pw.println(code + "," + result);
		}
		pw.close();
	}
	
	public static StockGroundTruth load(String fileName) throws Exception {
		StockGroundTruth sgt = new StockGroundTruth();
		
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = null;
		while ((s=lnr.readLine()) != null) {
			String[] t = s.split(",");
			String code = t[0];
			int result = Integer.parseInt(t[1]);
			sgt.codeToRevenueMap.put(code, result);
		}				
		lnr.close();
		fr.close();	  
		
		return sgt;
	}	

	public int size() {
		return codeToRevenueMap.size();
	}

	/**
	 * @return the codeToRevenueMap
	 */
	public HashMap<String, Integer> getCodeToRevenueMap() {
		return codeToRevenueMap;
	}
}
