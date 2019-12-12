package ru.tihomirov.mymetro2.map;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import ru.tihomirov.mymetro2.MapActivity;
import ru.tihomirov.mymetro2.map.vec.VEC;
import ru.tihomirov.mymetro2.util.ExtPointF;
import ru.tihomirov.mymetro2.util.StationsNum;
import ru.tihomirov.mymetro2.util.ExtFloat;
import ru.tihomirov.mymetro2.util.Util;

import java.util.ArrayList;

/**
 * Created by Utyf on 25.02.2015.
 *
 */


public class MAP extends Parameters {

    public  String  name;
    String   ImageFileName;
    float    StationDiameter,StationRadius;
    float    LinesWidth;
    boolean  UpperCase;
    boolean  WordWrap;
    boolean  IsVector;
    String[] Transports;
    int[]    allowedTRPs, activeTRPs;
    private  VEC[]   vecs;
    StationLabels  stnLabels = new StationLabels();
    private  Line[] lines;
    Paint     p;
    //public boolean   isLoaded;

    public MAP() {
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setFilterBitmap(true);  // todo switch by settings
    }

    public synchronized int load(String nm) {  // loading map file
        int     i;
        Line    ll;
        param   prm;

        //isLoaded = false;
        MapData.isReady = false;
        name = nm;
        if( super.load(name)<0 ) return -1;

        WordWrap = true;
        IsVector = true;

        Section secOpt = getSec("Options");
        ImageFileName = secOpt.getParamValue("ImageFileName");
        UpperCase = secOpt.getParamValue("UpperCase").trim().toLowerCase().equals("true");

        String str = secOpt.getParamValue("Transports");
        if( !str.isEmpty() ) {
            Transports =str.split(",");
            for ( i=0; i < Transports.length; i++ )     Transports[i] = Transports[i].trim();
            allowedTRPs = new int[Transports.length];
            for ( i=0; i < Transports.length; i++ )    allowedTRPs[i] = TRP.getTRPnum(Transports[i]);
        } else {
            allowedTRPs = new int[TRP.getSize()]; // if no transport parameter - set all transports as allowed
            for ( i=0; i < allowedTRPs.length; i++ )   allowedTRPs[i] = i;
        }

        TRP.setAllowed(allowedTRPs);

        // copy from Transport
        int size=0, ii=0;
        for( int k : allowedTRPs )  if( k!=-1) size++;
        activeTRPs = new int[size];
        for( int k : allowedTRPs )  if( k!=-1) activeTRPs[ii++]=k;

        //if( TRP.routeStart==null )  // do not change active TRP if route marked
            TRP.setActive(activeTRPs);

        StationDiameter = ExtFloat.parseFloat(secOpt.getParamValue("StationDiameter"));
        if( StationDiameter==0 ) StationDiameter = 16f;
        StationRadius = StationDiameter/2;
        LinesWidth = ExtFloat.parseFloat(secOpt.getParamValue("LinesWidth"));
        if( LinesWidth==0 )  LinesWidth = StationDiameter * 0.5625f;  //  9/16

        prm = secOpt.getParam("WordWrap");
        if( prm!=null )
            WordWrap = prm.value.toLowerCase().equals("true");
        str = secOpt.getParamValue("IsVector");
        IsVector = str.isEmpty() || str.toLowerCase().equals("true") || str.equals("1");

        String[] strs = ImageFileName.split(",");
        vecs = new VEC[strs.length];
        for( i=0; i<strs.length; i++ )  {
            vecs[i] = new VEC();
            vecs[i].load(strs[i]);
        }
        System.gc();

        i = 1;
        stnLabels.clear();
        if( secsNum()>i && getSec(i).name.equals("StationLabels") )   // if StationLabels exist, it always second section
            stnLabels.load(getSec(i++));

        Section addSec=null;
        ArrayList<Line> la = new ArrayList<>();
        for( ; i<secsNum(); i++ ) {             // parsing lines parameters
            if( getSec(i).name.equals("AdditionalNodes") )  // last section
                { addSec=getSec(i);  break; }
            ll = new Line(getSec(i), this);
            la.add(ll);
        }
        lines = la.toArray(new Line[la.size()]);

        if( addSec!=null )  {  // if section AdditionalNodes was found
            for( int j=0; j<addSec.ParamsNum(); j++ )  {
                // strs = addSec.getParam(j).value.split(",");
                strs = Util.split( addSec.getParam(j).value, ',' );
                if( strs==null || strs.length<5 ) continue;
                ll = getLine(strs[0]);
                if( ll!=null )  ll.addAddNode(strs);
                else    Log.e("MAP /137", "Wrong line name for additionalNode - "+ addSec.getParam(j).value);
            }
        }

        for( Line l : lines )  l.CreatePath(); // create line path for drawing

/*        try {  // to do switch by settings
            if( vec[0].bmp!=null ) {
                // bmp = Bitmap.createBitmap((int)(vec.sclSize.x), (int)(vec.sclSize.y), Bitmap.Config.ARGB_8888);
                bmp = vecs[0].bmp;
                Canvas c = new Canvas(bmp);
                for ( VEC vv : vecs )
                    vv.DrawVEC(c);
                DrawMAP(c, false);
            }
        } catch (OutOfMemoryError E) {
            Log.e("MAP /115","Catch Out Of Memory error");
            bmp = null;
        } */

        secs=null;    // free memory
        MapData.isReady = true;
        //MapActivity.mapActivity.mapView.contentChanged(null);
        return 0;
    }

    public PointF getSize() {  // todo   adjust to all vecs
        if( vecs==null || vecs[0]==null ) return new PointF();
        return vecs[0].Size;
    }

