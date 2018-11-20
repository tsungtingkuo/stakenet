package stock.cluster;

import java.io.*;
import java.util.*;
import org.apache.commons.collections15.*;
import org.apache.commons.collections15.buffer.*;
import stock.directed.StockGraph;
import stock.edge.StockEdge;
import stock.undirected.StockUndirectedGraph;
import stock.vertex.StockVertex;


public class StockCluster
{
	static boolean bIsPrintDebug,bRemoveOneDegreeVertex,bRemoveBankCompany,bCountDirectOwnCompany;
	static StockGraph SG; 											
	static int iAlpha,iBeta;
	static boolean bIsCompanyGroupOK, bIsCompanySoftingGroupOk;
	Set<Set<StockVertex>> companyGroupSet;
	Map<StockVertex,Set<StockVertex>> companySoftlyGrouping;
	
	static private void printDebug(String str)
    {
    		if (bIsPrintDebug)
    			System.out.println(str);
    }
    static private Set<String> read_bank_file (String file_path) throws FileNotFoundException, IOException
    {

        FileInputStream fi = new FileInputStream(file_path);
        //DataInputStream in = new DataInputStream(fi);
        InputStreamReader in = new InputStreamReader(fi,"big5"); 
        BufferedReader br = new BufferedReader(in);
        Set<String> BankSet = new HashSet<String>();

        String s = null;
        while ((s = br.readLine()) != null)
        {
        		BankSet.add(s);
        }
        in.close();
        fi.close();
        return BankSet;
    }
    
    static private Map<String,Set<Number>> read_bussiness_group_file (String file_path) throws FileNotFoundException, IOException
    {

        FileInputStream fi = new FileInputStream(file_path);
        InputStreamReader in = new InputStreamReader(fi,"big5"); 
        BufferedReader br = new BufferedReader(in);
        Map<String,Set<Number>> bussinessGroupMap = new HashMap<String,Set<Number>>();

        String s = null;
        while ((s = br.readLine()) != null)
        {
        		String name = s.substring(0, s.indexOf(":"));
        		s = s.substring(s.indexOf(":")+1,s.length());
        		String[] tokens = s.split(",");
        		int i = 0;
        		Set<Number> members = new HashSet<Number>();
        		while (i < tokens.length)
        		{
        			members.add(Integer.valueOf(tokens[i]).intValue());
        			i++;
        		}
        		bussinessGroupMap.put(name, members);
        }
        in.close();
        fi.close();
        return bussinessGroupMap;
    }
    
    boolean isFoundInStockVertexSet(Set<StockVertex> vertexSet, int iStockNumber)
    {
    		for (StockVertex v:vertexSet)
		{
    			String code = v.getCode();
    			if (code.length() == 4)
    			{
				if (Integer.valueOf(code).intValue() == iStockNumber)
				{
					return true;
				}
    			}
		}
    		return false;
    }
    
    private void printClusterInfo(Set<Set<StockVertex>> clusterSet)
    {
		//////print information about cluster set --start
		int iGCC = 0;
		int iTotalPublicVertexCount = 0;
		Map<Number,Number> clusterCountMap = new HashMap<Number,Number>();
		for (Set<StockVertex> c:clusterSet)
		{               
			printDebug("Cluster size: " + c.size()+" vertex are: "+ c);
			iTotalPublicVertexCount += c.size();
			if (c.size() > iGCC)
			{
				iGCC = c.size();
			}
			if (clusterCountMap.get(c.size())!= null)
				clusterCountMap.put(c.size(),clusterCountMap.get(c.size()).intValue()+1);
			else
				clusterCountMap.put(c.size(),1);
		}
		printDebug("GCC size " + iGCC+ " clster set number : "+clusterSet.size()+" public vertex number : "+iTotalPublicVertexCount);
		//////print information about cluster set--end
    }
 
    private void printClusterInfo(Map<StockVertex,Set<StockVertex>> clusterSet)
    {
		//////print information about cluster set --start
		int iGCC = 0;
		int iTotalPublicVertexCount = 0;
		Map<Number,Number> clusterCountMap = new HashMap<Number,Number>();
		for (StockVertex key:clusterSet.keySet())
		{              
			Set<StockVertex> c = clusterSet.get(key);
			printDebug("Cluster size of "+key+" is " + c.size()+" vertex are: "+ c);
			int iClusterSizeShouldeBe = c.size()+1;
			iTotalPublicVertexCount += iClusterSizeShouldeBe;
			if (c.size() > iGCC)
			{
				iGCC = iClusterSizeShouldeBe;
			}
			if (clusterCountMap.get(iClusterSizeShouldeBe)!= null)
				clusterCountMap.put(iClusterSizeShouldeBe,clusterCountMap.get(iClusterSizeShouldeBe).intValue()+1);
			else
				clusterCountMap.put(iClusterSizeShouldeBe,1);
		}
		printDebug("GCC size " + iGCC+ " clster set number : "+clusterSet.size()+" public vertex number : "+iTotalPublicVertexCount);
		//////print information about cluster set--end
		
    }   
    
    private void didPara() throws FileNotFoundException, IOException
    {
		///////remove only one edge's vertex------start
		if (bRemoveOneDegreeVertex)
		{    
			Set<StockVertex> removeSet = new HashSet<StockVertex>();
			for (StockVertex v:SG.getVertices())
			{
				if (SG.getNeighborCount(v) == 1)
				{
					removeSet.add(v);			
				}
			}
		
			printDebug("removeing "+removeSet.size()+" degree 1 vertexs");
			for (StockVertex v:removeSet)
			{
				SG.removeVertex(v);		
			}		
		}       
		///////remove only one edge's vertex---------end
	
		///////remove bank vertex------start
		if (bRemoveBankCompany)
		{    
			Set<String> BankSet = read_bank_file("./bank.txt");
			Set<StockVertex> removeSet = new HashSet<StockVertex>();
			
			for (StockVertex v:SG.getVertices())
			{
				if (BankSet.contains(v.getName()))
				{
					removeSet.add(v);			
				}
			}
			
			printDebug("removeing "+removeSet.size()+" bank vertexs");
			for (StockVertex v:removeSet)
			{
				SG.removeVertex(v);		
			}		
		}       
		///////remove bank vertex---------end   

    }
	
