import java.util.*;

/**
 * Partly representing a graph. This class holds only edges of a graph.
 * It can find shortest paths between node A and node B.
 * It can find the minimal spanning tree of a graph.
 *
 * @author Group 21: Gabriel Andersson (911010-4131), Markus Pettersson (900326-4257)
 * @version (140225)
 */
public class DirectedGraph<E extends Edge> {
    /**
     * a value to represent something near positive infinity.
     */
    public final double CLOSE_TO_INF = 1000000000;
    // Number of nodes in graph
	private int noOfNodes;
    // Queue to quickly find the edges with smallest weight.
	private PriorityQueue<E> allEdges;
    // List holding edges. Each slot represents a node
    private LinkedList<Edge>[] nodeEdges;

    /**
     * Creates a DirectedGraph
     * @param noOfNodes the amount of nodes in the graph
     */
	public DirectedGraph(int noOfNodes) {
		this.noOfNodes = noOfNodes;
		allEdges = new PriorityQueue<E>(1, new EdgeComparator());
        nodeEdges = new LinkedList[noOfNodes];
	}

    /**
     * Adding an edge makes it considerable in this DirectedGraph
     *
     * @param e edge to add to the DirectedGraph. if <tt>e</tt> is invalid or null it will not be added.
     */
	public void addEdge(E e) {
		if (e == null || e.from < 0 || e.to < 0 || e.from >= noOfNodes || e.to >= noOfNodes){
			return;
        }
        if (!allEdges.contains(e)){
                allEdges.add(e);
                if(nodeEdges[e.from] == null){
                    nodeEdges[e.from] = new LinkedList<Edge>();
                }
                nodeEdges[e.from].add(e);
            }
	}

    /**
     * Expects graph to be complete.
     * If there is a path between the two nodes, this method will return the shortest one.
     *
     * @param from the node to travel from
     * @param to the node to reach as fast as possible
     * @return Iterator with all edges that represent the shortest path.
     * returns <tt>null</tt> if no path shorter than <tt>CLOSE_TO_INF</tt> is found.
     */
	public Iterator<E> shortestPath(int from, int to) {
        //parameter check
        if (from < 0 || to < 0 || from >= noOfNodes || to >= noOfNodes){ return null; }
        DijkstraPath[] paths = new DijkstraPath[noOfNodes];
        //goal position, cost = 0.
        paths[to] = new DijkstraPath();
        //Initiate recursion
        paths = sp(from, paths);
        //If no path is found, return null. Different from path between A to A.
        if (paths[from].getTotalWeight() < CLOSE_TO_INF){
            return paths[from].iterator();
        }
        return null;
   	}

    /*
     * Recursive help function.
     *
     * @return the updated set of paths.
     */
    private DijkstraPath[] sp(int from, DijkstraPath[] paths){
        // Initiate path from here.
        if(paths[from] == null){
            paths[from] = new DijkstraPath(CLOSE_TO_INF);
        }
        // All edges from this node.
        for (Edge e : nodeEdges[from]){
            //recursion call if not already made.
            if(paths[e.to] == null){
                paths = sp(e.to, paths);
            }
            // If new path is better, pick that one.
            if (paths[from].getTotalWeight() > e.getWeight() + paths[e.to].getTotalWeight()){
                paths[from] = new DijkstraPath();
                //Add edges in correct order.
                paths[from].addEdge(e);
                Iterator it = paths[e.to].iterator();
                while(it.hasNext()){
                    paths[from].addEdge((E)it.next());
                }
            }
        }
        return paths;
    }


    /**
     *  Creates a Minimal Spanning Tree of graph.
     *
     * @return An Iterator with the MST of the graph. Returns <tt>null</tt> if no complete MST can be found.
     */
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
					break;
				}
			}
		}
        //Check to see if there is more than one tree.
        if(nodes[0] == -1 * noOfNodes){
            return mst.iterator();
        }
		return null;
	}

    /*
     * Comparator used to order priority queue in order of edge weight.
     *
     */
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
