package main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Pair;
import android.util.SparseArray;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Texture;

/**
 * Created by spx on 21.06.15.
 */
abstract public class GUIElement {
    public Texture icon = null;
    public int x = 0;
    public int y = 0;
    public OnTouchCallback callback = null;
    public OnTouchCallback upCallback = null;
    public boolean waitingForGesture = true;
    public int pointerId = 0;
    public boolean isAlreadyClicked = false;

    public long startGestureTime = -1;
    public long timeDelta_ms = 0;

    public GUIElement(Activity main, int resource, int width, int height)
    {
        icon = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(), resource), width, height, true), true);
    }

    abstract public void render(FrameBuffer fb);

    public void setOnTouchCallback(OnTouchCallback acallback)
    {
         callback = acallback;
    }

    public void setOnUntouchCallback(OnTouchCallback acallback)
    {
        upCallback = acallback;
    }

    private int assignAppropriateFreePointer(SparseArray<Pair<Boolean, Point>> touches)
    {
        for (int i = 0; i < touches.size(); i++)
        {
            int key = touches.keyAt(i);
            Pair<Boolean, Point> p = touches.get(key);
            int posX = p.second.x;
            int posY = p.second.y;
            if (p.first == false && posX >= x && posY >= y &&
                    posX <= x + icon.getWidth() && posY <= y + icon.getHeight())
            {
                touches.setValueAt(key, new Pair(true, new Point(p.second.x, p.second.y)));
                return key;
            }
        }
        return -1;
    }

    void checkClicks(SparseArray<Pair<Boolean, Point>> touches)
    {
        /* */
          if (waitingForGesture)
          {
              pointerId = assignAppropriateFreePointer(touches);
              if (pointerId == -1) return;
              else
              {
                  startGestureTime = System.currentTimeMillis();
                  waitingForGesture = false;
                  isAlreadyClicked = true;
              }
          }

          Pair<Boolean, Point> p = touches.get(pointerId);
          if (p == null)
          {
               if (startGestureTime > 0)
                timeDelta_ms = System.currentTimeMillis() - startGestureTime;
               waitingForGesture = true;
               if (upCallback != null)
                    upCallback.call(this, 0, 0);
               timeDelta_ms = 0;
               startGestureTime = -1;
               isAlreadyClicked = false;
               return;
          }

          if (!waitingForGesture)
          {
               int posX = p.second.x;
               int posY = p.second.y;

               if ((posX >= x && posY >= y &&
                   posX <= x + icon.getWidth() && posY <= y + icon.getHeight()) || isAlreadyClicked)
               {
                   if (callback != null)
                   {
                       callback.call(this, posX, posY);
                   }

               }
          }
        /* */
    }

    public void setPosition(int ax, int ay)
    {
        x = ax;
        y = ay;
    }
}