    public StockCluster(boolean IsPrintDebug,
			    			StockGraph sg, 
						boolean RemoveOneDegreeVertex, 
						boolean RemoveBankCompany,
						boolean CountDirectOwnCompany,  											
						int alpha, 
						int beta) throws FileNotFoundException, IOException 
    {
    		bIsPrintDebug = IsPrintDebug;
    		bRemoveOneDegreeVertex = RemoveOneDegreeVertex;
    		bRemoveBankCompany = RemoveBankCompany;
    		bCountDirectOwnCompany = CountDirectOwnCompany;
    		iAlpha = alpha;
    		iBeta = beta;
    		SG = sg.clone();	
    		bIsCompanyGroupOK = false;
    		bIsCompanySoftingGroupOk = false; 		
    }
    private int GetIntersect(Set<Number> rightSet, Set<StockVertex> checkSet)
    {
    		int iRetNum = 0;
    		for (StockVertex v:checkSet)
    		{
    			if (v.getCode().length() ==4)
    			{
    				if (rightSet.contains(Integer.valueOf(v.getCode())))
    				{
    					iRetNum++;
    				}
    			}
    		}
    		return iRetNum;
    }
 
    public float getNMI(Set<Set<StockVertex>> clusterSet) throws FileNotFoundException, IOException
    {
    	
    		Map<String,Set<Number>> bussinessGroupMap = read_bussiness_group_file("./bussiness_group.txt");
    		//printDebug(bussinessGroupMap);
    		int iTotalKnownNumber = 0; //$N
    		
    				   // web answer	->	my answer
    		//int cc = 0;// correct	->	correct
    		//int cw = 0;// correct	->	wrong
    		//int ww = 0;// wrong		->	wrong
    		
    		for (Set<Number> correctGroup: bussinessGroupMap.values())
    		{
    			iTotalKnownNumber += correctGroup.size();
    		}
    		
    		float MI = 0.0f;
    		
    		
    		for (Set<StockVertex> checkSet:clusterSet)
    		{
    			//find set
    			if (checkSet.size() == 0)
    				continue;
    			for (Set<Number> correctGroup: bussinessGroupMap.values())
    			{
    				int intersect_wc = GetIntersect (correctGroup,checkSet);
    				if (intersect_wc == 0)
        				continue;
    				else
    				{
    					float nem = 0.0f, dem = 0.0f;
    					nem = intersect_wc / (float) iTotalKnownNumber;
    					float t = (iTotalKnownNumber * intersect_wc) / (float) (correctGroup.size() * checkSet.size());
    					dem =  (float) (Math.log(t)/(float)Math.log(2));
    					MI += nem * dem;	
    				}
    			}
    		}
    		float entropy_w = 0.0f;
    		float entropy_c = 0.0f;
    		for (Set<Number> correctGroup: bussinessGroupMap.values())
		{
			if (correctGroup.size() == 0)
				continue;
			else
			{
				float t = (correctGroup.size()/ (float) iTotalKnownNumber);
				entropy_w += t * (Math.log(t)/ (float) Math.log(2) );
			}
		}
    		for (Set<Number> correctGroup: bussinessGroupMap.values())
    		{
    			float t = correctGroup.size()/(float)iTotalKnownNumber;
    			entropy_c += (float) (t * (Math.log(t)/(float) Math.log(2)));
    		}
    		

    		entropy_w *= -1;
    		entropy_c *= -1;

    		float NMI = MI/((entropy_w+entropy_c)/2);
    		printDebug("entropy_w "+entropy_w+" entropy_c "+entropy_c+" NMI "+NMI);
    	    		
    		return NMI;
    		
    	
    }
    
  /*  public Set<Set<StockVertex>> getWeakComponent() throws FileNotFoundException, IOException
    {
    		WeakComponentClusterer<StockVertex,StockEdge> wcSearch = new WeakComponentClusterer<StockVertex,StockEdge>();
		Set<Set<StockVertex>> weakClusterSet = wcSearch.transform(SG);
		for (Set<StockVertex> set:weakClusterSet)
		{
			Set<StockVertex> removeSet = new HashSet<StockVertex>();
			for (StockVertex v:set)
			{
				if (v.getType() != StockVertex.TYPE_PUBLIC)
				{
					removeSet.add(v);
				}
			}
			for (StockVertex remove:removeSet)
				set.remove(remove);
		}
		
		for (Iterator iter = weakClusterSet.iterator(); iter.hasNext();)
		{
			Set<StockVertex> set = (Set<StockVertex>) iter.next();
			if (set.size() == 0)
			{
				iter.remove();
				weakClusterSet.remove(set);
			}
		}
		
		return weakClusterSet;
    }*/
    private int getSGNeighborVertexTypeCount(StockVertex vertex,  int vertexType)
    {
    		int iCount = 0;
    		for (StockVertex some:SG.getNeighbors(vertex))
		{
			if (some.getType() == vertexType)
			{
				iCount++;
			}
		}
    		return iCount;
    }
    private int getSGNeighborTypeCount(StockVertex vertex, int vertexType, int edgeType)
    {
    		int iCount = 0;
    		
    		for (StockVertex some:SG.getNeighbors(vertex))
		{
    			if (some.getType() == vertexType)
    			{
				StockEdge e = SG.findEdge(vertex, some);
	    			if (e != null)
	    			{
	    				if ( e.getType() == edgeType)
	    				{
	    					iCount++;
	    				}
	    			}
	    			else
	    			{
		    			e = SG.findEdge(some, vertex);
		    			if (e != null)
		    			{
		    				if ( e.getType() == edgeType)
		    				{
		    					iCount++;
		    				}
		    			}
	    			}
    			}
		}
    		return iCount;
    }
    // if a's code is smaller than b return true, otherwise return false
    // but there are some code like "B00113", this kind code is big than number
    private boolean isCodeASmallerB(StockVertex a, StockVertex b)
    {
    		if (a.getCode().compareTo(b.getCode()) >0)
    			return true;
    		else
    			return false;
    }
    
