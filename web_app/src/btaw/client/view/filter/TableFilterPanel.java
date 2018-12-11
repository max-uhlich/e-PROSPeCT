package btaw.client.view.filter;

import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class TableFilterPanel extends FilterPanel {

	public TableFilterPanel(Column column) {
		super(column);
		this.getOperator().addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (isNullSelected()) {
					disableExtraBoxes();
				} else {
					enableExtraBoxes();
				}
				TablePresenter.get().setEdited();
			}
		});
	}
	
	public abstract void enableExtraBoxes();
	public abstract void disableExtraBoxes();
}
