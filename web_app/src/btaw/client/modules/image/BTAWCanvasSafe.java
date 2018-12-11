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

public class BTAWCanvasSafe extends AView implements HasValueChangeHandlers<ArrayList<Circle>>{
	protected Canvas canvas;
	protected ImageElement image;
	private ArrayList<Circle> points;
	protected Marker marker;
	private Integer slice;
	
	/* Uses http://code.google.com/p/google-web-toolkit-incubator/wiki/GWTCanvas */
	public BTAWCanvasSafe(int width, int height, Marker m){
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
	
	public void addCircleSimple(Circle circle) {
		points.add(circle);
		ValueChangeEvent.fire(this, points);
		circle.draw(canvas);
	}
	
	public void addCircle(Circle circle) {
		ArrayList<Circle> new_pnts_by_x = new ArrayList<Circle>();
		ArrayList<Circle> new_pnts_by_y = new ArrayList<Circle>();
		if(!points.isEmpty()) {
			Circle prev_pnt = points.get(points.size() - 1);

			if(prev_pnt.x == circle.x && prev_pnt.y == circle.y) {
				return;
			}
			
			/*
			 * Check if it's a straight line, no fancy shit needs to happen.
			 */
			if(prev_pnt.x == circle.x) {
				int unit_vector_y = (circle.y - prev_pnt.y)/Math.abs(circle.y - prev_pnt.y);
				for(int i = prev_pnt.y + unit_vector_y; i != circle.y; i = i + unit_vector_y) {
					Circle c = new Circle(circle.x, i, circle.r, circle.m);
					points.add(c);
					c.draw(canvas);
				}
				ValueChangeEvent.fire(this, points);
				circle.draw(canvas);
				return;
			} else if (prev_pnt.y == circle.y) {
				int unit_vector_x = (circle.x - prev_pnt.x)/Math.abs(circle.x - prev_pnt.x);
				for(int i = prev_pnt.x + unit_vector_x; i != circle.x; i = i + unit_vector_x) {
					Circle c = new Circle(i, circle.y, circle.r, circle.m);
					points.add(c);
					c.draw(canvas);
				}
				ValueChangeEvent.fire(this, points);
				circle.draw(canvas);
				return;
			}
			
			/*
			 * If only 1 point change, we're ok, no drawing
			 */
			double distance = Math.sqrt(((circle.x - prev_pnt.x)*(circle.x - prev_pnt.x) + (circle.y - prev_pnt.y)*(circle.y - prev_pnt.y)));
			if(Math.round(distance) <= 1) {
				points.add(circle);
				ValueChangeEvent.fire(this, points);
				circle.draw(canvas);
				return;
			}

			int unit_vector_x = (circle.x - prev_pnt.x)/Math.abs(circle.x - prev_pnt.x);
			int unit_vector_y = (circle.y - prev_pnt.y)/Math.abs(circle.y - prev_pnt.y);
			
			/*
			 * Sweet... now do the good old y=mx+b shit.
			 */
			double m = ((double)(prev_pnt.y - circle.y))/(prev_pnt.x - circle.x);
			double b = prev_pnt.y - m * prev_pnt.x;
			
			if(m == 0) {
				return;
			}
			Circle i = new Circle(prev_pnt.x++, prev_pnt.y, prev_pnt.r, prev_pnt.m);
			while(i.x != circle.x) {
				i.y = (int) Math.round(m * i.x + b);
				new_pnts_by_x.add(new Circle(i.x, i.y, i.r, prev_pnt.m));
				i.x = i.x + unit_vector_x;
			}
			Circle j = new Circle(prev_pnt.x, prev_pnt.y++, prev_pnt.r, prev_pnt.m);
			while(j.y != circle.y) {
				j.x = (int) Math.round((j.y - b)/m);
				new_pnts_by_y.add(new Circle(j.x, j.y, j.r, prev_pnt.m));
				j.y = j.y + unit_vector_y;
			}
			if(new_pnts_by_x.size() > new_pnts_by_y.size()) {
				points.addAll(new_pnts_by_x);
				for(Circle c : new_pnts_by_x) {
					c.draw(canvas);
				}
			} else if(new_pnts_by_x.size() <= new_pnts_by_y.size()) {
				points.addAll(new_pnts_by_y);
				for(Circle c : new_pnts_by_y) {
					c.draw(canvas);
				}
			}
		}
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
