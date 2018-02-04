package sg.vinova.decoration_home.FloorChange;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.MaxstARUtil;
import com.maxst.ar.Trackable;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.TrackingResult;
import com.maxst.ar.TrackingState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import sg.vinova.decoration_home.arobject.ColoredCube;
import sg.vinova.decoration_home.arobject.TexturedCube;
import sg.vinova.decoration_home.arobject.TileObject;
import sg.vinova.decoration_home.util.BackgroundRenderHelper;

public class TileTrackerRenderer implements GLSurfaceView.Renderer {

    public static final String TAG = TileTrackerRenderer.class.getSimpleName();

    private int surfaceWidth;
    private int surfaceHeight;
    private BackgroundRenderHelper backgroundRenderHelper;

    private ColoredCube texturedCube;
    private float posX;
    private float posY;
    private Activity activity;

    TileTrackerRenderer(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);

        TrackingState state = TrackerManager.getInstance().updateTrackingState();
        TrackingResult trackingResult = state.getTrackingResult();

        backgroundRenderHelper.drawBackground();

        if (trackingResult.getCount() == 0) {
            return;
        }

        float [] projectionMatrix = CameraDevice.getInstance().getProjectionMatrix();

        Trackable trackable = trackingResult.getTrackable(0);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        texturedCube.setTransform(trackable.getPoseMatrix());
        texturedCube.setTranslate(posX, posY, -0.05f);
        texturedCube.setProjectionMatrix(projectionMatrix);
        texturedCube.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        surfaceWidth = width;
        surfaceHeight = height;

        texturedCube.setScale(0.3f, 0.3f, 0.01f);

        MaxstAR.onSurfaceChanged(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        backgroundRenderHelper = new BackgroundRenderHelper();
        backgroundRenderHelper.init();

        texturedCube = new ColoredCube();

        Bitmap bitmap = MaxstARUtil.getBitmapFromAsset("Tile/bathroom-tiles.jpg", activity.getAssets());
        //texturedCube.setTextureBitmap(bitmap);

        MaxstAR.onSurfaceCreated();
    }




    void setTranslate(float x, float y) {
        posX += x;
        posY += y;
    }

    void resetPosition() {
        posX = 0;
        posY = 0;
    }
}