    public LinkedList<StockVertex> getPublicVertexSortByDegree()
    {
    		Set<StockVertex> publicVertex = SG.getPublicVertices();
    		// first sort by public neighbor and hold degree
    		// second sort by private neighbor
    		// there sort by code 
    		Map<Number, Map<Number, LinkedList<StockVertex>>> sortedMap = new HashMap<Number, Map<Number, LinkedList<StockVertex>>>();
    		
    		while (!publicVertex.isEmpty())
    		{
    			StockVertex C1 = publicVertex.iterator().next();
    			int iSeocndSortedDegree= getSGNeighborVertexTypeCount(C1, StockVertex.TYPE_PUBLIC);
    			int iFirstSortedDegree = getSGNeighborTypeCount(C1, StockVertex.TYPE_PUBLIC, StockEdge.TYPE_HOLD);

    			if (sortedMap.get(iFirstSortedDegree) == null)
    		    {
    				Map<Number, LinkedList<StockVertex>> privateDegreeMap = new HashMap<Number, LinkedList<StockVertex>>();
    				LinkedList<StockVertex> vertexList = new LinkedList<StockVertex>();
    				vertexList.add(C1);
    				privateDegreeMap.put(iSeocndSortedDegree, vertexList);
    				sortedMap.put(iFirstSortedDegree,privateDegreeMap);
    		    }
    		    else
    		    {
    		    		if (sortedMap.get(iFirstSortedDegree).get(iSeocndSortedDegree) == null)
    		    		{
    		    			//Map<Number, Set<StockVertex>> privateDegreeMap = new HashMap<Number, Set<StockVertex>>();
    		    			LinkedList<StockVertex> vertexList = new LinkedList<StockVertex>();
    		    			vertexList.add(C1);
    			    		sortedMap.get(iFirstSortedDegree).put(iSeocndSortedDegree, vertexList);
    		    		}
    		    		else
    		    		{
    		    			//System.out.println(C1.getCode());
    		    			LinkedList<StockVertex> vertexList = sortedMap.get(iFirstSortedDegree).get(iSeocndSortedDegree);
    		    			//find a good place
    		    			boolean bIsFound =false;
    		    			for (int i = 0; i < vertexList.size() && !bIsFound ; i++)
    		    			{
    		    				StockVertex v = vertexList.get(i);
    		    				//System.out.println(v.getCode());
    		    				if ( i == 0 &&
    		    					isCodeASmallerB(C1, v))
    		    				{
    		    					vertexList.addFirst(C1);
    		    					bIsFound = true;
    		    				}
    		    				else if ( i == vertexList.size()-1 &&
    		    						isCodeASmallerB(v, C1))
    		    				{
    		    					vertexList.addLast(C1);
    		    					bIsFound = true;
    		    				}
    		    				else 
    		    				{
    		    					if (i >= 1)
    		    					{
	    		    					StockVertex v1 = vertexList.get(i-1);
	    		    					if ( isCodeASmallerB(C1, v) &&
	    		    						 isCodeASmallerB(v1, C1))
	    		    					{
	    		    						vertexList.add(i, C1);
	    		    						bIsFound = true;
	    		    					}
    		    					}
    		    				}
    		    			}
    		    		}
    		    }
    			publicVertex.remove(C1);
		}
 	
    		
    		
    		LinkedList<StockVertex> linkedList;
    		linkedList = new LinkedList<StockVertex>();
    		Object[] keys = sortedMap.keySet().toArray();
    		for(int i = keys.length-1 ; i > -1; i--) 
    		{
    			//Map<Number, Map<Number, LinkedList<StockVertex>>>
    			Map<Number, LinkedList<StockVertex>> subSortedMap = sortedMap.get(keys[i]);
    			Object[] subKeys = subSortedMap.keySet().toArray();
        		for(int j = subKeys.length-1 ; j > -1; j--) 
        		{
        			LinkedList<StockVertex> list = subSortedMap.get(subKeys[j]);
        			linkedList.addAll(list);
        		}
    		}
    		System.out.println(linkedList);
    		
    		return linkedList;
    }
    
    private Set<Set<StockVertex>> tryToMerge (	Set<Set<StockVertex>> originalClusterSet, 
    												LinkedList<Set<StockVertex>> candidates,
    												BusinessGroup processingBusinessGroup)
    {
		//int score = 0;
		//Set<StockVertex> targetSet = null;
		//boolean bAdd = false;
		Set<StockVertex> subClusterSet = processingBusinessGroup.getAll();
		Set<Set<StockVertex>> newClusterSet = new HashSet<Set<StockVertex>>();
		
		//System.out.println("/////////////////////");
		//System.out.println(processingBusinessGroup.core.getCode()+" "+processingBusinessGroup.core.getName());
		//System.out.println("direct "+processingBusinessGroup.directLinkOwnSet);
		//System.out.println("triangel "+processingBusinessGroup.triangelSet);
		//System.out.println("indirect "+processingBusinessGroup.indirectLinkSet);
		
		int score0 = 0, score1 = 0;
		//for (int i = 0; i< candidates.size(); i++)
		{
			//System.out.println(candidates.get(0));
			score0 = processingBusinessGroup.getMergeScore(candidates.get(0));
			//System.out.println(candidates.get(1));
			score1 = processingBusinessGroup.getMergeScore(candidates.get(1));
			if (score0 > score1)
			{
				//bAdd = true;
				//targetSet = set0;	
				//System.out.println("this merged to set0");
				newClusterSet.addAll(originalClusterSet);
				for (Set<StockVertex> v: newClusterSet)
				{
					if (v.equals(candidates.get(0)))
						v.addAll(subClusterSet);
				}
				//candidates.get(0).addAll(subClusterSet);
			}
			else if (score0 < score1)
			{
				//bAdd = true;
				//targetSet = set1;	
				//System.out.println("this merged to set1");
				//candidates.get(1).addAll(subClusterSet);
				newClusterSet.addAll(originalClusterSet);
				for (Set<StockVertex> v: newClusterSet)
				{
					if (v.equals(candidates.get(1)))
						v.addAll(subClusterSet);
				}
			}
			else 
			{
				//boolean bFOund = false;
				//System.out.println("merge three to one");
				for (Iterator<Set<StockVertex>> iter = originalClusterSet.iterator(); iter.hasNext(); )
				{
					Set<StockVertex> v = (Set<StockVertex>) iter.next();
					if (v.equals(candidates.get(1)))
					{
						//skip this
						/*iter.remove();
						if (clusterSet.remove(v))
							bFOund = true;
						else
							System.out.println("damn");*/
						
					}
					else if (v.equals(candidates.get(0)))
					{
						//keep this and added subcluster
						candidates.get(0).addAll(subClusterSet);
						newClusterSet.add(candidates.get(0));
					}
					else
						newClusterSet.add(v);
				}
				
			}		
		}	
		//System.out.println("/////////////////////");
		return newClusterSet;
    }
    
