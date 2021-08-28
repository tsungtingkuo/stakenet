package stock.edge;

import org.apache.commons.collections15.Transformer;

public class StockEdgeValueTransformer implements Transformer<StockEdge, Number> {

	@Override
	public Number transform(StockEdge e) {
		return e.getValue();
	}
}

