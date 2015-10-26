package br.ufla.tmsmap;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;

/**
 * Created by rthoth on 26/10/15.
 */
public abstract class Style {

	protected static final StyleFactory STYLE_FACTORY = CommonFactoryFinder.getStyleFactory();
	protected static final FilterFactory FILTER_FACTORY = CommonFactoryFinder.getFilterFactory();

	public abstract org.geotools.styling.Style createStyle();


	protected Literal literal(Object value) {
		return FILTER_FACTORY.literal(value);
	}

	protected org.geotools.styling.Style combine(Symbolizer... symbolizers) {
		Rule rule = STYLE_FACTORY.createRule();

		for (Symbolizer symbolizer : symbolizers)
			rule.symbolizers().add(symbolizer);

		FeatureTypeStyle featureTypeStyle = STYLE_FACTORY.createFeatureTypeStyle(new Rule[]{rule});

		org.geotools.styling.Style style = STYLE_FACTORY.createStyle();
		style.featureTypeStyles().add(featureTypeStyle);

		return style;
	}
}
