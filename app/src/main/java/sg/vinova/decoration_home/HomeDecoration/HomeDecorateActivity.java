package sg.vinova.decoration_home.HomeDecoration;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.ResultCode;
import com.maxst.ar.SensorDevice;
import com.maxst.ar.TrackerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.vinova.decoration_home.ARActivity;
import sg.vinova.decoration_home.R;
import sg.vinova.decoration_home.services.SceneLoader;
import sg.vinova.decoration_home.util.SampleUtil;
import sg.vinova.decoration_home.view.ModelSurfaceView;

public class HomeDecorateActivity extends ARActivity implements View.OnTouchListener {

    private InstantTrackerRenderer instantTargetRenderer;
    private int preferCameraResolution = 0;
    private GLSurfaceView glSurfaceView;
    @BindView(R.id.start)
    AppCompatButton btnStart;

    private static final int REQUEST_CODE_OPEN_FILE = 1000;

    private String paramAssetDir;
    private String paramAssetFilename;
    /**
     * The file to load. Passed as input parameter
     */
    private String paramFilename;
    /**
     * Enter into Android Immersive mode so the renderer is full screen or not
     */
    private boolean immersiveMode = true;
    /**
     * Background GL clear color. Default is light gray
     */
    private float[] backgroundColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};

    private ModelSurfaceView gLView;

    private SceneLoader scene;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Try to get input parameters
        Bundle b = getIntent().getExtras();
        if (b != null) {
            this.paramAssetDir = b.getString("assetDir");
            this.paramAssetFilename = b.getString("assetFilename");
            this.paramFilename = b.getString("uri");
            this.immersiveMode = "true".equalsIgnoreCase(b.getString("immersiveMode"));
            try{
                String[] backgroundColors = b.getString("backgroundColor").split(" ");
                backgroundColor[0] = Float.parseFloat(backgroundColors[0]);
                backgroundColor[1] = Float.parseFloat(backgroundColors[1]);
                backgroundColor[2] = Float.parseFloat(backgroundColors[2]);
                backgroundColor[3] = Float.parseFloat(backgroundColors[3]);
            }catch(Exception ex){
                // Assuming default background color
            }
        }
        Log.i("Renderer", "Params: assetDir '" + paramAssetDir + "', assetFilename '" + paramAssetFilename + "', uri '"
                + paramFilename + "'");

        handler = new Handler(getMainLooper());
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.

        setContentView(R.layout.activity_home_decor);
        ButterKnife.bind(this);

        init();
    }

    private void init() {


        instantTargetRenderer = new InstantTrackerRenderer(this);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(instantTargetRenderer);
        glSurfaceView.setOnTouchListener(this);

        preferCameraResolution = getSharedPreferences(SampleUtil.PREF_NAME, Activity.MODE_PRIVATE).getInt(SampleUtil.PREF_KEY_CAM_RESOLUTION, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
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
        glSurfaceView.onPause();

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
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
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
                    instantTargetRenderer.setAngle(
                            instantTargetRenderer.getAngle() +
                                    ((dx + dy) * TOUCH_SCALE_FACTOR));

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



}
