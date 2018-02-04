package sg.vinova.decoration_home.HomeDecoration;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.ResultCode;
import com.maxst.ar.SensorDevice;
import com.maxst.ar.TrackerManager;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.IDisplay;
import org.rajawali3d.view.ISurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.vinova.decoration_home.ARActivity;
import sg.vinova.decoration_home.R;
import sg.vinova.decoration_home.util.SampleUtil;

public class HomeDecorateActivity extends ARActivity implements View.OnTouchListener, IDisplay {

    private InstantTrackerRenderer instantTargetRenderer;
    private int preferCameraResolution = 0;
    private GLSurfaceView glSurfaceView;
    @BindView(R.id.start)
    AppCompatButton btnStart;
    @BindView(R.id.rajwali_surface)
    ISurface mRenderSurface;

    ISurfaceRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_decor);
        ButterKnife.bind(this);
        preferCameraResolution = getSharedPreferences(SampleUtil.PREF_NAME, Activity.MODE_PRIVATE).getInt(SampleUtil.PREF_KEY_CAM_RESOLUTION, 0);
        mRenderSurface = (ISurface) findViewById(R.id.rajwali_surface);
        init();


    }



    private void init() {
        // Create the renderer
        mRenderer = createRenderer();
        onBeforeApplyRenderer();
        applyRenderer();

        }

    protected void onBeforeApplyRenderer() {

    }

    @CallSuper
    protected void applyRenderer() {
        mRenderSurface.setSurfaceRenderer(mRenderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRenderer.onResume();

        SensorDevice.getInstance().start();
        TrackerManager.getInstance().startTracker(TrackerManager.TRACKER_TYPE_INSTANT);

        ResultCode resultCode = ResultCode.Success;

        switch (preferCameraResolution) {
            case 0:
                resultCode = CameraDevice.getInstance().start(0, 640, 480);
                break;

            case 1:
                resultCode = CameraDevice.getInstance().start(0, 1280, 720);
                break;
        }

        if (resultCode != ResultCode.Success) {
            Toast.makeText(this, R.string.camera_open_fail, Toast.LENGTH_SHORT).show();
            finish();
        }

        MaxstAR.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        mRenderer.onPause();

        TrackerManager.getInstance().quitFindingSurface();
        TrackerManager.getInstance().stopTracker();
        CameraDevice.getInstance().stop();
        SensorDevice.getInstance().stop();

        MaxstAR.onPause();
    }

    @OnClick(R.id.start)
    void onClickStartButton(){
        String text = btnStart.getText().toString();
        if (text.equals(getResources().getString(R.string.start_tracking))) {
            TrackerManager.getInstance().findSurface();
            instantTargetRenderer.resetPosition();
            btnStart.setText(getResources().getString(R.string.stop_tracking));
        } else {
            TrackerManager.getInstance().quitFindingSurface();
            btnStart.setText(getResources().getString(R.string.start_tracking));
        }
    }

    private static final float TOUCH_TOLERANCE = 5;
    private float touchStartX;
    private float touchStartY;
    private float translationX;
    private float translationY;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchStartX = x;
                touchStartY = y;

                final float[] screen = new float[2];
                screen[0] = x;
                screen[1] = y;

                final float[] world = new float[3];

                TrackerManager.getInstance().getWorldPositionFromScreenCoordinate(screen, world);
                translationX = world[0];
                translationY = world[1];
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float dx = Math.abs(x - touchStartX);
                float dy = Math.abs(y - touchStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    touchStartX = x;
                    touchStartY = y;

                    final float[] screen = new float[2];
                    screen[0] = x;
                    screen[1] = y;

                    final float[] world = new float[3];

                    TrackerManager.getInstance().getWorldPositionFromScreenCoordinate(screen, world);
                    float posX = world[0];
                    float posY = world[1];

                    instantTargetRenderer.setTranslate(posX - translationX, posY - translationY);
                    translationX = posX;
                    translationY = posY;
                }
                break;
            }

            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }


    @Override
    public ISurfaceRenderer createRenderer() {
        return new LoadModelRenderer(HomeDecorateActivity.this,HomeDecorateActivity.this);
    }


    protected static abstract class AExampleRenderer extends Renderer {

        final HomeDecorateActivity activity;

        public AExampleRenderer(Context context, @Nullable HomeDecorateActivity activity) {
            super(context);
            this.activity = activity;
        }

        @Override
        public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {

            super.onRenderSurfaceCreated(config, gl, width, height);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
        }

    }


    private final class LoadModelRenderer extends AExampleRenderer {
        private PointLight mLight;
        private Object3D mObjectGroup;
        private Animation3D mCameraAnim, mLightAnim;

        public LoadModelRenderer(Context context, @Nullable HomeDecorateActivity activity) {
            super(context, activity);
        }

        @Override
        protected void initScene() {
            mLight = new PointLight();
            mLight.setPosition(0, 0, 4);
            mLight.setPower(3);

            getCurrentScene().addLight(mLight);
            getCurrentCamera().setZ(16);

            LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                    mTextureManager, R.raw.chair);
            try {
                objParser.parse();
                mObjectGroup = objParser.getParsedObject();
                getCurrentScene().addChild(mObjectGroup);

                mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 360);
                mCameraAnim.setDurationMilliseconds(8000);
                mCameraAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
                mCameraAnim.setTransformable3D(mObjectGroup);
            } catch (ParsingException e) {
                e.printStackTrace();
            }

            mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                    new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                    360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

            mLightAnim.setDurationMilliseconds(3000);
            mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            mLightAnim.setTransformable3D(mLight);

            getCurrentScene().registerAnimation(mCameraAnim);
            getCurrentScene().registerAnimation(mLightAnim);

            mCameraAnim.play();
            mLightAnim.play();
        }

    }
}
