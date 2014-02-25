import java.util.*;

public class DirectedGraph<E extends Edge> {
    int calls= 0;
	private int noOfNodes;
	private PriorityQueue<E> allEdges;

    //private ComparableDijkstraPath[] Dpath;

	public DirectedGraph(int noOfNodes) {
		this.noOfNodes =noOfNodes;
       // Dpath = new ComparableDijkstraPath[noOfNodes];
		allEdges = new PriorityQueue<E>(1, new EdgeComparator());
	}

	public void addEdge(E e) {
		if (e == null)
			return;
		if (!allEdges.contains(e))
			allEdges.add(e);
	}

    //TODO: JAVADOCKA MIG DÅ!
	public Iterator<E> shortestPath(int from, int to) {
        if (from <0 || to <0 || from >= noOfNodes || to >= noOfNodes){ return null; }
        LinkedList<Edge>[] EL = new LinkedList[noOfNodes];
        ComparableDijkstraPath[] paths = new ComparableDijkstraPath[noOfNodes];
        for (int i = 0; i <noOfNodes; i++){
            EL[i] = new LinkedList<Edge>();
            for(Edge e : allEdges){
                if(e.from == i){
                    EL[i].add(e);
                } //TODO: Effektivisera mig.
            }
        }
        //goal position, cost = 0.
        paths[to] = new ComparableDijkstraPath(0);
        paths = sp(from, to, paths, EL);
        return paths[from].iterator();
   	}
    //TODO: DOCKA MIG! (köt eller?)
    private ComparableDijkstraPath[] sp(int from, int to, ComparableDijkstraPath[] paths, LinkedList<Edge>[] EL){
        if(paths[from] == null){
            paths[from] = new ComparableDijkstraPath();
        }
        for (Edge e : EL[from]){
            if(paths[e.to] == null){
                paths = sp(e.to, to, paths, EL);
            }
            if (paths[from].totalWeight > e.getWeight() + paths[e.to].totalWeight){
                paths[from] = new ComparableDijkstraPath(paths[e.to]);
                paths[from].addEdge(e);
            }
        }
        return paths;
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
