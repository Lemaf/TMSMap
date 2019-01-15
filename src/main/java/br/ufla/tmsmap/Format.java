package br.ufla.tmsmap;

import java.awt.image.BufferedImage;

/**
 * Created by rthoth on 05/11/15.
 */
public enum Format {

	PNG(BufferedImage.TYPE_INT_ARGB, "png"), JPEG(BufferedImage.TYPE_INT_RGB, "jpg");

	public final Integer type;
	public final String formatName;

	Format(Integer type, String formatName) {
		this.type = type;
		this.formatName = formatName;
	}

	Format(String formatName) {
		this.type = formatName.equalsIgnoreCase("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
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
