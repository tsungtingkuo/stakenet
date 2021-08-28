package stock.edge;

import java.io.*;

public class StockEdge extends Number implements Serializable {
	
	private static final long serialVersionUID = -8904609442674908066L;

	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_HOLD = 1;
	public static final int TYPE_TRANSFER = 2;
	
	// Current id
	static int currentID = 0;

	// Auto generated id
	int id = 0;
	
	// Number of stock hold, or stock transferred between people
	// Come from stock holder or stock transfer files
	long stock = 0;

	// Record the names of source and destination vertices
	String sourceName = null;
	String destinationName = null;
	
	// Heterogeneous
	int type = TYPE_UNKNOWN;
	
	// Normalized (stock/1000)*price
	double value = 0;
	
	// Use this instead of create StockEdge directly
	public static StockEdge generateEdge(long stock, String sourceName, String destinationName, int type) {
		StockEdge se = new StockEdge();
		se.id = StockEdge.currentID;
		StockEdge.currentID++;
		se.stock = stock;
		se.sourceName = sourceName;
		se.destinationName = destinationName;
		se.type = type;
		return se;
	}

	// Use this instead of create StockEdge directly
	public static StockEdge generateEdge(String edge) {
		String[] t = edge.split(",");
		StockEdge se = new StockEdge();
		se.id = StockEdge.currentID;
		StockEdge.currentID++;
		se.stock = Long.parseLong(t[0]);
		se.sourceName = t[1];
		se.destinationName = t[2];
		se.type = Integer.parseInt(t[3]);
		return se;
	}

	public String toFileString() {
		return stock + "," + sourceName + "," + destinationName + "," + type;
	}
		
	public String toString() {
		String result = Long.toString(stock);
		long s = stock;
		if(stock > 1000000) {
			s = stock/1000000;
			result = s + "M";
		}
		return result;
	}

	@Override
	public double doubleValue() {
		return (double)stock;
	}

	@Override
	public float floatValue() {
		return (float)stock;
	}

	@Override
	public int intValue() {
		return (int)stock;
	}

	@Override
	public long longValue() {
		return stock;
	}

	/**
	 * @return the stock
	 */
	public long getStock() {
		return stock;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @return the destinationName
	 */
	public String getDestinationName() {
		return destinationName;
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

	/**
	 * @param stock the stock to set
	 */
	public void setStock(long stock) {
		this.stock = stock;
	}
}
