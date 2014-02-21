import java.util.*;

public class DirectedGraph<E extends Edge> {

	private int noOfNodes;
	private PriorityQueue<E> allEdges;
    private ComparableDijkstraPath[] Dpath;

	public DirectedGraph(int noOfNodes) {
		this.noOfNodes =noOfNodes;
        Dpath = new ComparableDijkstraPath[noOfNodes];
		allEdges = new PriorityQueue<E>(1, new EdgeComparator());
	}

	public void addEdge(E e) {
		if (e == null)
			return;
		if (!allEdges.contains(e))
			allEdges.add(e);
	}
    private ComparableDijkstraPath calculate(int i, ComparableDijkstraPath path){
        if(Dpath[i] == null){
            Dpath[i] = new ComparableDijkstraPath();
        }
        //
        //Find edges of i, see of Dpath[edges.to]==null, calculate
        //If node has path to end, return cost
        //Else return min of paths from (calculate+cost)s

        return null;
    }

	public Iterator<E> shortestPath(int from, int to) {
        Dpath[to] = new ComparableDijkstraPath();
        for (int i = 0; i < noOfNodes;i++){
            Dpath[i] = calculate(i, Dpath[i]);
        }



        //Producera nodlista som är nåbar
        //init av fastestpathcost[] och path[]
        // pathfrom

        //funcsp(){
        //}
        // get path of from, , , , to./*

        //jmf fp[något] och cost+fp[annat]
        //lagra p[snabbast]

        //return p[from].iterator()
        /*
          * EL lista för noder.
              fastestpathstoto[]
          *   index = from
          *   fptt[from] ... fptt[from] - ==from.fptt[new]
          */

        return null;
	}
    //int[]jensa =
    //int[]
    private double sp(int from, int to){
        //jensa[from] = min(indcsandajskdas, djdljdsakl)
        /*

            if(jensa[from] > costof(b) + jensa[b])
                jensa[from] = costof(b)+jensa[b];
                path[from]=b;
         */

        return 0;
    }

	
//TODO: Javadocka mig.
	public Iterator<E> minimumSpanningTree() {
		int[] nodes = new int[noOfNodes];
		for (int i = 0; i < noOfNodes; i++) {
			nodes[i] = -1;
		}
		Iterator<E> it = allEdges.iterator();
		ArrayList<E> mst = new ArrayList<E>();
		
		//Go through edges.
		while (it.hasNext()){
			E edge = it.next();
			int src = edge.from, goal = edge.to;			
			while(nodes[src] >= 0 || nodes[goal] >= 0){
				if (src == goal) {
					break;
				} else if (nodes[src] > nodes[goal]){
					src = nodes[src];
				} else {
					goal = nodes[goal];
				} 
			}
			//Adding an edge to system. 
			if (src != goal){
				mst.add(edge);
				if(src < goal){							
					nodes[src] += nodes[goal];
					nodes[goal] = src; 			
				} else {
					nodes[goal] += nodes[src];
					nodes[src] = goal; 		
				}
				if (mst.size() == noOfNodes-1){
					return mst.iterator();
				}
			}
		}
		//There is no mst.
		return null;
	}

	private class EdgeComparator implements Comparator<E> {

		@Override
		public int compare(E o1, E o2) {
			double val1 = o1.getWeight(), val2 = o2.getWeight();
			if (val1 < val2)
				return 1;
			return val1 == val2 ? 0 : -1;
		}
	}

}
