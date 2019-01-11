package br.ufla.tmsmap;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;

public class LabelLayer implements Layer {

	private Font font;
	private Color color;
	private final Coordinate coordinate;
	private Integer xOffset = null;
	private Integer yOffset = null;
	private String label;

	public LabelLayer(String label, Coordinate point, Font font, Color color) {
		this.label = label;
		this.coordinate = point;
		this.font = font;
		this.color = color;
	}

	public LabelLayer setYOffset(Integer offset) {
		yOffset = offset;
		return this;
	}

	public LabelLayer setXOffset(Integer offset) {
		xOffset = offset;
		return this;
	}

	@Override
	public org.geotools.map.Layer createMapLayer(MapViewport mapViewport, int i, ColorModel colorModel) {
		return new LabelImageLayer(mapViewport.getBounds());
	}

	private class LabelImageLayer extends DirectLayer {

		private final ReferencedEnvelope bounds;

		public LabelImageLayer(ReferencedEnvelope bounds) {
			this.bounds = bounds;
		}

		@Override
		public void draw(Graphics2D graphics2D, MapContent mapContent, MapViewport mapViewport) {

			AffineTransform transform = mapViewport.getWorldToScreen();

			double[] coordinates = new double[]{coordinate.x, coordinate.y, 0, 0};

			transform.transform(coordinates, 0, coordinates, 2, 1);

			int _xOffset = (xOffset != null) ? xOffset : 0;
			int _yOffset = (yOffset != null) ? yOffset : 0;

			graphics2D.setColor(color);

			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			graphics2D.setFont(font);

			graphics2D.drawString(label, (int)(coordinates[2] - _xOffset), (int)(coordinates[3] - _yOffset));
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return bounds;
		}
	}

}
