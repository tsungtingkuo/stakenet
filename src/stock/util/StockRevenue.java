package stock.util;

import java.io.*;
import java.util.*;
import utility.*;

public class StockRevenue {

	public HashMap<String, HashMap<Integer, HashMap<Integer, Long>>> codeMap = new HashMap<String, HashMap<Integer, HashMap<Integer, Long>>>();

	public static void main(String[] args) throws Exception {
		StockRevenue sr = StockRevenue.load("revenue.csv");
		Utility.saveVector("e.txt", sr.getEvaluable(98, 10, 91, 10));
	}
	
	public Vector<String> getEvaluable(int highYear, int highMonth, int lowYear, int lowMonth) {
		Vector<String> evaluable = new Vector<String>();
		
	    for(String code : codeMap.keySet()) {
	    	boolean isEvaluable = true;
	    	
			for(int i=lowYear; i<highYear; i++) {
			
				int high = 12;
				int low = 1;
				if(i == highYear-1) {
					high = highMonth;
				}
				if(i == lowYear) {
					low = lowMonth;
				}
				
				for(int j=low; j<=high; j++) {
					if(getRevenue(code, i, j) == null) {
						isEvaluable = false;
						break;
					}
				}
			}
			
			if(isEvaluable == true) {
				evaluable.add(code);
			}
	    }
	    
		return evaluable;
	}
	
	public void addRevenue(String code, int year, int month, long revenue) {
		HashMap<Integer, HashMap<Integer, Long>> yearMap = codeMap.get(code);
		if(yearMap == null) {
			yearMap = new HashMap<Integer, HashMap<Integer, Long>>();
			codeMap.put(code, yearMap);
		}
		
		HashMap<Integer, Long> monthMap = yearMap.get(year);
		if(monthMap == null) {
			monthMap = new HashMap<Integer, Long>();
			yearMap.put(year, monthMap);
		}
		
		Long r = monthMap.get(month);
		if(r == null) {
			monthMap.put(month, revenue);
		}
	}
	
	public Long getRevenue(String code, int year, int month) {
		HashMap<Integer, HashMap<Integer, Long>> yearMap = codeMap.get(code);
		if(yearMap == null) {
			return null;
		}
		
		HashMap<Integer, Long> monthMap = yearMap.get(year);
		if(monthMap == null) {
			return null;
		}
		
		return monthMap.get(month);
	}
	
	public int getRevenueSize() {
		int count = 0;
	    for(Iterator<String> i = codeMap.keySet().iterator(); i.hasNext(); ) {
	    	String code = i.next();
			HashMap<Integer, HashMap<Integer, Long>> yearMap = codeMap.get(code);
		    for(Iterator<Integer> j = yearMap.keySet().iterator(); j.hasNext(); ) {
		    	int year = j.next();
				HashMap<Integer, Long> monthMap = yearMap.get(year);
				count += monthMap.size();
		    }
		}
	    return count;
	}
	
	public int getCompanySize() {
	    return codeMap.size();
	}
	
	public static StockRevenue load(String fileName) throws Exception {
		StockRevenue sr = new StockRevenue();
		
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = null;
		while ((s=lnr.readLine()) != null) {
			String[] t = s.split(",");
			String code = t[0];
			int year = Integer.parseInt(t[1]);
			int month = Integer.parseInt(t[2]);
			long revenue = Long.parseLong(t[3]);
			sr.addRevenue(code, year, month, revenue);
		}				
		lnr.close();
		fr.close();	  
		
		return sr;
	}
	
	public static void save(StockRevenue sr, String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(fileName);
	    for(Iterator<String> i = sr.codeMap.keySet().iterator(); i.hasNext(); ) {
	    	String code = i.next();
			HashMap<Integer, HashMap<Integer, Long>> yearMap = sr.codeMap.get(code);
		    for(Iterator<Integer> j = yearMap.keySet().iterator(); j.hasNext(); ) {
		    	int year = j.next();
				HashMap<Integer, Long> monthMap = yearMap.get(year);
			    for(Iterator<Integer> k = monthMap.keySet().iterator(); k.hasNext(); ) {
			    	int month = k.next();
					long revenue = monthMap.get(month);
					pw.println(code + "," + year + "," + month + "," + revenue);
			    }
		    }
		}					
	    pw.close();
	}
	
	public long[] getAnnualRevenue(String code, int targetYear, int targetMonth) {
		
		//System.out.print("revenue, year = " + targetYear + ", month = " + targetMonth + " (");
		
		long[] annualHistoricalRevenue = new long[12];
		int count = 0;
		int low = targetYear - 1;
		int high = targetYear;
		if(targetMonth == 1) {
			high = targetYear - 1;
		}
		for(int j = low; j <= high; j++) {
			int start = 1;
			int stop = 12;
			if(j == low) {
				start = targetMonth;
			}
			if(j == high) {
				stop = targetMonth - 1;
				if(stop == 0) {
					stop = 12;
				}
			}
			for(int k = start; k <= stop; k++) {
				//System.out.print(j + "/" + k + " ");
				Long r = getRevenue(code, j, k);
				if(r == null) {
					return null;
				}
				if(count < 12) {
					annualHistoricalRevenue[count] = r;
				}
				count++;
			}
		}
		//System.out.println(")");
		return annualHistoricalRevenue;
	}
}
