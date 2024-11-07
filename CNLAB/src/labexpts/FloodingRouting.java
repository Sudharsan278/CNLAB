package labexpts;

import java.util.*;

class FloodingRouting {

    // Method to perform flooding from the source router
    void flood(int[][] networkGraph, int sourceRouter) {
        // Number of routers in the network (number of nodes in the graph)
        int numRouters = networkGraph.length;

        // Queue to manage the routers to be processed in BFS order
        Queue<Integer> queue = new LinkedList<>();

        // Array to track if a router has received the message
        boolean[] visited = new boolean[numRouters];

        // Mark the source router as visited and enqueue it
        visited[sourceRouter] = true;
        queue.add(sourceRouter);

        System.out.println("Flooding from router: " + sourceRouter);

        // Process the routers in the queue
        while (!queue.isEmpty()) {
            int currentRouter = queue.poll();
            System.out.println("Router " + currentRouter + " received the message.");

            // Send the message to all adjacent routers (neighbors)
            for (int i = 0; i < numRouters; i++) {
                // Check if there is an edge between the current router and router i
                // and if router i hasn't received the message yet
                if (networkGraph[currentRouter][i] != 0 && !visited[i]) {
                    visited[i] = true; // Mark router i as visited
                    queue.add(i);      // Enqueue router i
                    System.out.println("Router " + currentRouter + " sends message to Router " + i);
                }
            }
        }

        System.out.println("Flooding completed.");
    }

    public static void main(String[] args) {
        // Example network graph represented as an adjacency matrix.
        // Here, 0 means no direct link between routers, and non-zero values represent the presence of a link.
        int[][] networkGraph = new int[][] {
            { 0, 1, 0, 0, 0, 0 },
            { 1, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 1, 0, 0 },
            { 0, 0, 1, 0, 1, 0 },
            { 0, 0, 0, 1, 0, 1 },
            { 0, 0, 0, 0, 1, 0 }
        };

        // Create an instance of FloodingRouting
        FloodingRouting flooding = new FloodingRouting();
        
        // Start flooding from router 0
        flooding.flood(networkGraph, 0);
    }
}
