package br.ufla.tmsmap.test;

import br.ufla.tmsmap.*;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by rthoth on 21/10/15.
 */
public class TMSMapTest {


	@Test
	public void simpleTest() throws IOException {
		TMSMap tmsMap = new TMSMap();

		tmsMap.addLayer(TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png")));
		tmsMap.setViewport(Viewport.of(new ReferencedEnvelope(-45.1, -44.9, -21.2, -21, DefaultGeographicCRS.WGS84), 12));


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

		int[] ws = {5000, 2500, 5000};
		int[] hs = {5000, 5000, 2500};

		PolygonStyle polygonStyle = (PolygonStyle) new PolygonStyle()
				  .fillColor(new Color(255, 255, 255))
				  .fillOpacity(0.2f)
				  .color(new Color(0xf, 0xf, 0xf))
				  .width(2);

		tmsMap.addLayer(ShapefileLayer.from("test-data/shapefiles/shp03.shp", polygonStyle));

		for (int i = 0; i < ws.length; i++) {
			final File outFile = File.createTempFile("TMSMAP-" + ws[i] + "x" + hs[i] + "-", ".png");
			tmsMap.render(ws[i], hs[i], outFile);
		}
	}

}
