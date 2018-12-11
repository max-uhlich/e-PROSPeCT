package btaw.client.modules.table;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.image.ImagePresenter;
import btaw.shared.model.query.filter.Filter;

public class ExportDataPresenter extends ImagePresenter implements ClickHandler {

	public interface View extends ImagePresenter.View {
		void setLink(String link);
	}
	private View ui;
	private Presenter presenter;
	
	public ExportDataPresenter(Presenter parent, View ui) {
		super(parent, ui);
		this.ui = ui;
		this.presenter = parent;
	}
	
	@Override
	public void bindAll() {
		rpcService.getCSV(TablePresenter.get().getTableAsList(), TablePresenter.get().getColumns(), new PresenterCallback<String>(this) {

			@Override
			protected void success(String result) {
				//Window.alert(result);
				
				//ui.setLink(result);
				//String url = GWT.getModuleBaseURL() + "DownloadServlet?fileInfo1=" + "Export_bcff4fd5-d265-4369-9c79-c4355b6a9e88.csv";
				//Window.alert(url);
				//Window.open(url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
				String url = GWT.getModuleBaseURL() + "DownloadServlet?fileInfo1=" + result;
				//Window.alert(url);
				ui.setLink(url);
			}
			
		});
	}
	
	@Override
	public IView getDisplay() {
		return ui;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/** Do not use below for now **/
	@Override
	public boolean isType(String val) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSegTypes(LinkedHashMap<String, Integer> segTypes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}
}
