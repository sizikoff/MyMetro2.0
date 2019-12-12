package ru.tihomirov.mymetro2.map.vec;

import android.graphics.DashPathEffect;

/**
 * Created by Utyf on 27.02.2015.
 *
 */

public class VEC_Element_Dashed extends VEC_Element_Line {

    public VEC_Element_Dashed(String param, VEC vv) {
        super(param, vv);
        dashPathEffect = new DashPathEffect(
                new float[]{7f*v.scale, 2f*v.scale}, //interval
                0);       //phase
    }
}
