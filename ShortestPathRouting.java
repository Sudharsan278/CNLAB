package labexpts;

import java.util.*;

class ShortestPathRouting {
    static final int NUM_ROUTERS = 9;

    // Method to find the router with the minimum routing cost, which has not yet been finalized.
    int getMinCostRouter(int[] routingCost, Boolean[] finalized) {
        int minCost = Integer.MAX_VALUE, minIndex = -1;

        for (int router = 0; router < NUM_ROUTERS; router++) {
            if (!finalized[router] && routingCost[router] <= minCost) {
                minCost = routingCost[router];
                minIndex = router;
            }
        }

        return minIndex;
    }

    // Method to print the shortest path from the source to a given destination.
    void printPath(int[] predecessors, int destination) {
        if (predecessors[destination] == -1) {
            System.out.print(destination);
            return;
        }
        printPath(predecessors, predecessors[destination]);
        System.out.print(" -> " + destination);
    }

    // Method to display the shortest path routing table with distances and paths.
    void printRoutingTable(int[] routingCost, int[] predecessors, int sourceRouter) {
        System.out.println("Router\tMinimum Cost\tPath");
        for (int router = 0; router < NUM_ROUTERS; router++) {
            System.out.print(router + "\t\t" + routingCost[router] + "\t\t");
            printPath(predecessors, router);
            System.out.println();
        }
    }

    // Implementation of Dijkstra's algorithm to find the shortest path from the source router to all others.
    void calculateShortestPath(int[][] networkGraph, int sourceRouter) {
        int[] routingCost = new int[NUM_ROUTERS]; // Holds the minimum cost from the source router to each router.
        Boolean[] finalized = new Boolean[NUM_ROUTERS]; // Tracks if the shortest cost for each router is finalized.
        int[] predecessors = new int[NUM_ROUTERS]; // Tracks the predecessor of each router to reconstruct paths.

        // Initialize all routing costs as infinite, finalized[] as false, and predecessors[] as -1.
        for (int i = 0; i < NUM_ROUTERS; i++) {
            routingCost[i] = Integer.MAX_VALUE;
            finalized[i] = false;
            predecessors[i] = -1;
        }

        // The cost of the source router to itself is always 0.
        routingCost[sourceRouter] = 0;

        // Calculate the shortest path for each router.
        for (int count = 0; count < NUM_ROUTERS - 1; count++) {
            // Get the router with the minimum routing cost from those not yet finalized.
            int u = getMinCostRouter(routingCost, finalized);
            
            // Mark the picked router as finalized.
            finalized[u] = true;

            // Update the routing cost of the adjacent routers of the picked router.
            for (int v = 0; v < NUM_ROUTERS; v++) {
                if (!finalized[v] && networkGraph[u][v] != 0 && routingCost[u] != Integer.MAX_VALUE
                    && routingCost[u] + networkGraph[u][v] < routingCost[v]) {
                    routingCost[v] = routingCost[u] + networkGraph[u][v];
                    predecessors[v] = u; // Update the predecessor of router v.
                }
            }
        }

        // Print the routing table with the minimum cost from the source router to each router.
        printRoutingTable(routingCost, predecessors, sourceRouter);
    }

    // Main method
    public static void main(String[] args) {
        // Example network graph represented as an adjacency matrix.
        int[][] networkGraph = new int[][] { 
            { 0, 4, 0, 0, 0, 0, 0, 8, 0 },
            { 4, 0, 8, 0, 0, 0, 0, 11, 0 },
            { 0, 8, 0, 7, 0, 4, 0, 0, 2 },
            { 0, 0, 7, 0, 9, 14, 0, 0, 0 },
            { 0, 0, 0, 9, 0, 10, 0, 0, 0 },
            { 0, 0, 4, 14, 10, 0, 2, 0, 0 },
            { 0, 0, 0, 0, 0, 2, 0, 1, 6 },
            { 8, 11, 0, 0, 0, 0, 1, 0, 7 },
            { 0, 0, 2, 0, 0, 0, 6, 7, 0 } 
        };
        
        ShortestPathRouting routing = new ShortestPathRouting();
        routing.calculateShortestPath(networkGraph, 0); // Source router is 0
    }
}
