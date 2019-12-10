import java.util.Comparator;

public class Node implements Comparable<Node> {

    private int g;
    private int h;
    private int f;
    private Node prev;
    private Point position;

    public Node (int g, int h, int f, Point position, Node prev){
        this.g = g;
        this.h = h;
        this.f = f;
        this.prev = prev;
        this.position = position;
    }


    public int getG(){
        return g;
    }


    public int getF(){
        return f;
    }

    public void setPosition(Point p){
        position = p;
    }

    public Point getPosition(){
        return position;
    }

    public Node getPrevNode(){
        return prev;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.getF(), o.getF());

    }
}
