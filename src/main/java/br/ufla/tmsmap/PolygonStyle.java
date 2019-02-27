package br.ufla.tmsmap;

import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.*;
import org.opengis.filter.expression.Literal;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by rthoth on 26/10/15.
 */
public class PolygonStyle extends LineStringStyle {
	private Color fillColor;
	private float fillOpacity;
	private java.util.List<Graphic> graphics;

	public PolygonStyle fillColor(Color color) {
		this.fillColor = color;

		return this;
	}

	@Override
	public Style createStyle() {
		Stroke stroke = getStroke();
		Fill fill = STYLE_FACTORY.createFill(literal(fillColor), literal(fillOpacity));

		java.util.List<Symbolizer> symbolizers = new ArrayList<>();
		symbolizers.add(STYLE_FACTORY.createPolygonSymbolizer(stroke, fill, null));

		if(graphics != null) {

			graphics.forEach(g -> {

				Fill completeFill = STYLE_FACTORY.fill(g, null, null);
				PolygonSymbolizer p = STYLE_FACTORY.createPolygonSymbolizer();
				p.setFill(completeFill);
				p.setStroke(stroke);

				symbolizers.add(p);
			});
		}

		Symbolizer[] scs = symbolizers.toArray(new Symbolizer[0]);

		Style combine = combine(scs);

		return combine;
	}

	public PolygonStyle fillOpacity(float opacity) {
		this.fillOpacity = opacity;
		return this;
	}

	public PolygonStyle setShapeSymbolFill(String wellKnownName, Color color, int size, Double rotation, int width, float opacity, float[] dashArray) {

		Literal sizeMarker = FILTER_FACTORY.literal(size);
		Literal rotationMarker = FILTER_FACTORY.literal(rotation);
		Literal opacityMarker = FILTER_FACTORY.literal(opacity);

		Stroke stroke = STYLE_FACTORY.createStroke(literal(color), literal(width), literal(opacity));
		stroke.setDashArray(dashArray);

		Fill fill = null;

		Mark[] markers = {STYLE_FACTORY.createMark(FILTER_FACTORY.literal(wellKnownName), stroke, fill, sizeMarker, rotationMarker)};

		Graphic graphic = STYLE_FACTORY.createGraphic(null, markers, null, opacityMarker, sizeMarker, rotationMarker);

		return this.addGraphic(graphic);
	}

	private PolygonStyle addGraphic(Graphic graphic) {

		if(graphics == null)
			graphics = new ArrayList<>();

		graphics.add(graphic);

		return this;
	}

}
