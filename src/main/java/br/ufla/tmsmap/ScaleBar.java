package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.opengis.geometry.DirectPosition;

import java.awt.*;

/**
 * Created by rthoth on 29/10/15.
 */
public class ScaleBar<T extends ScaleBar<?>> {

	public static final int INTERNAL_MARGIN = 5;
	private static final int[] SCALES = {
			  500000, 250000, 100000, 50000, 25000, 10000, 1000, 500, 250, 100, 50, 25, 10
	};
	private static final String[] SCALES_LABELS = {
			  "500km", "250km", "100km", "50km", "25km", "10km", "1km", "500m", "250m", "100m", "50m", "25m", "10m"
	};
	private static final int SUBSCALES = 5;

	private static double R;

	static {
		double majorAxis = 6378137;
		double flattening = 1 / 298.257223563;
		double e2 = 2 * flattening - flattening * flattening;

		R = Math.PI * majorAxis * (2 - (e2 / 2) + (3 / 16) * (e2 * e2));
	}

	protected Integer bottom = null;
	protected Integer left = null;
	protected Integer right = null;
	protected Integer top = null;
	protected Integer height;

	public T bottom(int bottom) {
		this.bottom = bottom;
		this.top = null;
		return (T) this;
	}

	public T height(int height) {
		this.height = height;

		return (T) this;
	}

	public T left(int left) {
		this.left = left;
		this.right = null;
		return (T) this;
	}

	public T right(int right) {
		this.right = right;
		this.left = null;
		return (T) this;
	}

	public T top(int top) {
		this.top = top;
		this.bottom = null;
		return (T) this;
	}

	public static class Simple extends ScaleBar<Simple> implements Layer {

		private final Font font;
		private final Color color;

		public Simple(Font font, Color color) {
			this.font = font;
			this.color = color;
		}

		public static Simple from(Font font, Color color) {
			return new Simple(font, color);
		}

		@Override
		public org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom) {
			return new MapSimple(zoom, viewport.getBounds());
		}

		private class MapSimple extends DirectLayer {
			private final int zoom;
			private final ReferencedEnvelope bounds;

			public MapSimple(int zoom, ReferencedEnvelope bounds) {
				this.zoom = zoom;
				this.bounds = bounds;
			}


			@Override
			public void draw(Graphics2D graphics, MapContent map, MapViewport viewport) {
				DirectPosition lowerCorner = viewport.getBounds().getLowerCorner(),
						  upperCorner = viewport.getBounds().getUpperCorner();

				double minLat = TMSMap.projectLat(upperCorner.getOrdinate(1)),
						  maxLat = TMSMap.projectLat(lowerCorner.getOrdinate(1));

				double length = (maxLat - minLat) * R;
				double inverse_resolution = viewport.getScreenArea().getHeight() / length;

				int width = (int) viewport.getScreenArea().getWidth();
				int width_2 = width / 2;

				int i;
				for (i = 0; i <= SCALES.length; i++) {
					if (inverse_resolution * SCALES[i] <= width_2)
						break;
				}

				String label = SCALES_LABELS[i];

				FontMetrics fontMetrics = graphics.getFontMetrics(font);
				int labelWidth = fontMetrics.stringWidth(label);
				int labelHeight = fontMetrics.getHeight();

				int x, y;

				if (left != null)
					x = left;
				else
					x = width - labelWidth - right;

				if (top != null)
					y = top + labelHeight;
				else
					y = (int) (viewport.getScreenArea().getHeight() - bottom - height - INTERNAL_MARGIN);

				graphics.setColor(color);

				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				graphics.setFont(font);
				graphics.drawString(label, x, y);

				int scaleWidth = (int) (SCALES[i] * inverse_resolution);

				if (left != null) {
					x = left;
				} else {
					x = width - scaleWidth - right;
				}

				if (top != null) {
					y = top + labelHeight + INTERNAL_MARGIN;
				} else {
					y = (int) (viewport.getScreenArea().getHeight() - bottom - height);
				}

				graphics.fillRect(x, y, scaleWidth, height);

				int subscaleWidth = scaleWidth / SUBSCALES;

				graphics.setColor(new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2));
				for (int subX = x + subscaleWidth, subEnd = x + scaleWidth - subscaleWidth; subX < subEnd; subX += subscaleWidth * 2) {
					graphics.fillRect(subX, y, subscaleWidth, height);
				}
			}

			@Override
			public ReferencedEnvelope getBounds() {
				return bounds;
			}
		}
	}
}
