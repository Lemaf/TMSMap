package br.ufla.tmsmap;

import org.geotools.map.MapViewport;

/**
 * Created by rthoth on 21/10/15.
 */
public interface Layer {
	org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom);
}
