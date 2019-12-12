package ru.tihomirov.mymetro2.map.vec;

import android.graphics.Canvas;
import android.graphics.Paint;

import ru.tihomirov.mymetro2.util.ExtInteger;


public class VEC_Element_PenColor extends VEC_Element {

    int Color;

    public VEC_Element_PenColor(String param, VEC vv) {
        super(vv);
        if( param.isEmpty() ) Color=0;
        else Color = ExtInteger.parseInt(param, 16);
    }

    @Override
    public void Draw(Canvas canvas, Paint paint) {
        paint.setColor( Color + v.Opaque );
    }
}
