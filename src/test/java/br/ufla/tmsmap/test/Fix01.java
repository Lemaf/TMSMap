package br.ufla.tmsmap.test;

import java.awt.Color;
import java.io.File;
import java.net.URL;

import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.testng.annotations.Test;

import br.ufla.tmsmap.JTSLayer;
import br.ufla.tmsmap.PolygonStyle;
import br.ufla.tmsmap.Style;
import br.ufla.tmsmap.TMSLayer;
import br.ufla.tmsmap.TMSMap;

import com.vividsolutions.jts.geom.Geometry;

public class Fix01 {

	@Test
	public void test01() throws Exception {
		TMSMap map = new TMSMap(0);

		map.addLayer(TMSLayer.from(new URL(
				"http://www.car.gov.br/mosaicos/{z}/{x}/{y}.jpg")));

		DefaultGeographicCRS crs = DefaultGeographicCRS.WGS84;

		Style style = new PolygonStyle()
				.fillColor(new Color(0xff, 0xff, 0xff)).fillOpacity(1)
				.color(new Color(0, 0, 0)).opacity(1);
	
		Geometry geom = JTSTest.geomOf("MULTIPOLYGON(((-53.436620235443115 -27.320553829732614, -53.43801498413085 -27.322708054993825, -53.43707084655762 -27.32320371152177, -53.435611724853516 -27.321049495888314, -53.436620235443115 -27.320553829732614)))");
		
		map.addLayer(JTSLayer.from(crs, style, geom));
		map.zoomTo(geom.getEnvelopeInternal(), 709, 354, 0, 14, 256, 256);
		int vp = 69, hp = 70;
		
		map.padding(map.getZoom() * hp, map.getZoom() * vp);
		map.render(709, 354, File.createTempFile(String.format("TMSMap-Fix01-test01-%d-%d----", hp, vp), ".png"));
	}

}
