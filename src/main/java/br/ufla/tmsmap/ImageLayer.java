package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

/**
 * Created by rthoth on 29/10/15.
 */
public class ImageLayer implements Layer {
	private final File imageFile;
	private Integer top = null;
	private Integer bottom;
	private Integer right;
	private Integer left;

	public ImageLayer(File imageFile) {
		this.imageFile = imageFile;
	}

	public static ImageLayer from(File imageFile) {
		return new ImageLayer(imageFile);
	}

	public ImageLayer bottom(int bottom) {
		this.top = null;
		this.bottom = bottom;
		return this;
	}

	@Override
	public org.geotools.map.Layer createMapLayer(MapViewport viewport, int zoom, ColorModel colorSpace) {
		return new MapImageLayer(viewport, zoom);
	}

	public ImageLayer left(int left) {
		this.right = null;
		this.left = left;
		return this;
	}

	public ImageLayer right(int right) {
		this.right = right;
		this.left = null;
		return this;
	}

	public ImageLayer top(int top) {
		this.top = top;
		this.bottom = null;

		return this;
	}

	private class MapImageLayer extends DirectLayer {

		private final int zoom;
		private ReferencedEnvelope bounds;

		public MapImageLayer(MapViewport viewport, int zoom) {
			this.bounds = viewport.getBounds();
			this.zoom = zoom;
		}

		@Override
		public void draw(Graphics2D graphics, MapContent map, MapViewport viewport) {
			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(imageFile);
			} catch (IOException e) {
				throw new ImageLayerException(imageFile.getAbsolutePath(), e);
			}

			int x, y;

			// left
			if (left != null)
				x = left;
			else
				x = (int) (viewport.getScreenArea().getWidth() - bufferedImage.getWidth() - right);

			if (top != null)
				y = top;
			else
				y = (int) (viewport.getScreenArea().getHeight() - bufferedImage.getHeight() - bottom);

			graphics.drawImage(bufferedImage, x, y, null);
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return bounds;
		}
	}
}
