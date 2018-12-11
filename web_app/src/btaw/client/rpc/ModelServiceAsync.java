package btaw.client.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import btaw.client.framework.PresenterCallback;
import btaw.client.modules.tab.KaplanMeierTab;
import btaw.client.modules.tab.Tab;
import btaw.server.util.Public;
import btaw.shared.model.Category;
import btaw.shared.model.DefinitionData;
import btaw.shared.model.KMData;
import btaw.shared.model.KM_Graph;
import btaw.shared.model.PSA_Graph;
import btaw.shared.model.Study;
import btaw.shared.model.SynonymData;
import btaw.shared.model.TableRow;
import btaw.shared.model.query.Query;
import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.SavedDefinitionColumn;
import btaw.shared.model.query.column.StringTableColumn;
import btaw.shared.model.query.column.SynthTableColumn;
import btaw.shared.model.query.saved.GWTPWorkaround;
import btaw.shared.model.query.saved.SavedQuery;
import btaw.shared.model.query.saved.SavedQueryDisplayData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelServiceAsync{
	@Public
	public void login(String name, String password, AsyncCallback<Boolean> callback);
	public void change_password(String cur_password, String new_password, AsyncCallback<Boolean> callback);
	@Public
	public void isSessionValid(AsyncCallback<Boolean> callback);
	public void updateTable(Query query, AsyncCallback<List<TableRow>> callback);
	public void replicate_definition(List<DefinitionData> copy_me, AsyncCallback<List<DefinitionData>> callback);
	public void updateCurrentDefinitions(List<DefinitionData> defList, Column col, AsyncCallback<List<DefinitionData>> callback);
	public void getCategories(AsyncCallback<List<Category>> callback);
	public void getPidColumn(AsyncCallback<Column> callback);
	public void logout(AsyncCallback<Void> callback);
	public void initSynth(SynthTableColumn synth, AsyncCallback<SynthTableColumn> callback);
	public void dropSynthTable(SynthTableColumn col, boolean cascade, AsyncCallback<Void> callback);
	public void getStudies(String pid, AsyncCallback<List<Study>> callback);
	public void getStringSuggestions(StringTableColumn stringColumn, AsyncCallback<List<String>> callback);
	public void getSessionID(AsyncCallback<Integer> callback);
	public void insertDrawnRegion(ArrayList<String> insStmts, int query_id,
			AsyncCallback<Void> callback);
	public void getRegionsOfInterest(AsyncCallback<LinkedHashMap<String, Integer>> callback);
	public void updateTable(String rawQuery,
			AsyncCallback<HashMap<List<String>, List<TableRow>>> callback);
	void saveQuery(SavedQuery sq, AsyncCallback<Void> callback);
	void getNamedQueries(AsyncCallback<List<SavedQueryDisplayData>> callback);
	void getUnnamedQueries(AsyncCallback<List<SavedQueryDisplayData>> callback);
	void getSavedFeatures(AsyncCallback<List<SynthTableColumn>> callback);
	void getSavedQuery(Integer savedQueryID, AsyncCallback<SavedQuery> callback);
	void deleteSavedQuery(Integer savedQueryID, AsyncCallback<Void> callback);
	void getDates(String sql, AsyncCallback<List<Date>> callback);
	void insertUserComment(int pid, String category, String comment, AsyncCallback<Void> callback);
	void insertGlobalComment(int pid, String comment,
			AsyncCallback<Void> callback);
	void getGlobalComments(int pid, AsyncCallback<String> callback);
	void getAllUserComments(int pid,
			AsyncCallback<HashMap<String, HashMap<String, String>>> callback);
	void getUserComment(int pid, String category, AsyncCallback<String> callback);
	void getCommentCategories(AsyncCallback<List<String>> callback);
	void getUsername(AsyncCallback<String> callback);
	void getUserList(AsyncCallback<List<String>> callback);
	void getAllUserComments(int pid, List<String> usernames,
			List<String> categories,
			AsyncCallback<HashMap<String, HashMap<String, String>>> callback);
	void getSegPoints(int seg_id, int study_id, AsyncCallback<List<GWTPWorkaround>> callback);
	void getKMData(List<String> pids, Column start, Column end, Column censor, AsyncCallback<List<KMData>> callback);
	void generateKMChart(ArrayList<ArrayList<String>> pids,
			ArrayList<String> kmNames, Column start, Column end, Column censor, AsyncCallback<ArrayList<KM_Graph>> callback);
	void getCSV(List<TableRow> tableData, List<Column> tableCols, AsyncCallback<String> callback);
	void saveDefinition(DefinitionData dd, AsyncCallback<SavedDefinitionColumn> callback);
	void deleteDefinition(Column c, AsyncCallback<Void> callback);
	void getChiSqPVal(ArrayList<ArrayList<String>> pids, Column start, Column end, Column censor, AsyncCallback<String> callback);
	void getGlobalSynonyms(AsyncCallback<ArrayList<SynonymData>> callback);
	void getUserSynonyms(AsyncCallback<ArrayList<SynonymData>> callback);
	void insertUserSynonyms(ArrayList<SynonymData> synonyms,
			AsyncCallback<Void> callback);
	void cancelInsert(AsyncCallback<Void> callback);
	void cancelRawQuery(AsyncCallback<Void> callback);
	void cancelValidatedQuery(AsyncCallback<Void> callback);
	void getUserSegmentations(
			AsyncCallback<LinkedHashMap<Date, Integer>> callback);
	void getUserSegPoints(int queryId,
			AsyncCallback<List<GWTPWorkaround>> callback);
	void getAggregateResult(Query query, AsyncCallback<String> callback);
	void generateBarChart(HashMap<String, Integer> hist, String colName,
			AsyncCallback<String> callback);
	void generateHistogram(LinkedHashMap<String,ArrayList<Double>> columnValues, String colName, Integer binSize,
			HashMap<String, Double> mean, HashMap<String, Double> stdDev, AsyncCallback<String> callback);
	void getPsaValues(String pid, AsyncCallback<PSA_Graph> callback);
	void getBarSummary(Set<String> pids, String field,String table,AsyncCallback<List<List<String>>> callback);
	void getDrugTxSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getMetastasisSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getBiopsySummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getProgressionSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getPrimaryTxSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getAgeSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getPSACohortSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getGleasonSummary(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getPsaAggregateValues(ArrayList<String> pids, Column center, AsyncCallback<ArrayList<PSA_Graph>> callback);
	void getPatientSummary(String pid, AsyncCallback<List<List<String>>> callback);
	void getDashboard(Set<String> pids, AsyncCallback<List<List<String>>> callback);
	void getPatientEvents(String pid, AsyncCallback<List<List<String>>> callback);
	void getDatacutDate(AsyncCallback<String> callback);
	void getUserParameters(AsyncCallback<HashMap<String, String>> callback);
	void initializeLog(String statement, AsyncCallback<String> callback);
}
