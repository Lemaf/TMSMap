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

	private void baseTest(String test, String wkt, int hp, int vp, int zmin,
			int zmax, int width, int height) throws Exception {
		TMSMap map = new TMSMap(0);

		map.addLayer(TMSLayer.from(new URL(
				"http://www.car.gov.br/mosaicos/{z}/{x}/{y}.jpg")));

		DefaultGeographicCRS crs = DefaultGeographicCRS.WGS84;

		Style style = new PolygonStyle().fillColor(new Color(0xff, 0xff, 0xff))
				.fillOpacity(0.2f).color(new Color(0, 0, 0)).opacity(1);

		Geometry geom = JTSTest.geomOf(wkt);

		map.addLayer(JTSLayer.from(crs, style, geom));
		map.zoomTo(geom.getEnvelopeInternal(), 709, 354, 0, 14, 256, 256);

		map.padding(map.getZoom() * hp, map.getZoom() * vp);
		map.render(width, height, File.createTempFile(
				String.format("TMSMap-Fix01-%s-%d-%d----", test, hp, vp),
				".png"));
	}

	@Test
	public void test01() throws Exception {
		baseTest(
				"test01",
				"MULTIPOLYGON(((-53.436620235443115 -27.320553829732614, -53.43801498413085 -27.322708054993825, -53.43707084655762 -27.32320371152177, -53.435611724853516 -27.321049495888314, -53.436620235443115 -27.320553829732614)))",
				70, 70, 0, 14, 709, 354);
	}
	
	@Test
	public void test02() throws Exception {
		
		baseTest("test02", 
				"MULTIPOLYGON(((-52.05787897109985 -27.69418732246004, -52.05775022506714 -27.69135636056095, -52.0589582985964 -27.69128599600773, -52.05907851640924 -27.69329860324865, -52.05876087845144 -27.69341029439656, -52.05862783098505 -27.69348618943529, -52.05866612295935 -27.69388531968319, -52.05817927421956 -27.6941574929726, -52.05798072601163 -27.69420382931954, -52.05787897109985 -27.69418732246004)))", 
				67, 67, 0, 14, 709, 354);
	}

}
