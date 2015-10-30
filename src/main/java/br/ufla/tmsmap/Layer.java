package br.ufla.tmsmap;

import org.geotools.map.MapViewport;

import java.awt.image.ColorModel;

/**
 * Created by rthoth on 21/10/15.
 */
public interface Layer {
	org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom, ColorModel colorSpace);
}
