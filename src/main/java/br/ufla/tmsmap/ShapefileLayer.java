package br.ufla.tmsmap;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapViewport;

import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by rthoth on 26/10/15.
 */
public class ShapefileLayer implements Layer {

	private final File file;
	private final Style style;

	public ShapefileLayer(File file, Style polygonStyle) {
		this.file = file;
		this.style = polygonStyle;
	}

	public static ShapefileLayer from(String path, Style polygonStyle) {
		return new ShapefileLayer(new File(path), polygonStyle);
	}

	@Override
	public org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom, ColorModel colorSpace) {

		ShapefileDataStore dataStore;
		try {
			dataStore = new ShapefileDataStore(file.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new ShapefileException(this.toString(), e);
		}

		try {
			return new FeatureLayer(dataStore.getFeatureSource(), style.createStyle());
		} catch (IOException e) {
			throw new ShapefileException(this.toString(), e);
		}
	}

	@Override
	public String toString() {
		return "Shapefile(" + file + ")";
	}
}
