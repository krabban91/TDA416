import java.util.ArrayList;
import java.util.LinkedList;

public class ComparableDijkstraPath implements Comparable<Object>{
    protected double totalWeight;
    protected LinkedList<Edge> path;

    public ComparableDijkstraPath(){
        totalWeight = 0;
        path = new LinkedList<Edge>();
    }

    public ComparableDijkstraPath(ComparableDijkstraPath clone){
        this.totalWeight = clone.totalWeight;
        this.path = (LinkedList<Edge>)clone.path.clone();
    }

    public void addEdge(Edge e){
        totalWeight += e.getWeight();
        path.add(e);
    }

    @Override
    public int compareTo(Object o) {
        if(o.getClass() == this.getClass()){
            ComparableDijkstraPath cmp = (ComparableDijkstraPath) o;
            if(this.totalWeight > cmp.totalWeight){
                return 1;
            }
            if(this.totalWeight < cmp.totalWeight){
                return -1;
            }
        }
        return 0;
    }
}
