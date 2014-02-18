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

	public Iterator<E> minimumSpanningTree() {
		int[] nodes = new int[noOfNodes];
		for (int i = 0; i < noOfNodes; i++) {
			nodes[i] = -1;
		}
		Iterator<E> it = allEdges.iterator();
		ArrayList<E> mst = new ArrayList<>();
		
		//Go through edges.
		/*
		 * This will go on until nodes[src]==nodes[goal] or both are negative values. 
		 * if goal != src, 
		 */
		while (it.hasNext()){
			E edge = it.next();
			int src = edge.from, goal = edge.to;
			
			while(nodes[src] >= 0 || nodes[goal] >= 0){
				if (nodes[src] > nodes[goal]){
					src = nodes[src];
				} else if (nodes[goal] > nodes[src]){
					goal = nodes[goal];
				} else {
					break;
				}
				System.out.println(edge.to);
				System.out.println("nu du knut.");
			}
			
			//Adding an edge to system. 
			if (src != goal){
				mst.add(edge);
				nodes[edge.from] = edge.to;	//Adding edge in nodes[].
				if(src < goal){
					nodes[src] += nodes[goal];
					//nodes[goal] = src; 			//Not here since no real edge here.
				} else {
					nodes[goal] += nodes[src];
					// nodes[src] = goal; 		//Not here since no real edge here.
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
