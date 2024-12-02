package labexpts;

import java.util.*;

//Class to represent the network's topology using nodes (routers) and edges (links)
class Router {
 String routerId;
 Map<Router, Integer> neighbors = new HashMap<>(); // Neighbor router with cost

 public Router(String routerId) {
     this.routerId = routerId;
 }

 public void addNeighbor(Router router, int cost) {
     neighbors.put(router, cost);
 }

 @Override
 public String toString() {
     return routerId;
 }
}

//Class to represent the link-state network
class Network {
 Map<String, Router> routers = new HashMap<>();

 public void addRouter(String routerId) {
     routers.putIfAbsent(routerId, new Router(routerId));
 }

 public void addLink(String router1, String router2, int cost) {
     Router r1 = routers.get(router1);
     Router r2 = routers.get(router2);
     if (r1 != null && r2 != null) {
         r1.addNeighbor(r2, cost);
         r2.addNeighbor(r1, cost); // Since it's an undirected graph
     }
 }

 // Simulate Link State Advertisements
 public void sendLSAs() {
     for (Router router : routers.values()) {
         System.out.println("Router " + router.routerId + " sends LSA: " + router.neighbors);
     }
 }

 // Shortest path algorithm (Dijkstra) to find the shortest path from source router
 public Map<Router, Integer> calculateShortestPaths(String sourceId) {
     Router source = routers.get(sourceId);
     Map<Router, Integer> distances = new HashMap<>();
     PriorityQueue<Router> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

     for (Router router : routers.values()) {
         if (router.equals(source)) {
             distances.put(router, 0);
         } else {
             distances.put(router, Integer.MAX_VALUE);
         }
         queue.add(router);
     }

     while (!queue.isEmpty()) {
         Router current = queue.poll();
         int currentDist = distances.get(current);

         for (Map.Entry<Router, Integer> entry : current.neighbors.entrySet()) {
             Router neighbor = entry.getKey();
             int weight = entry.getValue();
             int distanceThroughCurrent = currentDist + weight;

             if (distanceThroughCurrent < distances.get(neighbor)) {
                 distances.put(neighbor, distanceThroughCurrent);
                 queue.remove(neighbor);
                 queue.add(neighbor);
             }
         }
     }
     return distances;
 }
}

//Main Class to initialize network and simulate link state routing
public class LinkStateRouting {
 public static void main(String[] args) {
     Network network = new Network();

     // Adding routers
     network.addRouter("A");
     network.addRouter("B");
     network.addRouter("C");
     network.addRouter("D");
     network.addRouter("E");
     network.addRouter("F");
     
     
     // Adding links (bidirectional links between routers with specified costs)
     network.addLink("A", "B", 4);
     network.addLink("A", "E", 5);
     network.addLink("B", "C", 2);
     network.addLink("B", "F", 6);
     network.addLink("C", "D", 3);
     network.addLink("C", "E", 1);
     network.addLink("D", "F", 7);
     network.addLink("F", "E", 8);

     // Step 1: Send LSAs to simulate network topology sharing
     System.out.println("Link State Advertisements (LSAs):");
     network.sendLSAs();

     // Step 2: Calculate shortest paths from a source router
     String sourceRouterId = "A";
     Map<Router, Integer> shortestPaths = network.calculateShortestPaths(sourceRouterId);

     // Display shortest paths from the source router
     System.out.println("\nShortest paths from router " + sourceRouterId + ":");
     for (Map.Entry<Router, Integer> entry : shortestPaths.entrySet()) {
         System.out.println("To " + entry.getKey() + " - Cost: " + entry.getValue());
     }
 }
}
