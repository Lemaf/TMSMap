package br.ufla.tmsmap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.DirectPosition;

import java.util.Iterator;

/**
 * Created by rthoth on 21/10/15.
 */
public class TileRange implements Iterable<Tile> {

	public final int length;
	public final int width;
	public final int height;
	public final DirectPosition lowerCorner;
	public final DirectPosition upperCorner;
	public final int zoom;
	public final int minX;
	public final int maxX;
	public final int maxY;
	public final int minY;

	public TileRange(ReferencedEnvelope envelope, int zoom) {
		lowerCorner = envelope.getLowerCorner();
		upperCorner = envelope.getUpperCorner();
		this.zoom = zoom;

		minX = SlippyUtil.lngToTile(lowerCorner.getOrdinate(0), zoom);
		maxX = SlippyUtil.lngToTile(upperCorner.getOrdinate(0), zoom);

		assert minX <= maxX : "minX <= maxX";

		minY = SlippyUtil.latToTile(upperCorner.getOrdinate(1), zoom);
		maxY = SlippyUtil.latToTile(lowerCorner.getOrdinate(1), zoom);

		assert minY <= maxY : "minY <= maxY";

		width = (maxX - minX) + 1;
		height = (maxY - minY) + 1;

		length = width * height;
	}

	@Override
	public Iterator<Tile> iterator() {
		return new TileIterator();
	}


	private class TileIterator implements Iterator<Tile> {

		private int y = minY;
		private int x = minX;
		private Tile _next = null;

		@Override
		public boolean hasNext() {
			if (_next == null && y <= maxY && x <= maxX) {
				_next = new Tile(x, y, zoom);

				if (x < maxX) {
					x++;
				} else {
					x = minX;
					y++;
				}

				return true;
			}

			return false;
		}

		@Override
		public Tile next() {
			if (_next == null)
				throw new IllegalStateException();

			try {
				return _next;
			} finally {
				_next = null;
			}
		}

		@Override
		public void remove() {

		}
	}
}
