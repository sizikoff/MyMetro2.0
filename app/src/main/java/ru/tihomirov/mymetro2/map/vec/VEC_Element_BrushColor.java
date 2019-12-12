package ru.tihomirov.mymetro2.map.vec;


import android.graphics.Canvas;
import android.graphics.Paint;

import ru.tihomirov.mymetro2.util.ExtInteger;

/**
 * Created by Utyf on 27.02.2015.
 *
 */


public class VEC_Element_BrushColor extends VEC_Element {

    int Color;

    public VEC_Element_BrushColor(String param, VEC vv) {
        super(vv);
        Color = ExtInteger.parseInt(param, 16);
    }

    @Override
    public void Draw(Canvas canvas, Paint paint) {
        v.currBrushColor = Color;
    }
}
