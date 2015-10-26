package br.ufla.tmsmap;

import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;

import java.awt.*;

/**
 * Created by rthoth on 26/10/15.
 */
public class PolygonStyle extends LineStringStyle {
	private Color fillColor;
	private float fillOpacity;

	public PolygonStyle fillColor(Color color) {
		this.fillColor = color;

		return this;
	}

	@Override
	public Style createStyle() {
		Stroke stroke = getStroke();
		Fill fill = STYLE_FACTORY.createFill(literal(fillColor), literal(fillOpacity));

		return combine(STYLE_FACTORY.createPolygonSymbolizer(stroke, fill, null));
	}

	public PolygonStyle fillOpacity(float opacity) {
		this.fillOpacity = opacity;
		return this;
	}
}
