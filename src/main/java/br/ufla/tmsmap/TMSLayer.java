package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSLayer implements Layer {

	private static final int TILE_WIDTH = 256;
	private static final int TILE_HEIGHT = 256;

	private final String baseUrl;
	private final int tileWidth;
	private final int tileHeight;

	public TMSLayer(String url, int tileWidth, int tileHeight) throws MalformedURLException {
		assert url != null : "URL is null";
		assert tileHeight > 0 : "TileHeight must be greater than zero";
		assert tileWidth > 0 : "TileWidth must be greater than zero";

		this.baseUrl = url;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public TMSLayer(String url) throws MalformedURLException {
		this(url, TILE_WIDTH, TILE_HEIGHT);
	}

	public static TMSLayer from(String url) throws MalformedURLException {
		return new TMSLayer(url);
	}

	public static TMSLayer from(File file) throws MalformedURLException, UnsupportedEncodingException {
		return new TMSLayer(URLDecoder.decode(file.toURI().toURL().toString(), "UTF-8"));
	}

	@Override
	public DirectLayer toMapLayer(Viewport viewport) {
		return new TMSDirectLayer(viewport);
	}

	private URL urlOf(Tile tile) throws MalformedURLException {
		return new URL(baseUrl
				  .replace("{x}", String.valueOf(tile.x))
				  .replace("{y}", String.valueOf(tile.y))
				  .replace("{z}", String.valueOf(tile.z))
		);
	}

	public class TMSDirectLayer extends DirectLayer {
		private final Viewport viewport;

		public TMSDirectLayer(Viewport viewport) {
			this.viewport = viewport;
		}

		private double calculateDx(MapViewport viewport, double screenAspectRatio) {
			return ((screenAspectRatio * viewport.getBounds().getHeight()) - viewport.getBounds().getWidth()) / 2D;
		}

		private double calculateDy(MapViewport viewport, double screenAspectRatio) {
			return ((viewport.getBounds().getWidth() / screenAspectRatio) - viewport.getBounds().getHeight()) / 2D;
		}

		@Override
		public void draw(Graphics2D graphics, MapContent map, MapViewport viewport) {

			double screenAspectRatio = viewport.getScreenArea().getWidth() / viewport.getScreenArea().getHeight();
			double worldAspectRatio = viewport.getBounds().getWidth() / viewport.getBounds().getHeight();

			double dx = 0, dy = 0;

			if (screenAspectRatio >= worldAspectRatio) {
				dx = calculateDx(viewport, screenAspectRatio);
			} else {
				dy = calculateDy(viewport, screenAspectRatio);
			}

			ReferencedEnvelope renderBounds = new ReferencedEnvelope(viewport.getBounds());
			renderBounds.expandBy(dx, dy);

			TileRange tileRange = new TileRange(renderBounds, this.viewport.getZoom());

			int x1, y1, x2, y2;

			x1 = (int) (tileWidth * (SlippyUtil.lngToTileDouble(tileRange.lowerCorner.getOrdinate(0), this.viewport.getZoom()) - tileRange.minX));
			x2 = (int) (tileWidth * (SlippyUtil.lngToTileDouble(tileRange.upperCorner.getOrdinate(0), this.viewport.getZoom()) - tileRange.minX));

			y1 = (int) (tileHeight * (SlippyUtil.latToTileDouble(tileRange.upperCorner.getOrdinate(1), this.viewport.getZoom()) - tileRange.minY));
			y2 = (int) (tileHeight * (SlippyUtil.latToTileDouble(tileRange.lowerCorner.getOrdinate(1), this.viewport.getZoom()) - tileRange.minY));

			AffineTransform scaleTransform = new AffineTransform(), transform;

			double xscale = viewport.getScreenArea().getWidth() / (x2 - x1),
					  yscale = viewport.getScreenArea().getHeight() / (y2 - y1);
			scaleTransform.scale(xscale, yscale);

			URL url;
			BufferedImage image;
			Tile.TileView view;

			double northtTile, southTile, westTile, eastTile;

			for (Tile tile : tileRange) {

				x1 = (int) (tileWidth * (tile.x - tileRange.minX) * xscale);
				y1 = (int) (tileHeight * (tile.y - tileRange.minY) * yscale);

				transform = new AffineTransform();
				transform.translate(x1 * 2, y1 * 2);
				transform.concatenate(scaleTransform);

				try {
					url = urlOf(tile);
				} catch (MalformedURLException e) {
					throw new TMSLayerException(this, tile, e);
				}

				try {
					image = ImageIO.read(url);
				} catch (IOException e) {
					throw new TMSLayerException(this, tile, e);
				}

				view = tile.view(this.viewport);

				if (!view.within) {

					northtTile = SlippyUtil.latToTileDouble(tileRange.upperCorner.getOrdinate(1), this.viewport.getZoom());
					southTile = SlippyUtil.latToTileDouble(tileRange.lowerCorner.getOrdinate(1), this.viewport.getZoom());
					westTile = SlippyUtil.lngToTileDouble(tileRange.lowerCorner.getOrdinate(0), this.viewport.getZoom());
					eastTile = SlippyUtil.lngToTileDouble(tileRange.upperCorner.getOrdinate(0), this.viewport.getZoom());

					x1 = (int) (tileWidth * normalize(westTile - tile.x));
					y1 = (int) (tileHeight * normalize(northtTile - tile.y));

					x2 = ((int) (tileWidth * normalize(eastTile - tile.x)));
					y2 = ((int) (tileHeight * normalize(southTile - tile.y)));


					try {
						image = image.getSubimage(x1, y1, x2 - x1, y2 - y1);
					} catch (Throwable cause) {
						throw new TMSLayerException(this, tile, cause);
					}
				}

				graphics.drawImage(image, transform, null);
			}
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return viewport.getEnvelope();
		}

		private double normalize(double value) {
			if (value < 0)
				return 0D;
			else if (value > 1)
				return 1D;
			else
				return value;
		}

		@Override
		public String toString() {
			return "TMS Layer(" + baseUrl + ")";
		}
	}
}
