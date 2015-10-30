package br.ufla.tmsmap;

import org.geotools.styling.Stroke;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by rthoth on 26/10/15.
 */
public class LineStringStyle extends Style {

	protected Color color;
	protected int width;
	protected float opacity;
	protected float[] dashArray;

	public LineStringStyle color(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public org.geotools.styling.Style createStyle() {
		return combine(STYLE_FACTORY.createLineSymbolizer(getStroke(), null));
	}

	public LineStringStyle dashArray(float... dashsArray) {
		this.dashArray = Arrays.copyOf(dashsArray, dashsArray.length);
		return this;
	}

	protected Stroke getStroke() {
		Stroke stroke = STYLE_FACTORY.createStroke(literal(color), literal(width), literal(opacity));

		if (this.dashArray != null && this.dashArray.length > 0)
			stroke.setDashArray(dashArray);

		return stroke;
	}

	public LineStringStyle opacity(float opacity) {
		this.opacity = opacity;
		return this;
	}

	public LineStringStyle width(int width) {
		this.width = width;
		return this;
	}
}
