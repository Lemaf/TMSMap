package br.ufla.tmsmap;

import org.geotools.map.MapViewport;

import java.awt.image.ColorModel;
import java.util.concurrent.ExecutorService;

/**
 * Created by rthoth on 15/03/16.
 */
public interface ConcurrentLayer extends Layer {
	org.geotools.map.Layer createMapLayer(MapViewport mapViewport, int zoom, ColorModel colorModel, ExecutorService executor);
}
