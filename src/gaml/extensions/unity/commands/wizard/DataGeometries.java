package gaml.extensions.unity.commands.wizard;
public class DataGeometries {
	private String speciesName;
	private Double height = 1.0;
	private Double buffer = 0.0;
	private Boolean hasCollider = false;
	private String tag = "";
	
	
	public DataGeometries() {
		super();
	}
	public String getSpeciesName() {
		return speciesName;
	}
	public void setSpeciesName(String speciesName) {
		this.speciesName = speciesName;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public Boolean getHasCollider() {
		return hasCollider;
	}
	public void setHasCollider(Boolean hasCollider) {
		this.hasCollider = hasCollider;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Double getBuffer() {
		return buffer;
	}
	public void setBuffer(Double buffer) {
		this.buffer = buffer;
	}
	
	
}