package bearmaps.proj2c.server.handler.impl;

import bearmaps.proj2c.AugmentedStreetMapGraph;
import bearmaps.proj2c.server.handler.APIRouteHandler;
import spark.Request;
import spark.Response;
import bearmaps.proj2c.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bearmaps.proj2c.utils.Constants.SEMANTIC_STREET_GRAPH;
import static bearmaps.proj2c.utils.Constants.ROUTE_LIST;

/**
 * Handles requests from the web browser for map images. These images
 * will be rastered into one large image to be displayed to the user.
 * @author rahul, Josh Hug, Siyuan Chen
 */
public class RasterAPIHandler extends APIRouteHandler<Map<String, Double>, Map<String, Object>> {

    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside RasterAPIHandler.processRequest(). <br>
     * ullat : upper left corner latitude, <br> ullon : upper left corner longitude, <br>
     * lrlat : lower right corner latitude,<br> lrlon : lower right corner longitude <br>
     * w : user viewport window width in pixels,<br> h : user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
            "lrlon", "w", "h"};

    /**
     * The result of rastering must be a map containing all of the
     * fields listed in the comments for RasterAPIHandler.processRequest.
     **/
    private static final String[] REQUIRED_RASTER_RESULT_PARAMS = {"render_grid", "raster_ul_lon",
            "raster_ul_lat", "raster_lr_lon", "raster_lr_lat", "depth", "query_success"};


    @Override
    protected Map<String, Double> parseRequestParams(Request request) {
        return getRequestParams(request, REQUIRED_RASTER_REQUEST_PARAMS);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param requestParams Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @param response : Not used by this function. You may ignore.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image;
     *                    can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    @Override
    public Map<String, Object> processRequest(Map<String, Double> requestParams, Response response) {
        Map<String, Object> results = new HashMap<>();
        String[][] render_grid;
        double raster_ul_lon, raster_ul_lat, raster_lr_lon, raster_lr_lat;
        raster_ul_lon = Constants.ROOT_ULLON;
        raster_ul_lat = Constants.ROOT_ULLAT;
        raster_lr_lon = Constants.ROOT_LRLON;
        raster_lr_lat = Constants.ROOT_LRLAT;
        int depth = 7;

        double expectedLrLon = requestParams.get("lrlon");
        double expectedUlLon = requestParams.get("ullon");
        double expectedLrLat = requestParams.get("lrlat");
        double expectedUlLat = requestParams.get("ullat");

        if (!velidateCoordinate(expectedUlLon, expectedUlLat, expectedLrLon, expectedLrLat))
            return queryFail();

        double expectedWidth = requestParams.get("w");
        double expectedLonDPP = (expectedLrLon - expectedUlLon) / expectedWidth;
        double totalWidth = Constants.ROOT_LRLON - Constants.ROOT_ULLON;
        double totalHeight = Constants.ROOT_ULLAT - Constants.ROOT_LRLAT;
        double widthPerTile = totalWidth / 128;
        double heightPerTile = totalHeight / 128;

        int maxDepth = depth;
        while (maxDepth >= 0) {
            double parts = Math.pow(2, maxDepth);
            double maxLonDPP = totalWidth / (parts * 256);
            if (maxLonDPP >= expectedLonDPP) break;
            widthPerTile = totalWidth / parts;
            heightPerTile = totalHeight / parts;
            depth = maxDepth;
            maxDepth--;
        }

        double maxUlLon = raster_ul_lon;
        double minLrLon = raster_lr_lon;
        double minUlLat = raster_ul_lat;
        double maxLrLat = raster_lr_lat;

        while (raster_ul_lon <= expectedUlLon) {
            maxUlLon += widthPerTile;
            if (maxUlLon > expectedUlLon) break;
            raster_ul_lon = maxUlLon;
        }

        while (raster_ul_lat >= expectedUlLat) {
            minUlLat -= heightPerTile;
            if (minUlLat < expectedUlLat) break;
            raster_ul_lat = minUlLat;
        }

        while (raster_lr_lon >= expectedLrLon) {
            minLrLon -= widthPerTile;
            if (minLrLon < expectedLrLon) break;
            raster_lr_lon = minLrLon;
        }

        while (raster_lr_lat <= expectedLrLat) {
            maxLrLat += heightPerTile;
            if (maxLrLat > expectedLrLat) break;
            raster_lr_lat = maxLrLat;
        }

        int tileOffset = (int) new BigDecimal(
                (raster_ul_lon - Constants.ROOT_ULLON) / widthPerTile).
                setScale(2, RoundingMode.HALF_UP).doubleValue();

        int numTiles = (int) new BigDecimal(
                (raster_lr_lon - raster_ul_lon) / widthPerTile).
                setScale(2, RoundingMode.HALF_UP).doubleValue();

        int rowOffset = (int) new BigDecimal(
                (Constants.ROOT_ULLAT - raster_ul_lat) / heightPerTile).
                setScale(2, RoundingMode.HALF_UP).doubleValue();

        int numRows = (int) new BigDecimal(
                (raster_ul_lat - raster_lr_lat) / heightPerTile).
                setScale(2, RoundingMode.HALF_UP).doubleValue();

        render_grid = new String[numRows][numTiles];

        for (int i = rowOffset, row = 0; row < numRows; i++, row++)
            for (int j = tileOffset, col = 0; col < numTiles; j++, col++)
                render_grid[row][col] = String.format("d%d_x%d_y%d.png", depth, j, i);

        results.put("render_grid", render_grid);
        results.put("depth", depth);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("query_success", true);
        return results;
    }

    private boolean velidateCoordinate(double ullon, double ullat, double lrlon, double lrlat) {
        return !(lrlon < Constants.ROOT_ULLON || lrlat > Constants.ROOT_ULLAT ||
                ullon  > Constants.ROOT_LRLON || ullat < Constants.ROOT_LRLAT || lrlon < ullon ||
                lrlat > ullat);
    }

    @Override
    protected Object buildJsonResponse(Map<String, Object> result) {
        boolean rasterSuccess = validateRasteredImgParams(result);

        if (rasterSuccess) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writeImagesToOutputStream(result, os);
            String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
            result.put("b64_encoded_image_data", encodedImage);
        }
        return super.buildJsonResponse(result);
    }

    private Map<String, Object> queryFail() {
        Map<String, Object> results = new HashMap<>();
        results.put("render_grid", null);
        results.put("raster_ul_lon", 0);
        results.put("raster_ul_lat", 0);
        results.put("raster_lr_lon", 0);
        results.put("raster_lr_lat", 0);
        results.put("depth", 0);
        results.put("query_success", false);
        return results;
    }

    /**
     * Validates that Rasterer has returned a result that can be rendered.
     * @param rip : Parameters provided by the rasterer
     */
    private boolean validateRasteredImgParams(Map<String, Object> rip) {
        for (String p : REQUIRED_RASTER_RESULT_PARAMS) {
            if (!rip.containsKey(p)) {
                System.out.println("Your rastering result is missing the " + p + " field.");
                return false;
            }
        }
        if (rip.containsKey("query_success")) {
            boolean success = (boolean) rip.get("query_success");
            if (!success) {
                System.out.println("query_success was reported as a failure");
                return false;
            }
        }
        return true;
    }

    /**
     * Writes the images corresponding to rasteredImgParams to the output stream.
     * In Spring 2016, students had to do this on their own, but in 2017,
     * we made this into provided code since it was just a bit too low level.
     */
    private  void writeImagesToOutputStream(Map<String, Object> rasteredImageParams,
                                                  ByteArrayOutputStream os) {
        String[][] renderGrid = (String[][]) rasteredImageParams.get("render_grid");
        int numVertTiles = renderGrid.length;
        int numHorizTiles = renderGrid[0].length;

        BufferedImage img = new BufferedImage(numHorizTiles * Constants.TILE_SIZE,
                numVertTiles * Constants.TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphic = img.getGraphics();
        int x = 0, y = 0;

        for (int r = 0; r < numVertTiles; r += 1) {
            for (int c = 0; c < numHorizTiles; c += 1) {
                graphic.drawImage(getImage(Constants.IMG_ROOT + renderGrid[r][c]), x, y, null);
                x += Constants.TILE_SIZE;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += Constants.TILE_SIZE;
                }
            }
        }

        /* If there is a route, draw it. */
        double ullon = (double) rasteredImageParams.get("raster_ul_lon"); //tiles.get(0).ulp;
        double ullat = (double) rasteredImageParams.get("raster_ul_lat"); //tiles.get(0).ulp;
        double lrlon = (double) rasteredImageParams.get("raster_lr_lon"); //tiles.get(0).ulp;
        double lrlat = (double) rasteredImageParams.get("raster_lr_lat"); //tiles.get(0).ulp;

        final double wdpp = (lrlon - ullon) / img.getWidth();
        final double hdpp = (ullat - lrlat) / img.getHeight();
        AugmentedStreetMapGraph graph = SEMANTIC_STREET_GRAPH;
        List<Long> route = ROUTE_LIST;

        if (route != null && !route.isEmpty()) {
            Graphics2D g2d = (Graphics2D) graphic;
            g2d.setColor(Constants.ROUTE_STROKE_COLOR);
            g2d.setStroke(new BasicStroke(Constants.ROUTE_STROKE_WIDTH_PX,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            route.stream().reduce((v, w) -> {
                g2d.drawLine((int) ((graph.lon(v) - ullon) * (1 / wdpp)),
                        (int) ((ullat - graph.lat(v)) * (1 / hdpp)),
                        (int) ((graph.lon(w) - ullon) * (1 / wdpp)),
                        (int) ((ullat - graph.lat(w)) * (1 / hdpp)));
                return w;
            });
        }

        rasteredImageParams.put("raster_width", img.getWidth());
        rasteredImageParams.put("raster_height", img.getHeight());

        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BufferedImage getImage(String imgPath) {
        BufferedImage tileImg = null;
        if (tileImg == null) {
            try {
                File in = new File(imgPath);
                tileImg = ImageIO.read(in);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return tileImg;
    }

    public static void main(String[] args) {
        RasterAPIHandler rasterAPIHandler = new RasterAPIHandler();
        Map<String, Double> map = new HashMap<>();
        map.put("lrlon", -122.24053369025242);
        map.put("ullon", -122.24163047377972);
        map.put("w", 892.0);
        map.put("h", 875.0);
        map.put("ullat", 37.87655856892288);
        map.put("lrlat", 37.87548268822065);

        /**map.put("lrlon", Constants.ROOT_LRLON);
        map.put("ullon", Constants.ROOT_ULLON);
        map.put("w", 892.0);
        map.put("h", 875.0);
        map.put("ullat", Constants.ROOT_ULLAT);
        map.put("lrlat", Constants.ROOT_LRLAT);*/

        Map<String, Object> result = rasterAPIHandler.processRequest(map, null);
        String[][] grid = (String[][])result.get("render_grid");
        for (String[] i : grid) {
            for(String j : i) {
                System.out.println(j);
            }
        }
    }
}
