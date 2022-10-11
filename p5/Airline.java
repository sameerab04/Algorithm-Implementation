import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
<h1> Airline Program</h1>
The Airline Program implements an application that creates an undirected graph
as an adjacency list, creates a minimum spanning tree (based on distance), allows
the user to calculate the shortest path between two locations based on distance, cost
or hops. Given a price limit, the program will output all possible trips less than or
equal to the price limit. The program allows the user to add or remove a route from the
schedule.

@author Sameera Boppana
@version 1.0
@since 23 July 2020
*/
public class Airline {
     static String[] locations;
     static LinkedList<Edge>[] adjList;
     static int V;
     static boolean[] marked;  // marked[v] = is there an s-v path
     static Edge[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
     static double[] distTo;      // distTo[v] = number of edges shortest s-v path
     static double[] costTo;
     static double[] hopsTo;
     static IndexMinPQ<Double> pq;    // priority queue of vertices
     static File file;

     /**
      Creates an undirected graph based on the inputted file.
      Graph is represented with an adjacency list of vertices.
      The method loops through the file and obtains the total number of vertices,
      the location names, and all the possible routes.

      @param fname name of the inputted file used to create the graph
     */
     public Airline(String fname) {
            try {
                file = new File(fname);
                Scanner reader = new Scanner(file);
                int size = Integer.parseInt(reader.nextLine());
                locations = new String[size];
                V = locations.length;
                adjList = (LinkedList<Edge>[]) new LinkedList<?>[size];

                for (int i = 0; i < V; i++) {
                    locations[i] = reader.nextLine();
                }
                while (reader.hasNextLine()) {
                    //reads in the current route
                    String route = reader.nextLine();
                    String[] elements = route.split(" ");
                    int loc_1 = Integer.parseInt(elements[0]);
                    int loc_2 = Integer.parseInt(elements[1]);
                    int dist = Integer.parseInt(elements[2]);
                    double cost = Double.parseDouble(elements[3]);

                    //populating adjacency list
                    Edge forwardEdge = new Edge((loc_1 - 1), (loc_2 - 1), dist, cost);
                    Edge reverseEdge = new Edge((loc_2 - 1), (loc_1 - 1), dist, cost);
                    if (adjList[loc_1 - 1] == null) {
                        adjList[loc_1 - 1] = new LinkedList<>();
                    }
                    if (adjList[loc_2 - 1] == null) {
                        adjList[loc_2 - 1] = new LinkedList<>();
                    }
                    adjList[loc_1 - 1].add(forwardEdge);
                    adjList[loc_2 - 1].add(reverseEdge);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Cannot find file");
            }
        }
      /**
        Prints out all the direct routes by looping through the undirected graph
      */
        public static void displayDirectRoutes(){
            for(int i=0; i<adjList.length; i++){
                for(int j=0; j<adjList[i].size(); j++){
                    System.out.println(locations[adjList[i].get(j).getStart()] + "," + locations[adjList[i].get(j).getEnd()] + " : " +
                        adjList[i].get(j).getDist() + " miles "  + adjList[i].get(j).getCost());
                }
            }
        }
        /**
          Creates and displays a minimum spanning tree of the graph based on distance.
          This method loops through all the vertices to find the minimum spanning forest.
        */
        public static void EagerPrim() {
               edgeTo = new Edge[V];
               distTo = new double[V];
               marked = new boolean[V];
               pq = new IndexMinPQ<Double>(V);
               for (int v = 0; v < V; v++) {
                   distTo[v] = Double.POSITIVE_INFINITY;
               }

               for (int v = 0; v < V; v++)    {
                   if (!marked[v]) {
                       prim(v);      // minimum spanning forest
                   }
               }
           }

           /**
              Run's the Eager Prim algorithm from vertex s
              The method inserts the current vertex and distance into the priority queue
              and deletes the vertex with the smallest weight.
              @param s vertex to start at

           */
           public static void prim(int s) {
               distTo[s] = 0.0;
               pq.insert(s, distTo[s]);
               while (!pq.isEmpty()) {
                   int v = pq.delMin();
                   scan(v);
               }
           }

           /**
            Method scans vertex v. Loops through all the edges of v
            Makes necessary changes to the priority queue. If the current
            edge weight is less than the weight of the same vertex that is already
            in the priorty queue, the current edge weight overwrites the previous weight.

            @param v current certex
           */
           public static void scan( int v) {
               marked[v] = true;
               for (Edge e : adj(v)) {
                   int w = e.other(v);
                   if (marked[w])
                   {
                       continue;         // v-w is obsolete edge
                   }
                   if (e.distance() < distTo[w]) {
                       distTo[w] = e.distance();
                       edgeTo[w] = e;
                       if (pq.contains(w))
                       {
                           pq.change(w, distTo[w]);
                       }
                       else
                       {
                           pq.insert(w, distTo[w]);
                       }
                   }
               }
           }

          /**
          *  Returns an Iterable Object of Edges for a minimum spanning tree
          *  @return Iterable<Edge> all edges in the minimum spanning tree
          *  @see Edge
          */
           public Iterable<Edge> edges() {
               LinkedList<Edge> mst = new LinkedList<Edge>();
               for (int v = 0; v < edgeTo.length; v++) {
                   Edge e = edgeTo[v];
                   if (e != null) {
                       mst.add(e);
                   }
               }
               return mst;
           }

        /**
          Returns the index for a given location.
          The method loops through th the array of locations
          and checks for string equality.

          @param s String representation of a location
          @return int index for location
        */
        public static int getIndex(String s) {
            int vertex = 0;
            for (int i = 0; i < V; i++) {
                if (locations[i].equals(s)) {
                    vertex = i;
                }
            }
            return vertex;
        }

        /**
          Returns an Iterable<Edge> for a given vertex in the graph
          @param v int vertex
          @see Edge
          @return Iterable<Edge> all edges for specified vertex
        */
        public static Iterable<Edge> adj(int v) {
            return adjList[v];
        }

        /**
        Computes the shortest path based on the inputted string choice
        Determine choice and insert s and weight into a priority queue.
        and remove the smallest weight from the priority queue. Make
        necessary adjustments to the priority queue by calling relax(Edge e, String choice)

        @param s int source vertex
        @param choice String representation of of choice ("d" for distance, "c" for cost)
        */

        public void Dijkstra(int s, String choice) {
            if (choice.equals("d")) {
                distTo = new double[V];
                edgeTo = new Edge[V];
                for (int v = 0; v < V; v++)
                    distTo[v] = Double.POSITIVE_INFINITY;
                distTo[s] = 0.0;

                // relax vertices in order of distance from s
                pq = new IndexMinPQ<Double>(V);
                pq.insert(s, distTo[s]);
                while (!pq.isEmpty()) {
                    int v = pq.delMin();
                    for (Edge e : adj(v))
                        relax(e, choice);
                }
            } else {
                costTo = new double[V];
                edgeTo = new Edge[V];
                for (int v = 0; v < V; v++)
                    costTo[v] = Double.POSITIVE_INFINITY;
                costTo[s] = 0;

                // relax vertices in order of distance from s
                pq = new IndexMinPQ<Double>(V);
                pq.insert(s, costTo[s]);
                while (!pq.isEmpty()) {
                    int v = pq.delMin();
                    for (Edge e : adj(v))
                        relax(e, choice);
                }
            }
        }


        /**
          Determine choice from input and determine if end vertex weight is greater than
          starting vertex weight + edge weight. If greater, update the end vertex weight
          with start vertex weight + edge weight and set edgeTo of endpoint to current edge.
          @param e current Edge
          @param choice String representation of of choice ("d" for distance, "c" for cost)

        */
        private void relax(Edge e, String choice) {
            int v = e.from(), w = e.to();
            if (choice.equals("d")) {
                if (distTo[w] > distTo[v] + e.distance()) {
                    distTo[w] = distTo[v] + e.distance();
                    edgeTo[w] = e;
                    if (pq.contains(w)) pq.change(w, distTo[w]);
                    else pq.insert(w, distTo[w]);
                }
            } else {
                double cost = e.getCost();
                if (costTo[w] > costTo[v] + e.getCost()) {
                    costTo[w] = costTo[v] + e.getCost();
                    edgeTo[w] = e;
                    if (pq.contains(w)) pq.change(w, costTo[w]);
                    else pq.insert(w, costTo[w]);
                }
            }
        }

        /**
        Returns the length of the shortest path (distance) from s to v
        @param v int end vertex
        @return double distance from s to v
        */

        public double distTo(int v) {
            return distTo[v];
        }

        /**
        Returns the length of the shortest path (cost) from s to v
        @param v int end vertex
        @return int cost from s to v
        */
        public int costTo(int v) {
            return (int) costTo[v];
        }

        /**
        Returns the length of the shortest path (hops) from s to v
        @param v int end vertex
        @return int hops from s to v
        */
        public int hopsTo(int v){
          return (int) hopsTo[v];
        }

        /**
          Returns boolean value for if a path exists from s to v given choice represenation
          @param v int end vertex
          @param choice String representation of of choice ("d" for distance, "c" for cost)
          @return boolean if path exists
        */
        public boolean hasPathTo(int v, String choice) {
            if(choice.equals("d")){
                return distTo[v] < Double.POSITIVE_INFINITY;
            }else if(choice.equals("c")){
                return costTo[v] < Double.POSITIVE_INFINITY;
            }
          return hopsTo[v] < Double.POSITIVE_INFINITY;
        }


        /**
          Returns the shortest path from s to va as an Iterable Object or null if no path exists
          loops through all edges in edgeTo of the given vertex
          @param v int vertex
          @param choice String representation of of choice ("d" for distance, "c" for cost)
          @return Iterable<Edge>
          @see Edge
        */
        public Iterable<Edge> pathTo(int v, String choice) {
            if (!hasPathTo(v, choice)) return null;
            Stack<Edge> path = new Stack<Edge>();
            for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
                path.push(e);
            }
            return path;
        }

        /**
          Performs a Breadth First traversal starting at int s
          Initalizes all locations of hopsTo array to POSITIVE_INFINITY and
          marks hopsTo[s] to 0 and adds to queue. While the queue is not empty,
          dequeue next vertex and loop through all the edges of that vertex.
          If the edge is not marked, create a new edge and increment value at
          hopsTo of the current vertex by 1. This increments the number of hops
          it takes to reach that vertex, from vertex s.
          @param s int vertex
        */
        public void BreadthFirstPaths(int s) {
            marked = new boolean[V];
            hopsTo = new double[V];
            edgeTo = new Edge[V];

            Queue<Integer> q = new Queue<Integer>();
            for (int v = 0; v < V; v++) {
                hopsTo[v] = Double.POSITIVE_INFINITY;
            }
            hopsTo[s] = 0;
            marked[s] = true;
            q.enqueue(s);

            while (!q.isEmpty()) {
                int v = q.dequeue();
                //loop through all vertices for a vertex
                for (Edge w : adj(v)) {
                    //if vertex is not yet visited, create new edge
                    if (!marked[w.getEnd()]) {
                        Edge newEdge = new Edge(v, w.getEnd(), w.getDist(), w.getCost());
                        edgeTo[w.getEnd()] = newEdge;
                        hopsTo[w.getEnd()] = hopsTo[v] + 1;
                        marked[w.getEnd()] = true;
                        q.enqueue(w.getEnd());
                    }
                }
            }
        }

        /**
          Prints all trips under specified cost limit.
          Loops through each vertex of the graph and calls
          overloaded recursive funciton.
          @param cost double representation of cost limit

        */
        public static void printTrips(double cost) {
            String res = "";
            for (int i = 0; i < adjList.length; i++) {
                boolean[] visited = new boolean[V];
                printTrips(cost, 0, i, adjList[i], visited, res);
            }
        }
        /**
          Overloaded recrusive method to print all trips under specified cost limit
          Obtain Iterator of Edges from the current list. While iterator is not empty,
          get the next edge and add the cost to the current cost total. If endVertex of current
          edge not yet visited and current cost is under cost limit, add edge information to
          result string and recurse. In recrusive call, send in cost limit, current cost, end vertex of
          current edge, the list of edges associated with end vertex, the array of visited vertices and
          the resulting string.
          @param cost double represenation of cost Limit
          @param curr_cost double represenation of the current cost total
          @param i int represenatation of the current vertex
          @param list LinkedList of edges for the current endVertex
          @param visited boolean array of visited vertices, to avoid cycles
          @param res String representation of a possible trip

        */
        public static void printTrips(double cost, double curr_cost, int i, LinkedList<Edge> list, boolean[] visited, String res) {
            visited[i] = true;
            Iterator<Edge> iterList = list.listIterator();
            //output current path
            if (!res.equals("")) {
                String tmp = " Cost: " + curr_cost + " Path: ";
                System.out.println(tmp + res);
            }
            while (iterList.hasNext()) {
                Edge e = iterList.next();
                int n = e.getEnd();
                curr_cost += e.getCost();
                //if not visited and under cost limit
                if ((!visited[n]) && curr_cost <= cost) {
                    String temp;
                    if (res.equals("")) {
                        // add curr cost to each print to print cost of each path
                        temp = (locations[e.getStart()] + " " + e.getCost() + " " + locations[e.getEnd()]);
                    } else {
                        temp = " " + e.getCost() + " " + locations[e.getEnd()];
                    }
                    res += temp;
                    //recurse through next vertices
                    printTrips(cost, curr_cost, n, adjList[n], visited, res);

                    //reset
                    int diff = res.length() - temp.length();
                    res = res.substring(0, diff);

                    visited[n] = false;
                }
                curr_cost -= e.getCost();
            }
        }

        /**
          Adds a trip to the schedule. Given the starting and ending locations find the
          associated vertices and see if the route already exists. If the route does not
          exist then create a new edge going from start to end and an edge going from
          end to start (since the graph is undirected). Add the edges to the graph,
          and write out the route with the distance and cost to the the data file.
          @param src String representation of the starting location
          @param target String representation of the ending location
          @param dist  distance of the new route
          @param cost  cost of the new route
          @throws java.io.IOException if file not found
        */
        public static void addTrip(String src, String target, int dist, double cost) throws IOException {
            int startVertex = getIndex(src);
            int endVertex = getIndex(target);
            boolean found = false;
            //check if route already exists
            for(int i=0; i<adjList[startVertex].size(); i++){
                if(adjList[startVertex].get(i).getEnd() == endVertex){
                    System.out.println("Route Already Exists");
                    found = true;
                }
            }
            //if route not found
            if(!found){
              //create new edges and add to graph
               Edge forward = new Edge(startVertex, endVertex, dist, cost);
               Edge reverse = new Edge(endVertex, startVertex, dist, cost);
               adjList[startVertex].add(forward);
               adjList[endVertex].add(reverse);
              //write to file
              FileWriter fWriter = new FileWriter(file, true);
              BufferedWriter bw = new BufferedWriter(fWriter);
              DecimalFormat formatter = new DecimalFormat("#.00");
              String temp = (startVertex + 1) + " " + (endVertex + 1) + " " + dist + " " + formatter.format(cost);
              bw.write(temp);
              bw.close();
            }
        }

        /**
          Removes a trip from the schedule given the starting location and ending locaiton.
          Get the associated vertices and remove the route from the graph in both the forward
          and reverse direction. Loop through the file appending each line to a StringBuilder,
          skipping the line of the removed route. Write out the StringBuilder to the original File,
          replacing the file's original content.
          @param source String representation of the starting location
          @param target String representation of the ending location
          @throws java.io.IOException if file not found

        */
        public static void removeTrip(String source, String target) throws IOException {
            int startVertex = getIndex(source) ;
            int endVertex = getIndex(target) ;
            String line = (startVertex + 1) + " " + (endVertex + 1) + " ";
            DecimalFormat formatter = new DecimalFormat("#.00");
            //remove from graph forward direction
            for(int i=0; i<adjList[startVertex].size(); i++){
                if(adjList[startVertex].get(i).getEnd() == endVertex){
                    String c =  formatter.format(adjList[startVertex].get(i).getCost());
                    line += String.format(adjList[startVertex].get(i).getDist() + " " + c);
                    adjList[startVertex].remove(i);
                }
            }
            //remove from graph in reverse direction
            for(int i=0; i<adjList[endVertex].size(); i++){
                if(adjList[endVertex].get(i).getEnd() == startVertex){
                    adjList[endVertex].remove(i);
                }
            }
            //remove from file
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String currLine;
            //loop through file
            while((currLine = br.readLine()) != null){
              //skip removed route
                if(!currLine.equals(line)){
                    sb.append(currLine + "\n");
                }
            }
            FileWriter fWriter = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fWriter);
            bw.write(sb.toString());
            bw.close();
        }

