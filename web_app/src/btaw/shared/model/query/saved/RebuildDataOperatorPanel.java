package btaw.shared.model.query.saved;

public class RebuildDataOperatorPanel extends RebuildDataFilterPanel {

	private static final long serialVersionUID = -3524745784573389964L;
	private int unique;
	private boolean and;

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
}
