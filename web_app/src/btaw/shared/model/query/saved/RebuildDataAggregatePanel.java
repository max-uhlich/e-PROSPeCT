package btaw.shared.model.query.saved;

import java.io.Serializable;

import btaw.shared.model.query.column.Column;

public class RebuildDataAggregatePanel implements Serializable {

	private static final long serialVersionUID = -7006240149935973874L;
	private int opIndex;
	private Column col;
	public int getOpIndex() {
		return opIndex;
	}
	public void setOpIndex(int opIndex) {
		this.opIndex = opIndex;
	}
	public Column getCol() {
		return col;
	}
	public void setCol(Column col) {
		this.col = col;
	}
}
