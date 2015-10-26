package br.ufla.tmsmap;

import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.StreamingRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSMap {

	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private final List<Layer> layers = new LinkedList<>();
		private Viewport viewport;

	public TMSMap addLayer(Layer layer) {
		layers.add(layer);
		return this;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void render(int width, int height, File file) throws IOException {
		assert width > 0 : "width is less than or equal to zero";
		assert height > 0 : "height is less than ou equal to zero";

		StreamingRenderer render = new StreamingRenderer();

		MapContent mapContent = newMapContent(width, height);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Rectangle rectangle = new Rectangle(width, height);

		Graphics2D graphics = image.createGraphics();
		graphics.setColor(TRANSPARENT);
		graphics.fill(rectangle);

		render.setMapContent(mapContent);
		render.paint(graphics, rectangle, viewport.getEnvelope());

		String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

		ImageIO.write(image, extension, file);

		mapContent.getMaxBounds();

		return;
	}

	private MapContent newMapContent(int width, int height) {
		MapContent mapContent = new MapContent();

		for (Layer layer : layers)
			mapContent.addLayer(layer.toMapLayer(getViewport()));

		MapViewport mapViewport = viewport.getMapViewport();

		mapViewport.setMatchingAspectRatio(true);

		mapViewport.setScreenArea(new Rectangle(width, height));

		mapContent.setViewport(mapViewport);

		return mapContent;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
}
