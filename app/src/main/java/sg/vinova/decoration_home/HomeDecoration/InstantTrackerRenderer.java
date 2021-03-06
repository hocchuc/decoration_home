/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */

package sg.vinova.decoration_home.HomeDecoration;

import android.app.Activity;
import android.graphics.Bitmap;
import com.maxst.ar.CameraDevice;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import com.maxst.ar.MaxstAR;
import com.maxst.ar.MaxstARUtil;
import com.maxst.ar.Trackable;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.TrackingResult;
import com.maxst.ar.TrackingState;

import sg.vinova.decoration_home.arobject.ChariObject;
import sg.vinova.decoration_home.arobject.TexturedCube;
import sg.vinova.decoration_home.util.BackgroundRenderHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class InstantTrackerRenderer implements Renderer {

	public static final String TAG = InstantTrackerRenderer.class.getSimpleName();

	private int surfaceWidth;
	private int surfaceHeight;
	private BackgroundRenderHelper backgroundRenderHelper;
	private ChariObject chariObject;
	private TexturedCube texturedCube;
	private float posX;
	private float posY;
	private Activity activity;

	InstantTrackerRenderer(Activity activity) {
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

		chariObject.setTransform(trackable.getPoseMatrix());
		chariObject.setTranslate(posX, posY, -0.05f);
		chariObject.setProjectionMatrix(projectionMatrix);
		chariObject.draw();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {

		surfaceWidth = width;
		surfaceHeight = height;

		chariObject.setScale(0.3f, 0.3f, 1f);

		MaxstAR.onSurfaceChanged(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		backgroundRenderHelper = new BackgroundRenderHelper();
		backgroundRenderHelper.init();

		chariObject = new ChariObject();
		Bitmap bitmap = MaxstARUtil.getBitmapFromAsset("MaxstAR_Cube.png", activity.getAssets());
		chariObject.setTextureBitmap(bitmap);

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
