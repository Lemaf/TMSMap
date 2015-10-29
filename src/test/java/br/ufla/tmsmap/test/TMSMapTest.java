package br.ufla.tmsmap.test;

import br.ufla.tmsmap.*;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSMapTest {


	public static final ReferencedEnvelope ENVELOPE = new ReferencedEnvelope(-45.1, -44.9, -21.2, -21, DefaultGeographicCRS.WGS84);
	public static final File LAVRAS_TILES = new File("test-data/lavras/{z}/{x}/{y}.png");

	@Test()
	public void simpleTest() throws IOException {
		TMSMap tmsMap = new TMSMap();

		tmsMap.addLayer(TMSLayer.from(LAVRAS_TILES));
		tmsMap.setViewport(Viewport.of(ENVELOPE, 12));


		LineStringStyle lineStyle = new LineStringStyle();
		lineStyle.color(new Color(192, 206, 255));
		lineStyle.opacity(0.9f);
		lineStyle.width(2);

		tmsMap.addLayer(ShapefileLayer.from("test-data/shapefiles/hidro.shp", lineStyle));

		lineStyle = new LineStringStyle()
				  .color(new Color(255, 0, 140))
				  .opacity(0.8f)
				  .width(10);

		tmsMap.addLayer(ShapefileLayer.from("test-data/shapefiles/river.shp", lineStyle));

		int[] ws = {2000, 1000, 2000};
		int[] hs = {2000, 2000, 1000};

		PolygonStyle polygonStyle = (PolygonStyle) new PolygonStyle()
				  .fillColor(new Color(255, 255, 255))
				  .fillOpacity(0.2f)
				  .color(new Color(0xf, 0xf, 0xf))
				  .width(2);

		tmsMap.addLayer(ShapefileLayer.from("test-data/shapefiles/shp03.shp", polygonStyle));

		for (int i = 0; i < ws.length; i++) {
			final File outFile = File.createTempFile("TMSMAP-" + ws[i] + "x" + hs[i] + "-", ".png");
			tmsMap.render(ws[i], hs[i], outFile);
			assertThat(outFile).exists().canRead();
		}
	}

	@Test()
	public void compassRose() throws IOException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(LAVRAS_TILES));

		map.addLayer(ImageLayer.from(new File("test-data/compass-rose.png")).right(50).bottom(50));
		map.addLayer(ImageLayer.from(new File("test-data/compass-rose.png")).left(50).top(50));

		map.setViewport(Viewport.of(ENVELOPE, 12));

		int[] ws = {2000, 1000, 2000}, hs = {1000, 2000, 2000};

		for (int i = 0; i < ws.length; i++) {
			File outImageFile = File.createTempFile("TMSMAP-Compass-rose-" + ws[i] + "x" + hs[i] + "-", ".png");
			map.render(ws[i], hs[i], outImageFile);

			assertThat(outImageFile).exists().canRead();
		}
	}

	@Test
	public void scaleBar() throws IOException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(LAVRAS_TILES));
		map.addLayer(ScaleBar.Simple.from(new Font(Font.MONOSPACED, Font.ITALIC, 32), new Color(0xff, 0xff, 0xff)).right(10).bottom(10).height(20));
		map.addLayer(ScaleBar.Simple.from(new Font(Font.MONOSPACED, Font.ITALIC, 16), new Color(0xff, 0xff, 0xff)).right(10).top(10).height(20));
		map.addLayer(ScaleBar.Simple.from(new Font(Font.MONOSPACED, Font.ITALIC, 16), new Color(0xff, 0xff, 0xff)).left(10).bottom(10).height(20));
		map.addLayer(ScaleBar.Simple.from(new Font(Font.MONOSPACED, Font.ITALIC, 32), new Color(0xff, 0xff, 0xff)).left(10).top(10).height(20));

		map.setViewport(Viewport.of(ENVELOPE, 12));

		int[] ws = {2000, 1000, 2000, 4000}, hs = {2000, 1000, 1000, 4000};

		for (int i = 0; i < ws.length; i++) {
			File out = File.createTempFile(String.format("TMSMAP-scaleBarSimple-%dx%d", ws[i], hs[i]), ".png");
			map.render(ws[i], hs[i], out);
			assertThat(out).exists().canRead();
		}
	}
}
