package bearmaps.proj2c;

import bearmaps.hw4.WeightedEdge;
import bearmaps.hw4.WeirdSolver;
import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.Point;
import bearmaps.proj2ab.PointSet;
import bearmaps.proj2ab.WeirdPointSet;
import bearmaps.proj2c.utils.MyTrieSet;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, Siyuan Chen
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private Map<Point, Node> nodePointMap = new HashMap<>();
    private Map<String, Node> locationNames = new HashMap<>();
    private MyTrieSet trieSet = new MyTrieSet();

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        List<Node> nodes = this.getNodes();

        for (Node node : nodes) {
            nodePointMap.put(new Point(node.lon(), node.lat()), node);

            if (node.name() != null && !trieSet.contains(node.name())) {
                trieSet.add(cleanString(node.name()));
                locationNames.put(node.name(), node);
            }
        }
    }


    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        List<Point> points = new ArrayList<>();
        for (Map.Entry<Point, Node> entry : nodePointMap.entrySet())
            if (this.neighbors(entry.getValue().id()).size() > 0)
                points.add(entry.getKey());

        PointSet kdTree = new WeirdPointSet(points);
        Node closest = nodePointMap.get(kdTree.nearest(lon, lat));
        return closest.id();
    }


    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        prefix = cleanString(prefix);
        List<String> tmp = trieSet.keysWithPrefix(prefix);
        List<String> results = new LinkedList<>();
        Collection<Node> cleanedNames = locationNames.values();

        for (Map.Entry<String, Node> entry : locationNames.entrySet()) {
            if (cleanedNames.contains(entry.getValue()) &&
                    tmp.contains(cleanString(entry.getValue().name())))
                results.add(entry.getKey());
        }

        return results;
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> results = new LinkedList<>();
        List<String> matchedLocations = getLocationsByPrefix(locationName);
        for (String s : matchedLocations) {
            Map<String, Object> map = new HashMap<>();
            map.put(s, locationNames.get(s).name());
            map.put("lat", locationNames.get(s).lat());
            map.put("lon", locationNames.get(s).lon());
            map.put("id", locationNames.get(s).id());
            results.add(map);
        }
        return results;
    }


    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }
}
