package nl.ehi2vsd5.hboict.creazapp.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import java.util.Random;

/**
 * @author Youri Tomassen
 * @author Creaz
 */

public class Util {

    public static int getRandomMaterialColor(Context context, int arrayId, long sourceId) {
        int returnColor = Color.GRAY;

        if (arrayId != 0) {
            Random random = new Random(sourceId);
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = random.nextInt(colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }
}
