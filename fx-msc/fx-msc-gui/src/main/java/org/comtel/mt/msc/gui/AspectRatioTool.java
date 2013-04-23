package org.comtel.mt.msc.gui;

public class AspectRatioTool {

	public enum AR {
		FOUR_THREE,
	}

	private AspectRatioTool() {
	}
	
	public static int getHeight(int pixel, AR ar) {
		int height = -1;
		switch (ar) {
		case FOUR_THREE:
			height = (int) Math.round(Math.sqrt(pixel * 3 / 4));
			break;
		default:
			throw new IllegalStateException("not supported aspect ratio: " + ar);
		}
		return height;
	}
	
	public static int getWidth(int pixel, AR ar) {
		int width = -1;
		switch (ar) {
		case FOUR_THREE:
			width = pixel / getHeight(pixel, ar);
			break;
		default:
			throw new IllegalStateException("not supported aspect ratio: " + ar);
		}
		return width;
	}
	
}
