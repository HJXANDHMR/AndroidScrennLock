package com.example.android_scrennlock;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GestureLock extends View {
/**
 * 这个是自定义view，可以实现绘制图案，和解锁
 */

	private Point[][] points = new Point[3][3];
	private boolean inited = false;
	private Bitmap bitmapnormal;
	private Bitmap bitmappress;
	private Bitmap bitmaperror;
	private float bitmapR;
	float mouseX, mouseY;
	private boolean isDraw = false;
	private ArrayList<Point> pointlist = new ArrayList<Point>();
	private ArrayList<Integer> passList = new ArrayList<Integer>();

	private onDrawFinishendLister lister;

	public GestureLock(Context context) {
		super(context);
	}

	public GestureLock(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GestureLock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	Paint pressPaint = new Paint();
	Paint errorPaint = new Paint();

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!inited) {
			init();
		}
		drawPoint(canvas);
		if (pointlist.size() > 0) {
			Point a = pointlist.get(0);
			for (int i = 1; i < pointlist.size(); i++) {
				Point b = pointlist.get(i);
				drawline(canvas, a, b);
				a = b;
			}
			if (isDraw) {
				drawline(canvas, a, new Point(mouseX, mouseY));

			}

		}
	}

	private void drawline(Canvas canvas, Point a, Point b) {
		if (a.state == Point.STATE_PRESS) {
			canvas.drawLine(a.x, a.y, b.x, b.y, pressPaint);
		} else if (a.state == Point.STATE_ERROR) {
			canvas.drawLine(a.x, a.y, b.x, b.y, errorPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		int[] ij;
		int i, j;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			restPoint();
			ij = getselectPoint();
			if (ij != null) {
				isDraw = true;
				i = ij[0];
				j = ij[1];
				points[i][j].state = Point.STATE_PRESS;
				pointlist.add(points[i][j]);
				passList.add(i * 3 + j);

			}
			break;
		case MotionEvent.ACTION_MOVE:

			if (isDraw) {
				ij = getselectPoint();
				if (ij != null) {
					i = ij[0];
					j = ij[1];

					if (!pointlist.contains(points[i][j])) {
						points[i][j].state = Point.STATE_PRESS;
						pointlist.add(points[i][j]);
						passList.add(i * 3 + j);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			boolean valid = false;
			if (lister != null && isDraw) {
				valid = lister.onDrawfinished(passList);
			}
			if (!valid) {
				for (Point p : pointlist) {
					p.state = Point.STATE_ERROR;
				}
			}
			isDraw = false;
			break;

		}
		this.postInvalidate();
		return true;
	}

	private int[] getselectPoint() {
		Point point = new Point(mouseX, mouseY);
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				if (points[i][j].distance(point) < bitmapR) {
					int[] result = new int[2];
					result[0] = i;
					result[1] = j;
					return result;
				}
			}
		}

		return null;
	}

	private void drawPoint(Canvas canvas) {
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				if (points[i][j].state == Point.STATE_NORMAL) {
					// NORMAL
					canvas.drawBitmap(bitmapnormal, points[i][j].x - bitmapR,
							points[i][j].y - bitmapR, paint);
				} else if (points[i][j].state == Point.STATE_PRESS) {
					// PRESS
					canvas.drawBitmap(bitmappress, points[i][j].x - bitmapR,
							points[i][j].y - bitmapR, paint);
				} else {
					// ERROR
					canvas.drawBitmap(bitmaperror, points[i][j].x - bitmapR,
							points[i][j].y - bitmapR, paint);
				}
			}
		}
	}

	private void init() {

		pressPaint.setColor(Color.YELLOW);
		pressPaint.setStrokeWidth(5);
		errorPaint.setColor(Color.RED);
		errorPaint.setStrokeWidth(5);
		bitmapnormal = BitmapFactory.decodeResource(getResources(),
				R.drawable.normal);
		bitmappress = BitmapFactory.decodeResource(getResources(),
				R.drawable.press);
		bitmaperror = BitmapFactory.decodeResource(getResources(),
				R.drawable.error);
		bitmapR = bitmapnormal.getHeight() / 2;
		int width = getWidth();
		int height = getHeight();
		int offset = Math.abs(width - height) / 2;
		int offsetX, offsetY;
		int space;
		if (width > height) {
			offsetX = offset;
			offsetY = 0;
			space = height / 4;
		} else {
			offsetX = 0;
			offsetY = offset;
			space = width / 4;
		}
		points[0][0] = new Point(offsetX + space, offsetY + space);
		points[0][1] = new Point(offsetX + space * 2, offsetY + space);
		points[0][2] = new Point(offsetX + space * 3, offsetY + space);

		points[1][0] = new Point(offsetX + space, offsetY + space * 2);
		points[1][1] = new Point(offsetX + space * 2, offsetY + space * 2);
		points[1][2] = new Point(offsetX + space * 3, offsetY + space * 2);

		points[2][0] = new Point(offsetX + space, offsetY + space * 3);
		points[2][1] = new Point(offsetX + space * 2, offsetY + space * 3);
		points[2][2] = new Point(offsetX + space * 3, offsetY + space * 3);
		inited = true;
	}

	public void restPoint() {
		passList.clear();
		pointlist.clear();
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				points[i][j].state = Point.STATE_NORMAL;
			}
		}
		this.postInvalidate();
	}

	public interface onDrawFinishendLister {
		boolean onDrawfinished(ArrayList<Integer> passList);
	}

	public void setonDrawFinishendLister(onDrawFinishendLister lister) {
		this.lister = lister;
	}

}
