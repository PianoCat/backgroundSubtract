package com.zhy.backgroundsubtract;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity implements CvCameraViewListener {

	private ImageView imageView1 = null;
	private ImageView imageView2 = null;
	private AnimationDrawable animationDrawable1 = null;
	private AnimationDrawable animationDrawable2 = null;

	private BackgroundSubtractorMOG2 mog2 = null;
	private Mat foreground = null;
	private Mat frame = null;

	private String imgPath = "/storage/external_SD/images/WavingTrees/";

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {

				imageView1 = (ImageView) findViewById(R.id.imageview);
				imageView2 = (ImageView) findViewById(R.id.imageview2);

				animationDrawable1 = new AnimationDrawable();
				animationDrawable2 = new AnimationDrawable();
				
				mog2 = new BackgroundSubtractorMOG2();
				foreground = new Mat();

				for (int i = 0; i < 287; i++) {
					String imgStr = imgPath + "b00";
					if (i < 10)
						imgStr += "00" + i + ".bmp";
					else if (i < 100)
						imgStr += "0" + i + ".bmp";
					else
						imgStr += i + ".bmp";
					
					animationDrawable1.addFrame(
							Drawable.createFromPath(imgStr), 100);

					frame = Highgui.imread(imgStr, 0);
					
					// 运动前景检测
					mog2.apply(frame, foreground, 0.001);
					// 腐蚀+膨胀
					Imgproc.erode(foreground, foreground, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(5, 5)));
					
					Imgproc.dilate(foreground, foreground, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(5, 5)));
					
					Imgproc.erode(foreground, foreground, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(4, 4)));
					
					Imgproc.dilate(foreground, foreground, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(4, 4)));

					Highgui.imwrite(imgPath + "result.bmp", foreground);

					animationDrawable2.addFrame(
							Drawable.createFromPath(imgPath + "result.bmp"),
							100);
				}

				// 设置是否重复播放，false为重复
				animationDrawable1.setOneShot(false);
				animationDrawable2.setOneShot(false);
				imageView1.setImageDrawable(animationDrawable1);
				imageView2.setImageDrawable(animationDrawable2);
				animationDrawable1.start();
				animationDrawable2.start();

			}
				break;
			default:
				super.onManagerConnected(status);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				mLoaderCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {

	}

	@Override
	public void onCameraViewStopped() {

	}

	@Override
	public Mat onCameraFrame(Mat inputFrame) {
		return null;
	}

}
