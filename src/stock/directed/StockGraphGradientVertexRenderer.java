package stock.directed;

import java.awt.Color;

import stock.vertex.StockVertex;
import stock.vertex.StockVertexTransformer;


import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;

public class StockGraphGradientVertexRenderer extends GradientVertexRenderer<StockVertex, Number> {

	public StockGraphGradientVertexRenderer(Color colorOne, Color colorTwo,	boolean cyclic) {
		super(colorOne, colorTwo, cyclic);
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer#paintVertex(edu.uci.ics.jung.visualization.RenderContext, edu.uci.ics.jung.algorithms.layout.Layout, java.lang.Object)
	 */
	@Override
	public void paintVertex(RenderContext<StockVertex, Number> rc,
			Layout<StockVertex, Number> layout, StockVertex v) {
		StockVertexTransformer svt = new StockVertexTransformer();
		rc.setVertexDrawPaintTransformer(svt);
		super.paintVertex(rc, layout, v);
	}
	
	
}
