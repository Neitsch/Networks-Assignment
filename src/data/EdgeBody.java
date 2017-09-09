package data;

public class EdgeBody implements Body {
	private final Integer source, destination;
	private final Long updateTime;
	private final Float weight;

	public EdgeBody(Integer source, Integer destination, Float weight, Long updateTime) {
		super();
		this.source = source;
		this.destination = destination;
		this.weight = weight;
		this.updateTime = updateTime;
	}

	public Integer getDestination() {
		return destination;
	}

	public Integer getSource() {
		return source;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public Float getWeight() {
		return weight;
	}
}
