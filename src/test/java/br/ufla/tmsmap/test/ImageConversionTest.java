package br.ufla.tmsmap.test;

import br.ufla.tmsmap.ImageLayer;
import br.ufla.tmsmap.JTSLayer;
import br.ufla.tmsmap.TMSMap;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;

/**
 * Created by rthoth on 30/10/15.
 */
public class ImageConversionTest {

	@Test
	public void tilePNGToJPEG() throws IOException {

		TMSMap map = new TMSMap();

		map.addLayer(TMSHelper.getTMSLayer01());
		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

		File tempFile = File.createTempFile("TMS-tilePNGToJPEG-", ".jpg");
		FileOutputStream outputStream = new FileOutputStream(tempFile);
		map.render(500, 500, TMSMap.JPEG, outputStream);

		assertThat(tempFile).exists().canRead().has(FileHelper.HAS_SOME_CONTENT);
	}

	@Test
	public void tilePNGToJPEGWithPNG() throws Exception {
		TMSMap map = new TMSMap();

		map.addLayer(TMSHelper.getTMSLayer01());
		map.addLayer(ImageLayer.from(new File("test-data/compass-rose.png")).right(50).bottom(50));
		map.addLayer(JTSLayer.from(WGS84, Helper.getStyle01(), JTSTest.geomOf(JTSTest.POLY_01)));

		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);


		File out = File.createTempFile("TMS-tilePNGToJPEGWithPNG-", ".jpg");
		map.render(1000, 1000, out);

		assertThat(out).exists().canRead().has(FileHelper.HAS_SOME_CONTENT);
	}
}
