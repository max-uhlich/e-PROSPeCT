package btaw.client.framework;

import com.google.gwt.event.shared.HandlerManager;


public class EventBus extends HandlerManager {
	private static EventBus eventBus;

	private EventBus() {
		super(null);
	}
	public static EventBus getInstance(){
		if(eventBus==null){
			eventBus=new EventBus();
		}
		return eventBus;
	}
}
