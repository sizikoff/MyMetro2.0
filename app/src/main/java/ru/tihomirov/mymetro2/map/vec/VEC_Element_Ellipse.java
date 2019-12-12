package ru.tihomirov.mymetro2.map.vec;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import ru.tihomirov.mymetro2.util.ExtFloat;

/**
 * Created by Utyf on 27.02.2015.
 *
 */


public class VEC_Element_Ellipse extends VEC_Element {
    float    Width, x1,y1,x2,y2;
    RectF    rct;

    public VEC_Element_Ellipse(String param, VEC vv) {
        super(vv);

        String[] strs=param.split(",");

        x1 = ExtFloat.parseFloat(strs[0])*v.scale;
        y1 = ExtFloat.parseFloat(strs[1])*v.scale;
        x2 = ExtFloat.parseFloat(strs[2])*v.scale;
        y2 = ExtFloat.parseFloat(strs[3])*v.scale;
        if( strs.length < 5 )   Width = 1*v.scale;
        else Width = ExtFloat.parseFloat(strs[4])*v.scale;
        rct = new RectF(x1, y1, x2, y2);
    }

    @Override
    public void Draw(Canvas canvas, Paint p) {
        float        wd;
        Paint.Style  ps;

        wd = p.getStrokeWidth();
        ps = p.getStyle();

        if( v.currBrushColor!=-1 ) {
            int clr = p.getColor();
            p.setStyle(Paint.Style.FILL);
            p.setColor(v.currBrushColor + v.Opaque);
            canvas.drawOval(rct, p);
            p.setColor( clr );
        }

        p.setStrokeWidth(Width);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawOval(rct, p);

        p.setStyle( ps );
        p.setStrokeWidth( wd );
    }
}
