package main;

import android.app.Activity;

import com.threed.jpct.FrameBuffer;

/**
 * Created by spx on 21.06.15.
 */
public class GUIButton extends GUIElement {
    public GUIButton(Activity main, int resource, int width, int height)
    {
        super(main, resource, width, height);
    }

    @Override
    public void render(FrameBuffer fb) {
        int width = icon.getWidth();
        int height = icon.getHeight();

        fb.blit(icon, 0, 0, x, y, width, height, true);
    }

}
