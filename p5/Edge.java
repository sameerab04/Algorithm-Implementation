/* ************************************
 * Class representing the Edges     *
 ************************************
 */

 import java.io.*;
 import java.text.DecimalFormat;
 import java.util.*;

  public class Edge {
    double cost;
    int distance;
    int end;
    int start;

    public Edge(int s, int e, int d, double c) {
        this.start = s;
        end = e;
        distance = d;
        cost = c;
    }

    public double getCost() {
     return cost;
    }

    public int getDist() {
        return distance;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    /**
     * Return the vertex where this edge begins.
     */
    public int from() {
        return start;
    }

    /**
     * Return the vertex where this edge ends.
     */
    public int to() {
        return end;
    }

    /**
     * Return the weight of this edge.
     */
    public double distance() {
        return distance;
    }

    /**
     * Return the endpoint of this edge that is different from the given vertex
     * (unless a self-loop).
     */
    public int other(int vertex) {
        if (vertex == start) return end;
        else if (vertex == end) return start;
        else throw new RuntimeException("Illegal endpoint");
    }
}
