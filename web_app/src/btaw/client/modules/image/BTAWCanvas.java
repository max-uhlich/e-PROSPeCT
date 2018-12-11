package btaw.client.modules.image;

import java.util.ArrayList;

import btaw.client.framework.AView;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

public class BTAWCanvas extends AView implements HasValueChangeHandlers<ArrayList<Circle>>{
	protected Canvas canvas;
	protected ImageElement image;
	private ArrayList<Circle> points;
	protected Marker marker;
	private Integer slice;
	
	/* Uses http://code.google.com/p/google-web-toolkit-incubator/wiki/GWTCanvas */
	public BTAWCanvas(int width, int height, Marker m){
		canvas = new Canvas(width, width);
		initWidget(canvas);
		points = new ArrayList<Circle>();
		this.marker = m;
	}
	
	public Marker setMarker(Marker marker){
		this.marker = marker;
		return marker;
	}
	public Marker getMarker(){
		return marker;
	}
	
	public void setImage(String url){
		String[] urls = new String[]{url};
		ImageLoader.loadImages(urls, new ImageLoader.CallBack() {

			public void onImagesLoaded(ImageElement[] imageHandles) {
				ImageElement img = imageHandles[0];
				image = img;
				repaint();
			}
		});
	}


	public class Canvas extends GWTCanvas implements HasMouseDownHandlers,
			HasMouseMoveHandlers, HasMouseUpHandlers {

		public Canvas(int i, int j) {
			super(i, j);
		}

		@Override
		public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
			return addDomHandler(handler, MouseDownEvent.getType());
		}

		@Override
		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
			return addDomHandler(handler, MouseMoveEvent.getType());
		}

		@Override
		public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
			return addDomHandler(handler, MouseUpEvent.getType());
		}

	}
	
	protected int distance(int x, int y, int x2, int y2) {
		return (int) Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
	}
	
	public void addCircle(Circle circle) {
		points.add(circle);
		ValueChangeEvent.fire(this, points);
		circle.draw(canvas);
		
	}

	public void clear() {
		points.clear();
		ValueChangeEvent.fire(this, points);
		repaint();
	}
	
	public void repaint() {
		canvas.clear();
		if (image != null)
			canvas.drawImage(image, 0, 0);

		for (Circle c : points) {
			c.draw(canvas);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<ArrayList<Circle>> handler) {
		return addHandler(handler, ValueChangeEvent.getType()) ;
	}

	public ArrayList<Circle> getPoints() {
		return points;
	}
	
	protected void removeTop() {
		points.remove(0);
	}
	
	public void setSlice(Integer newSlice){
		slice = newSlice;
	}
	public Integer getSlice(){
		
		return slice;
	}
	
	protected Circle getTop() {
		return points.get(0);
	}
}
