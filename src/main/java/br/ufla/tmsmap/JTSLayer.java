package br.ufla.tmsmap;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapViewport;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Collections;
import java.util.List;

/**
 * Created by rthoth on 29/10/15.
 */
public class JTSLayer implements Layer {
	private static SimpleFeatureType defaultFeatureType;

	static {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.add("the_geom", Geometry.class, DefaultGeographicCRS.WGS84);

		builder.setName("JTSLayer");

		defaultFeatureType = builder.buildFeatureType();
	}

	private final Geometry[] geometries;
	private final CoordinateReferenceSystem crs;
	private final Style style;

	public JTSLayer(CoordinateReferenceSystem crs, Style style, Geometry... geometries) {
		this.crs = crs;
		this.geometries = geometries;
		this.style = style;
	}

	public static JTSLayer from(CoordinateReferenceSystem crs, Style style, Geometry... geometries) {
		return new JTSLayer(crs, style, geometries);
	}

	@Override
	public org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom) {

		org.geotools.styling.Style mapStyle = style.createStyle();

		DefaultFeatureCollection collection = new DefaultFeatureCollection();

		for (int i = 0; i < geometries.length; i++) {
			List<Object> values = Collections.<Object>singletonList(geometries[i]);

			SimpleFeatureImpl feature = new SimpleFeatureImpl(values, defaultFeatureType, new FeatureIdImpl(String.valueOf(i)));
			collection.add(feature);
		}

		return new FeatureLayer(collection, mapStyle);
	}
}
