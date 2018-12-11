
package btaw.client.event;
import btaw.client.event.PersonSelectHandler;
import com.google.gwt.event.shared.GwtEvent;
public class PersonSelectEvent extends GwtEvent<PersonSelectHandler> {

	public static Type<PersonSelectHandler> TYPE = PersonSelectHandler.TYPE;
	private String id;
		
	public PersonSelectEvent(String id){
		this.id=id;
	}
	public String getPid(){
		return this.id;
	}
		
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PersonSelectHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PersonSelectHandler handler) {
		handler.OnPersonSelectEvent(this);
	}
}