    public void setActiveTransports() {
        //if( TRP.routeStart==null )  // do not change active if route marked
        TRP.setAllowed(allowedTRPs);
        TRP.setActive(activeTRPs);
    }

    public Line getLine(String nm)  {
        if( lines==null ) return null;
        for( Line ll : lines )
            if( ll.name.equals(nm) )   return ll;
        return null;
    }

    public Line getLine(int tNum, int lNum)  {
        if( lines==null ) return null;
        for( Line ll : lines )
            if( ll.trpNum==tNum && ll.lineNum==lNum )   return ll;
        return null;
    }

    private StationsNum stationByPoint(float x, float y) {
        int st;

        for( Line ll : lines )
            if( (st=ll.stationByPoint(x,y)) != -1 )
                return new StationsNum( ll.trpNum, ll.lineNum, st );

        return null;
    }

    private StationsNum[] stationsByPoint(float x, float y, int hitCircle) {  // todo  use it
        ArrayList<StationsNum> stns = new ArrayList<>();
        Integer[] st;

        for( Line ll : lines )
            if( (st=ll.stationsByPoint(x,y,hitCircle)) != null )
                for( Integer stn : st )
                  stns.add( new StationsNum( ll.trpNum, ll.lineNum, stn ) );

        if( stns.size()>0 ) return stns.toArray( new StationsNum[stns.size()] );
        return null;
    } //*/

    public String singleTap(float x, float y, int hitCircle) {
        StationsNum ls;
        StationsNum[] stns=stationsByPoint(x, y, hitCircle);

        if( stns != null ) {
            if( stns.length<2 ) ls = stns[0];
            else {
                //MapActivity.mapActivity.mapView.menuStns = stns;
                //MapActivity.mapActivity.mapView.showContextMenu();
                MapActivity.mapActivity.showStationsMenu(stns);
                return null;
                /*ls=stns[1];
                String str="hits:";
                for( StationsNum stn : stns )
                    str = str + " " + stn.trp+","+ stn.line+","+ stn.stn;
                Log.e("MAP /225",str); */
            }
            MapActivity.mapActivity.mapView.selectStation(ls);
        } else {
            if( vecs==null || vecs.length==0 || vecs[0]==null ) return null;
            String action = vecs[0].SingleTap(x,y);  // todo   proceed all vecs
            if( action==null ) {
                TRP.routeStart = TRP.routeEnd = null;
                TRP.clearRoute();
            }

            return action;
        }
        return null;
    }

    public boolean doubleTap(float x, float y) {
        if( !MapData.isReady ) return false;
        StationsNum ls=stationByPoint(x,y);
        if( ls==null )  return false;

        Intent intent = new Intent(MapActivity.mapActivity, StationInfoActivity.class);
        intent.putExtra("trp",     ls.trp);
        intent.putExtra("line",    ls.line);
        intent.putExtra("station", ls.stn);
        MapActivity.mapActivity.startActivity(intent);
        return true;
    }

    public synchronized void Draw(Canvas canvas)  {
        PointF pnt;
        Line   ll;

        int s = canvas.save();

        DrawMAP(canvas);

        if (TRP.routeStart != null && TRP.routeEnd != null) {   // greying map
            canvas.drawColor(0xb4ffffff);
        }

        if (TRP.routeExists()) {   // drawing route
            TRP.drawRoute(canvas,p);
        }

        if( TRP.routeEnd != null ) {   // mark end station
            ll = getLine(TRP.routeEnd.trp,TRP.routeEnd.line);
            if( ll!=null && !ExtPointF.isNull(pnt=ll.getCoord(TRP.routeEnd.stn)) ) {
                p.setARGB(255, 11, 5, 203);
                p.setStyle(Paint.Style.FILL);
                canvas.drawCircle(pnt.x, pnt.y, StationRadius, p);
                p.setARGB(255, 240, 40, 200);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(StationRadius/2.5f);
                canvas.drawCircle(pnt.x, pnt.y, StationRadius*0.875f, p);
                ll.drawText(canvas,TRP.routeEnd.stn);
            }
        }
        if( TRP.routeStart != null ) {  // mark start station and draw times
            ll = getLine(TRP.routeStart.trp,TRP.routeStart.line);
            if( ll!=null && !ExtPointF.isNull(pnt=ll.getCoord(TRP.routeStart.stn)) ) {
                p.setARGB(255, 10, 133, 26);
                p.setStyle(Paint.Style.FILL);
                canvas.drawCircle(pnt.x, pnt.y, StationRadius, p);
                p.setARGB(255, 240, 40, 200);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(StationRadius/2.5f);
                canvas.drawCircle(pnt.x, pnt.y, StationRadius*0.875f, p);
                ll.drawText(canvas,TRP.routeStart.stn);
            }
        }
        canvas.restoreToCount(s);
    }

    private void DrawMAP(Canvas canvas)  {
        //int s = canvas.save();
        //canvas.scale(scale, scale);           // VEC is not scaled
        for( VEC vv : vecs)  vv.Draw(canvas);   // draw background
        //canvas.restoreToCount(s);

        if( !IsVector ) {  // for pixel maps - draw times end exit
            if( TRP.routeStart!=null )
                for( Line ll : lines )  ll.drawAllTexts(canvas);
            return;
        }

        p.setStyle(Paint.Style.STROKE);
        for( Line ll : lines )
            ll.DrawLines(canvas, p);

        p.setStyle(Paint.Style.FILL);
        for( Line ll : lines )
            ll.DrawYellowStations(canvas, p);

        TRP.DrawTransfers(canvas, p, this);

        p.setStyle(Paint.Style.FILL);
        for( Line ll : lines )   ll.DrawStations    (canvas, p);
        for( Line ll : lines )   ll.DrawStationNames(canvas, p);
    }
}
