package ru.tihomirov.mymetro2.map;

import ru.tihomirov.mymetro2.util.StationsNum;

/**
 * Created by Utyf on 22.03.2015.
 *
 */

public class RouteNode extends StationsNum {
    public float  time;  // time arriving to station

    public RouteNode(StationsNum stn) {
        super( stn.trp, stn.line, stn.stn );
    }

    public RouteNode(int t, int l, int s) {
        super( t, l, s );
    }
}
