package ru.tihomirov.mymetro2.map.vec;

import android.graphics.Canvas;
import android.graphics.Paint;

import ru.tihomirov.mymetro2.util.ExtFloat;


public class VEC_Element_AngleTextOut extends VEC_Element_TextOut {

    float angle;

    public VEC_Element_AngleTextOut(String param, VEC vv) {
        super( param.substring(param.indexOf(',')+1), vv );  // skip first parameter - angle

        String[] strs=param.split(",");
        angle = -1 * ExtFloat.parseFloat(strs[0]);
    }

    @Override
    public void Draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.rotate(angle, x,y);

        super.Draw(canvas,paint);

        canvas.restore();
    }
}
