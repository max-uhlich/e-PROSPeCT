package btaw.client.modules.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btaw.client.event.AddColumnEvent;
import btaw.client.framework.IView;
import btaw.client.framework.Presenter;
import btaw.client.framework.PresenterCallback;
import btaw.client.modules.filter.FilterPresenter;
import btaw.client.modules.main.MainPresenter;
import btaw.client.view.widgets.ColumnLabel;
import btaw.client.view.widgets.QueryDeleteButton;
import btaw.client.view.widgets.SavedQueryLabel;
import btaw.client.view.widgets.TabCloseButton;
import btaw.shared.model.Category;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.IntegerTableColumn;
import btaw.shared.model.query.column.SavedDefinitionColumn;
import btaw.shared.model.query.column.SynthTableColumn;
import btaw.shared.model.query.saved.SavedQueryDisplayData;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CategoryPresenter extends Presenter implements ClickHandler {
	private static List<Category> cats;
	private static Column pid;

	public static List<Category> getCats() {
		if (cats == null) {
			return new ArrayList<Category>();
		}
		return cats;
	}

	public static Column getPidCol() {
		if (pid == null) {
			return new IntegerTableColumn("", "", "", null);
		}
		return pid;
	}

	public interface View extends IView {
		FlowPanel getMainPanel();

		MultiWordSuggestOracle getSuggestOracle();

		List<QueryDeleteButton> addCategory(String cat, List<Column> columns);
		
		QueryDeleteButton addColumn(Column c);
		
		void deleteColumn(Column c);

		SuggestBox getSuggestBox();

		ColumnLabel search(Column col, String cat);

		void selectAllSearch();

		void clearSearch();

		void addPreviousQueries(List<SavedQueryDisplayData> data,
				CategoryPresenter presenter);

		void addSavedQueries(List<SavedQueryDisplayData> data,
				CategoryPresenter presenter);

		void setupSavedCats();

		void setupSavedFeatures();

		// void addSavedFeatures(List<SavedQueryDisplayData> data,
		// CategoryPresenter presenter);
		void addSavedFeatures(List<SynthTableColumn> data,
				CategoryPresenter presenter);

		SavedQueryLabel getPreviousQuery();

		void resetCnt();

	}

	private static CategoryPresenter cp;

	public static CategoryPresenter get() {
		return cp;
	}

	private final Map<String, Column> cols;
	private final View ui;
	private List<String> suggestions;
	private ColumnLabel found;

	public CategoryPresenter(Presenter parent, View ui) {
		super(parent);
		this.ui = ui;
		cols = new HashMap<String, Column>();
		suggestions = new ArrayList<String>();
		cp = this;
	}

	@Override
	protected void bindAll() {
		SuggestBox box = ui.getSuggestBox();
		final TextBoxBase textBox = box.getTextBox();

		textBox.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				if (textBox.getText().trim().equals("Search...")) {
					textBox.setText("");
					textBox.getElement().getStyle().setColor("#000000");
				}
			}
		});
		textBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!textBox.getText().trim().equals("Search...")) {
					ui.selectAllSearch();
					event.stopPropagation();
					ui.clearSearch();
				}
			}
		});
		textBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (textBox.getText().trim().equals("")) {
					textBox.setText("Search...");
					textBox.getElement().getStyle().setColor("#AAAAAA");
					if(found!=null)
						found.getElement().getStyle().setBackgroundColor("#FFFFFF");
				}
			}
		});
		//textBox.addChangeHandler(new ChangeHandler() {
			//@Override
			//public void onChange(ChangeEvent event) {
				//Window.alert("Changed");
				/*Column col = cols.get(textBox.getText());
				if (col != null) {
					//Window.alert("Column found");
					ColumnLabel found = ui.search(col);
					if (found != null) {
						Window.alert("Column found");
						//fireEvent(new AddColumnEvent(col));
					}

				} else {
					Window.alert("Column not found");
				}*/
			//}
		//});
		box.addSelectionHandler(new SelectionHandler<MultiWordSuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent event) {
				if(found!=null)
					found.getElement().getStyle().setBackgroundColor("#FFFFFF");
				//Column col = cols.get(textBox.getText());
				String text = textBox.getText();
				Column col = cols.get(text.substring(0, text.indexOf(" (")));
				String cat = text.substring(text.indexOf("(")+1, text.indexOf(")"));
				if (col != null) {
					found = ui.search(col, cat);
				}
			}
		});
	}

	@Override
	protected IView getDisplay() {
		return ui;
	}

	//Adds a column to this Category (right now its just hooked up for Saved Definitions)
	public void addColumn(Column c){

		ui.getSuggestOracle().add(c.getName() + " (" + ((Category)cats.get(0)).getName() + ")");
		suggestions.add(c.getName() + " (" + ((Category)cats.get(0)).getName() + ")");
		cols.put(c.getName(), c);
		
		QueryDeleteButton delete_button = ui.addColumn(c);
		
		delete_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//Window.alert("Ya CLICKED " + ((QueryDeleteButton)event.getSource()).getRef().getName());
				
				if (Window.confirm("Are you sure you want to delete \"" + ((QueryDeleteButton)event.getSource()).getRef().getName() + "\"?")){
					Column c = ((QueryDeleteButton)event.getSource()).getRef();
					CategoryPresenter.get().deleteColumn(c);

					rpcService.deleteDefinition(c, new PresenterCallback<Void>(CategoryPresenter.this) {
						@Override
						protected void success(Void result) {
							//Nothing else to be done
						}
					});
					
				} else {
					//Do nothing
				}
				
			}
		});
	}
	
	//Removes a column from Saved Definitions
	public void deleteColumn(Column c){
		
		suggestions.remove(c.getName() + " (" + ((Category)cats.get(0)).getName() + ")");
		rebuildSuggestOracle();
		cols.remove(c.getName());
		ui.deleteColumn(c);

	}
	
	private void rebuildSuggestOracle(){
		ui.getSuggestOracle().clear();
		ui.getSuggestOracle().addAll(suggestions);
	}
	
	/**
	 * Add in all available categories. Retrieves said category list from RPC.
	 */
	@Override
	protected void activated(HasWidgets container) {
		this.rpcService.getCategories(new PresenterCallback<List<Category>>(this) {
			@Override
			protected void success(List<Category> result) {
				cats = result;
				for (Category cat : result) {
					for (Column col : cat.getColumns()) {
						ui.getSuggestOracle().add(col.getName() + " (" + cat.getName() + ")");
						suggestions.add(col.getName() + " (" + cat.getName() + ")");
						cols.put(col.getName(), col);
					}
					
					List<QueryDeleteButton> delete_buttons = ui.addCategory(cat.getName(), cat.getColumns());
					if (delete_buttons.size() > 0) {
						for (QueryDeleteButton delete_button : delete_buttons){
							delete_button.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									//Window.alert("Ya CLICKED " + ((QueryDeleteButton)event.getSource()).getRef().getName());
									
									if (Window.confirm("Are you sure you want to delete \"" + ((QueryDeleteButton)event.getSource()).getRef().getName() + "\"?")){
										Column c = ((QueryDeleteButton)event.getSource()).getRef();
										CategoryPresenter.get().deleteColumn(c);

										rpcService.deleteDefinition(c, new PresenterCallback<Void>(CategoryPresenter.this) {
											@Override
											protected void success(Void result) {
												//Nothing else to be done
											}
										});
										
									} else {
										//Do nothing
									}
									
								}
							});
						}
					}
				}

			}
		});

	}

	public void getUnnamedQueries() {

		this.rpcService
				.getUnnamedQueries(new PresenterCallback<List<SavedQueryDisplayData>>(
						this) {
					@Override
					protected void success(List<SavedQueryDisplayData> result) {
						// todo
					}
				});

	}

	public void getNamedQueries() {

		this.rpcService
				.getNamedQueries(new PresenterCallback<List<SavedQueryDisplayData>>(
						this) {
					@Override
					protected void success(List<SavedQueryDisplayData> result) {
						CategoryPresenter.this.ui.addSavedQueries(result,
								CategoryPresenter.this);
						CategoryPresenter.this.ui.resetCnt();
					}
				});

	}

	public void getSavedFeatures() {

		this.rpcService
				.getSavedFeatures(new PresenterCallback<List<SynthTableColumn>>(
						this) {
					@Override
					protected void success(List<SynthTableColumn> result) {
						CategoryPresenter.this.ui.addSavedFeatures(result,
								CategoryPresenter.this);
						CategoryPresenter.this.ui.resetCnt();
					}
				});

	}

	public void getPreviousQuery() {
		FilterPresenter.get().loadQuery(
				CategoryPresenter.this.ui.getPreviousQuery());
	}

	public void resetHighlight() {
		CategoryPresenter.this.ui.resetCnt();
	}

	@Override
	public void onClick(ClickEvent event) {
		this.resetHighlight();
		FilterPresenter.get().loadQuery((SavedQueryLabel) event.getSource());
	}

}
