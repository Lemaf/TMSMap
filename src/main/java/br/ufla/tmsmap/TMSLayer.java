package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.opengis.geometry.DirectPosition;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSLayer implements ConcurrentLayer {

	public static final int TILE_WIDTH = 256;
	public static final int TILE_HEIGHT = 256;

	private final String baseUrl;
	private final int tileWidth;
	private final int tileHeight;
	private final boolean tms;

	private TMSLayer(String url, int tileWidth, int tileHeight, boolean tms) throws MalformedURLException {
		assert url != null : "URL is null";
		assert tileHeight > 0 : "TileHeight must be greater than zero";
		assert tileWidth > 0 : "TileWidth must be greater than zero";

		this.baseUrl = url;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tms = tms;
	}

	private TMSLayer(String url, boolean tms) throws MalformedURLException {
		this(url, TILE_WIDTH, TILE_HEIGHT, tms);
	}

	public static TMSLayer from(URL url) throws MalformedURLException {
		return from(url, true);
	}

	public static TMSLayer from(URL url, boolean tms) throws MalformedURLException {
		return new TMSLayer(url.toString(), tms);
	}

	public static TMSLayer from(File file, boolean tms) throws MalformedURLException, UnsupportedEncodingException {
		return new TMSLayer(URLDecoder.decode(file.toURI().toURL().toString(), "UTF-8"), tms);
	}

	public static TMSLayer from(File file) throws MalformedURLException, UnsupportedEncodingException {
		return from(file, true);
	}

	@Override
	public DirectLayer createMapLayer(MapViewport viewport, int zoom, ColorModel colorSpace) {
		return new TMSDirectLayer(viewport, zoom, colorSpace);
	}

	@Override
	public Layer createMapLayer(MapViewport mapViewport, int zoom, ColorModel colorModel, ExecutorService executor) {
		return new TMSConcurrentDirectLayer(mapViewport, zoom, colorModel, executor);
	}

	private URL urlOf(Tile tile) throws MalformedURLException {
		return new URL(baseUrl
				.replace("{x}", String.valueOf(tile.x))
				.replace("{y}", String.valueOf(tile.y))
				.replace("{z}", String.valueOf(tile.z))
		);
	}

	public class TMSDirectLayer extends DirectLayer {
		protected final int zoom;
		protected final MapViewport viewport;
		protected final ColorModel colorModel;

		public TMSDirectLayer(MapViewport viewport, int zoom, ColorModel colorModel) {
			this.viewport = viewport;
			this.zoom = zoom;
			this.colorModel = colorModel;
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

			if (tms) {
				int max = (int) (tileHeight * SlippyUtil.latToTileDouble(-90D, zoom));

				int temp = y1;
				y1 = max - y2;
				y2 = max - temp;
			}


			int width = x2 - x1, height = y2 - y1;

			TileRange tileRange = new TileRange(viewport.getBounds(), zoom, tms);

			AffineTransform scaleTransform = new AffineTransform();

			double xscale = viewport.getScreenArea().getWidth() / width,
					yscale = viewport.getScreenArea().getHeight() / height;

			scaleTransform.scale(xscale, yscale);

			x1 = x1 % tileWidth;
			if (tms) {
				y1 = y2 % tileHeight;
				if (y1 != 0)
					y1 = tileHeight - y1;
			} else {
				y1 = y1 % tileHeight;
			} 

			drawTileRange(tileRange, graphics, x1, y1, scaleTransform);
		}

		protected void drawTileRange(TileRange tileRange, Graphics2D graphics, int x1, int y1, AffineTransform baseTransform) {

			AffineTransform transform = new AffineTransform();
			URL url;
			BufferedImage image;

			loop:
			for (Tile tile : tileRange) {
				try {
					url = urlOf(tile);
				} catch (MalformedURLException e) {
					throw new TMSLayerException(this, tile, e);
				}

				try {
					image = ImageIO.read(url);
				} catch (IOException e) {
					Throwable cause = e;
					while (cause != null) {
						if (cause instanceof FileNotFoundException) {
							continue loop;
						} else {
							cause = cause.getCause();
						}
					}

					throw new TMSLayerException(this, tile, e);
				}

				drawTile(tileRange, graphics, x1, y1, baseTransform, transform, image, tile);
			}
		}

		protected void drawTile(TileRange tileRange, Graphics2D graphics, int x1, int y1, AffineTransform baseTransform, AffineTransform transform, BufferedImage image, Tile tile) {
			int x2;
			int y2;
			transform.setToIdentity();
			transform.concatenate(baseTransform);

			x2 = tileWidth * (tile.x - tileRange.minX);

			if (tms) {
				y2 = tileHeight * (tileRange.maxY - tile.y);
			} else {
				y2 = tileHeight * (tile.y - tileRange.minY);
			}
			
			transform.translate(x2 - x1, y2 - y1);

			try {
				graphics.drawImage(image, transform, null);
			} catch (Throwable throwable) {
				throw new TMSLayerException(this, tile, throwable);
			}
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return viewport.getBounds();
		}

		@Override
		public String toString() {
			return "TMS Layer(" + baseUrl + ")";
		}
	}

	private class TMSConcurrentDirectLayer extends TMSDirectLayer {
		private final ExecutorService executor;

		public TMSConcurrentDirectLayer(MapViewport viewport, int zoom, ColorModel colorModel, ExecutorService executor) {
			super(viewport, zoom, colorModel);
			this.executor = executor;
		}

		@Override
		public ReferencedEnvelope getBounds() {
			return viewport.getBounds();
		}

		@Override
		protected void drawTileRange(TileRange tileRange, Graphics2D graphics, int x1, int y1, AffineTransform baseTransform) {
			LinkedList<Future<T2<BufferedImage, Tile>>> futures = new LinkedList<>();
			for (Tile tile : tileRange) {
				futures.add(executor.submit(new GetTile(tile)));
			}

			AffineTransform transform = new AffineTransform();

			while (!futures.isEmpty()) {
				Iterator<Future<T2<BufferedImage, Tile>>> iterator = futures.iterator();
				while (iterator.hasNext()) {
					Future<T2<BufferedImage, Tile>> future = iterator.next();

					if (future.isDone()) {
						try {
							transform.setToIdentity();
							T2<BufferedImage, Tile> t2 = future.get();
							drawTile(tileRange, graphics, x1, y1, baseTransform, transform, t2._1, t2._2);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							iterator.remove();
						}
					}
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private class GetTile implements Callable<T2<BufferedImage, Tile>> {
			private final Tile tile;

			public GetTile(Tile tile) {
				this.tile = tile;
			}

			@Override
			public T2<BufferedImage, Tile> call() throws Exception {
				return new T2(ImageIO.read(urlOf(tile)), tile);
			}
		}
	}
}
