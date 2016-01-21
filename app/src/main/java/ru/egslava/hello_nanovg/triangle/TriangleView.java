package ru.egslava.hello_nanovg.triangle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import ru.egslava.hello_nanovg.JNI;

/**
 * A simple GLSurfaceView sub-class that demonstrate how to perform
 * OpenGL ES 2.0 rendering into a GL Surface. Note the following important
 * details:
 *
 * - The class must use a custom context factory to enable 2.0 rendering.
 *   See ContextFactory class definition below.
 *
 * - The class must use a custom EGLConfigChooser to be able to select
 *   an EGLConfig that supports 2.0. This is done by providing a config
 *   specification to eglChooseConfig() that has the attribute
 *   EGL10.ELG_RENDERABLE_TYPE containing the EGL_OPENGL_ES2_BIT flag
 *   set. See ConfigChooser class definition below.
 *
 * - The class must select the surface's format, then choose an EGLConfig
 *   that matches it exactly (with regards to red/green/blue/alpha channels
 *   bit depths). Failure to do so would result in an EGL_BAD_MATCH error.
 */
public class TriangleView extends GLSurfaceView {
    static String TAG = "GL2JNIView";
    static final boolean DEBUG = false;
    private GestureDetector movingGestureDetector;

    public TriangleView(Context context) {
        super(context);
        init(false, 0, 0);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(false, 0, 0);
    }

    private void init(boolean translucent, int depth, int stencil) {

        /* By default, GLSurfaceView() creates a RGB_565 opaque surface.
         * If we want a translucent one, we should change the surface's
         * format here, using PixelFormat.TRANSLUCENT for GL Surfaces
         * is interpreted as any 32-bit surface with alpha by SurfaceFlinger.
         */
        if (translucent) {
            this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }

        /* Setup the context factory for 2.0 rendering.
         * See ContextFactory class definition below
         */
        setEGLContextFactory(new ContextFactory());

        /* We need to choose an EGLConfig that matches the format of
         * our surface exactly. This is going to be done in our
         * custom config chooser. See ConfigChooser class definition
         * below.
         */
        setEGLConfigChooser( translucent ?
                             new ConfigChooser(8, 8, 8, 8, depth, stencil) :
                             new ConfigChooser(5, 6, 5, 0, depth, stencil) );

        /* Set the renderer responsible for frame rendering */
        setRenderer(new Renderer());

        final TypedArray attrs = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackground});
        final int bgColor = attrs.getColor(0, 0xFFF);
        attrs.recycle();
        final float r = ((float) Color.red(bgColor)) / 255.f,
            g = ((float) Color.green(bgColor)) / 255.f,
            b = ((float) Color.blue(bgColor)) / 255.f,
            a = ((float) Color.alpha(bgColor)) / 255.f;

        JNI.setBackgroundColor(r, g, b, a);

        initFlinger();  // I don't use isInEditMode because it all the same will crash on native interface loading
    }

    private static class Renderer implements GLSurfaceView.Renderer {
        public void onDrawFrame(GL10 gl) {
            JNI.step();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            JNI.init(width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Do nothing.
        }
    }



    static void checkEglError(String prompt, EGL10 egl) {
        int error;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
            Log.e(TAG, String.format("%s: EGL error: 0x%x", prompt, error));
        }
    }



    OverScroller flinger;
    private void initFlinger(){
        flinger = new OverScroller(getContext());
        movingGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                flinger.abortAnimation();
                flinger.startScroll(flinger.getCurrX(), flinger.getCurrY(), -(int)distanceX, -(int)distanceY);
                invalidate();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                flinger.fling(flinger.getCurrX(), flinger.getCurrY(), (int) velocityX, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
                invalidate();
                return true;
            }
        });
    }

    @Override
    public void computeScroll() {

        int lastX = flinger.getCurrX();
        int lastY = flinger.getCurrY();
        if (flinger.computeScrollOffset()){
            onScrollChanged(flinger.getCurrX(), flinger.getCurrY(), lastX, lastY);
        }
        super.computeScroll();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!flinger.isFinished()){
            ViewCompat.postInvalidateOnAnimation(this);
        }
        JNI.setTranslation(l, t);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = movingGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
        if (result)getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }
}
