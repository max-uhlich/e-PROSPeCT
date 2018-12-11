package btaw.client.modules.image.jaccard;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import btaw.client.modules.image.BTAWCanvasSafe;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.Marker;

public class DrawingCanvasSafe extends BTAWCanvasSafe {

	protected static final int RADIUS = 1;
	private boolean down = false;
	private boolean breakLine = false;
	private Circle regionStart = null;
	
	public DrawingCanvasSafe(int width, int height, Marker m) {
		super(width, height, m);
		/*
		canvas.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (down) {
					addCircle(new Circle(event.getX(), event.getY(), RADIUS, marker));
				}

			}

		});*/

		canvas.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if(breakLine) {
					regionStart = new Circle(event.getX(), event.getY(), RADIUS, marker);
					addCircleSimple(regionStart);
					breakLine = false;
				} else {
					addCircle(new Circle(event.getX(), event.getY(), RADIUS, marker));
					down = true;
				}
			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				down = false;
			}
		});
	}
	
	public void breakLine() {
		breakLine = true;
	}
	
	public Circle getRegionStart() {
		return regionStart;
	}
	
	

}
