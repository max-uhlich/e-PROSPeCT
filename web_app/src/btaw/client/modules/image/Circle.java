package btaw.client.modules.image;

import java.io.Serializable;

public class Circle implements Serializable {
	public int x;
	public int y;
	public int r;
	public Marker m;

	public Circle(int x, int y, int r, Marker m) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.m = m;
	}

	public void draw(BTAWCanvas.Canvas canvas) {
		canvas.setLineWidth(m.thickness);
		canvas.setStrokeStyle(m.color);
		canvas.beginPath();
		canvas.arc(x, y, r, 0, 360, false);
		canvas.closePath();
		canvas.stroke();
	}
	
	public void draw(BTAWCanvasSafe.Canvas canvas) {
		canvas.setLineWidth(m.thickness);
		canvas.setStrokeStyle(m.color);
		canvas.beginPath();
		canvas.arc(x, y, r, 0, 360, false);
		canvas.closePath();
		canvas.stroke();
	}
	
}
