package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.opengis.geometry.DirectPosition;

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
	public DirectLayer toMapLayer(MapViewport viewport, int zoom) {
		return new TMSDirectLayer(viewport, zoom);
	}

	private URL urlOf(Tile tile) throws MalformedURLException {
		return new URL(baseUrl
				  .replace("{x}", String.valueOf(tile.x))
				  .replace("{y}", String.valueOf(tile.y))
				  .replace("{z}", String.valueOf(tile.z))
		);
	}

	public class TMSDirectLayer extends DirectLayer {
		private final int zoom;
		private final MapViewport viewport;

		public TMSDirectLayer(MapViewport viewport, int zoom) {
			this.viewport = viewport;
			this.zoom = zoom;
		}

		@Override
		public void draw(Graphics2D graphics, MapContent map, MapViewport viewport) {

			int x1, y1, x2, y2;

			DirectPosition lowerCorner = viewport.getBounds().getLowerCorner(),
					  upperCorner = viewport.getBounds().getUpperCorner();

			x1 = (int) (tileWidth * (SlippyUtil.lngToTileDouble(lowerCorner.getOrdinate(0), zoom)));
			x2 = (int) (tileWidth * (SlippyUtil.lngToTileDouble(upperCorner.getOrdinate(0), zoom)));

			y1 = (int) (tileHeight * (SlippyUtil.latToTileDouble(upperCorner.getOrdinate(1), zoom)));
			y2 = (int) (tileHeight * (SlippyUtil.latToTileDouble(lowerCorner.getOrdinate(1), zoom)));

			int width = x2 - x1, height = y2 - y1;

			double screenAspectRatio = viewport.getScreenArea().getWidth() / viewport.getScreenArea().getHeight(),
			mapAspectRatio = ((double)width) / height;

			if (screenAspectRatio >= mapAspectRatio) {
				double dx = (height * screenAspectRatio - width) / 2D;
				x1 -= dx;
				x2 += dx;
			} else {
				double dy = ((width / screenAspectRatio) - height) / 2D;
				y1 -= dy;
				y2 += dy;
			}

			width = x2 - x1;
			height = y2 - y1;

			double lng1 = SlippyUtil.pixelToLng(x1, zoom, tileWidth),
			lng2 = SlippyUtil.pixelToLng(x2, zoom, tileWidth),
			lat1 = SlippyUtil.pixelToLat(y1, zoom, tileHeight),
			lat2 = SlippyUtil.pixelToLat(y2, zoom, tileHeight);

			ReferencedEnvelope envelope = new ReferencedEnvelope(lng1, lng2, lat2, lat1, viewport.getBounds().getCoordinateReferenceSystem());

			TileRange tileRange = new TileRange(envelope, zoom);


			AffineTransform scaleTransform = new AffineTransform(), transform;

			double xscale = viewport.getScreenArea().getWidth() / width,
					  yscale = viewport.getScreenArea().getHeight() / height;

			//scaleTransform.scale(xscale, yscale);

			URL url;
			BufferedImage image;

			double northtTile, southTile, westTile, eastTile;

			northtTile = SlippyUtil.latToTileDouble(tileRange.upperCorner.getOrdinate(1), zoom);
			//southTile = SlippyUtil.latToTileDouble(tileRange.lowerCorner.getOrdinate(1), this.viewport.getZoom());
			westTile = SlippyUtil.lngToTileDouble(tileRange.lowerCorner.getOrdinate(0), zoom);
			//eastTile = SlippyUtil.lngToTileDouble(tileRange.upperCorner.getOrdinate(0), this.viewport.getZoom());

			AffineTransform scaleAndTranslateTransform = new AffineTransform(scaleTransform);

			x1 = (int) (tileWidth * (westTile - tileRange.minX));
			y1 = (int) (tileHeight * (northtTile - tileRange.minY));

			scaleAndTranslateTransform.translate(-x1, -y1);

			for (Tile tile : tileRange) {

				transform = new AffineTransform(scaleAndTranslateTransform);

				x1 = tileWidth * (tile.x - tileRange.minX);
				y1 = tileHeight * (tile.y - tileRange.minY);

				transform.translate(x1, y1);

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

				graphics.drawImage(image, transform, null);
			}
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return viewport.getBounds();
		}

		private double normalize(double value) {
			if (value < 0D)
				return 0D;
			else if (value > 1D)
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
