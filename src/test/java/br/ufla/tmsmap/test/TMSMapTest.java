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
		tmsMap.setViewport(Viewport.of(new ReferencedEnvelope(-45.50, -44.85, -21.3, -20.9, DefaultGeographicCRS.WGS84), 12));


		PolygonStyle polygonStyle = new PolygonStyle().fillColor(new Color(0xff, 0xff, 0xff));
		polygonStyle.color(new Color(0x00, 0xff, 0xff, 0xa0));
		polygonStyle.opacity(0.9f);
		polygonStyle.fillColor(new Color(0x00, 0x00, 0xff));
		polygonStyle.fillOpacity(0.5f);
		polygonStyle.width(5);

		LineStringStyle lineStyle = new LineStringStyle();
		lineStyle.color(new Color(0xff, 0x00, 0xff));
		lineStyle.opacity(1);
		lineStyle.width(10);

		tmsMap.addLayer(ShapefileLayer.from("test-data/shapefiles/shp01.shp", polygonStyle));
		tmsMap.addLayer(ShapefileLayer.from("test-data/shapefiles/shp02.shp", lineStyle));

		final File outFile = File.createTempFile("TMSMAP", ".png");
		tmsMap.render(5000, 2500, outFile);
	}

}
