package br.ufla.tmsmap;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

public class MarkerLayer implements Layer {

	private final File imageFile;
	private final Coordinate coordinate;
	private Integer xOffset = null;
	private Integer yOffset = null;

	public MarkerLayer(File imageFile, Coordinate point) {
		this.imageFile = imageFile;
		this.coordinate = point;
	}

	public MarkerLayer setYOffset(Integer offset) {
		yOffset = offset;
		return this;
	}

	public MarkerLayer setXOffset(Integer offset) {
		xOffset = offset;
		return this;
	}

	@Override
	public org.geotools.map.Layer createMapLayer(MapViewport mapViewport, int i, ColorModel colorModel) {
		return new MarkerImageLayer(mapViewport.getBounds());
	}

	private class MarkerImageLayer extends DirectLayer {

		private final ReferencedEnvelope bounds;

		public MarkerImageLayer(ReferencedEnvelope bounds) {
			this.bounds = bounds;
		}

		@Override
		public void draw(Graphics2D graphics2D, MapContent mapContent, MapViewport mapViewport) {
			AffineTransform transform = mapViewport.getWorldToScreen();

			double[] coordinates = new double[]{coordinate.x, coordinate.y, 0, 0};
			transform.transform(coordinates, 0, coordinates, 2, 1);

			try {
				BufferedImage image = ImageIO.read(imageFile);
				int _xOffset = (xOffset != null) ? xOffset : image.getWidth() / 2;
				int _yOffset = (yOffset != null) ? yOffset : image.getHeight() / 2;

				graphics2D.drawImage(image, (int)(coordinates[2] - _xOffset), (int)(coordinates[3] - _yOffset), null);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return bounds;
		}
	}

}
