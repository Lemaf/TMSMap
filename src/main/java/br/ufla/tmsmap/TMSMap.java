package br.ufla.tmsmap;

import com.vividsolutions.jts.geom.Envelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.geometry.DirectPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static br.ufla.tmsmap.TMSLayer.TILE_HEIGHT;
import static br.ufla.tmsmap.TMSLayer.TILE_WIDTH;
import static java.lang.Math.*;
import static org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSMap {

	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final double RAD_180__PI = 180D / PI;
	private static final Map<Format, Integer> IMAGE_TYPE_MAP = new HashMap<>();

	static {
		IMAGE_TYPE_MAP.put(Format.PNG, BufferedImage.TYPE_INT_ARGB);
		IMAGE_TYPE_MAP.put(Format.JPEG, BufferedImage.TYPE_INT_RGB);
	}

	private final List<Layer> layers = new LinkedList<>();
	private final ScheduledThreadPoolExecutor executor;

	private Viewport viewport;
	private Integer horizontalPadding = null;
	private Integer verticalPadding = null;

	public TMSMap(int poolSize) {
		executor = (poolSize > 0) ? new ScheduledThreadPoolExecutor(poolSize) : null;
	}

	public TMSMap() {
		this(8);
	}

	public static double unprojectLat(double y) {
		return atan(sinh(PI * (1 - 2 * y))) * RAD_180__PI;
	}

	public static double unprojectLng(double x) {
		return x * 360D - 180D;
	}

	public static double projectLat(double lat) {
		double rad = toRadians(lat);
		return (1 - (log(Math.tan(rad) + 1 / cos(rad)) / PI)) / 2;
	}

	public static double projectLng(double lng) {
		return (lng + 180) / 360;
	}

	public TMSMap addLayer(Layer layer) {
		layers.add(layer);
		return this;
	}

	private void dispose(MapContent mapContent) {

		for (org.geotools.map.Layer layer : mapContent.layers()) {
			try {
				layer.preDispose();
			} finally {

			}
		}

		mapContent.dispose();
	}

	private MapContent newMapContent(int width, int height, ColorModel colorModel) {
		MapContent mapContent = new MapContent();

		ReferencedEnvelope envelope = viewport.getEnvelope();

		DirectPosition lower = envelope.getLowerCorner();
		DirectPosition upper = envelope.getUpperCorner();

		double lng1, lng2, lat1, lat2;

		lng1 = lower.getOrdinate(0);
		lng2 = upper.getOrdinate(0);
		lat1 = upper.getOrdinate(1);
		lat2 = lower.getOrdinate(1);

		double x1 = projectLng(lng1);
		double x2 = projectLng(lng2);
		double y1 = projectLat(lat1);
		double y2 = projectLat(lat2);


		if (horizontalPadding != null || verticalPadding != null) {
			int hPadding = horizontalPadding != null ? horizontalPadding : 0;
			int vPadding = verticalPadding != null ? verticalPadding : 0;

			int n = (int) Math.pow(2, viewport.getZoom());

			int xPixels = n * TILE_WIDTH;
			int yPixels = n * TILE_HEIGHT;

			x1 *= xPixels;
			x2 *= xPixels;
			y1 *= yPixels;
			y2 *= yPixels;

			double xscale = width / (x2 - x1), yscale = height / (y2 - y1);

			x1 -= hPadding / xscale;
			x2 += hPadding / xscale;
			y1 -= vPadding / yscale;
			y2 += vPadding / yscale;

			x1 /= xPixels;
			x2 /= xPixels;
			y1 /= yPixels;
			y2 /= yPixels;
		}

		double mapAspect = (x2 - x1) / (y2 - y1);
		double screenAspect = ((double) width) / height;

		if (screenAspect != mapAspect) {
			if (screenAspect > mapAspect) {
				double dx = ((y2 - y1) * screenAspect - (x2 - x1)) / 2D;
				x1 -= dx;
				x2 += dx;
			} else {
				double dy = ((x2 - x1) / screenAspect - (y2 - y1)) / 2D;
				y1 -= dy;
				y2 += dy;
			}

			lng1 = unprojectLng(x1);
			lng2 = unprojectLng(x2);
			lat1 = unprojectLat(y1);
			lat2 = unprojectLat(y2);

			envelope = new ReferencedEnvelope(lng1, lng2, lat2, lat1, envelope.getCoordinateReferenceSystem());
		}


		MapViewport mapViewport = new MapViewport(envelope, false);
		mapViewport.setScreenArea(new Rectangle(width, height));

		for (Layer layer : layers) {
			if (executor != null && layer instanceof ConcurrentLayer) {
				mapContent.addLayer(((ConcurrentLayer) layer).createMapLayer(mapViewport, viewport.getZoom(), colorModel, executor));
			} else {
				mapContent.addLayer(layer.createMapLayer(mapViewport, viewport.getZoom(), colorModel));
			}
		}

		mapContent.setViewport(mapViewport);

		return mapContent;
	}

	public void padding(int horizontal, int vertical) {
		this.horizontalPadding = horizontal;
		this.verticalPadding = vertical;
	}

	public void render(int width, int height, Format format, OutputStream outputStream) {
		try {
			dispose(render0(width, height, format, outputStream));
		} catch (Throwable e) {
			throw new TMSMapException(e);
		}
	}

	public void render(int width, int height, File file) throws IOException {
		String formatName = file.getName().substring(file.getName().lastIndexOf('.') + 1);

		MapContent mapContent;
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			mapContent = render0(width, height, Format.from(formatName), outputStream);
		}

		try {
			File bboxFile = new File(file.getParentFile(), file.getName() + ".bbox");
			try (FileOutputStream outputStream = new FileOutputStream(bboxFile)) {
				ReferencedEnvelope envelope = mapContent.getViewport().getBounds();

				StringBuilder sb = new StringBuilder();
				sb.append(envelope.getCoordinateReferenceSystem().getName().getCode())
						.append(';')
						.append(envelope.getMinX())
						.append(',')
						.append(envelope.getMinY())
						.append(":")
						.append(envelope.getMaxX())
						.append(',')
						.append(envelope.getMaxY());

				outputStream.write(sb.toString().getBytes());
				outputStream.flush();
			}
		} finally {
			dispose(mapContent);
		}

	}

	private MapContent render0(int width, int height, Format format, OutputStream outputStream) throws IOException {
		assert width > 0 : "width is less than or equal to zero";
		assert height > 0 : "height is less than ou equal to zero";
		assert format != null : "formatName is null!";

		StreamingRenderer render = new StreamingRenderer();


		Integer imageType = IMAGE_TYPE_MAP.get(format);

		assert imageType != null : "Invalid format " + format.name();

		BufferedImage image = new BufferedImage(width, height, imageType);

		MapContent mapContent = newMapContent(width, height, image.getColorModel());

		Graphics2D graphics = image.createGraphics();
		graphics.setColor(TRANSPARENT);
		graphics.fill(mapContent.getViewport().getScreenArea());

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		render.setMapContent(mapContent);
		render.paint(graphics, mapContent.getViewport().getScreenArea(), mapContent.getViewport().getBounds());

		ImageIO.write(image, format.formatName, outputStream);

		return mapContent;
	}

	public Viewport getViewport() { return this.viewport; }

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}

	public void zoom(double lngMin, double lngMax, double latMin, double latMax, int zoom) {
		setViewport(new Viewport(new ReferencedEnvelope(lngMin, lngMax, latMin, latMax, WGS84), zoom));
	}

	public void zoom(Envelope env, int zoom) {
		zoom(env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY(), zoom);
	}

	public void zoomTo(Envelope envelope, int width, int height) {
		zoomTo(envelope, width, height, 0, 15, 256, 256);
	}

	public void zoomTo(Envelope envelope, int width, int height, int minZoom, int maxZoom, int tileWidth, int tileHeight) {
		assert minZoom <= maxZoom : "minZoom <= maxZoom";

		int pZoom;

		double n = width / (tileWidth * (projectLng(envelope.getMaxX()) - projectLng(envelope.getMinX())));
		int z = (int) (log(n) / log(2));
		pZoom = min(max(minZoom, z), maxZoom);

		n = height / (tileHeight * (projectLat(envelope.getMinY()) - projectLat(envelope.getMaxY())));
		z = (int) (log(n) / log(2));

		zoom(envelope, min(pZoom, z));
	}

	public Integer getZoom() {
		return viewport != null ? viewport.getZoom() : null;
	}
}
