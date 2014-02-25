import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ComparableDijkstraPath<E extends Edge> implements Comparable<Object>{
    protected double totalWeight;
    protected LinkedList<E> path;

    public ComparableDijkstraPath(){
        totalWeight = 10000000; //TODO: find a good random value.
        path = new LinkedList<E>();
    }
    public ComparableDijkstraPath(int weight){
        totalWeight = weight;
        path = new LinkedList<E>();
    }

    public ComparableDijkstraPath(ComparableDijkstraPath clone){
        this.totalWeight = clone.totalWeight;
        this.path = (LinkedList<E>)clone.path.clone();
    }

    public void addEdge(E e){
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
    public Iterator<E> iterator(){
        return path.iterator();
    }
}
