package stock.cluster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import stock.vertex.StockVertex;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;


public class SummaryGraph {

    /**
     * the graph
     */
    UndirectedSparseGraph<String, Number> graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String, Number> vv;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * @return 
     * 
     */
    public SummaryGraph()
    {
    	
    }
    
    public void viewSocio(Graph<StockVertex,Number> tGraph, int minDegree)
    {   
        // create a simple graph for the demo
        graph = getSimpleGraph(tGraph);
        
        Set<String> removeSet = new HashSet<String>();
        for (String v:graph.getVertices())
        { 
        		if (graph.getNeighborCount(v) < minDegree)
        		{	
        			removeSet.add(v);
        		}
        }  
        for (String v:removeSet)
        	{
        		graph.removeVertex(v);
        	}
        showGrpah();
    }
    
    public void viewEgo(Graph<StockVertex,Number> tGraph, String codeOfEgoGraph, int levelOfEgoGraph)
    {   
   		UndirectedSparseGraph<String, Number> retGrpah = new UndirectedSparseGraph<String, Number>();
   		StockVertex target = null;
   		Buffer<StockVertex> queue = new UnboundedFifoBuffer<StockVertex>();
        
        for (StockVertex v:tGraph.getVertices())
        {
        		if (v.getCode().equals(codeOfEgoGraph))
        		{
        			target = v; 
        		}
        }
   		
		retGrpah.addVertex(target.getName()); 
		queue.add(target);
		Set<StockVertex> addedSet = new HashSet <StockVertex>();
		addedSet.add(target);
		for (int i = 0; i<levelOfEgoGraph && !queue.isEmpty(); i++)
		{			
			//while (!queue.isEmpty())
			{
				StockVertex v = queue.remove();
				for (StockVertex n:tGraph.getNeighbors(v))
				{
					retGrpah.addVertex(n.getName());
					if ( 	retGrpah.findEdge(v.getName(), n.getName()) == null && 
							retGrpah.findEdge(n.getName(), v.getName()) == null)
						retGrpah.addEdge(tGraph.findEdge(v, n), v.getName(), n.getName());
					if (!addedSet.contains(n))
					{
						queue.add(n);
						addedSet.add(n);
					}
				}			
			}
		}
        graph = retGrpah;    
        showGrpah();
    }    
    
    private UndirectedSparseGraph<String, Number> getSimpleGraph(Graph<StockVertex,Number> tGraph)
    {
    		UndirectedSparseGraph<String, Number> retGrpah = new UndirectedSparseGraph<String, Number>();
        for (StockVertex v:tGraph.getVertices())
        {
        		retGrpah.addVertex(v.getName()); 
        		for (StockVertex n:tGraph.getNeighbors(v))
        		{
        			retGrpah.addVertex(n.getName());
        			if ( 	retGrpah.findEdge(v.getName(), n.getName()) == null && 
        					retGrpah.findEdge(n.getName(), v.getName()) == null)
        				retGrpah.addEdge(tGraph.findEdge(v, n), v.getName(), n.getName());
        		}
        }
        return retGrpah;
    }
    
    @SuppressWarnings("unchecked")
	private void showGrpah()
    {

        vv =  new VisualizationViewer<String,Number>(new SpringLayout<String,Number>(graph));
        

        vv.addGraphMouseListener(new TestGraphMouseListener<String>());
        vv.getRenderer().setVertexRenderer(
        		new GradientVertexRenderer<String,Number>(
        				Color.white, Color.red, 
        				Color.white, Color.blue,
        				vv.getPickedVertexState(),
        				false));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.lightGray));
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer(Color.lightGray));
        
        // add my listeners for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller<String>());
        vv.setEdgeToolTipTransformer(new Transformer<Number,String>() {
			public String transform(Number edge) {
				return "E"+graph.getEndpoints(edge).toString();
			}});
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderer().getVertexLabelRenderer().setPositioner(new InsidePositioner());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);
        vv.setForeground(Color.lightGray);
        
        // create a frome to hold the graph
        final JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<String,Number>();
        vv.setGraphMouse(graphMouse);
        
        vv.addKeyListener(graphMouse.getModeKeyListener());
        vv.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });

        JButton reset = new JButton("reset");
        reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setToIdentity();
				vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
			}});

        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        controls.add(reset);
        content.add(controls, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true); 	
    }
    
    /**
     * A nested class to demo the GraphMouseListener finding the
     * right vertices after zoom/pan
     */
    static class TestGraphMouseListener<V> implements GraphMouseListener<V> {
        
    		public void graphClicked(V v, MouseEvent me) {
    		    System.err.println("Vertex "+v+" was clicked at ("+me.getX()+","+me.getY()+")");
    		}
    		public void graphPressed(V v, MouseEvent me) {
    		    System.err.println("Vertex "+v+" was pressed at ("+me.getX()+","+me.getY()+")");
    		}
    		public void graphReleased(V v, MouseEvent me) {
    		    System.err.println("Vertex "+v+" was released at ("+me.getX()+","+me.getY()+")");
    		}
    }
}
