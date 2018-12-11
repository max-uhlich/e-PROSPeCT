package btaw.client.modules.synth;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;

import btaw.client.event.AddColumnEvent;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.SynthTableColumn;
import btaw.shared.model.query.column.SynthTableColumn.Function;
import btaw.shared.model.query.filter.Filter;


public class SynthPresenter extends Presenter {

	public interface View extends IView{
		public Button getCancel();
		public Button getCreate();
		public String getFormula();
		public String getName();
		public void close();
		public void show();
		public Function getFunction();
	}
	View ui;
	public SynthPresenter(Presenter parent, View view) {
		super(parent);
		this.ui = view;
	}

	@Override
	protected void bindAll() {
		ui.getCancel().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ui.close();	
			}
		});
		ui.getCreate().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				SynthTableColumn col = new SynthTableColumn(ui.getName(),ui.getFormula(), ui.getFunction());
				try{
				for(Column column: TablePresenter.get().getColumns()){
					if(column instanceof SynthTableColumn){
						col.addPossibleDependancy((SynthTableColumn) column);
					}
				}
				}catch(NullPointerException e){}
				rpcService.initSynth(col, new PresenterCallback<SynthTableColumn>(SynthPresenter.this) {

					@Override
					protected void success(SynthTableColumn result) {
						fireEvent(new AddColumnEvent(result));
						ui.close();
					}
				});
			}
			
		});
	}

	@Override
	protected IView getDisplay() {
		return this.ui;
	}

	@Override
	protected void activated(HasWidgets container) {

	}

	public void show() {
		ui.show();
	}
	
	public String getName() {
		return ui.getName();
	}
	
	public String getFormula() {
		return ui.getFormula();
	}
	
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

}
