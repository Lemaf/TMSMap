package br.ufla.tmsmap;

import org.geotools.styling.Stroke;

import java.awt.*;

/**
 * Created by rthoth on 26/10/15.
 */
public class LineStringStyle extends Style {

	protected Color color;
	protected int width;
	protected float opacity;

	public LineStringStyle color(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public org.geotools.styling.Style createStyle() {
		return combine(STYLE_FACTORY.createLineSymbolizer(getStroke(), null));
	}

	protected Stroke getStroke() {
		return STYLE_FACTORY.createStroke(literal(color), literal(width), literal(opacity));
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
