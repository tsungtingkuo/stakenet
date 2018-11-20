package stock.vertex;

import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;

public class StockVertexTransformer implements Transformer<StockVertex, Paint> {

	@Override
	public Paint transform(StockVertex sv) {
		
		// For public / non-public
		switch(sv.type) {
		case StockVertex.TYPE_PUBLIC:
			return Color.blue;
		default:
			return Color.green;
		}
		
		// For revenue
		/*
		switch(sv.ground) {
		case StockGroundTruth.REVENUE_DECREASE:
			return Color.green;
			
		case StockGroundTruth.REVENUE_INCREASE:
			return Color.red;

		default:
			return Color.blue;
		}
		*/
	}

}
