package br.ufla.tmsmap;

/**
 * Created by rthoth on 21/10/15.
 */
public interface Layer {
	org.geotools.map.Layer toMapLayer(Viewport viewport);
}
