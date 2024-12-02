package labexpts;

import java.util.*;

// NetworkRouter class to represent each router in the network
class NetworkRouter {
    String id;
    Map<NetworkRouter, Integer> neighbors = new HashMap<>(); // Neighbor routers with link cost
    Zone zone;

    public NetworkRouter(String id) {
        this.id = id;
    }

    public void addNeighbor(NetworkRouter router, int cost) {
        neighbors.put(router, cost);
    }

    @Override
    public String toString() {
        return id;
    }
}

// Zone class to represent each hierarchical level or cluster in the network
//Updated Zone class to support multiple cluster heads
class Zone {
 String zoneId;
 List<NetworkRouter> clusterHeads = new ArrayList<>(); // Multiple cluster heads
 List<NetworkRouter> routers = new ArrayList<>();

 public Zone(String zoneId) {
     this.zoneId = zoneId;
 }

 // Designate one or more routers as cluster heads for inter-zone communication
 public void addClusterHead(NetworkRouter router) {
     if (!clusterHeads.contains(router)) {
         clusterHeads.add(router);
         router.zone = this;
     }
 }

 public void addRouter(NetworkRouter router) {
     routers.add(router);
     router.zone = this;
 }

 @Override
 public String toString() {
     return "Zone " + zoneId;
 }
}


// HierarchicalNetwork class to handle routing within and between zones
class HierarchicalNetwork {
    Map<String, Zone> zones = new HashMap<>();
    List<NetworkRouter> interZoneRouters = new ArrayList<>(); // Routers connected to other zones

    // Add a new zone to the network
    public void addZone(String zoneId) {
        zones.putIfAbsent(zoneId, new Zone(zoneId));
    }

    // Add a router to a specified zone
    public void addRouterToZone(String zoneId, String routerId) {
        Zone zone = zones.get(zoneId);
        if (zone != null) {
            NetworkRouter router = new NetworkRouter(routerId);
            zone.addRouter(router);
        }
    }

    // Define the cluster head for a zone
    public void setClusterHead(String zoneId, String routerId) {
        Zone zone = zones.get(zoneId);
        if (zone != null) {
            for (NetworkRouter router : zone.routers) {
                if (router.id.equals(routerId)) {
                    zone.addClusterHead(router);
                    interZoneRouters.add(router); // Add cluster head to inter-zone routers
                }
            }
        }
    }

    // Add a link between two routers with a specific cost
    public void addLink(String router1Id, String router2Id, int cost) {
        for (Zone zone : zones.values()) {
            for (NetworkRouter router : zone.routers) {
                if (router.id.equals(router1Id)) {
                    for (NetworkRouter neighbor : zone.routers) {
                        if (neighbor.id.equals(router2Id)) {
                            router.addNeighbor(neighbor, cost);
                            neighbor.addNeighbor(router, cost); // Undirected link
                        }
                    }
                }
            }
        }
    }

    // Add an inter-zone link through cluster heads
    public void addInterZoneLink(String head1Id, String head2Id, int cost) {
        NetworkRouter head1 = null;
        NetworkRouter head2 = null;

        // Find the cluster head routers
        for (NetworkRouter router : interZoneRouters) {
            if (router.id.equals(head1Id)) {
                head1 = router;
            }
            if (router.id.equals(head2Id)) {
                head2 = router;
            }
        }

        // Add the link between the two cluster heads
        if (head1 != null && head2 != null) {
            head1.addNeighbor(head2, cost);
            head2.addNeighbor(head1, cost);
        }
    }

