package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Created by rthoth on 21/10/15.
 */
public class Viewport {

	public static final int DEFAULT_ZOOM = 15;

	private final ReferencedEnvelope envelope;
	private final int zoom;

	public Viewport(ReferencedEnvelope envelope, int zoom) {
		this.envelope = envelope;
		this.zoom = zoom;
	}

	public ReferencedEnvelope getEnvelope() {
		return envelope;
	}

	public int getZoom() {
		return zoom;
	}

	public static Viewport of(ReferencedEnvelope envelope, int zoom) {
		return new Viewport(envelope, zoom);
	}
}
