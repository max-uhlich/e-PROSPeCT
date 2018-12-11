package btaw.client.view.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;


import btaw.client.BTAW_GUI;
import btaw.client.modules.table.TablePresenter;
import btaw.shared.model.SynonymData;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.StringTableColumn;
import btaw.shared.model.query.filter.Filter;
import btaw.shared.model.query.filter.StringTableColumnFilter;
import btaw.shared.model.query.saved.RebuildDataFilterPanel;
import btaw.shared.model.query.value.LiteralStringValue;
import btaw.shared.model.query.column.TableColumn;

public class StringFilterPanel extends TableFilterPanel {

	SuggestBox input;
	private MultiWordSuggestOracle suggestOracle;
	private ArrayList<SynonymData> synonyms;
	public StringFilterPanel(Column column) {
		super(column);
		synonyms = new ArrayList<SynonymData>();

		BTAW_GUI.getRpcService().getGlobalSynonyms(new AsyncCallback<ArrayList<SynonymData>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ArrayList<SynonymData> result) {
				synonyms.addAll(result);
			}
		});
		BTAW_GUI.getRpcService().getUserSynonyms(new AsyncCallback<ArrayList<SynonymData>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ArrayList<SynonymData> result) {
				synonyms.addAll(result);
			}
		});
	}

	@Override
	protected Panel getDifferentPart() {
		HorizontalPanel hp = new HorizontalPanel();
		input = new SuggestBox();
		
		ArrayList<String> ops = new ArrayList<String>(StringTableColumn.opStringList());
		this.setOperators(ops);
		input.setWidth("150px");
		
		input.getValueBox().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				//String s = input.getTextBox().getText();
				String s = input.getValueBox().getText();
				for(SynonymData sd : synonyms) {
					if(sd.getSynonym().equals(s)) {
						input.setText(sd.getWord());
					}
				}
				TablePresenter.get().setEdited();
			}
		});

		input.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				 if(input.getValueBox().getText().trim().length()==0) {
					 input.showSuggestionList();
			     }
			}
		});
		
		suggestOracle = (MultiWordSuggestOracle) input.getSuggestOracle();
		BTAW_GUI.getRpcService().getStringSuggestions((StringTableColumn) getColumn(), new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<String> result) {
				suggestOracle.setDefaultSuggestionsFromText(result);
				for (String r : result){
					suggestOracle.add(r);
				}
			}
			
		});
		
		hp.add(input);
		return hp;
	}

	@Override
	public Filter getFilter() {
		StringTableColumnFilter filter = new StringTableColumnFilter((StringTableColumn)this.getColumn(), StringTableColumn.Op.values()[this.getOpIndex()], new LiteralStringValue(input.getText()));
		
		return filter;
	}
	

	public void storeState(RebuildDataFilterPanel rb) {
		rb.setLeftPar(this.lbrack());
		rb.setRightPar(this.rbrack());
		rb.setOpIndex(this.getOperator().getSelectedIndex());
		rb.setCol(this.getColumn());
		rb.setValue(new String(input.getText()));
	}
	
	public void setState(RebuildDataFilterPanel rb) {
		this.setLBrack(rb.isLeftPar());
		this.setRBrack(rb.isRightPar());
		this.getOperator().setSelectedIndex(rb.getOpIndex());
		if(this.isNullSelected()){
			disableExtraBoxes();
		} else {
			this.input.setText(rb.getValue());
		}
		this.setColumn(rb.getCol());
	}
	
	@Override
	public String getDefinitionString() {
		String s = "";
		
		if(this.lbrack()) {
			s += "(";
		}
		s += this.getColumn().getName() + " ";
		s += this.getOperator().getItemText(this.getOpIndex()) + " ";
		s += "'" + this.input.getText() + "' ";
		if(this.rbrack()) {
			s += ")";
		}
		
		return s;
	}

	@Override
	public void disableExtraBoxes() {
		this.input.setText("");
		this.input.getValueBox().setEnabled(false);
		//this.input.getValueBox().setEnabled(false);
	}
	
	@Override
	public void enableExtraBoxes() {
		//this.input.getValueBox().setEnabled(true);
		this.input.getValueBox().setEnabled(true);
	}
	
	protected Widget getIntervalColumns() {
		return null;
	}

}
