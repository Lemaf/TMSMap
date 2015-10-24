package br.ufla.tmsmap;

/**
 * Created by rthoth on 22/10/15.
 */
public class TMSLayerException extends RuntimeException {
	public TMSLayerException(TMSLayer.TMSDirectLayer layer, Tile tile, Throwable cause) {
		super(layer.toString() + " - " + tile.toString(), cause);
	}
}
