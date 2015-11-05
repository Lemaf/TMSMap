package br.ufla.tmsmap;

/**
 * Created by rthoth on 05/11/15.
 */
public enum Format {

	PNG("png"), JPEG("jpg");

	public final String formatName;

	Format(String formatName) {
		this.formatName = formatName;
	}

	public static Format from(String formatName) {
		if (JPEG.formatName.equalsIgnoreCase(formatName))
			return JPEG;
		else if (PNG.formatName.equalsIgnoreCase(formatName))
			return PNG;

		return null;
	}
}