    private LinkedList<Set<StockVertex>> tryToMerge (LinkedList<Set<StockVertex>> originalClusterSet, 
													LinkedList<Set<StockVertex>> candidates,
													BusinessGroup processingBusinessGroup)
	{
		//int score = 0;
		//Set<StockVertex> targetSet = null;
		//boolean bAdd = false;
		Set<StockVertex> subClusterSet = processingBusinessGroup.getAll();
		LinkedList<Set<StockVertex>> newClusterSet = new LinkedList<Set<StockVertex>>();
		
		//System.out.println("/////////////////////");
		//System.out.println(processingBusinessGroup.core.getCode()+" "+processingBusinessGroup.core.getName());
		//System.out.println("direct "+processingBusinessGroup.directLinkOwnSet);
		//System.out.println("triangel "+processingBusinessGroup.triangelSet);
		//System.out.println("indirect "+processingBusinessGroup.indirectLinkSet);
		
		int score0 = 0, score1 = 0;
		//for (int i = 0; i< candidates.size(); i++)
		{
			//System.out.println(candidates.get(0));
			score0 = processingBusinessGroup.getMergeScore(candidates.get(0));
			//System.out.println(candidates.get(1));
			score1 = processingBusinessGroup.getMergeScore(candidates.get(1));
			if (score0 > score1)
			{
				//bAdd = true;
				//targetSet = set0;	
				//System.out.println("this merged to set0");
				newClusterSet.addAll(originalClusterSet);
				for (Set<StockVertex> v: newClusterSet)
				{
					if (v.equals(candidates.get(0)))
					v.addAll(subClusterSet);
				}
				//candidates.get(0).addAll(subClusterSet);
			}
			else if (score0 < score1)
			{
				//bAdd = true;
				//targetSet = set1;	
				//System.out.println("this merged to set1");
				//candidates.get(1).addAll(subClusterSet);
				newClusterSet.addAll(originalClusterSet);
				for (Set<StockVertex> v: newClusterSet)
				{
					if (v.equals(candidates.get(1)))
					v.addAll(subClusterSet);
				}
			}
			else 
			{
				//boolean bFOund = false;
				//System.out.println("merge three to one");
				for (Iterator<Set<StockVertex>> iter = originalClusterSet.iterator(); iter.hasNext(); )
				{
					Set<StockVertex> v = (Set<StockVertex>) iter.next();
					if (v.equals(candidates.get(1)))
					{
						//skip this
						/*iter.remove();
						if (clusterSet.remove(v))
						bFOund = true;
						else
						System.out.println("damn");*/					
					}
					else if (v.equals(candidates.get(0)))
					{
						//keep this and added subcluster
						candidates.get(0).addAll(subClusterSet);
						newClusterSet.add(candidates.get(0));
					}
					else
						newClusterSet.add(v);
				}
			
			}		
		}	
		//System.out.println("/////////////////////");
		return newClusterSet;
	}

    
    
    Set<Set<StockVertex>> getBussinessGroupNew() throws FileNotFoundException, IOException
    {
    	
    		didPara();
    		
        Set<Set<StockVertex>> clusterSet = new HashSet<Set<StockVertex>>();
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
        
        //Set<StockVertex> publicVertex = new HashSet<StockVertex>();
        //Set<StockVertex> publicVertex = SG.getPublicVertices();
        LinkedList<StockVertex> linkedList = getPublicVertexSortByDegree();
       
        //while (!publicVertex.isEmpty())
        while(!linkedList.isEmpty())
		{
        	//StockVertex C1 = publicVertex.iterator().next();
        		StockVertex C1 = linkedList.getFirst();
        		        		
        		
			//Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			//addd C1
			//subClusterSet.add(C1);
        		BusinessGroup processingBusinessGroup = new BusinessGroup(C1);  	
			
			
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 -> C2
			//      C2        some 1,2
			Map<StockVertex, Set<StockVertex>> IndirectLinkMap = new HashMap<StockVertex, Set<StockVertex>>();
			Set<StockVertex> TriangelSet = new HashSet<StockVertex>();
		
			for (StockVertex some:SG.getNeighbors(C1))
			{
				for (StockVertex neighborsneighbor:SG.getNeighbors(some))
				{
					if (C1 == neighborsneighbor)
						continue;
					if (	neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC &&
							SG.findEdge(C1, some) != null && 
							SG.findEdge(some, neighborsneighbor) != null)
					{																		
						if (!IndirectLinkMap.containsKey(neighborsneighbor))
						{				
							Set<StockVertex> subSet = new HashSet<StockVertex>();
							subSet.add(some);
							IndirectLinkMap.put(neighborsneighbor, subSet);
						}
						else
						{
							Set<StockVertex> subSet = IndirectLinkMap.get(neighborsneighbor);
							if (!subSet.contains(some))
							{
								subSet.add(some);
							}
						}
					} // end of IndirectLinkMap detect
					
					//detect triangle cluster
					if (SG.findEdge(C1, neighborsneighbor) != null || 
							SG.findEdge(neighborsneighbor, C1) != null)
					{
						if (	some.getType() == StockVertex.TYPE_PUBLIC)
							TriangelSet.add(some);
						if (	neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC)
							TriangelSet.add(neighborsneighbor);
					}
				}
			}
			
			
			
			///added to processingBusinessGroup --start
			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					processingBusinessGroup.indirectLinkSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{

							processingBusinessGroup.indirectLinkSet.add(some);
						}
					}
				}
			}
			
			//add direct own sub company		
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					processingBusinessGroup.directLinkOwnSet.add(subC);
				}
			}
			
			//add triangle
			for (StockVertex subC:TriangelSet)
			{
				processingBusinessGroup.triangelSet.add(subC);
			}	
			///added to processingBusinessGroup --end
			//
