package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.opengis.geometry.DirectPosition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Created by rthoth on 29/10/15.
 */
public class ScaleBar<T extends ScaleBar<?>> {

	public static final int INTERNAL_MARGIN = 5;
	public static final int BACKGROUND_PADDING = 5;
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

	public static class SimpleStyle {
		private Color dark = Color.BLACK;
		private Font font;
		private Color fontColor = Color.BLACK;
		private Color light = Color.WHITE;
		private Color background = new Color(0xff, 0xff, 0xff,  0x80);

		public SimpleStyle background(Color background) {
			if (background != null)
				this.background = background;
			return this;
		}

		public SimpleStyle dark(Color dark) {
			if (dark != null)
				this.dark = dark;
			return this;
		}

		public SimpleStyle font(Font font) {
			this.font = font;
			return this;
		}

		public SimpleStyle fontColor(Color fontColor) {
			if (fontColor != null)
				this.fontColor = fontColor;
			return this;
		}

		public SimpleStyle light(Color light) {
			if (light != null)
				this.light = light;
			return this;
		}
	}

	public static class Simple extends ScaleBar<Simple> implements Layer {

		private final Color backgroundColor;
		private final Color darkColor;
		private final Font font;
		private final Color fontColor;
		private final Color lightColor;
		private String label;

		public Simple(SimpleStyle style) {
			this.font = style.font;
			this.fontColor = style.fontColor;
			this.darkColor = style.dark;
			this.lightColor = style.light;
			this.backgroundColor = style.background;
		}

		public static SimpleStyle style() {
			return new SimpleStyle();
		}

		public static Simple from(SimpleStyle style) {
			return new Simple(style);
		}


		public static Simple from(Font font) {
			return new Simple(new SimpleStyle().font(font));
		}

		public static Simple from(Font font, Color fontColor) {
			return new Simple(new SimpleStyle().font(font).fontColor(fontColor));
		}

		public static Simple from(Font font, Color fontColor, Color backgroundColor) {
			return new Simple(new SimpleStyle().font(font).fontColor(fontColor).background(backgroundColor));
		}

		public String getLabel() {
			return label;
		}

		@Override
		public org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom, ColorModel colorSpace) {
			return new MapSimple(zoom, viewport.getBounds());
		}

		/**
		 * Retorna a imagem da escala desenhada conforme parâmetros do viewport e zoom
		 * @param viewport
		 * @param zoom
		 * @return
		 */
		public BufferedImage render(Viewport viewport, int w, int h, int zoom) {

			MapSimple simple = new MapSimple(zoom, viewport.getEnvelope());

			ReferencedEnvelope envelope = viewport.getEnvelope();
			MapViewport mapViewport = new MapViewport(envelope, false);
			mapViewport.setScreenArea(new Rectangle(w, h));

			BufferedImage image = new BufferedImage(w, h, Format.PNG.type);

			Graphics2D graphics = image.createGraphics();
			graphics.setColor(new Color(0, 0, 0, 0));
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			this.left = BACKGROUND_PADDING;
			this.right = null;
			this.top = BACKGROUND_PADDING;
			this.bottom = null;

			simple.draw(graphics, null, mapViewport);

			this.label = simple.label;

			return image.getSubimage(simple.bx, simple.by, simple.bw, simple.bh);
		}

		private class MapSimple extends DirectLayer {
			private final int zoom;
			private final ReferencedEnvelope bounds;
			private int bx, by, bh, bw;
			private String label;

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

				label = SCALES_LABELS[i];

				FontMetrics fontMetrics = graphics.getFontMetrics(font);
				int labelWidth = fontMetrics.stringWidth(label);
				int labelHeight = fontMetrics.getHeight();

				int scaleWidth = (int) (SCALES[i] * inverse_resolution);
				int x, y;

				bw = scaleWidth + 2 * BACKGROUND_PADDING;
				bh = height + 2 * BACKGROUND_PADDING + labelHeight + INTERNAL_MARGIN;

				if (left != null) {
					x = left;
					bx = left - BACKGROUND_PADDING;
				} else {
					x = width - labelWidth - right;
					bx = width - scaleWidth - right - BACKGROUND_PADDING;
				}

				if (top != null) {
					y = top + labelHeight;
					by = top - BACKGROUND_PADDING;
				} else {
					y = (int) (viewport.getScreenArea().getHeight() - bottom - height - INTERNAL_MARGIN);
					by = (int) (viewport.getScreenArea().getHeight() - bottom - height - labelHeight - INTERNAL_MARGIN - BACKGROUND_PADDING);
				}

				graphics.setColor(backgroundColor);
				graphics.fillRect(bx, by, bw, bh);

				graphics.setColor(fontColor);
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				graphics.setFont(font);
				graphics.drawString(label, x, y);


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

				graphics.setColor(darkColor);
				graphics.fillRect(x, y, scaleWidth, height);

				int subScaleWidth = scaleWidth / SUBSCALES;

				graphics.setColor(lightColor);
				for (int subX = x + subScaleWidth, subEnd = x + scaleWidth - subScaleWidth; subX < subEnd; subX += subScaleWidth * 2) {
					graphics.fillRect(subX, y, subScaleWidth, height);
				}
			}

			@Override
			public ReferencedEnvelope getBounds() {
				return bounds;
			}
		}
	}
}
