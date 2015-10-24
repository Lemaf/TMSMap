package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;

/**
 * Created by rthoth on 21/10/15.
 */
public class Viewport {

	private static final int DEFAULT_ZOOM = 15;

	private final ReferencedEnvelope envelope;
	private final int zoom;

	public Viewport(ReferencedEnvelope envelope) {
		this(envelope, DEFAULT_ZOOM);
	}

	public Viewport(ReferencedEnvelope envelope, int zoom) {
		this.envelope = envelope;
		this.zoom = zoom;
	}

	public static Viewport of(ReferencedEnvelope envelope) {
		return new Viewport(envelope);
	}

	public static Viewport of(ReferencedEnvelope envelope, int zoom) {
		return new Viewport(envelope, zoom);
	}

	public ReferencedEnvelope getEnvelope() {
		return envelope;
	}

	public MapViewport getMapViewport() {
		return new MapViewport(envelope);
	}

	public int getZoom() {
		return zoom;
	}

	public Viewport zoomOut() {
		return new Viewport(envelope, zoom - 1);
	}
}