/*			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					subClusterSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{

							subClusterSet.add(some);
						}
					}
				}
			}
			
			//add direct own sub company		
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}
			
			//add triangle
			for (StockVertex subC:TriangelSet)
			{
				subClusterSet.add(subC);
			}	*/
			
			
			LinkedList<Set<StockVertex>> candidates = new LinkedList<Set<StockVertex>>();
			
    			//above try to find the C1's business group
    			
    			Set<StockVertex> subClusterSet = processingBusinessGroup.getAll();
    			   			
    			//below try to merge business group
    			
			// check not repeat
			for (Set<StockVertex> checkSet: clusterSet)
			{
				boolean bIsAlreadyInThisCandidate = false;
				for (Iterator<StockVertex> iter = subClusterSet.iterator(); 
					iter.hasNext() && !bIsAlreadyInThisCandidate;)
				{
					StockVertex k = (StockVertex) iter.next();					
					if (checkSet.contains(k) )
					{
						//printDebug("found");
						bIsAlreadyInThisCandidate = true;
						candidates.add(checkSet);
						//continue;
					}
				}
			}		
			
			//add subClusterset to ClusterSet
			if (candidates.size() == 0)
			{
				// no candidate to merge
				if (subClusterSet.size() > 0)
					clusterSet.add(subClusterSet);
			}
			else
			{
				// yes, find some candidates	
				if (candidates.size() == 1)
				{
					//only one candidate
					candidates.getFirst().addAll(subClusterSet);
				}
				else
				{
					// more than one candidate
					// try to merge
					clusterSet = tryToMerge(clusterSet, candidates, processingBusinessGroup);
				}		
			}
			//publicVertex.removeAll(subClusterSet);
			linkedList.removeAll(subClusterSet);
		}// end of while (!publicVertex.isEmpty())

		//printClusterInfo(clusterSet);		
        return clusterSet;
    }   
    
    public LinkedList<Set<StockVertex>> getBussinessGroupLinkedList() throws FileNotFoundException, IOException
    {
    	
    		didPara();
    		
    		LinkedList<Set<StockVertex>> clusterSet = new LinkedList<Set<StockVertex>>();
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
        
        //Set<StockVertex> publicVertex = new HashSet<StockVertex>();
        //Set<StockVertex> publicVertex = SG.getPublicVertices();
        LinkedList<StockVertex> linkedList = getPublicVertexSortByDegree();
       
        //while (!publicVertex.isEmpty())
        while(!linkedList.isEmpty())
		{
        	//StockVertex C1 = publicVertex.iterator().next();
        		StockVertex C1 = linkedList.getFirst();
        		        		
        		
			//Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			//addd C1
			//subClusterSet.add(C1);
        		BusinessGroup processingBusinessGroup = new BusinessGroup(C1);  	
			
			
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 -> C2
			//      C2        some 1,2
			Map<StockVertex, Set<StockVertex>> IndirectLinkMap = new HashMap<StockVertex, Set<StockVertex>>();
			Set<StockVertex> TriangelSet = new HashSet<StockVertex>();
		
			for (StockVertex some:SG.getNeighbors(C1))
			{
				for (StockVertex neighborsneighbor:SG.getNeighbors(some))
				{
					if (C1 == neighborsneighbor)
						continue;
					if (	neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC &&
							SG.findEdge(C1, some) != null && 
							SG.findEdge(some, neighborsneighbor) != null)
					{																		
						if (!IndirectLinkMap.containsKey(neighborsneighbor))
						{				
							Set<StockVertex> subSet = new HashSet<StockVertex>();
							subSet.add(some);
							IndirectLinkMap.put(neighborsneighbor, subSet);
						}
						else
						{
							Set<StockVertex> subSet = IndirectLinkMap.get(neighborsneighbor);
							if (!subSet.contains(some))
							{
								subSet.add(some);
							}
						}
					} // end of IndirectLinkMap detect
					
					//detect triangle cluster
					if (SG.findEdge(C1, neighborsneighbor) != null || 
							SG.findEdge(neighborsneighbor, C1) != null)
					{
						if (	some.getType() == StockVertex.TYPE_PUBLIC)
							TriangelSet.add(some);
						if (	neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC)
							TriangelSet.add(neighborsneighbor);
					}
				}
			}
			
			
			
			///added to processingBusinessGroup --start
			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					processingBusinessGroup.indirectLinkSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{

							processingBusinessGroup.indirectLinkSet.add(some);
						}
					}
				}
			}
			
			//add direct own sub company		
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					processingBusinessGroup.directLinkOwnSet.add(subC);
				}
			}
			
			//add triangle
			for (StockVertex subC:TriangelSet)
			{
				processingBusinessGroup.triangelSet.add(subC);
			}	
			///added to processingBusinessGroup --end
			//