        /**
          Menu-driven loop that asks for the user what operation to perform.
          Based on the operation, the main method calls the appropriate methods and
          outputs the correct information.
          @param args Unused
          @throws java.io.IOException if file not found
        */
        public static void main(String[] args) throws IOException {
             Scanner s = new Scanner(System.in);
             System.out.print("Enter File Name: ");
             String fname = s.next();
             Airline t = new Airline(fname);
             int choice = -1;
             //if choice == 0 quit
             while(choice != 0){
               //print menu options
                 System.out.println("----------------------------");
                 String menu = "1) Display Direct Routes\n2) Calculate MST\n3) Shortest Path (miles)\n4) Shortest Path(cost)\n5) Shortest Hops\n";
                 menu += "6)Print Trips Under Cost \n7) Add Routes\n8) Remove Route\n0) Quit";
                 System.out.println(menu);
                 System.out.print("Please pick an option: ");
                 choice = s.nextInt();

                 switch(choice){
                     case(1):
                         displayDirectRoutes();
                         break;
                     case(2):
                         t.EagerPrim();
                         Iterable<Edge> edges = t.edges();
                         for(Edge e: edges){
                             System.out.println(locations[e.getEnd()] + "," + locations[e.getStart()] + " : " + e.getDist());
                         }
                         break;
                     case(3):
                         System.out.print("Starting Location: ");
                         String source = s.next();
                         System.out.print("Ending Location: ");
                         String target = s.next();
                         int start = t.getIndex(source);
                         int end = t.getIndex(target);
                         t.Dijkstra(start, "d");
                         if (t.hasPathTo(end, "d")) {
                             System.out.println();
                             System.out.println("SHORTEST DISTANCE PATH from " + source + " to " + target);
                             System.out.println("----------------------------------------------");
                             System.out.println("Shortest distance from " + source + " to " + target + ": " + t.distTo(end));
                             System.out.println("Path with edges (in reverse order):");
                             System.out.print(target + " ");
                             for (Edge e : t.pathTo(end, "d")) {
                                 System.out.print(e.getDist() + " " + locations[e.getStart()] + " ");
                             }
                             System.out.println();
                         }
                         break;
                     case(4):
                         System.out.print("Starting Location: ");
                         source = s.next();
                         System.out.print("Ending Location: ");
                         target = s.next();
                         start = t.getIndex(source);
                         end = t.getIndex(target);
                         t.Dijkstra(start, "c");
                         if (t.hasPathTo(end, "c")) {
                             System.out.println();
                             System.out.println("SHORTEST COST PATH from " + source + " to " + target);
                             System.out.println("----------------------------------------------");
                             System.out.println("Shortest cost from " + source + " to " + target + ": " + t.costTo(end));
                             System.out.println("Path with edges (in reverse order):");
                             System.out.print(target + " ");
                             for (Edge e : t.pathTo(end, "c")) {
                                 System.out.print((int) e.getCost() + " " + locations[e.getStart()] + " ");
                             }
                             System.out.println();
                         }
                         break;
                     case(5):
                         System.out.print("Starting Location: ");
                         source = s.next();
                         System.out.print("Ending Location: ");
                         target = s.next();
                         start = t.getIndex(source);
                         end = t.getIndex(target);
                         t.BreadthFirstPaths(start);
                         if (t.hasPathTo(end, "h")) {
                             System.out.println();
                             System.out.println("FEWEST HOPS from " + source + " to " + target);
                             System.out.println("----------------------------------------------");
                             System.out.println("Shortest path from " + source + " to " + target + ": " + t.hopsTo(end));
                             System.out.println("Path with edges (in reverse order):");
                             System.out.print(target + " ");
                             Iterable<Edge> paths = t.pathTo(end, "h");
                             for (Edge e : paths) {
                                 System.out.print(" " + locations[e.getStart()] + " ");
                             }
                             System.out.println();
                         }
                         break;
                     case(6):
                         System.out.print("Cost Limit: ");
                         double cost = Double.parseDouble(s.next());
                         System.out.println("ALL PATHS OF COST $" + cost + " OR LESS");
                         System.out.println("-----------------------------------");
                         t.printTrips(cost);
                         break;
                     case(7):
                         System.out.print("Starting Location: ");
                         source = s.next();
                         System.out.print("Ending Location: ");
                         target = s.next();
                         System.out.print("Distance: ");
                         int distance = s.nextInt();
                         System.out.print("Cost: ");
                         cost = Double.parseDouble(s.next());
                         t.addTrip(source, target, distance, cost);
                         break;
                     case(8):
                         System.out.print("Starting Location: ");
                         source = s.next();
                         System.out.print("Ending Location: ");
                         target = s.next();
                         t.removeTrip(source, target);
                 }
             }
         }
}
