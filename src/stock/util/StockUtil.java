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
		s = s.replaceAll("�@", "");
		s = s.replaceAll(" ", "");
		
		// For special characters
		s = s.replaceAll("amp;", "");
		s = s.replaceAll("&#[0-9][0-9][0-9][0-9][0-9];", "");
		s = s.replaceAll("&#[0-9][0-9][0-9];", "");
		s = s.replaceAll("0", "");
		s = s.replaceAll("-", "");
		s = s.replaceAll("��", "");
		s = s.replaceAll("'", "");
		s = s.replaceAll("\\(\\)", "");
		
		// For ���q
		s = s.replaceAll("�ѥ��������q", "");
		s = s.replaceAll("�ѥ������q", "");
		s = s.replaceAll("�ѥ�������", "");
		s = s.replaceAll("�ѥ�����", "");
		s = s.replaceAll("�]�ѡ^���q", "");
		s = s.replaceAll("�]�ѡ^��", "");
		s = s.replaceAll("�]�ѡ^", "");
		s = s.replaceAll("���ѡ�", "");
		s = s.replaceAll("\\(��\\)���q", "");
		s = s.replaceAll("\\(��\\)", "");
		s = s.replaceAll("&lt;��&gt;���q", "");
		s = s.replaceAll("�������q", "");
		s = s.replaceAll("\\(��\\)���q", "");
		s = s.replaceAll("\\(��\\)", "");
		s = s.replaceAll("\\(��\\)", "");
		
		
		// For �H�U
		s = s.replaceAll("�H�U�����U�����Ҩ�H�U�]���M��", "");
		s = s.replaceAll("�H�U�����U�g�ޥ]�A�H�U�M��", "");
		s = s.replaceAll("�H�U�����U�����Ҩ�H�U�M��", "");
		s = s.replaceAll("���U�g�ޥ]�A�H�U�]���M��", "");		
		s = s.replaceAll("���U�޲z�H�U�]���M��", "");
		s = s.replaceAll("�������ѫH�U�]���M��", "");
		s = s.replaceAll("���U�H�U�]���M��", "");
		s = s.replaceAll("���U�H�U�~���M��", "");
		s = s.replaceAll("���U�H���]���M��", "");
		s = s.replaceAll("���U�H���]���M��", "");		
		s = s.replaceAll("���U�H�U���]�M��", "");
		s = s.replaceAll("���U�g�ޫH�U�M��", "");
		s = s.replaceAll("�Ѳ��H�U�O�ޱM��", "");
		s = s.replaceAll("�Ѳ���X�H�U�M��", "");		
		s = s.replaceAll("���U�H�U�]�M��", "");
		s = s.replaceAll("�Ѳ��H�U�M��", "");
		s = s.replaceAll("���ѫH�U�M��", "");
		s = s.replaceAll("�H�U�]���M��", "");
		s = s.replaceAll("���U�]���M��", "");
		s = s.replaceAll("���U�H�U�M��", "");		
		s = s.replaceAll("(�H�U�]��)", "");
		s = s.replaceAll("�������ѷ|", "");	
		s = s.replaceAll("��������", "");	
		s = s.replaceAll("�H�U�M��", "");	
		s = s.replaceAll("�]���M��", "");
		s = s.replaceAll("���U�g��", "");
		s = s.replaceAll("�H�U��", "");
		s = s.replaceAll("�M��", "");
		s = s.replaceAll("�H�U", "");
		s = s.replaceAll("���U.*", "");
		s = s.replaceAll("�U��.*", "");
		s = s.replaceAll("��.*", "");
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
