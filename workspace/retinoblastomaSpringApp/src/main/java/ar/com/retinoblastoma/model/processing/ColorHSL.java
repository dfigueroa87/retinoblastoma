package ar.com.retinoblastoma.model.processing;

public class ColorHSL {

	private String name;

	private Rank h;
	private Rank l;
	private Rank s;

	private int occurrence;

	private Double percentage;

	public ColorHSL(String name, Rank h, Rank s, Rank l) {
		this.name = name;
		this.h = h;
		this.s = s;
		this.l = l;
		this.occurrence = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public boolean isColor(Double h, Double s, Double l) {
		if (this.h.isRank(h) && this.s.isRank(s) && this.l.isRank(l))
			return true;
		return false;

	}

	public void addOcurrence() {
		occurrence++;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(int total) {
		percentage = ((double) occurrence) / ((double) total);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColorHSL other = (ColorHSL) obj;
		if (h == null) {
			if (other.h != null)
				return false;
		} else if (!h.equals(other.h))
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		if (l == null) {
			if (other.l != null)
				return false;
		} else if (!l.equals(other.l))
			return false;
		return true;
	}

}
