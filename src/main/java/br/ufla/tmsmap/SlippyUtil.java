package br.ufla.tmsmap;

import static java.lang.Math.*;

/**
 * Created by rthoth on 21/10/15.
 */
public class SlippyUtil {

	public static int lngToTile(double lng, int zoom) {
		int tile = (int) floor((lng + 180) / 360 * (1 << zoom));

		if (tile < 0)
			tile = 0;

		if (tile >= (1 << zoom))
			tile = ((1 << zoom) - 1);

		return tile;
	}

	public static int latToTile(double lat, int zoom) {
		double rad = toRadians(lat);
		int tile = (int) floor((1 - log(tan(rad) + 1 / cos(rad)) / PI) / 2 * (1 << zoom));

		if (tile < 0)
			tile = 0;

		if (tile >= (1 << zoom))
			tile = ((1 << zoom) - 1);

		return tile;
	}

	public static double tileTolat(int y, int zoom) {
		double n = PI - (2 * PI * y) / pow(2, zoom);
		return toDegrees(atan(sinh(n)));
	}

	public static double tileTolng(int x, int zoom) {
		return x / pow(2, zoom) * 360D - 180D;
	}

	public static double latToTileDouble(double lat, int zoom) {
		double rad = toRadians(lat);

		double tile = (1 - log(tan(rad) + 1 / cos(rad)) / PI) / 2 * (1 << zoom);

		if (tile < 0)
			tile = 0;

		if (tile >= (1 << zoom))
			tile = (1 << zoom) - 1;

		return tile;
	}

	public static double lngToTileDouble(double lng, int zoom) {
		double tile = (lng + 180) / 360 * (1 << zoom);

		if (tile < 0)
			tile = 0;

		if (tile >= (1 << zoom))
			tile = (1 << zoom) - 1;

		return tile;
	}
}
