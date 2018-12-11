package btaw.client.modules.image.distance;

import btaw.client.modules.image.BTAWCanvas;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.Marker;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class DistanceCanvas extends BTAWCanvas {
	private boolean down;
	private Circle circle;
	private boolean remove;

	public DistanceCanvas(int width, int height, Marker m) {
		super(width, height, m);
		
		canvas.addMouseMoveHandler(new MouseMoveHandler() {

			public void onMouseMove(MouseMoveEvent event) {
				if (down) {
					circle.r = distance(event.getX(),event.getY(),circle.x,circle.y);
					repaint();
				}

			}

		});
		canvas.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (circle != null)
					remove = true;
				circle = new Circle(event.getX(), event.getY(), 2, marker);
				addCircle(circle);
				down = true;

			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (remove){
					removeTop();
					repaint();
				}
				down = false;
			}
		});
	}

	public Circle getCircle() {
		return getTop();
	}
	
	public void setCircle(Circle c) {
		this.circle = c;
		addCircle(c);
		repaint();
	}

	

	
	
	

}
