package stock.ranker;

import stock.edge.StockEdge;
import stock.vertex.StockVertex;
import edu.uci.ics.jung.algorithms.importance.*;

public class StockAbstractRanker extends AbstractRanker<StockVertex, StockEdge> {

	AbstractRanker<StockVertex, StockEdge> ar = null;
	
	public StockAbstractRanker(AbstractRanker<StockVertex, StockEdge> ar) {
		this.ar = ar;
	}
	
	@Override
	public Object getRankScoreKey() {
		return null;
	}

	@Override
	public void step() {
	}

	/**
	 * @return the ar
	 */
	public AbstractRanker<StockVertex, StockEdge> getAr() {
		return ar;
	}
}
