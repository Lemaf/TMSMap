package br.ufla.tmsmap;

import org.opengis.geometry.DirectPosition;

/**
 * Created by rthoth on 21/10/15.
 */
public class Tile {
	public final int x;
	public final int y;
	public final int z;

	public Tile(int x, int y, int zoom) {
		this.x = x;
		this.y = y;
		this.z = zoom;
	}

	public TileView view(Viewport viewport) {
		return new TileView(viewport);
	}

	@Override
	public String toString() {
		return "Tile(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * Created by rthoth on 22/10/15.
	 */
	public class TileView {

		public final double north;
		public final double south;
		public final double west;
		public final double east;
		public final boolean within;

		public TileView(Viewport viewport) {
			north = SlippyUtil.tileTolat(y, viewport.getZoom());
			south = SlippyUtil.tileTolat(y + 1, viewport.getZoom());

			west = SlippyUtil.tileTolng(x, viewport.getZoom());
			east = SlippyUtil.tileTolng(x + 1, viewport.getZoom());

			DirectPosition ws = viewport.getEnvelope().getLowerCorner();
			DirectPosition en = viewport.getEnvelope().getUpperCorner();

			if (west < ws.getOrdinate(0))
				within = false;
			else if (east > en.getOrdinate(0))
				within = false;
			else  if (north > en.getOrdinate(1))
				within = false;
			else if (south < ws.getOrdinate(1))
				within = false;
			else
				within = true;

			if (within) {

			}
		}
	}
}
