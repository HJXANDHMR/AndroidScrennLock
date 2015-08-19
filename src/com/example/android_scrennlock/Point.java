package com.example.android_scrennlock;

public class Point {

	public static int STATE_NORMAL = 0;
	public static int STATE_PRESS = 1;
	public static int STATE_ERROR = 2;
	float x;
	float y;
	int state = STATE_NORMAL;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float distance(Point point) {
		float distance = (float) Math.sqrt((x - point.x) * (x - point.x)
				+ (y - point.y) * (y - point.y));

		return distance;

	}
}
