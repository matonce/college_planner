package emdogan.projekt;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by emdogan on 2/8/19.
 */

/* how to use

Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
tvIcon1 = (TextView) findViewById(R.id.tvIcon1); 
tvIcon2 = (TextView) findViewById(R.id.tvIcon2);
 tvIcon3 = (TextView) findViewById(R.id.tvIcon3);
tvIcon1.setTypeface(iconFont);
tvIcon2.setTypeface(iconFont);
tvIcon3.setTypeface(iconFont);

*/

public class FontManager {
    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";
    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void markAsIconContainer(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                markAsIconContainer(child, typeface);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        } else if (v instanceof Button){
            ((Button) v).setTypeface(typeface);
        }
    }
}
