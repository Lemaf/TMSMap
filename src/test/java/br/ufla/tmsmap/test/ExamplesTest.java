package br.ufla.tmsmap.test;

import br.ufla.tmsmap.*;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by rthoth on 30/10/15.
 */
public class ExamplesTest {

	@Test
	public void example01() throws IOException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png"), false));
		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

		File imageFile = File.createTempFile("TMSMap-Example-01.01-", ".png");
		OutputStream imageStream = new FileOutputStream(File.createTempFile("TMSMap-Example-01.02-", ".png"));

		map.render(500, 500, imageFile);
		map.render(500, 500, Format.PNG, imageStream);

	}

	@Test
	public void example02() throws IOException {
		TMSMap map = new TMSMap();

		URL url = new URL(URLDecoder.decode(new File("test-data/lavras/{z}/{x}/{y}.png").toURI().toURL().toString(), "UTF-8"));

		map.addLayer(TMSLayer.from(url, false));
		map.addLayer(ShapefileLayer.from("test-data/shapefiles/hidro.shp", Helper.getLineStyle01()));
		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

		File imageFile = File.createTempFile("TMSMap-Example-02-", ".jpg");
		map.render(1000, 1250, imageFile);
	}

	@Test
	public void example03() throws IOException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png"), false));
		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

		map.addLayer(ImageLayer.from(new File("test-data/compass-rose.png")).right(40).top(20));
		map.padding(50, 25);

		File imageFile = File.createTempFile("TMSMap-Example-03-", ".jpg");
		map.render(500, 500, imageFile);
	}

	@Test
	public void example04() throws IOException {
		TMSMap map = new TMSMap();

		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
		Color color = new Color(255, 255, 255);

		map.addLayer(TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png"), false));
		map.addLayer(ScaleBar.Simple.from(font, color).bottom(10).left(10).height(10));

		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

		map.render(600, 600, File.createTempFile("TMSMap-Example-04-", ".png"));
	}

	@Test
	public void example05() throws ParseException, IOException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png"), false));

		CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;

		Style polygonStyle = new PolygonStyle()
				  .fillColor(new Color(157, 255, 105))
				  .fillOpacity(0.5f)
				  .color(new Color(255, 102, 0))
				  .opacity(0.9f);

		Geometry geometry1 = JTSTest.geomOf(JTSTest.POLY_01);
		Geometry geometry2 = JTSTest.geomOf(JTSTest.POLY_02);

		map.addLayer(JTSLayer.from(crs, polygonStyle, geometry1, geometry2));

		Envelope envelope = geometry1.getEnvelopeInternal();
		envelope.expandToInclude(geometry2.getEnvelopeInternal());

		map.zoom(envelope.getMinX(), envelope.getMaxX(), envelope.getMinY(), envelope.getMaxY(), 12);

		map.padding(200, 200);

		map.render(500, 500, File.createTempFile("TMSMap-Example-05-", ".png"));
	}


	@Test
	public void example06_tms() throws IOException, ParseException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(new File("test-data/lavras-tms/{z}/{x}/{y}.png"), false));

		Style style = new PolygonStyle()
				.fillColor(new Color(0x97FF33))
				.fillOpacity(0.6f)
				.color(new Color(0xFF4921))
				.opacity(0.6f);

		Geometry g1 = JTSTest.geomOf(JTSTest.POLY_01);
		Geometry g2 = JTSTest.geomOf(JTSTest.POLY_02);

		Envelope env = g1.getEnvelopeInternal();
		env.expandToInclude(g2.getEnvelopeInternal());

		map.zoom(env, 12);
		map.padding(100, 100);

		map.addLayer(JTSLayer.from(DefaultGeographicCRS.WGS84, style, g1, g2));

		map.render(1200, 1200, File.createTempFile("TMSMap-Example-06-tms", ".png"));
	}
}
