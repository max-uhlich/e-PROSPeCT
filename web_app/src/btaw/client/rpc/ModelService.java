package btaw.client.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import btaw.client.modules.tab.KaplanMeierTab;
import btaw.client.modules.tab.Tab;
import btaw.server.util.Public;
import btaw.shared.model.BTAWDatabaseException;
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
import btaw.shared.util.SynthColumnException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("app")
public interface ModelService extends RemoteService {
	@Public
	public Boolean login(String name, String password);
	public Boolean change_password(String cur_password, String new_password);
	@Public
	public Boolean isSessionValid();
	public List<TableRow> updateTable(Query query);
	public List<DefinitionData> replicate_definition(List<DefinitionData> copy_me);
	public List<DefinitionData> updateCurrentDefinitions(List<DefinitionData> defList, Column col);
	public List<Category> getCategories();
	public Column getPidColumn();
	public void logout();
	public SynthTableColumn initSynth(SynthTableColumn synth) throws SynthColumnException, BTAWDatabaseException;
	public void dropSynthTable(SynthTableColumn col, boolean cascade) throws SynthColumnException;
	public List<Study> getStudies(String pid) throws BTAWDatabaseException;
	public List<String> getStringSuggestions(StringTableColumn stringColumn) throws BTAWDatabaseException;
	public int getSessionID();
	public void insertDrawnRegion(ArrayList<String> insStmts, int query_id) throws BTAWDatabaseException;
	public LinkedHashMap<String, Integer> getRegionsOfInterest() throws BTAWDatabaseException;
	public HashMap<List<String>, List<TableRow>> updateTable(String rawQuery);
	public void saveQuery(SavedQuery sq);
	public List<SavedQueryDisplayData> getUnnamedQueries();
	public List<SavedQueryDisplayData> getNamedQueries();
	public List<SynthTableColumn> getSavedFeatures();
	public SavedQuery getSavedQuery(Integer savedQueryID);
	public void deleteSavedQuery(Integer savedQueryID);
	public List<Date> getDates(String sql);
	public void insertUserComment(int pid, String category, String comment);
	public void insertGlobalComment(int pid, String comment);
	public String getGlobalComments(int pid);
	public HashMap<String, HashMap<String,String>> getAllUserComments(int pid);
	public String getUserComment(int pid, String category);
	public List<String> getCommentCategories();
	public String getUsername();
	public List<String> getUserList();
	public HashMap<String, HashMap<String, String>> getAllUserComments(int pid,
			List<String> usernames, List<String> categories);
	public List<GWTPWorkaround> getSegPoints(int seg_id, int study_id);
	public List<KMData> getKMData(List<String> pids, Column start, Column end, Column censor);
	public ArrayList<KM_Graph> generateKMChart(ArrayList<ArrayList<String>> pids, ArrayList<String> kmNames, Column start, Column end, Column censor);
	public String getCSV(List<TableRow> tableData, List<Column> tableCols);
	public SavedDefinitionColumn saveDefinition(DefinitionData dd);
	public void deleteDefinition(Column c);
	public String getChiSqPVal(ArrayList<ArrayList<String>> pids, Column start, Column end, Column censor);
	public ArrayList<SynonymData> getGlobalSynonyms();
	public ArrayList<SynonymData> getUserSynonyms();
	public void insertUserSynonyms(ArrayList<SynonymData> synonyms);
	public void cancelInsert();
	public void cancelRawQuery();
	public void cancelValidatedQuery();
	public LinkedHashMap<Date, Integer> getUserSegmentations();
	public List<GWTPWorkaround> getUserSegPoints(int queryId);
	public String getAggregateResult(Query query);
	public String generateBarChart(HashMap<String, Integer> hist, String colName);
	public String generateHistogram(
			LinkedHashMap<String, ArrayList<Double>> columnValues,
			String colName, Integer binSize, HashMap<String, Double> mean,
			HashMap<String, Double> stdDev);
	public PSA_Graph getPsaValues(String pid) throws BTAWDatabaseException;
	public List<List<String>> getBarSummary(Set<String> pids, String field,String table) throws BTAWDatabaseException;
	public List<List<String>> getDrugTxSummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getMetastasisSummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getBiopsySummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getProgressionSummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getPrimaryTxSummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getAgeSummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getPSACohortSummary(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getGleasonSummary(Set<String> pids) throws BTAWDatabaseException;
	public ArrayList<PSA_Graph> getPsaAggregateValues(ArrayList<String> pids, Column center) throws BTAWDatabaseException;
	public List<List<String>> getPatientSummary(String pid) throws BTAWDatabaseException;
	public List<List<String>> getDashboard(Set<String> pids) throws BTAWDatabaseException;
	public List<List<String>> getPatientEvents(String pid) throws BTAWDatabaseException;
	public String getDatacutDate();
	public HashMap<String, String> getUserParameters();
	public String initializeLog(String statement);
}