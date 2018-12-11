package btaw.shared.model.query.filter;

public abstract class ImageFilter extends Filter {
	protected Integer regionId;

	private static final long serialVersionUID = -4769061189078601168L;

	public Filter setType(Integer regionId) {
		this.regionId = regionId;
		return this;
	}

}
