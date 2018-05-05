package ca.ubc.cs.cpsc210.translink.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.R;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;

import java.util.HashMap;
import java.util.Map;

// A plotter for bus stop locations
public class BusStopPlotter extends MapViewOverlay {
    /** clusterer */
    private RadiusMarkerClusterer stopClusterer;
    /** maps each stop to corresponding marker on map */
    private Map<Stop, Marker> stopMarkerMap = new HashMap<>();
    /** marker for stop that is nearest to user (null if no such stop) */
    private Marker nearestStnMarker;
    private Activity activity;
    private StopInfoWindow stopInfoWindow;

    /**
     * Constructor
     * @param activity  the application context
     * @param mapView  the map view on which buses are to be plotted
     */
    public BusStopPlotter(Activity activity, MapView mapView) {
        super(activity.getApplicationContext(), mapView);
        this.activity = activity;
        nearestStnMarker = null;
        stopInfoWindow = new StopInfoWindow((StopSelectionListener) activity, mapView);
        newStopClusterer();
    }

    public RadiusMarkerClusterer getStopClusterer() {
        return stopClusterer;
    }

    /**
     * Mark all visible stops in stop manager onto map.
     */
    public void markStops(Location currentLocation) {
        Drawable stopIconDrawable = activity.getResources().getDrawable(R.drawable.stop_icon);
        updateVisibleArea();
        newStopClusterer();
        clearMarkers();
        //  complete the implementation of this method (Task 5)


        for (Stop st:StopManager.getInstance()) {

            if (Geometry.rectangleContainsPoint(northWest, southEast, st.getLocn())) {
                //if(getMarker(st)==null)
                //{
                    Marker m = new Marker(mapView);
                    m.setRelatedObject(st);
                    String RouteNo="\n";
                    for (Route r :st.getRoutes()) {
                        RouteNo+=r.getNumber()+"\n";
                       }
                       m.setTitle(st.getNumber() + " " + st.getName() + RouteNo);

                m.setPosition(Geometry.gpFromLL(st.getLocn()));
                    m.setIcon(stopIconDrawable);
                    m.setInfoWindow(stopInfoWindow);
                    setMarker(st, m);
                    stopClusterer.add(m);
                    //stopInfoWindow.onOpen(m);
                   // stopClusterer.renderer(stopClusterer.clusterer(mapView),null,mapView);
                //}

            }
//            else {
//                if(getMarker(st)!=null)
//                {
//                    getMarker(st).setRelatedObject(null);
//                    clearMarker(st);
//
//                }
//            }
        }
        if (currentLocation != null) {
            Stop st = StopManager.getInstance().findNearestTo(new LatLon(currentLocation.getLatitude(), currentLocation.getLongitude()));
            updateMarkerOfNearest(st);
        }


    }

    /**
     * Create a new stop cluster object used to group stops that are close by to reduce screen clutter
     */
    private void newStopClusterer() {
        stopClusterer = new RadiusMarkerClusterer(activity);
        stopClusterer.getTextPaint().setTextSize(20.0F * BusesAreUs.dpiFactor());
        int zoom =  mapView == null ? 16 : mapView.getZoomLevel();
        if (zoom == 0) zoom = MapDisplayFragment.DEFAULT_ZOOM;
        int radius = 1000 / zoom;

        stopClusterer.setRadius(radius);
        Drawable clusterIconD = activity.getResources().getDrawable(R.drawable.stop_cluster);
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
        stopClusterer.setIcon(clusterIcon);
    }

    /**
     * Update marker of nearest stop (called when user's location has changed).  If nearest is null,
     * no stop is marked as the nearest stop.
     *
     * @param nearest   stop nearest to user's location (null if no stop within StopManager.RADIUS metres)
     */
    public void updateMarkerOfNearest(Stop nearest) {
        Drawable stopIconDrawable = activity.getResources().getDrawable(R.drawable.stop_icon);
        Drawable closestStopIconDrawable = activity.getResources().getDrawable(R.drawable.closest_stop_icon);

        //  complete the implementation of this method (Task 6)

    //    this.nearestStnMarker=new Marker(mapView);
        this.nearestStnMarker=getMarker(nearest);
//        this.nearestStnMarker.setRelatedObject(nearest);
//        this.nearestStnMarker.setPosition(new GeoPoint(nearest.getLocn().getLatitude(),nearest.getLocn().getLongitude()));
//        this.nearestStnMarker.setTitle(nearest.getNumber()+nearest.getName());
//        setMarker(nearest,this.nearestStnMarker);

//        this.nearestStnMarker.setIcon(closestStopIconDrawable);

        if(this.nearestStnMarker!=null){
            this.nearestStnMarker.setIcon(closestStopIconDrawable);
          //  stopInfoWindow.onOpen(this.nearestStnMarker);
          //  stopClusterer.add(this.nearestStnMarker);
            for (Marker m:stopMarkerMap.values())
            {
                if(m!=this.nearestStnMarker)
                {
                    m.setIcon(stopIconDrawable);
                    //stopInfoWindow.onOpen(m);
                    //stopClusterer.add(m);
                }


            }
        }
        else
        {
            this.nearestStnMarker=null;
            for (Marker m:stopMarkerMap.values())
            {
                m.setIcon(stopIconDrawable);
            }
        }


    }

    /**
     * Manage mapping from stops to markers using a map from stops to markers.
     * The mapping in the other direction is done using the Marker.setRelatedObject() and
     * Marker.getRelatedObject() methods.
     */
    private Marker getMarker(Stop stop) { return stopMarkerMap.get(stop); }
    private void setMarker(Stop stop, Marker marker) { stopMarkerMap.put(stop, marker); }
    private void clearMarker(Stop stop) { stopMarkerMap.remove(stop); }
    private void clearMarkers() { stopMarkerMap.clear(); }
}
