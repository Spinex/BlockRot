package main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Texture;

/**
 * Created by spx on 22.06.15.
 */
public class GUIController extends GUIElement {

    public Texture stickIcon;
    //wspolrzedne 0 0 to srodek kontrolera
    int stickOffsetX;
    int stickOffsetY;

    int controllerRadius;

    public GUIController(Activity main, int resource, int width, int height, int stickResource, int stickWidth, int stickHeight, int radius)
    {
        super(main, resource, width, height);
        stickOffsetX = 0;
        stickOffsetY = 0;

        controllerRadius = radius;

        stickIcon = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(), stickResource), stickWidth, stickHeight, true), true);
    }

    double getNormalizedFactorX()
    {
        return (double)stickOffsetX / (icon.getWidth() / 2);
    }

    double getNormalizedFactorY()
    {
        return (double)stickOffsetY / (icon.getHeight() / 2);
    }

    @Override
    public void render(FrameBuffer fb) {
        int width = icon.getWidth();
        int height = icon.getHeight();

        fb.blit(icon, 0, 0, x, y, width, height, true);

        int stickWidth = stickIcon.getWidth();
        int stickHeight = stickIcon.getHeight();

        fb.blit(stickIcon, 0, 0, x + width/2 - stickWidth/2 + stickOffsetX, y + height/2 - stickHeight/2 - stickOffsetY, stickWidth, stickHeight, true);
    }
}
