package btaw.client.view.filter;

import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;

public class OperatorPanel extends HorizontalPanel {
	public final RadioButton and;
	public final RadioButton or;
	private int unique;
	public OperatorPanel(Integer unique){
		//Window.alert("New OPerator PAnel " + unique);
		
		this.unique = unique;
		HorizontalPanel holder = new HorizontalPanel();
		this.and = new RadioButton("bool"+unique,"AND");
		this.or = new RadioButton("bool"+unique,"OR");
		holder.add(and);
		holder.add(or);
		and.setValue(true);
		this.add(holder);
		this.getElement().addClassName("operatorPanelClass");
		this.and.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TablePresenter.get().setEdited();
			}
		});
		this.or.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TablePresenter.get().setEdited();
			}
		});
	}
	public boolean isAnd(){
		return and.getValue();
	}

	public void storeState(RebuildDataFilterPanel rb) {
		rb.setOperator(true);
		rb.setUnique(this.unique);
		rb.setAnd(this.isAnd());
	}
	public void setState(RebuildDataFilterPanel rb) {
		//Window.alert("rb.isAnd() " + rb.isAnd());
		
		this.and.setValue(rb.isAnd());
		this.or.setValue(!rb.isAnd());
		this.unique = rb.getUnique();
	}
	public String getDefinitionString() {
		String s = "";
		
		if(and.getValue())
			s += " AND ";
		if(or.getValue())
			s += " OR ";
		
		return s;
	}
}
