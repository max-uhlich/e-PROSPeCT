package btaw.shared.model.query.column;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import btaw.shared.model.Category;
import btaw.shared.model.query.Query;
import btaw.shared.util.SynthColumnException;

public class SynthTableColumn extends FloatTableColumn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7305761741417344237L;
	private List<Category> categories;
	private Set<SynthTableColumn> synthesized = new HashSet<SynthTableColumn>();
	private Set<SynthTableColumn> dependancies = new HashSet<SynthTableColumn>();
	private String sqlString = "";
	private String name;
	private Query subQuery = new Query();
	private String preParse;
	private String firstCol = null;
	public Function func = Function.None;
	private String id;

	public enum Function {
		None, Avg, Sum, Variance, Min, Max, StdDev
	}

	public SynthTableColumn() {
	}

	public SynthTableColumn(String name, String formula, Function function) {
		this.name = name;
		this.preParse = formula;
		this.func = function;
	}

	public void setCategories(List<Category> cats) {
		this.categories = cats;
	}

	public void clearCategories() {
		this.categories = new ArrayList<Category>();
	}

	public String getRawString() {
		return this.preParse;
	}

	public void write(String s) {
		sqlString += s;
	}

	public void addColumn(String s) throws SynthColumnException {
		for (Category cat : this.categories) {
			for (Column col : cat.getColumns()) {
				if (col instanceof TableColumn) {

					if (((TableColumn) col).getName().equalsIgnoreCase(s)) {

						if (!(col instanceof IntegerTableColumn
								|| col instanceof DateTableColumn || col instanceof FloatTableColumn))
							throw new SynthColumnException("Column '" + s
									+ "' does not contain numbers");
						subQuery.addColumn((TableColumn) col);
						if (col instanceof DateTableColumn) {
							sqlString += " cast((date_part('year',"
									+ ((TableColumn) col).toSQLString()
									+ ")*365.25+date_part('doy',"
									+ ((TableColumn) col).toSQLString()
									+ ")) as float) ";
						} else {
							sqlString += ((TableColumn) col).getSQLName();
						}
						if (firstCol == null) {
							firstCol = ((TableColumn) col).getTableName();
						}
						return;
					}
				}
			}
		}
		for (SynthTableColumn column : this.getSynthesized()) {
			if (column.getName().equalsIgnoreCase(s)) {
				subQuery.addColumn(column);
				sqlString += column.getSQLName();
				if (firstCol == null) {
					firstCol = column.getTableName();
				}
				column.addDependancy(this);
				return;
			}
		}
		throw new SynthColumnException("Column '" + s + "' not found.");
	}

	public String getName() {
		return new String(name);
	}

	public String getRenderedSQL() {
		return this.sqlString;
	}

	public List<Column> subCols() {
		return this.subQuery.getColumns();
	}

	public String getSQLName() {
		return this.getTableName() + "." + safeName();
	}

	public String safeName() {
		return name.replaceAll("\\s+", "_");
	}

	public String getTableStatement() {
		String sql = "create view " + this.getTableName() + " as ";
		if (this.func != Function.None) {
			sql += "SELECT " + firstCol + ".pid, " + this.safeName()
					+ " from (SELECT " + this.func.toString() + "(" + sqlString
					+ ") AS " + this.safeName() + " FROM "
					+ subQuery.getJoinedTables();
		} else {
			sql += "(SELECT " + firstCol + ".pid, " + sqlString + " AS "
					+ this.safeName() + " FROM " + subQuery.getJoinedTables();
		}
		sql += ")";
		if (this.func != Function.None)
			sql += " as " + this.safeName() + ", " + firstCol;
		return sql;
	}

	public String getUniqueTableName() {
		return "";
	}

	@Override
	public String toSQLString() {
		return this.getSQLName();
	}

	public Set<SynthTableColumn> getSynthesized() {
		this.synthesized.remove(this);
		return synthesized;
	}

	public void addPossibleDependancy(SynthTableColumn column) {
		this.synthesized.add(column);
		this.synthesized.remove(this);
	}

	public void addDependancy(SynthTableColumn col) {
		this.dependancies.add(col);
		this.dependancies.remove(this);
	}

	public Set<SynthTableColumn> getDependants() {
		this.dependancies.remove(this);
		return dependancies;
	}

	public String getTableName() {
		return "sketchpad.synth_" + id + safeName();
	}

	public String getTableDropStatement() {
		return "drop view " + this.getTableName();
	}

	public void setUnique(String id) {
		this.id = id;
	}

	public String getTableDropCascadeStatement() {
		return "drop view " + this.getTableName() + " cascade";
	}

	public String getId() {
		return id;
	}
}
