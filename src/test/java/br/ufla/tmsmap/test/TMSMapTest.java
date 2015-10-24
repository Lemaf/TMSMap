package br.ufla.tmsmap.test;

import br.ufla.tmsmap.TMSLayer;
import br.ufla.tmsmap.TMSMap;
import br.ufla.tmsmap.Viewport;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.testng.annotations.Test;

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
		final File outFile = File.createTempFile("TMSMAP", ".png");
		tmsMap.render(5000, 4000, outFile);
	}

}
