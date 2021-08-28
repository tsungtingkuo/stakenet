package stock.util;

import java.io.*;
import java.util.*;
import stock.directed.StockGraph;
import stock.edge.StockEdge;
import stock.vertex.StockVertex;
import utility.*;

public class StockGraphGenerator {

	StockGraph graph = new StockGraph();
	Vector<String> pubList = new Vector<String>();
	HashMap<String, String> exceptionMap = new HashMap<String, String>();
	HashMap<String, String> stockTransferTypes = new HashMap<String, String>();
	HashSet<String> stockTransferTypeTargets = new HashSet<String>();
	
	// Market Value
	boolean marketValue = false;
	int targetYear = 0;
	int targetMonth = 0;
	StockRevenue sr = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		StockGraphGenerator.generateAnnual(98, 10, true);
		
		//StockGraphGenerator.generateAnnuals(98, 90);
		
		//StockGraphGenerator.generate(98, 10, 97, 9, "vertex.txt", "edge.txt");		
		//StockGraphGenerator.find(97, 10, 87, 9);
		//StockGraphGenerator.print(97, 10, 87, 9, "«H°U");
		
		/*
		StockGraphGenerator sgg = new StockGraphGenerator();
		sgg.pubList = Utility.loadVector("pub.txt");
		sgg.loadExceptions("exception.csv");
		sgg.loadCompanyNames("company.csv");
		String sh = sgg.findLatestFile("relation", "021191", "SH", 94, 12, 94, 12);
		System.out.println(sh);
		StockVertex destination = sgg.graph.getVertexByCode("021191");
		System.out.println(destination);
		sgg.loadStockHolder(sh, "021191");
		*/
	}
	
	public StockGraphGenerator() throws Exception {
		super();
		this.graph.addNewVertex("");	// Market node
		this.sr = StockRevenue.load("price.csv");
	}

	public static void generateAnnuals(int highYear, int lowYear, boolean marketValue) throws Exception {
		for(int i=lowYear; i<highYear; i++) {
			for(int j=1; j<=12; j++) {
				generateAnnual(i+1, j, marketValue);
			}
		}
	}
	
	public static void generateAnnual(int targetYear, int targetMonth, boolean marketValue) throws Exception {
		int highYear = targetYear;
		int lowYear = targetYear - 1;
		int highMonth = targetMonth - 1;
		int lowMonth = targetMonth;
		if(targetMonth == 1) {
			highYear = lowYear;
			highMonth = 12;
		}
		String mv = "";
		if(marketValue == true) {
			mv = "_MV";
		}
		String vertexFileName = "graph/vertex_" + targetYear + "_" + targetMonth + mv + ".txt";		
		String edgeFileName = "graph/edge_" + targetYear + "_" + targetMonth + mv + ".txt";
		generate(targetYear, targetMonth, highYear, highMonth, lowYear, lowMonth, vertexFileName, edgeFileName, marketValue);
		//System.out.println(highYear + ", " + highMonth + ", " + lowYear + ", " + lowMonth + ", " + vertexFileName + ", " + edgeFileName);
	}
	
	public static void generate(int targetYear, int targetMonth, int highYear, int highMonth, int lowYear, int lowMonth, String vertexFileName, String edgeFileName, boolean marketValue) throws Exception {
		StockGraphGenerator sgg = new StockGraphGenerator();
		sgg.setMarketValue(marketValue);
		sgg.setTargetYear(targetYear);
		sgg.setTargetMonth(targetMonth);
		sgg.pubList = Utility.loadVector("pub.txt");
		sgg.loadExceptions("exception.csv");
		sgg.loadCompanyNames("company.csv");
		sgg.loadRelations("relation", highYear, highMonth, lowYear, lowMonth);
		StockGraph.save(sgg.graph, vertexFileName, edgeFileName);		
	}

	public static void find(int highYear, int highMonth, int lowYear, int lowMonth) throws Exception {
		StockGraphGenerator sgg = new StockGraphGenerator();
		sgg.pubList = Utility.loadVector("pub.txt");
		sgg.findStockTransferType("relation", highYear, highMonth, lowYear, lowMonth);
		Utility.printStringHashMap(sgg.stockTransferTypes);
	}

	public static void print(int highYear, int highMonth, int lowYear, int lowMonth, String targetType) throws Exception {
		StockGraphGenerator sgg = new StockGraphGenerator();
		sgg.pubList = Utility.loadVector("pub.txt");
		sgg.printStockTransferType("relation", highYear, highMonth, lowYear, lowMonth, targetType);
		Utility.printStringHashSet(sgg.stockTransferTypeTargets);
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
	
	public void loadRelations(String relationDir, int highYear, int highMonth, int lowYear, int lowMonth) throws Exception {
		int shc = 0;
		int stc = 0;
		String[] dirs = new File(relationDir).list();
		for(int i=0; i<dirs.length; i++) {
			
			System.out.print("Generating " + dirs[i] + " ... ");

			// Stock holder
			String sh = findLatestFile(relationDir, dirs[i], "SH", highYear, highMonth, lowYear, lowMonth);
			shc += loadStockHolder(sh, dirs[i]);
			
			// Stock transfer
			TreeSet<String> st = findAllFiles(relationDir, dirs[i], "ST", highYear, highMonth, lowYear, lowMonth);
			stc += loadStockTransfer(st, dirs[i]);

			System.out.println("done.");
		}
		System.out.println("Stock holder edges = " + shc);
		System.out.println("Stock transfer edges = " + stc);
	}

	public void findStockTransferType(String relationDir, int highYear, int highMonth, int lowYear, int lowMonth) throws Exception {
		String[] dirs = new File(relationDir).list();
		for(int i=0; i<dirs.length; i++) {
			
			System.out.print("Finding " + dirs[i] + " ... ");

			// Stock transfer
			TreeSet<String> st = findAllFiles(relationDir, dirs[i], "ST", highYear, highMonth, lowYear, lowMonth);
			findStockTransferType(st, dirs[i]);

			System.out.println("done.");
		}
	}
	
	
	public void printStockTransferType(String relationDir, int highYear, int highMonth, int lowYear, int lowMonth, String targetType) throws Exception {
		String[] dirs = new File(relationDir).list();
		for(int i=0; i<dirs.length; i++) {
			
			System.out.print("Printing " + dirs[i] + " ... ");

			// Stock transfer
			TreeSet<String> st = findAllFiles(relationDir, dirs[i], "ST", highYear, highMonth, lowYear, lowMonth);
			printStockTransferType(st, dirs[i], targetType);

			System.out.println("done.");
		}
	}
	

	// If not found, return null
	public String findLatestFile(String relationDir, String stockDir, String postfix, int highYear, int highMonth, int lowYear, int lowMonth) {
		for(int i=highYear; i>=lowYear; i--) {
			int high = 12;
			int low = 1;
			if(i == highYear) {
				high = highMonth;
			}
			if(i == lowYear) {
				low = lowMonth;
			}
			for(int j=high; j>=low; j--) {
				String s = relationDir + "/" + stockDir + "/" + stockDir + "_";
				s += Integer.toString(i);
				s += "_";
				if(j < 10) {
					s += "0";
				}
				s += Integer.toString(j);
				s += "_";
				s += postfix;
				s += ".csv";
				
				File f = new File(s);
				if(f.exists()) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
	}
	
	// If not found, return null
	public TreeSet<String> findAllFiles(String relationDir, String stockDir, String postfix, int highYear, int highMonth, int lowYear, int lowMonth) {
		TreeSet<String> ts = new TreeSet<String>();
		for(int i=highYear; i>=lowYear; i--) {
			int high = 12;
			int low = 1;
			if(i == highYear) {
				high = highMonth;
			}
			if(i == lowYear) {
				low = lowMonth;
			}
			for(int j=high; j>=low; j--) {
				String s = relationDir + "/" + stockDir + "/" + stockDir + "_";
				s += Integer.toString(i);
				s += "_";
				if(j < 10) {
					s += "0";
				}
				s += Integer.toString(j);
				s += "_";
				s += postfix;
				s += ".csv";
				
				File f = new File(s);
				if(f.exists()) {
					ts.add(f.getAbsolutePath());
				}
			}
		}
		return ts;
	}
	
	// If not found, return empty set
	public TreeSet<String> findAllFiles(String relationDir, String stockDir, String postfix) {
		TreeSet<String> ts = new TreeSet<String>();
		File[] dirs = new File(relationDir + "/" + stockDir).listFiles();
		for(int i=0; i<dirs.length; i++) {
			if(dirs[i].getName().contains(postfix)) {
				ts.add(dirs[i].getAbsolutePath());
			}
		}
		return ts;
	}

	public int loadStockHolder(String fileName, String destinationCode) throws Exception {
		HashSet<String> hs = new HashSet<String>();
		
		// Exception
		if (exceptionMap.containsKey(destinationCode)) {
			destinationCode = exceptionMap.get(destinationCode);
		}
			
		StockVertex destination = graph.getVertexByCode(destinationCode);
		
		int count = 0;
		
		if(fileName != null) {
			FileReader fr = new FileReader(fileName);
			LineNumberReader lnr = new LineNumberReader(fr);
			String s = lnr.readLine();	// Header, ignore it
			
			while ((s=lnr.readLine()) != null) {
				if(s.length() > 0) {
					String[] t = s.split(",");
					String name = StockUtil.preprocessStockName(t[1]);
					long stock = Long.parseLong(t[3].trim());

					// Market value
					stock *= this.computePrice(destinationCode);
					
					if(!hs.contains(name)) {					
						StockVertex source = graph.addNewVertex(name);
						StockEdge se = graph.addNewEdge(source, destination, stock, StockEdge.TYPE_HOLD);
						if(se != null) {
							count++;
						}
						hs.add(name);
					}
				}
			}
			
			lnr.close();
			fr.close();
		}
		
		return count;
	}
	
	public int loadStockTransfer(TreeSet<String> fileNames, String code) throws Exception {
		int count = 0;
		for(String fileName : fileNames) {
			count += loadStockTransfer(fileName, code);
		}
		return count;
	}

	public int loadStockTransfer(String fileName, String code) throws Exception {
		int count = 0;
		
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = lnr.readLine();	// Header, ignore it
		
		while ((s=lnr.readLine()) != null) {
			if(s.length() > 0) {
				
				String[] t = s.split(",");
				
				//boolean isGiven = false;
				String sourceName = "";
				String destinationName = "";
				long stock = 0;
					
				if(pubList.contains(code)) {
					//isGiven = t[4].equalsIgnoreCase("ÃØ»P");
					sourceName = StockUtil.preprocessStockName(t[3]);
					destinationName = StockUtil.preprocessStockName(t[6]);
					stock = Long.parseLong(t[9].trim());
					
					// Market value
					stock *= this.computePrice(code);
				}
				else {
					//isGiven = t[5].equalsIgnoreCase("ÃØ»P");
					sourceName = StockUtil.preprocessStockName(t[4]);
					destinationName = StockUtil.preprocessStockName(t[8]);
					stock = Long.parseLong(t[11].trim());		
					
					// Market value
					stock *= this.computePrice(code);
				}
				
				//if(isGiven == true) {
					StockVertex source = graph.addNewVertex(sourceName);
					StockVertex destination = graph.addNewVertex(destinationName, stock);
					StockEdge se = graph.findEdge(source, destination);
					if(se == null) {
						se = graph.addNewEdge(source, destination, stock, StockEdge.TYPE_TRANSFER);
						if(se != null) {
							count++;
						}
					}
					else {
						se.setStock(se.getStock() + stock);
					}
				//}
			}
		}
		
		lnr.close();
		fr.close();
		
		return count;
	}

	public void findStockTransferType(TreeSet<String> fileNames, String code) throws Exception {
		for(String fileName : fileNames) {
			findStockTransferType(fileName, code);
		}
	}

	public void findStockTransferType(String fileName, String code) throws Exception {
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = lnr.readLine();	// Header, ignore it
		
		while ((s=lnr.readLine()) != null) {
			if(s.length() > 0) {
				
				String[] t = s.split(",");
				
				String type = "";
				String target = "";
				
				if(pubList.contains(code)) {
					type = t[4];
					target = t[6];
				}
				else {
					type = t[5];
					target = t[8];
				}
				stockTransferTypes.put(type, target);				
			}
		}
		
		lnr.close();
		fr.close();
	}

	public void printStockTransferType(TreeSet<String> fileNames, String code, String targetType) throws Exception {
		for(String fileName : fileNames) {
			printStockTransferType(fileName, code, targetType);
		}
	}

	public void printStockTransferType(String fileName, String code, String targetType) throws Exception {
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = lnr.readLine();	// Header, ignore it
		
		while ((s=lnr.readLine()) != null) {
			if(s.length() > 0) {
				
				String[] t = s.split(",");
				
				String type = "";
				String target = "";
				
				if(pubList.contains(code)) {
					type = t[4];
					target = t[6];
				}
				else {
					type = t[5];
					target = t[8];
				}
				
				if(type.equalsIgnoreCase(targetType)) {
					stockTransferTypeTargets.add(StockUtil.preprocessStockName(target));
				}				
			}
		}
		
		lnr.close();
		fr.close();
	}

	public void loadCompanyNames(String fileName) throws Exception {
		FileReader fr = new FileReader(fileName);
		LineNumberReader lnr = new LineNumberReader(fr);
		String s = lnr.readLine();	// Header, ignore it
		
		while ((s=lnr.readLine()) != null) {
			String[] t = s.split(",");
			String code = t[0];
			String name = StockUtil.preprocessStockName(t[1]);
			long stock = Long.parseLong(t[2].trim());
			
			// Market value
			stock *= this.computePrice(code);
		
			if(!exceptionMap.containsKey(code)) {
				StockVertex sv = StockVertex.generateVertex(code, name, stock, StockVertex.TYPE_PUBLIC);
				this.graph.addVertex(sv);
			}
		}					
		
		lnr.close();
		fr.close();
	}

	/**
	 * @return the marketValue
	 */
	public boolean isMarketValue() {
		return marketValue;
	}

	/**
	 * @param marketValue the marketValue to set
	 */
	public void setMarketValue(boolean marketValue) {
		this.marketValue = marketValue;
	}

	/**
	 * @return the targetYear
	 */
	public int getTargetYear() {
		return targetYear;
	}

	/**
	 * @param targetYear the targetYear to set
	 */
	public void setTargetYear(int targetYear) {
		this.targetYear = targetYear;
	}

	/**
	 * @return the targetMonth
	 */
	public int getTargetMonth() {
		return targetMonth;
	}

	/**
	 * @param targetMonth the targetMonth to set
	 */
	public void setTargetMonth(int targetMonth) {
		this.targetMonth = targetMonth;
	}
	
	public long computePrice(String code) {
		if(this.marketValue == true) {
			Long price = this.sr.getRevenue(code, this.targetYear, this.targetMonth);
			if(price == null) {
				return 1000;		// Default value, price = 10.00
			}
			else {
				return price;
			}
		}
		else {
			return 1;
		}
	}
}
