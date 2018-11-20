package stock.vertex;

import java.io.*;

public class StockVertex implements Comparable<StockVertex>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8415951958884706533L;
	
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_PUBLIC = 1;
	public static final int TYPE_PRIVATE = 2;
	public static final int TYPE_PEOPLE = 3;
	
	static boolean fullName = false;
	
	// Code of stock, equals "" if this vertex is a people
	// Come from company file
	String code = "";
	
	// Name of stock or people
	// Come from company, stock holder or stock transfer files
	String name = "";
	
	// Number of stock, or total stock hold in people
	// Come from stock holder files
	long stock = 0;
	
	// Used for visualization
	int ground = -1;

	// Heterogeneous
	int type = TYPE_UNKNOWN;
	
	// Used for sorting
	double value = 0;
	
	// English name of public stock
	// Come from English files
	String english = "";

	// Use this to have same behavior as StockEdge
	public static StockVertex generateVertex(String code, String name, long stock, int type) {
		StockVertex sv = new StockVertex();
		sv.code = code;
		sv.name = name;
		sv.stock = stock;
		sv.type = type;
		return sv;
	}
	
	// Use this to have same behavior as StockEdge
	public static StockVertex generateVertex(String vertex) {
		String[] t = vertex.split(",");
		StockVertex sv = new StockVertex();
		sv.code = t[0];
		sv.name = t[1];
		sv.stock = Long.parseLong(t[2]);
		sv.type = Integer.parseInt(t[3]);
		return sv;
	}
	
	public String toFileString() {
		return code + "," + name + "," + stock + "," + type;
	}
	
	public String toString() {
		if(fullName == true) {
			// Chinese + English
			String s = name;
			if(english.equalsIgnoreCase("") == false) {
				s += " (" + english + ")";
			}
			return s;
		}
		else {
			// English only
			return english;
		}
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the stock
	 */
	public long getStock() {
		return stock;
	}

	/**
	 * @return the ground
	 */
	public int getGround() {
		return ground;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int compareTo(StockVertex sv) {
		if(this.value > sv.value) {
			return 1;
		}
		else if(this.value < sv.value) {
			return -1;
		}
		else {
			return this.name.compareTo(sv.name);
		}
	}

	/**
	 * @return the english
	 */
	public String getEnglish() {
		return english;
	}

	/**
	 * @param english the english to set
	 */
	public void setEnglish(String english) {
		this.english = english;
	}

	/**
	 * @return the fullName
	 */
	public static boolean isFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public static void setFullName(boolean fullName) {
		StockVertex.fullName = fullName;
	}

	/**
	 * @param stock the stock to set
	 */
	public void setStock(long stock) {
		this.stock = stock;
	}

	/**
	 * @param ground the ground to set
	 */
	public void setGround(int ground) {
		this.ground = ground;
	}
}
