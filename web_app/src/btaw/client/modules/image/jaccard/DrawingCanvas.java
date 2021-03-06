package btaw.client.modules.image.jaccard;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import btaw.client.modules.image.BTAWCanvas;
import btaw.client.modules.image.Circle;
import btaw.client.modules.image.Marker;

public class DrawingCanvas extends BTAWCanvas {

	protected static final int RADIUS = 2;
	private boolean down = false;
	
	public DrawingCanvas(int width, int height, Marker m) {
		super(width, height, m);
		
		canvas.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (down) {
					addCircle(new Circle(event.getX(), event.getY(), RADIUS,marker));
				}

			}

		});

		canvas.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				addCircle(new Circle(event.getX(), event.getY(), RADIUS,marker));
				down = true;

			}
		});
		canvas.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				down = false;
			}
		});
	}

}
