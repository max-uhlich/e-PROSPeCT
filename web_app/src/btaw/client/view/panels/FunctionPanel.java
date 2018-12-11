package btaw.client.view.panels;

import btaw.client.framework.Presenter;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.function.FunctionView.Function;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.FunctionData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FunctionPanel extends HorizontalPanel implements ClickHandler {

	private Presenter presenter;
	private HorizontalPanel inner;
	private PushButton delete;
	private Label groupDescriptor;
	private Button apply;
	private String funcDescriptor;
	private FunctionData data;
	
	public FunctionPanel(Presenter p, String groupDescriptor, FunctionData fd) {
		this.presenter = p;
		this.funcDescriptor = groupDescriptor;
		this.data = fd;
		this.buildUI(groupDescriptor);
	}
	
	public void buildUI(String groupDescriptor) {
		inner = new HorizontalPanel();
		inner.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.delete = new PushButton(new Image("images/delete.png"));
		delete.getElement().setClassName("upDownButton");
		delete.addClickHandler(this);
		
		this.setWidth("100%");
		this.groupDescriptor = new Label(groupDescriptor);
		this.groupDescriptor.setWidth("100%");
		
		this.apply = new Button("Apply");
		
		inner.setSpacing(5);
		inner.add(this.groupDescriptor);
		
		delete.addClickHandler(this);
		apply.addClickHandler(this);
		
		HorizontalPanel rightAlignmentWrapper = new HorizontalPanel();
		rightAlignmentWrapper.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		rightAlignmentWrapper.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		rightAlignmentWrapper.setWidth("100%");
		rightAlignmentWrapper.add(this.apply);
		rightAlignmentWrapper.add(delete);

		this.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.add(inner);
		this.add(rightAlignmentWrapper);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == delete) {
			FilterPresenter.get().removeFunction(funcDescriptor);
		} else if(event.getSource() == apply) {
			if(data.getFunc() == Function.KAPLANMEIER)
				TablePresenter.get().doKaplanMeier(data);
		}
	}

	public void setFuncDescriptor(String funcDiscriptor) {
		this.funcDescriptor = funcDiscriptor;
	}

	public String getFuncDescriptor() {
		return funcDescriptor;
	}

	
}
