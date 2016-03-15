package br.ufla.tmsmap.test;

import br.ufla.tmsmap.TMSMap;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rthoth on 15/03/16.
 */
public class ZoomToTest {

	@Test
	public void test01() throws ParseException {
		TMSMap tmsMap = new TMSMap();

		Geometry geom = JTSTest.geomOf(JTSTest.POLY_01);

		tmsMap.zoomTo(geom.getEnvelopeInternal(), 400, 400);

		assertThat(tmsMap.getZoom()).isNotNull().isEqualTo(14);
	}

	@Test
	public void test02() throws ParseException {
		TMSMap tmsMap = new TMSMap();

		Geometry geom = JTSTest.geomOf(JTSTest.POLY_02);
		tmsMap.zoomTo(geom.getEnvelopeInternal(), 1000, 1000);
		assertThat(tmsMap.getZoom()).isNotNull().isEqualTo(15);
	}

	@Test
	public void test03() throws IOException {
		TMSMap map = new TMSMap();
		Geometry geom = JTSTest.geomFromGeoJSON(Error01Test.GeoJSON01);
		map.zoomTo(geom.getEnvelopeInternal(), 100, 100);
		assertThat(map.getZoom()).isNotNull().isEqualTo(13);
	}

	@Test
	public void test04() throws Exception {
		TMSMap map = new TMSMap();
		Envelope env = JTSTest.geomOf(JTSTest.POLY_02).getEnvelopeInternal();
		env.expandBy(0, 5);
		map.zoomTo(env, 1000, 1000);
		assertThat(map.getZoom()).isNotNull().isEqualTo(7);
	}
}
