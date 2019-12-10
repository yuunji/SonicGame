import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new LinkedList<>();
        Node currentNode = null;
        Queue<Node> openQueue = new PriorityQueue<>();
        Map<Point,Node> open = new HashMap<>();
        Map<Point,Node> closed = new HashMap<>();

        openQueue.add(new Node(0,start.manhattan(end),start.manhattan(end),start,null));
        while(!openQueue.isEmpty()){
            currentNode = openQueue.remove();

            if(withinReach.test(currentNode.getPosition(),end)){
                while(currentNode.getPrevNode() != null){
                    path.add(currentNode.getPosition());
                    currentNode = currentNode.getPrevNode();
                }
                Collections.reverse(path);
                return(path);
            }

            List<Point> neighbors = CARDINAL_NEIGHBORS.apply(currentNode.getPosition()).filter(canPassThrough)
                    .filter(pt -> !pt.equals(start) && !pt.equals(end)).collect(Collectors.toList());
            for(Point p: neighbors){
                if(!closed.containsKey(p)){

                    int newG = currentNode.getG()+1;
                    int newH = p.manhattan(end);
                    int newF = newG + newH;

                    if(open.containsKey(p)){
                        if(newG < open.get(p).getG()){
                            Node betterG = new Node(newG,newH,newF,p,currentNode);
                            openQueue.remove(open.get(p));
                            openQueue.add(betterG);
                            open.replace(p,betterG);
                        }
                    }

                    else{
                        Node addThis = new Node(newG,newH,newF,p,currentNode);
                        openQueue.add(addThis);
                        open.put(p,addThis);
                    }
                }
                //add to closed map
                closed.put(currentNode.getPosition(),currentNode);
            }

        }
        return path;

    }
}
