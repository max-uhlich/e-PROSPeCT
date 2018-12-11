package btaw.client.classes;


import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorPopupPanel extends PopupPanel 
{
	/**
	 * Error popup panel is used to display errors when exceptions are thrown.
	 * @param title - title of the error
	 * @param error - text of the error
	 * @param bgColor
	 */
	public ErrorPopupPanel(String title, String error, String bgColor)
	{
		super();
		this.getElement().getStyle().setBackgroundColor(bgColor);
		this.getElement().getStyle().setPadding(5, Unit.PX);
		VerticalPanel hp = new VerticalPanel();
		
		HorizontalPanel vp = new HorizontalPanel();
		Button cancel = new Button("OK");
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ErrorPopupPanel.this.hide();
				
			}
		});
		vp.add(cancel);
		Label titleLabel = new Label(title);
		titleLabel.getElement().getStyle().setBackgroundColor("#5D86C4");
		titleLabel.getElement().getStyle().setPadding(3, Unit.PX);
		titleLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		hp.add(titleLabel);
		Label err = new Label(error);
		err.getElement().getStyle().setPadding(3, Unit.PX);
		err.setWidth("200px");
		err.setWordWrap(true);
		err.getElement().getStyle().setPaddingBottom(10, Unit.PX);
		hp.add(err);
		
		hp.add(vp);
		this.add(hp);
	}
}
