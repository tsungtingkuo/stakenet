package stock.edge;

import org.apache.commons.collections15.Transformer;

public class StockEdgeTransformer implements Transformer<Number, String> {

    public String transform(Number e) {
    	return e.toString();
    }
}

