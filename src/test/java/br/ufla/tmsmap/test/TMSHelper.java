package br.ufla.tmsmap.test;

import br.ufla.tmsmap.Layer;
import br.ufla.tmsmap.TMSLayer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Created by rthoth on 30/10/15.
 */
public class TMSHelper {
	public static Layer getTMSLayer01() throws MalformedURLException, UnsupportedEncodingException {
		return TMSLayer.from(new File("test-data/lavras/{z}/{x}/{y}.png"), false);
	}
}
