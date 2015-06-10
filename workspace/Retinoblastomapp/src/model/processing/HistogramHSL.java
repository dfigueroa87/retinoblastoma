package model.processing;

import java.util.ArrayList;
import java.util.List;

public class HistogramHSL {

	private List<ColorHSL> colors;
	private int total;

	public HistogramHSL() {
		colors = new ArrayList<ColorHSL>();
		total = 0;
	}

	public boolean addColor(ColorHSL color) {
		if (!colors.contains(color)) {
			colors.add(color);
			return true;
		}
		return false;
	}

	public boolean removeColor(ColorHSL color) {
		return colors.remove(color);
	}

	public List<ColorHSL> getColors() {
		return colors;
	}

	public void setColors(List<ColorHSL> colors) {
		this.colors = colors;
	}

	public void calculate(Double h, Double s, Double l) {
		total++;
		boolean found = false;
		for (int i = 0; i < colors.size() && !found; i++) {
			found = false;
			ColorHSL color = colors.get(i);
			if (color.isColor(h, s, l)) {
				color.addOcurrence();
				color.setPercentage(total);
				found = true;
			} else {
				color.setPercentage(total);
			}

		}
	}

	public int getTotal() {
		return total;
	}

}
