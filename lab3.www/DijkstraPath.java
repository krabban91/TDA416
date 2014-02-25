import java.util.Iterator;
import java.util.LinkedList;

/**
 * Representing a Dijkstra path used to find fastest path from point A to point B.
 *
 * @author Group 21: Gabriel Andersson (911010-4131), Markus Pettersson (900326-4257)
 * @version (140225)
 */
public class DijkstraPath<E extends Edge>{
    //The total weight of the full path
    private double totalWeight;
    //The edges representing the path
    private LinkedList<E> path;

    /**
     * Creates a DijkstraPath with weight 0 and no edges.
     */
    public DijkstraPath(){
        totalWeight = 0;
        path = new LinkedList<E>();
    }

    /**
     * Creates a DijkstraPath with a certain weight and no edges.
     *
     * @param weight desired initial total weight of path.
     */
    public DijkstraPath(double weight){
        totalWeight = weight;
        path = new LinkedList<E>();
    }

    /**
     * Gets the total weight of the path.
     * @return <tt>totalweight</tt>
     */
    public double getTotalWeight(){
        return totalWeight;
    }

    /**
     * Adds the edge to the path and increases total weight.
     * @param e edge to add.
     */
    public void addEdge(E e){
        totalWeight += e.getWeight();
        path.add(e);
    }

    public Iterator<E> iterator(){
        return path.iterator();
    }
}
