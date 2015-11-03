package br.ufla.tmsmap.test;

import br.ufla.tmsmap.JTSLayer;
import br.ufla.tmsmap.TMSMap;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;

/**
 * Created by rthoth on 30/10/15.
 */
public class PaddingTest {

	@Test
	public void padding_5_5() throws IOException, ParseException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSHelper.getTMSLayer01());
		Geometry geometry = JTSTest.geomOf(JTSTest.POLY_01);

		map.addLayer(JTSLayer.from(WGS84, Helper.getStyle01(), geometry));

		Envelope envelope = geometry.getEnvelopeInternal();
		map.zoom(envelope.getMinX(), envelope.getMaxX(), envelope.getMinY(), envelope.getMaxY(), 12);

		map.padding(50, 50);

		File imageFile = File.createTempFile("TMS-Padding_5_5-", ".png");

		map.render(500, 500, imageFile);

		assertThat(imageFile).exists().canRead().has(FileHelper.HAS_SOME_CONTENT);
	}

	@Test
	public void padding_10_10() throws IOException, ParseException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSHelper.getTMSLayer01());
		Geometry geometry = JTSTest.geomOf(JTSTest.POLY_01);
		map.addLayer(JTSLayer.from(WGS84, Helper.getStyle01(), geometry));

		Envelope envelope = geometry.getEnvelopeInternal();

		map.zoom(envelope.getMinX(), envelope.getMaxX(), envelope.getMinY(), envelope.getMaxY(), 12);

		map.padding(10, 10);

		File imageFile = File.createTempFile("TMS-Padding_10_10-", ".png");
		map.render(500, 500, imageFile);

		assertThat(imageFile).exists().canRead().has(FileHelper.HAS_SOME_CONTENT);

	}
}
