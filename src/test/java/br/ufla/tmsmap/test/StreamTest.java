package br.ufla.tmsmap.test;

import br.ufla.tmsmap.*;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rthoth on 30/10/15.
 */
public class StreamTest {

	public static final Viewport VIEWPORT = Viewport.of(new ReferencedEnvelope(-45.28374, -45.18135, -21.13236, -21.04297, DefaultGeographicCRS.WGS84), 12);

	@Test
	public void basicSupport() throws IOException, ParseException {
		TMSMap map = new TMSMap();

		map.addLayer(getTMSLayer());
		map.addLayer(JTSLayer.from(DefaultGeographicCRS.WGS84, Helper.getStyle01(), JTSTest.geomOf(JTSTest.POLY_01)));
		map.setViewport(VIEWPORT);

		int[] ws = {1000, 500, 1000}, hs = {500, 1000, 1000};

		for (int i=0; i < ws.length; i++) {
			File out = File.createTempFile(String.format("TMSMap-Stream-%dx%d-", ws[i], hs[i]), ".png");

			FileOutputStream outputStream = new FileOutputStream(out);
			map.render(ws[i], hs[i], Format.PNG, outputStream);

			assertThat(out).exists().canRead().has(FileHelper.HAS_SOME_CONTENT);
		}
	}

	private TMSLayer getTMSLayer() throws MalformedURLException, UnsupportedEncodingException {
		return TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png"), false);
	}
}
