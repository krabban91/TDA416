import java.util.*;

public class DirectedGraph<E extends Edge> {

	private int noOfNodes;
	private PriorityQueue<E> allEdges;

	public DirectedGraph(int noOfNodes) {
		this.noOfNodes =noOfNodes;
		
		allEdges = new PriorityQueue<>(1, new EdgeComparator());
	}

	public void addEdge(E e) {
		if (e == null)
			return;
		if (!allEdges.contains(e))
			allEdges.add(e);
	}

	public Iterator<E> shortestPath(int from, int to) {
		return null;
	}
	
//TODO: Javadocka mig.
	public Iterator<E> minimumSpanningTree() {
		int[] nodes = new int[noOfNodes];
		for (int i = 0; i < noOfNodes; i++) {
			nodes[i] = -1;
		}
		Iterator<E> it = allEdges.iterator();
		ArrayList<E> mst = new ArrayList<>();
		
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
