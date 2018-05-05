package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// A bus route drawer
public class BusRouteDrawer extends MapViewOverlay {
    /** overlay used to display bus route legend text on a layer above the map */
    private BusRouteLegendOverlay busRouteLegendOverlay;
    /** overlays used to plot bus routes */
    private List<Polyline> busRouteOverlays;

    /**
     * Constructor
     * @param context   the application context
     * @param mapView   the map view
     */
    public BusRouteDrawer(Context context, MapView mapView) {
        super(context, mapView);
        busRouteLegendOverlay = createBusRouteLegendOverlay();
        busRouteOverlays = new ArrayList<>();
    }

    /**
     * Plot each visible segment of each route pattern of each route going through the selected stop.
     */
    public void plotRoutes(int zoomLevel) {
        //TODO: complete the implementation of this method (Task 7)
        updateVisibleArea();
        Stop st= StopManager.getInstance().getSelected();
        busRouteLegendOverlay.clear();
        busRouteOverlays.clear();
        if(st!=null)
        {
            System.out.println("selected: "+st.getName());
            for (Route r: st.getRoutes()) {
                System.out.println("Route: "+r.getNumber());
                busRouteLegendOverlay.add(r.getNumber());
                for (RoutePattern rp : r.getPatterns())
                {
                    Iterator<LatLon> is1=rp.getPath().iterator();
                    Iterator<LatLon> is2=rp.getPath().iterator();
                    is1.next();
                    while (is1.hasNext()&&is2.hasNext())
                    {
                        LatLon s1=is1.next();
                        LatLon s2=is2.next();
                      //  if ((Geometry.rectangleContainsPoint(northWest, southEast,s1)&&Geometry.rectangleContainsPoint(northWest, southEast, s2))||(Geometry.rectangleIntersectsLine(northWest,southEast,s1,s2)))
                        if(Geometry.rectangleIntersectsLine(northWest,southEast,s1,s2)||(Geometry.rectangleContainsPoint(northWest,southEast,s1))||Geometry.rectangleContainsPoint(northWest,southEast,s2))
                        {
                            List points = new ArrayList<GeoPoint>();
                            Polyline p = new Polyline(context);

                            p.setColor(busRouteLegendOverlay.getColor(r.getNumber()));
                            p.setWidth(getLineWidth(zoomLevel));
                            points.add(Geometry.gpFromLL(s1));
                            points.add(Geometry.gpFromLL(s2));
                            p.setPoints(points);
                            p.setVisible(true);
                            busRouteOverlays.add(p);

                        }
                    }
                }

        }





        }

    }

    public List<Polyline> getBusRouteOverlays() {
        return Collections.unmodifiableList(busRouteOverlays);
    }

    public BusRouteLegendOverlay getBusRouteLegendOverlay() {
        return busRouteLegendOverlay;
    }


    /**
     * Create text overlay to display bus route colours
     */
    private BusRouteLegendOverlay createBusRouteLegendOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);
        return new BusRouteLegendOverlay(rp, BusesAreUs.dpiFactor());
    }

    /**
     * Get width of line used to plot bus route based on zoom level
     * @param zoomLevel   the zoom level of the map
     * @return            width of line used to plot bus route
     */
    private float getLineWidth(int zoomLevel) {
        if(zoomLevel > 14)
            return 7.0f * BusesAreUs.dpiFactor();
        else if(zoomLevel > 10)
            return 5.0f * BusesAreUs.dpiFactor();
        else
            return 2.0f * BusesAreUs.dpiFactor();
    }
}
