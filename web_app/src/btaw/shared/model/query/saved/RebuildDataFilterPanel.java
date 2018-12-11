package btaw.shared.model.query.saved;

import java.io.Serializable;

import btaw.shared.model.query.column.Column;
import btaw.shared.model.query.column.TableColumn;

public class RebuildDataFilterPanel implements Serializable {

	private static final long serialVersionUID = 911733655448646222L;
	
	private Column col;
	private Column col2;
	private boolean leftPar;
	private boolean rightPar;
	private int opIndex;
	private int timeIndex;
	private int unique;
	private boolean and;
	private boolean isOperator = false;
	private boolean isInterval = false;
	private String value;
	private int valueIndex;
	private RebuildDataImageFilterPanel ifp;
	private RebuildDataSynthFilterPanel sfp;
	
	public boolean isLeftPar() {
		return leftPar;
	}
	public void setLeftPar(boolean leftPar) {
		this.leftPar = leftPar;
	}
	public boolean isRightPar() {
		return rightPar;
	}
	public void setRightPar(boolean rightPar) {
		this.rightPar = rightPar;
	}
	public int getOpIndex() {
		return opIndex;
	}
	public void setOpIndex(int opIndex) {
		this.opIndex = opIndex;
	}
	public int getTimeIndex() {
		return timeIndex;
	}
	public void setTimeIndex(int timeIndex) {
		this.timeIndex = timeIndex;
	}
	public Column getCol() {
		return col;
	}
	public void setCol(Column col) {
		this.col = col;
	}
	public Column getCol2() {
		return col2;
	}
	public void setCol2(Column col2) {
		this.col2 = col2;
	}
	public int getUnique() {
		return unique;
	}
	public void setUnique(int unique) {
		this.unique = unique;
	}
	public boolean isAnd() {
		return and;
	}
	public void setAnd(boolean and) {
		this.and = and;
	}
	public boolean isOperator() {
		return isOperator;
	}
	public boolean isInterval() {
		return isInterval;
	}
	public void setOperator(boolean isOperator) {
		this.isOperator = isOperator;
	}
	public void setInterval(boolean isInterval) {
		this.isInterval = isInterval;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getValueIndex() {
		return valueIndex;
	}
	public void setValueIndex(int valueIndex) {
		this.valueIndex = valueIndex;
	}
	public RebuildDataImageFilterPanel getImageFilterPanel() {
		return ifp;
	}
	public void setImageFilterPanel(RebuildDataImageFilterPanel ifp) {
		this.ifp = ifp;
	}
	public RebuildDataSynthFilterPanel getSynthFilterPanel() {
		return sfp;
	}
	public void setSynthFilterPanel(RebuildDataSynthFilterPanel sfp) {
		this.sfp = sfp;
	}
}