    // Calculate the shortest path within a zone
    public Map<NetworkRouter, Integer> calculateIntraZonePaths(Zone zone, NetworkRouter source) {
        Map<NetworkRouter, Integer> distances = new HashMap<>();
        PriorityQueue<NetworkRouter> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Initialize distances and add each router to the priority queue
        for (NetworkRouter router : zone.routers) {
            if (router.equals(source)) {
                distances.put(router, 0);
            } else {
                distances.put(router, Integer.MAX_VALUE);
            }
            queue.add(router);
        }

        // Perform Dijkstra's algorithm to calculate shortest paths
        while (!queue.isEmpty()) {
            NetworkRouter current = queue.poll();
            Integer currentDist = distances.get(current);

            // Only proceed if we have a valid distance
            if (currentDist == null || currentDist == Integer.MAX_VALUE) {
                continue; // Unreachable node, skip it
            }

            for (Map.Entry<NetworkRouter, Integer> entry : current.neighbors.entrySet()) {
                NetworkRouter neighbor = entry.getKey();
                int weight = entry.getValue();
                int distanceThroughCurrent = currentDist + weight;

                if (distanceThroughCurrent < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, distanceThroughCurrent);

                    // Reinsert the neighbor into the queue to update its priority
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return distances;
    }
}
// Main class to initialize network zones and simulate hierarchical routing
public class HierarchicalStateRouting {
    public static void main(String[] args) {
        HierarchicalNetwork network = new HierarchicalNetwork();

        // Define zones
        network.addZone("Zone1");
        network.addZone("Zone2");
        network.addZone("Zone3");
        network.addZone("Zone4");
        network.addZone("Zone5");

        // Add routers to each zone
        network.addRouterToZone("Zone1", "1A");
        network.addRouterToZone("Zone1", "1B");
        network.addRouterToZone("Zone1", "1C");
        
        network.addRouterToZone("Zone2", "2A");
        network.addRouterToZone("Zone2", "2B");
        network.addRouterToZone("Zone2", "2C");
        network.addRouterToZone("Zone2", "2D");


        network.addRouterToZone("Zone3", "3A");
        network.addRouterToZone("Zone3", "3B");
        
        network.addRouterToZone("Zone4", "4A");
        network.addRouterToZone("Zone4", "4B");
        network.addRouterToZone("Zone4", "4C");
        
        network.addRouterToZone("Zone5", "5A");
        network.addRouterToZone("Zone5", "5B");
        network.addRouterToZone("Zone5", "5C");
        network.addRouterToZone("Zone5", "5D");
        network.addRouterToZone("Zone5", "5E");
        
        // Define cluster heads for each zone
        network.setClusterHead("Zone1", "1B");
        network.setClusterHead("Zone1", "1C");
        network.setClusterHead("Zone2", "2A");
        network.setClusterHead("Zone2", "2D");
        network.setClusterHead("Zone3", "3B");
        network.setClusterHead("Zone4", "4A");
        network.setClusterHead("Zone5", "5A");
        network.setClusterHead("Zone5", "5C");
        

        // Add links within each zone
        network.addLink("1A", "1B", 1);
        network.addLink("1A", "1C", 1);
        network.addLink("1B", "1C", 1);
        
        network.addLink("2A", "2B", 1);
        network.addLink("2A", "2C", 1);
        network.addLink("2B", "2D", 1);
        network.addLink("2C", "2D", 1);
        
        network.addLink("3A", "3B", 1);
       
        network.addLink("4A", "4B", 1);
        network.addLink("4A", "4C", 1);
        network.addLink("4B", "4C", 1);
       
        network.addLink("5A", "5B", 1);
        network.addLink("5A", "5E", 1);
        network.addLink("5B", "5C", 1);
        network.addLink("5C", "5D", 1);
        network.addLink("5D", "5E", 1);
       
        
        
        // Add inter-zone link via cluster heads
        network.addInterZoneLink("1B", "2A", 1);
        network.addInterZoneLink("2D", "5C", 1);
        network.addInterZoneLink("4A", "5A", 1);
        network.addInterZoneLink("3B", "4A", 1);
        network.addInterZoneLink("1C", "3B", 1);
        

        // Display intra-zone paths for Zone1 from R1
        Zone zone1 = network.zones.get("Zone1");
        System.out.println("\nIntra-zone paths within Zone1 from 1A:");
        Map<NetworkRouter, Integer> distancesZone1 = network.calculateIntraZonePaths(zone1, zone1.routers.get(0));
        for (Map.Entry<NetworkRouter, Integer> entry : distancesZone1.entrySet()) {
            System.out.println("To " + entry.getKey() + " - Cost: " + entry.getValue());
        }
    }
}
