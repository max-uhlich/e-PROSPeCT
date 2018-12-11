package btaw.shared.model.query.saved;

import java.io.Serializable;

import btaw.shared.model.query.column.Column;

public class RebuildDataGroupByPanel implements Serializable {

	private static final long serialVersionUID = 7309961041667283156L;
	private Column col;
	public Column getCol() {
		return col;
	}
	public void setCol(Column col) {
		this.col = col;
	}
}