/*			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					subClusterSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{

							subClusterSet.add(some);
						}
					}
				}
			}
			
			//add direct own sub company		
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}
			
			//add triangle
			for (StockVertex subC:TriangelSet)
			{
				subClusterSet.add(subC);
			}	*/
			
			
			LinkedList<Set<StockVertex>> candidates = new LinkedList<Set<StockVertex>>();
			
    			//above try to find the C1's business group
    			
    			Set<StockVertex> subClusterSet = processingBusinessGroup.getAll();
    			   			
    			//below try to merge business group
    			
			// check not repeat
			for (Set<StockVertex> checkSet: clusterSet)
			{
				boolean bIsAlreadyInThisCandidate = false;
				for (Iterator<StockVertex> iter = subClusterSet.iterator(); 
					iter.hasNext() && !bIsAlreadyInThisCandidate;)
				{
					StockVertex k = (StockVertex) iter.next();					
					if (checkSet.contains(k) )
					{
						//printDebug("found");
						bIsAlreadyInThisCandidate = true;
						candidates.add(checkSet);
						//continue;
					}
				}
			}		
			
			//add subClusterset to ClusterSet
			if (candidates.size() == 0)
			{
				// no candidate to merge
				if (subClusterSet.size() > 0)
					clusterSet.add(subClusterSet);
			}
			else
			{
				// yes, find some candidates	
				if (candidates.size() == 1)
				{
					//only one candidate
					candidates.getFirst().addAll(subClusterSet);
				}
				else
				{
					// more than one candidate
					// try to merge
					clusterSet = tryToMerge(clusterSet, candidates, processingBusinessGroup);
				}		
			}
			//publicVertex.removeAll(subClusterSet);
			linkedList.removeAll(subClusterSet);
		}// end of while (!publicVertex.isEmpty())

		//printClusterInfo(clusterSet);		
        return clusterSet;
    }       
    
    public Set<Set<StockVertex>> getBussinessGroup() throws FileNotFoundException, IOException
    {
    	
    		didPara();
    		
        Set<Set<StockVertex>> clusterSet = new HashSet<Set<StockVertex>>();
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
        
        //Set<StockVertex> publicVertex = new HashSet<StockVertex>();
        Set<StockVertex> publicVertex = SG.getPublicVertices();
        //LinkedList<StockVertex> linkedList = getPublicVertexSortByDegree();
        
        int iMax = 0;
		while (!publicVertex.isEmpty())
        //while(!linkedList.isEmpty())
		{
        		StockVertex C1 = publicVertex.iterator().next();
        		//StockVertex C1 = linkedList.getFirst();
        		
			Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			//addd C1
			subClusterSet.add(C1);
			
			
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			//add direct own sub company
			
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
				if (iMax<DirectLinkOwnSet.size())
					iMax = DirectLinkOwnSet.size();
				//System.out.println(DirectLinkOwnSet.size()+"< bata");
			}
			
			
			
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 -> C2
			//      C2        some 1,2
			Map<StockVertex, Set<StockVertex>> IndirectLinkMap = new HashMap<StockVertex, Set<StockVertex>>();
			Set<StockVertex> TriangelSet = new HashSet<StockVertex>();
		
			for (StockVertex some:SG.getNeighbors(C1))
			{
				for (StockVertex neighborsneighbor:SG.getNeighbors(some))
				{
					if (C1 == neighborsneighbor)
						continue;
					if (	neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC &&
							SG.findEdge(C1, some) != null && 
							SG.findEdge(some, neighborsneighbor) != null)
					{																		
						if (!IndirectLinkMap.containsKey(neighborsneighbor))
						{				
							Set<StockVertex> subSet = new HashSet<StockVertex>();
							subSet.add(some);
							IndirectLinkMap.put(neighborsneighbor, subSet);
						}
						else
						{
							Set<StockVertex> subSet = IndirectLinkMap.get(neighborsneighbor);
							if (!subSet.contains(some))
							{
								subSet.add(some);
							}
						}
					} // end of IndirectLinkMap detect
					
					//detect triangle cluster
					if (SG.findEdge(C1, neighborsneighbor) != null || 
							SG.findEdge(neighborsneighbor, C1) != null)
					{
						if (	some.getType() == StockVertex.TYPE_PUBLIC)
							TriangelSet.add(some);
						if (	neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC)
							TriangelSet.add(neighborsneighbor);
					}
				}
			}
			
			//
			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					subClusterSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{

							subClusterSet.add(some);
						}
					}
				}
			}
			
			//add triangle
			for (StockVertex subC:TriangelSet)
			{
				subClusterSet.add(subC);
			}	
			
			boolean bIsAlreadyInCluster = false;
			Set<StockVertex> addToSet = null;
			// check not repeat
			for (Set<StockVertex> checkSet: clusterSet)
			{
				for (StockVertex k:subClusterSet)
				{
					if (checkSet.contains(k))
					{
						//printDebug("found");
						bIsAlreadyInCluster = true;
						addToSet = checkSet;
					}
				}
			}		
			//add subClusterset to ClusterSet
			if (!bIsAlreadyInCluster)
			{
				if (subClusterSet.size() > 0)
					clusterSet.add(subClusterSet);
			}
			else
			{
				for (StockVertex k:subClusterSet)
				{
					if (publicVertex.contains(k))
					//if (linkedList.contains(k))
						addToSet.add(k);
				}
			}
			publicVertex.removeAll(subClusterSet);
			//linkedList.removeAll(subClusterSet);
		}// end of while (!publicVertex.isEmpty())

		{
			//System.out.println("max "+iMax);
		}
		      	
		printClusterInfo(clusterSet);		
        return clusterSet;
    }
    public Set<StockVertex> getBussinessGroup(StockVertex targetV) throws FileNotFoundException, IOException
    {
		if (!bIsCompanyGroupOK)
		{
			companyGroupSet = getBussinessGroup();
			bIsCompanyGroupOK = true;
		}
        
        Set<StockVertex> retSet = null;
        for (Set<StockVertex> c:companyGroupSet)
        {     
        		if (c.contains(targetV))
        		{
        			retSet =  c;
        			System.out.println(c);
        		}
        }
        return retSet;
    }
  
    public Set<StockVertex> getBussinessGroup(String targetCode) throws FileNotFoundException, IOException
    {
    		if (!bIsCompanyGroupOK)
    		{
    			companyGroupSet = getBussinessGroup();
    			bIsCompanyGroupOK = true;
    		}
    			
        Set<StockVertex> retSet = null;
        for (Set<StockVertex> c:companyGroupSet)
        {     
        		for (StockVertex v:c)
        		{
        			if (v.getCode().compareToIgnoreCase(targetCode) == 0)
        				retSet = c;
        		}
        }
        return retSet;
    }    
    
    public Map<StockVertex,Set<StockVertex>> getCompanySoftingGroup() throws FileNotFoundException, IOException
	{
    		
    		didPara();
    		
    		Map<StockVertex,Set<StockVertex>> clusterSet = new HashMap<StockVertex,Set<StockVertex>>();
     	
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
		
		//Set<StockVertex> publicVertex = new HashSet<StockVertex>();
		//Set<StockVertex> publicVertex = SG.getPublicVertices();
    		LinkedList<StockVertex> linkedList = getPublicVertexSortByDegree();
		
		//while (!publicVertex.isEmpty())
    		while (!linkedList.isEmpty())
		{
			//StockVertex C1 = publicVertex.iterator().next();
    			StockVertex C1 = linkedList.getFirst();
			Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			
			
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			//add direct own sub company
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}
			
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 -> C2
			//      C2        some 1,2
			Map<StockVertex, Set<StockVertex>> IndirectLinkMap = new HashMap<StockVertex, Set<StockVertex>>();
			
			for (StockVertex some:SG.getNeighbors(C1))
			{
				for (StockVertex neighborsneighbor:SG.getNeighbors(some))
				{
					if (C1 == neighborsneighbor)
						continue;
					// start of IndirectLinkMap detect
					if (		neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC &&
							SG.findEdge(C1, some) != null && 
							SG.findEdge(some, neighborsneighbor) != null)
					{																		
						if (!IndirectLinkMap.containsKey(neighborsneighbor))
						{				
							Set<StockVertex> subSet = new HashSet<StockVertex>();
							subSet.add(some);
							IndirectLinkMap.put(neighborsneighbor, subSet);
						}
						else
						{
							Set<StockVertex> subSet = IndirectLinkMap.get(neighborsneighbor);
							if (!subSet.contains(some))
							{
								subSet.add(some);
							}
						}
					} // end of IndirectLinkMap detect
			
				}//end of for (StockVertex neighborsneighbor:sg.getNeighbors(some))
			}//end of for (StockVertex some:sg.getNeighbors(C1))
			
			//
			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					subClusterSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{					
							subClusterSet.add(some);
						}
					}
				}
			}
			
			clusterSet.put(C1,subClusterSet);
			//publicVertex.remove(C1);
			linkedList.remove(C1);
		}// end of while (!publicVertex.isEmpty())
		
		printClusterInfo(clusterSet);	
						
		return clusterSet;
	}
    
    Map<StockVertex,Set<StockVertex>> getCompanySoftingGroupOld() throws FileNotFoundException, IOException
	{
    		
    		didPara();
    		
    		Map<StockVertex,Set<StockVertex>> clusterSet = new HashMap<StockVertex,Set<StockVertex>>();
     	
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
		
		//Set<StockVertex> publicVertex = new HashSet<StockVertex>();
		Set<StockVertex> publicVertex = SG.getPublicVertices();
		
		while (!publicVertex.isEmpty())
		{
			StockVertex C1 = publicVertex.iterator().next();
			Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			
			
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			//add direct own sub company
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}
			
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 -> C2
			//      C2        some 1,2
			Map<StockVertex, Set<StockVertex>> IndirectLinkMap = new HashMap<StockVertex, Set<StockVertex>>();
			
			for (StockVertex some:SG.getNeighbors(C1))
			{
				for (StockVertex neighborsneighbor:SG.getNeighbors(some))
				{
					if (C1 == neighborsneighbor)
						continue;
					// start of IndirectLinkMap detect
					if (		neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC &&
							SG.findEdge(C1, some) != null && 
							SG.findEdge(some, neighborsneighbor) != null)
					{																		
						if (!IndirectLinkMap.containsKey(neighborsneighbor))
						{				
							Set<StockVertex> subSet = new HashSet<StockVertex>();
							subSet.add(some);
							IndirectLinkMap.put(neighborsneighbor, subSet);
						}
						else
						{
							Set<StockVertex> subSet = IndirectLinkMap.get(neighborsneighbor);
							if (!subSet.contains(some))
							{
								subSet.add(some);
							}
						}
					} // end of IndirectLinkMap detect
			
				}//end of for (StockVertex neighborsneighbor:sg.getNeighbors(some))
			}//end of for (StockVertex some:sg.getNeighbors(C1))
			
			//
			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					subClusterSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{					
							subClusterSet.add(some);
						}
					}
				}
			}
			
			clusterSet.put(C1,subClusterSet);
			publicVertex.remove(C1);
		}// end of while (!publicVertex.isEmpty())
		
		printClusterInfo(clusterSet);	
						
		return clusterSet;
	}
    public Set<StockVertex> getCompanySoftingGroup(StockVertex targetV) throws FileNotFoundException, IOException
    {
    		if (!bIsCompanySoftingGroupOk)
		{
    			companySoftlyGrouping = getCompanySoftingGroup();
    			bIsCompanySoftingGroupOk = true;
		}
    	
       // Map<StockVertex,Set<StockVertex>> clusterSet = getCompanySoftingGroup();
        Set<StockVertex> retSet = companySoftlyGrouping.get(targetV);
        return retSet;
    }
    public Set<StockVertex> getCompanySoftingGroup(String targetCode) throws FileNotFoundException, IOException
    {
    		if (!bIsCompanySoftingGroupOk)
    		{
			companySoftlyGrouping = getCompanySoftingGroup();
			bIsCompanySoftingGroupOk = true;
    		}
       // Map<StockVertex,Set<StockVertex>> clusterSet = getCompanySoftingGroup();
        Set<StockVertex> retSet = null;
        for (StockVertex key:companySoftlyGrouping.keySet())
        {
        		//String code = key.getCode();
			//if (code.length() == 4)
			{
        			//if (Integer.valueOf(code).intValue() == targetCode)
				//if (key.getCode().equals(targetCode))
				if (key.getCode().compareToIgnoreCase(targetCode) == 0)
        				retSet = companySoftlyGrouping.get(key);
			}
        }
        return retSet;
    }  
    public StockUndirectedGraph getStakeCompanyNet()
    {
    		return getSummarizedGraph();
    }

    
    public StockUndirectedGraph getSummarizedGraph()
	{
    	
    		StockUndirectedGraph retGraph= new StockUndirectedGraph(); 	
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
		Set<StockVertex> publicVertex = SG.getPublicVertices();
		
		while (!publicVertex.isEmpty())
		{
			
			StockVertex C1 = publicVertex.iterator().next();
			Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				//for (StockVertex some:SG.getSuccessors(C1))
				for (StockVertex some:SG.getNeighbors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			//add direct own sub company
			//if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}
			
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 ->some 3.....-> C2
			//      
			Buffer<StockVertex> queue = new UnboundedFifoBuffer<StockVertex>();
			subClusterSet.add(C1);
			Set<StockVertex> addedSet = new HashSet <StockVertex>();	
			for (StockVertex some:SG.getNeighbors(C1))
			{
				addedSet.add(some);
				if (some.getType() != StockVertex.TYPE_PUBLIC)
				{
					queue.add(some);				
				}
				else if (some.getType() == StockVertex.TYPE_PUBLIC)
				{
					subClusterSet.add(some);
				}				
				
				while (!queue.isEmpty())
				{
					StockVertex processing = queue.remove();
					for (StockVertex c:SG.getNeighbors(processing))
					{
						addedSet.add(c);
						if (c.getType() != StockVertex.TYPE_PUBLIC)
						{
							if (!addedSet.contains(c))
								queue.add(c);
						}
						else if (c.getType() == StockVertex.TYPE_PUBLIC)
						{
							subClusterSet.add(c);
						}
					}
				}
			}//end of for (StockVertex some:sg.getNeighbors(C1))
			
						
			retGraph.addVertex(C1);
			//clusterSet.put(C1,subClusterSet);
			for (StockVertex v:subClusterSet)
			{
				if (!C1.equals(v))
				{
					retGraph.addVertex(v);
					retGraph.addNewEdge(C1, v, 0, StockEdge.TYPE_UNKNOWN);
				}
			}
			//publicVertex.remove(C1);
			publicVertex.removeAll(subClusterSet);
		}// end of while (!publicVertex.isEmpty())
		
		//printClusterInfo(clusterSet);	
						
		return retGraph;
	}  
    public StockUndirectedGraph getStakePersonNet()
    {
    		return getNonPublicSummarizedGraph(StockVertex.TYPE_UNKNOWN,StockVertex.TYPE_PUBLIC);
    }
    
   
    public StockUndirectedGraph getNonPublicSummarizedGraph(int targetNodeType, int middleNodeType)
	{
    	//	if (targetNodeType == middleNodeType)
    	//		return null;
    	
		StockUndirectedGraph retGraph= new StockUndirectedGraph(); 	
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
		Set<StockVertex> nonpublicVertex = SG.getNonPublicVertices();
		//Set<StockVertex> nonpublicVertex = SG.getPublicVertices();
		
/*		for (StockVertex some:nonpublicVertex)
		{
			if (some.getType() == StockVertex.TYPE_UNKNOWN)
				System.out.println(some);
		}*/
		
		while (!nonpublicVertex.isEmpty())
		{
			
			StockVertex C1 = nonpublicVertex.iterator().next();
			Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
			
			//ex:  P1->P3 or P2->P1
			//  		P2 p3 
/*			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PEOPLE)
						DirectLinkOwnSet.add(some);
				}
				for (StockVertex some:SG.getPredecessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PEOPLE)
						DirectLinkOwnSet.add(some);
				}
			}
			//add direct own sub company
			//if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}*/
			
			for (StockVertex some:SG.getNeighbors(C1))
			{
				if (some.getType() == targetNodeType)
					subClusterSet.add(some);
			}
			
			
			// ex : P1 <- some 1 -> P2  
			//      P1 <- some 2 ->some 3.....-> P2
			//      
			Buffer<StockVertex> queue = new UnboundedFifoBuffer<StockVertex>();
			subClusterSet.add(C1);
			Set<StockVertex> addedSet = new HashSet <StockVertex>();	
			for (StockVertex some:SG.getNeighbors(C1))
			{
				addedSet.add(some);
				if (some.getType() == middleNodeType)
				{
					queue.add(some);				
				}
				else if (some.getType() == targetNodeType)
				{
					subClusterSet.add(some);
				}				
				
				while (!queue.isEmpty())
				{
					StockVertex processing = queue.remove();
					for (StockVertex c:SG.getNeighbors(processing))
					{
						addedSet.add(c);
						if (c.getType() == middleNodeType)
						{
							if (!addedSet.contains(c))
								queue.add(c);
						}
						else if (c.getType() == targetNodeType)
						{
							subClusterSet.add(c);
						}
					}
				}
			}//end of for (StockVertex some:sg.getNeighbors(C1))
			
						
			retGraph.addVertex(C1);
			//clusterSet.put(C1,subClusterSet);
			for (StockVertex v:subClusterSet)
			{
				if (!C1.equals(v))
				{
					retGraph.addVertex(v);
					retGraph.addNewEdge(C1, v, 0, StockEdge.TYPE_UNKNOWN);
				}
			}
			//publicVertex.remove(C1);
			nonpublicVertex.removeAll(subClusterSet);
		}// end of while (!publicVertex.isEmpty())
		
		//printClusterInfo(clusterSet);	
						
		return retGraph;
	}      
  /*  public Graph<StockVertex,Number> getSummaryGrpha() throws FileNotFoundException, IOException
	{
    	
    		Graph<StockVertex, Number> retGraph= new UndirectedSparseGraph<StockVertex, Number>();
   	
    		//Map<StockVertex,Set<StockVertex>> clusterSet = new HashMap<StockVertex,Set<StockVertex>>();
     	
		/////////////////////////////////////////////////////
		//
		// clustering
		//
		////////////////////////////////////////////////////
		
		//Set<StockVertex> publicVertex = new HashSet<StockVertex>();
		Set<StockVertex> publicVertex = SG.getPublicVertices();
		
		while (!publicVertex.isEmpty())
		{
			StockVertex C1 = publicVertex.iterator().next();
			Set<StockVertex> subClusterSet = new HashSet<StockVertex>();
				
			//ex:  C1->C3
			//  		C3
			Set<StockVertex> DirectLinkOwnSet = new HashSet<StockVertex>();
			if (bCountDirectOwnCompany ==  true)
			{
				for (StockVertex some:SG.getSuccessors(C1))
				{
					if (some.getType() == StockVertex.TYPE_PUBLIC)
						DirectLinkOwnSet.add(some);
				}
			}
			//add direct own sub company
			if (DirectLinkOwnSet.size() < iBeta)
			{
				for (StockVertex subC:DirectLinkOwnSet)
				{
					subClusterSet.add(subC);
				}
			}
			
			
			// ex : C1 <- some 1 -> C2  
			//      C1 <- some 2 -> C2
			//      C2        some 1,2
			Map<StockVertex, Set<StockVertex>> IndirectLinkMap = new HashMap<StockVertex, Set<StockVertex>>();
			
			for (StockVertex some:SG.getNeighbors(C1))
			{
				for (StockVertex neighborsneighbor:SG.getNeighbors(some))
				{
					if (C1 == neighborsneighbor)
						continue;
					// start of IndirectLinkMap detect
					if (		neighborsneighbor.getType() == StockVertex.TYPE_PUBLIC &&
							SG.findEdge(C1, some) != null && 
							SG.findEdge(some, neighborsneighbor) != null)
					{																		
						if (!IndirectLinkMap.containsKey(neighborsneighbor))
						{				
							Set<StockVertex> subSet = new HashSet<StockVertex>();
							subSet.add(some);
							IndirectLinkMap.put(neighborsneighbor, subSet);
						}
						else
						{
							Set<StockVertex> subSet = IndirectLinkMap.get(neighborsneighbor);
							if (!subSet.contains(some))
							{
								subSet.add(some);
							}
						}
					} // end of IndirectLinkMap detect
			
				}//end of for (StockVertex neighborsneighbor:sg.getNeighbors(some))
			}//end of for (StockVertex some:sg.getNeighbors(C1))
			
			//
			for (StockVertex C2:IndirectLinkMap.keySet())
			{
				Set<StockVertex> subSet = IndirectLinkMap.get(C2);
				if (subSet.size() >= iAlpha)
				{
					subClusterSet.add(C2);
					for (StockVertex some:subSet)
					{
						if (some != null && some.getType() == StockVertex.TYPE_PUBLIC)
						{					
							subClusterSet.add(some);
						}
					}
				}
			}
			
			retGraph.addVertex(C1);
			//clusterSet.put(C1,subClusterSet);
			for (StockVertex v:subClusterSet)
			{
				retGraph.addVertex(v);
				retGraph.addEdge(retGraph.getEdgeCount()+1, C1, v);
			}
			publicVertex.remove(C1);
		}// end of while (!publicVertex.isEmpty())
		
		//printClusterInfo(clusterSet);	
						
		return retGraph;
	}*/
    
    
    
    
}
