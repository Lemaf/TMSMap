package br.ufla.tmsmap.test;

import br.ufla.tmsmap.TMSLayer;
import br.ufla.tmsmap.TMSMap;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by rthoth on 30/10/15.
 */
public class ExamplesTest {

	@Test
	public void example01() throws IOException {
		TMSMap map = new TMSMap();

		map.addLayer(TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png")));
		map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

		File imageFile = File.createTempFile("TMSMap-Example-01.02-", ".png");
		OutputStream imageStream = new FileOutputStream(File.createTempFile("TMSMap-Example-01.01-", ".png"));

		map.render(500, 500, imageFile);
		map.render(500, 500, TMSMap.PNG, imageStream);
	}
}
