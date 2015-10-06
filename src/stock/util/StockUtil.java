package stock.util;

import java.io.*;
import java.util.*;

import stock.directed.StockGraph;
import stock.vertex.StockVertex;


public class StockUtil {
	
	public static boolean compareStockName(String stockName1, String stockName2) {
		String s1 = preprocessStockName(stockName1);
		String s2 = preprocessStockName(stockName2);
		return s1.equalsIgnoreCase(s2);
	}
	
	public static String preprocessStockName(String stockName) {
		
		// For space
		String s = stockName.trim();
		s = s.replaceAll("　", "");
		s = s.replaceAll(" ", "");
		
		// For special characters
		s = s.replaceAll("amp;", "");
		s = s.replaceAll("&#[0-9][0-9][0-9][0-9][0-9];", "");
		s = s.replaceAll("&#[0-9][0-9][0-9];", "");
		s = s.replaceAll("0", "");
		s = s.replaceAll("-", "");
		s = s.replaceAll("－", "");
		s = s.replaceAll("'", "");
		s = s.replaceAll("\\(\\)", "");
		
		// For 公司
		s = s.replaceAll("股份有限公司", "");
		s = s.replaceAll("股份有公司", "");
		s = s.replaceAll("股份有限公", "");
		s = s.replaceAll("股份有限", "");
		s = s.replaceAll("（股）公司", "");
		s = s.replaceAll("（股）公", "");
		s = s.replaceAll("（股）", "");
		s = s.replaceAll("﹝股﹞", "");
		s = s.replaceAll("\\(股\\)公司", "");
		s = s.replaceAll("\\(股\\)", "");
		s = s.replaceAll("&lt;股&gt;公司", "");
		s = s.replaceAll("有限公司", "");
		s = s.replaceAll("\\(限\\)公司", "");
		s = s.replaceAll("\\(限\\)", "");
		s = s.replaceAll("\\(有\\)", "");
		
		
		// For 信託
		s = s.replaceAll("信託部受託有價證券信託財產專戶", "");
		s = s.replaceAll("信託部受託經管包括信託專戶", "");
		s = s.replaceAll("信託部受託有價證券信託專戶", "");
		s = s.replaceAll("受託經管包括信託財產專戶", "");		
		s = s.replaceAll("受託管理信託財產專戶", "");
		s = s.replaceAll("分紅持股信託財產專戶", "");
		s = s.replaceAll("受託信託財產專戶", "");
		s = s.replaceAll("受託信託才產專戶", "");
		s = s.replaceAll("受託信托財產專戶", "");
		s = s.replaceAll("受託信托財產專戶", "");		
		s = s.replaceAll("受託信託產財專戶", "");
		s = s.replaceAll("受託經管信託專戶", "");
		s = s.replaceAll("股票信託保管專戶", "");
		s = s.replaceAll("股票綜合信託專戶", "");		
		s = s.replaceAll("受託信託財專戶", "");
		s = s.replaceAll("股票信託專戶", "");
		s = s.replaceAll("持股信託專戶", "");
		s = s.replaceAll("信託財產專戶", "");
		s = s.replaceAll("受託財產專戶", "");
		s = s.replaceAll("受託信託專戶", "");		
		s = s.replaceAll("(信託財產)", "");
		s = s.replaceAll("分紅持股會", "");	
		s = s.replaceAll("分紅持股", "");	
		s = s.replaceAll("信託專戶", "");	
		s = s.replaceAll("財產專戶", "");
		s = s.replaceAll("受託經管", "");
		s = s.replaceAll("信託部", "");
		s = s.replaceAll("專戶", "");
		s = s.replaceAll("信託", "");
		s = s.replaceAll("受託.*", "");
		s = s.replaceAll("託管.*", "");
		s = s.replaceAll("受.*", "");
		s = s.replaceAll("\\(\\)", "");
		
		
		return s;
	}
		
    public static HashSet<String> loadBussinessGroupCompanies(String file_path) throws Exception {
        FileInputStream fi = new FileInputStream(file_path);
        InputStreamReader in = new InputStreamReader(fi,"big5"); 
        BufferedReader br = new BufferedReader(in);
        HashSet<String> result = new HashSet<String>();

        String s = null;
        while ((s = br.readLine()) != null) {
        		s = s.substring(s.indexOf(":")+1,s.length());
        		String[] tokens = s.split(",");
        		int i = 0;
        		while (i < tokens.length) {
        			if(result.contains(tokens[i])) {
        				System.out.println(tokens[i]);
        			}
        			result.add(tokens[i]);
        			i++;
        		}
        }
        in.close();
        fi.close();
        return result;
    }
    
    public static Set<Set<StockVertex>> filterClusteringResult(Set<Set<StockVertex>> clusters, HashSet<String> bgc) {
    	Set<Set<StockVertex>> result = new HashSet<Set<StockVertex>>();
    	for(Set<StockVertex> group : clusters) {
    		Set<StockVertex> g = new HashSet<StockVertex>();
    		for(StockVertex v : group) {
    			String code = v.getCode();
    			if(code.equalsIgnoreCase("")==false) {
    				if(bgc.contains(code)) {
    					g.add(v);
    				}
    			}
    		}
    		if(g.size() > 0) {
    			result.add(g);
    		}
    	}
    	return result;
    }
    
    public static int computeClusterCompanies(Set<Set<StockVertex>> clusters) {
    	int count = 0;
      	for(Set<StockVertex> group : clusters) {
      		count += group.size();
      	}	
      	return count;
    }
    
    public static void saveClusters(Set<Set<StockVertex>> clusters, String fileName) throws Exception {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(clusters);
        oos.close();
    }
    
    @SuppressWarnings("unchecked")
	public static Set<Set<StockVertex>> loadClusters(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Set<Set<StockVertex>> clusters = (Set<Set<StockVertex>>)ois.readObject();
        ois.close();
        return clusters;
    }
    
    public static Set<Set<StockVertex>> computeClusters(StockGraph g) {
    	Set<Set<StockVertex>> clusters = new HashSet<Set<StockVertex>>();
    	
    	for(StockVertex sv : g.getPublicVertices()) {
    		clusters = computeClusters(g, clusters, sv);
    	}
    	
    	return clusters;
    }
    
    public static Set<Set<StockVertex>> computeClusters(StockGraph g, Set<Set<StockVertex>> clusters, StockVertex sv) {
    	
    	// Check direct-stakeholder
		for(Set<StockVertex> group : clusters) {
			for(StockVertex su : group) {
				if(g.findEdge(sv, su)!=null || g.findEdge(su, sv)!=null) {
					group.add(sv);
					return clusters;
				}
			}
		}
    	
    	// Check common-stakeholder
		for(StockVertex v : g.getInNeighbors(sv)) {
			for(Set<StockVertex> group : clusters) {
				for(StockVertex su : group) {
					if(g.findEdge(v, su)!=null) {
						group.add(sv);
						return clusters;
					}
				}
			}
		}
		
		// New group
		Set<StockVertex> newGroup = new HashSet<StockVertex>();
		newGroup.add(sv);
		clusters.add(newGroup);
		return clusters;
    }
}
