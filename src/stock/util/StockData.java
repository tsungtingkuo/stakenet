package stock.util;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;

public class StockData {

	HashMap<String, String> exceptionMap = new HashMap<String, String>();
	HashMap<String, String> nameMap = new HashMap<String, String>();
	HashMap<String, Long> capitalMap = new HashMap<String, Long>();
	HashMap<String, String> englishMap = new HashMap<String, String>();

	public static StockData load(String exceptionFileName, String companyFileName, String englishFileName) throws Exception {
		StockData sc = new StockData();
		sc.loadExceptions(exceptionFileName);
		sc.loadCompanies(companyFileName);
		sc.loadEnglishs(englishFileName);
		return sc;
	}

	public void loadCompanies(String fileName) throws Exception {
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = lnr.readLine();	// Header, ignore it
		while ((s=lnr.readLine()) != null) {
			String[] t = s.split(",");
			String code = t[0];
			String name = StockUtil.preprocessStockName(t[1]);
			Long capital = Long.parseLong(t[2].trim());
			if(!exceptionMap.containsKey(code)) {
				nameMap.put(code, name);
				capitalMap.put(code, capital);
			}
		}							
		lnr.close();
		fr.close();
	}
	
	public void loadExceptions(String fileName) throws Exception {
		FileReader frx = new FileReader(fileName);
		LineNumberReader lnrx = new LineNumberReader(frx);
		String sx = null;
		while ((sx=lnrx.readLine()) != null) {
			String[] tx = sx.split(",");
			exceptionMap.put(tx[0], tx[1]);
		}				
		lnrx.close();
		frx.close();			
	}
	
	public void loadEnglishs(String fileName) throws Exception {
		FileReader frx = new FileReader(fileName);
		LineNumberReader lnrx = new LineNumberReader(frx);
		String sx = null;
		while ((sx=lnrx.readLine()) != null) {
			String[] tx = sx.split(",");
			englishMap.put(tx[0], tx[1]);
		}				
		lnrx.close();
		frx.close();			
	}
	
	public String getName(String code) {
		return nameMap.get(code);
	}
	
	public Long getCapital(String code) {
		return capitalMap.get(code);
	}
	
	public String getEnglish(String code) {
		return englishMap.get(code);		
	}
}
