package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.geometry.DirectPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;
import static org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSMap {

	public static final String PNG = "png";
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final double RAD_180__PI = 180D / PI;
	private final List<Layer> layers = new LinkedList<>();
	private Viewport viewport;

	public static double unprojectLat(double y) {
		return atan(sinh(PI * (1 - 2 * y))) * RAD_180__PI;
	}

	public static double unprojectLng(double x) {
		return x * 360D - 180D;
	}

	public static double projectLat(double lat) {
		double rad = toRadians(lat);
		return (1 - (Math.log(Math.tan(rad) + 1 / cos(rad)) / PI)) / 2;
	}

	public static double projectLng(double lng) {
		return (lng + 180) / 360;
	}

	public TMSMap addLayer(Layer layer) {
		layers.add(layer);
		return this;
	}

	private MapContent newMapContent(int width, int height) {
		MapContent mapContent = new MapContent();

		ReferencedEnvelope envelope = viewport.getEnvelope();

		DirectPosition lower = envelope.getLowerCorner();
		DirectPosition upper = envelope.getUpperCorner();

		double x1 = projectLng(lower.getOrdinate(0));
		double y1 = projectLat(upper.getOrdinate(1));
		double x2 = projectLng(upper.getOrdinate(0));
		double y2 = projectLat(lower.getOrdinate(1));

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

			double lng1 = unprojectLng(x1);
			double lng2 = unprojectLng(x2);
			double lat1 = unprojectLat(y1);
			double lat2 = unprojectLat(y2);

			envelope = new ReferencedEnvelope(lng1, lng2, lat2, lat1, envelope.getCoordinateReferenceSystem());
		}


		MapViewport mapViewport = new MapViewport(envelope, false);
		mapViewport.setScreenArea(new Rectangle(width, height));

		for (Layer layer : layers)
			mapContent.addLayer(layer.createMapLayer(mapViewport, viewport.getZoom()));

		mapContent.setViewport(mapViewport);

		return mapContent;
	}

	public void render(int width, int height, String formarName, OutputStream outputStream) {
		try {
			dispose(render0(width, height, formarName, outputStream));
		} catch (Throwable e) {
			throw new TMSMapException(e);
		}
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

	public void render(int width, int height, File file) throws IOException {
		String formatName = file.getName().substring(file.getName().lastIndexOf('.') + 1);

		MapContent mapContent;
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			mapContent = render0(width, height, formatName, outputStream);
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

	private MapContent render0(int width, int height, String formatName, OutputStream outputStream) throws IOException {
		assert width > 0 : "width is less than or equal to zero";
		assert height > 0 : "height is less than ou equal to zero";

		StreamingRenderer render = new StreamingRenderer();

		MapContent mapContent = newMapContent(width, height);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = image.createGraphics();
		graphics.setColor(TRANSPARENT);
		graphics.fill(mapContent.getViewport().getScreenArea());

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		render.setMapContent(mapContent);
		render.paint(graphics, mapContent.getViewport().getScreenArea(), mapContent.getViewport().getBounds());

		ImageIO.write(image, formatName, outputStream);

		return mapContent;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}

	public void zoom(double lngMin, double lngMax, double latMin, double latMax, int zoom) {
		setViewport(new Viewport(new ReferencedEnvelope(lngMin, lngMax, latMin, latMax, WGS84), zoom));
	}
}
