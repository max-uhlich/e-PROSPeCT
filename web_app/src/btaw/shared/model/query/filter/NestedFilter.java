package btaw.shared.model.query.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.google.gwt.user.client.Window;

import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.TableColumn;

/** A filter of the form <Filter> <Op> <Filter>, where Oper is one of AND or OR.
 * 
 * @author Jon VanAlten
 *
 */
public class NestedFilter extends Filter implements Serializable{

	private static final long serialVersionUID = -3138098503699454834L;

	private ArrayList<FilterPart> filters;
	private Stack<NestedFilter> nestedFilterStack;
	private NestedFilter currentNestedFilter;
	private boolean topLevel;

	private Op lastOp;

	/**
	 * Publicly visible constructor may only create top level filter.
	 */
	public NestedFilter() {
		this(true);
	}
	
	/**
	 * Protected constructor allows other levels to be created internally.
	 * 
	 * @param topLevel Flag to determine if the filter being created is the top nesting level.
	 */
	protected NestedFilter(boolean topLevel) {
		this.filters = new ArrayList<FilterPart>();
		this.nestedFilterStack = new Stack<NestedFilter>();
		this.currentNestedFilter = this;
		
		this.topLevel = topLevel;
	}
	
	public void addFilter(Filter filter) throws FilterStateException {
		if (!topLevel) {
			throw new FilterStateException("Filters can only be added directly to the top level.");
		}
		if (filter == null) {
			throw new FilterStateException("Null filter cannot be added.");
		}
		if (!filters.isEmpty()) {
			throw new FilterStateException("Any filter after the first must have an operator.");
		}
		currentNestedFilter.addFilter(new FilterPart(null, filter));
	}
	public void add(Filter filter){
		currentNestedFilter.addFilter(new FilterPart(lastOp, filter));
		lastOp=null;
	}
	public void addFilter(Op op, Filter filter) throws FilterStateException {
		if (!topLevel) {
			throw new FilterStateException("Filters can only be added directly to the top level.");
		}
		if (filter == null) {
			throw new FilterStateException("Null filter cannot be added.");
		}
		if (filters.isEmpty()) {
			throw new FilterStateException("The first filter must have an operator.");
		}
		currentNestedFilter.addFilter(new FilterPart(null, filter));
	}

	protected void addFilter(FilterPart part) {
		filters.add(part);
	}
	public void operator(Op op){
		this.lastOp=op;
	}
	public void startNestedPart(Op op) throws FilterStateException {
		if (currentNestedFilter.isEmpty()) {
			throw new FilterStateException("A nesting level must have a filter.");
		}
		if (op == null) {
			throw new FilterStateException("Nesting level must be qualified with AND or OR.");
		}
		NestedFilter newNestedFilter = new NestedFilter(false);
		currentNestedFilter.addFilter(new FilterPart(op, newNestedFilter));
		nestedFilterStack.push(currentNestedFilter);
		currentNestedFilter = newNestedFilter;
	}
	public void startNestedPart() throws FilterStateException {
		NestedFilter newNestedFilter = new NestedFilter(false);
		currentNestedFilter.addFilter(new FilterPart(lastOp,newNestedFilter));
		nestedFilterStack.push(currentNestedFilter);
		currentNestedFilter = newNestedFilter;
		lastOp=null;
	}
	
	public void endNestedPart() throws FilterStateException {
		if (currentNestedFilter.isEmpty()) {
			throw new FilterStateException("A nesting level must have a filter.");
		}
		if(nestedFilterStack.isEmpty()){
			throw new FilterStateException("Too many closing brackets.");
		}
		currentNestedFilter = nestedFilterStack.pop();
	}
	public boolean stackEmpty(){
		return nestedFilterStack.isEmpty();
	}
	public boolean isEmpty() {
		return filters.isEmpty();
	}
	
	public ArrayList<ScoredImageFilter> getScoredImageFilters() {
		ArrayList<ScoredImageFilter> imageFilters = new ArrayList<ScoredImageFilter>();
		return imageFilters;
	}
	public ArrayList<FilterPart> getFilters() {
		return filters;
	}
	@Override
	public String toSQLString() {
		String ret = new String();
		ret+="(";
		for(FilterPart part: this.filters){
			if(part.getOper()!=null){
				ret+=" "+part.getOper().toString()+" ";
			}
			if(part.getFilter().toSQLString() != null) {
				ret+=part.getFilter().toSQLString();
			}
		}
		ret+=")";
		if(ret.equals("()")) {
			return null;
		}
		return ret;
	}
	
	public Set<Column> getColumns()
	{
		HashSet<Column> cols = new HashSet<Column>();
		for(FilterPart part: this.filters){
			if(part.getFilter() instanceof NestedFilter){
				cols.addAll(((NestedFilter) part.getFilter()).getColumns());
			}else{
				if(part.getFilter() instanceof TableColumnFilter){
					cols.add(((TableColumnFilter) part.getFilter()).getColumn());
				}
			}
		}

		return cols;
	}
	
	/** The binary operators that can be used to join the Filters in a NestedFilter.
	 * 
	 * @author Jon VanAlten
	 *
	 */
	public enum Op {
		AND, OR
	}
	
	public Filter findFilter(Column col) {
		ArrayList<Filter> flat = this.flatten();
		
		for(Filter f : flat){
			if (((TableColumnFilter)f).getColumn().equals(col))
				return f;
		}
		
		return null;
	}
	
	public ArrayList<Filter> flatten(){
		ArrayList<Filter> flat = new ArrayList<Filter>();
		
		for(FilterPart part : this.filters){
			if(part.getFilter() instanceof NestedFilter){
				flat.addAll(((NestedFilter)part.getFilter()).flatten());
			}else{
				if(part.getFilter() instanceof TableColumnFilter){
					flat.add((TableColumnFilter)part.getFilter());
				}
			}
		}
		
		return flat;
	}
	
}