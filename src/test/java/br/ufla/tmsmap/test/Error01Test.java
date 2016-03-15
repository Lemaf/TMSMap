package br.ufla.tmsmap.test;

import br.ufla.tmsmap.*;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by rthoth on 14/03/16.
 */
public class Error01Test {

	public static final String GeoJSON01 = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[-51.0492848167988,-27.89210643971175],[-51.04887470906191,-27.89207859875211],[-51.04830357086472,-27.8918850412397],[-51.04783137348287,-27.89177017122113],[-51.04764259925285,-27.89161616382021],[-51.0473273568596,-27.89139481345698],[-51.04702595658597,-27.89129988109029],[-51.04685997239693,-27.8911429122049],[-51.04640072661452,-27.89079811382925],[-51.04580774797054,-27.89070072050715],[-51.04535798945048,-27.89027857542384],[-51.04360280987672,-27.8896399737853],[-51.04577732102382,-27.88911860503255],[-51.04725120869767,-27.88872835182303],[-51.0488540486466,-27.88839247594994],[-51.05075706091848,-27.88734686026103],[-51.05127266528417,-27.88787968083935],[-51.05207249647686,-27.88805066231766],[-51.05255861113055,-27.88862318420908],[-51.05273852772962,-27.88925632125012],[-51.05292643192821,-27.88988271481749],[-51.05290966853334,-27.89052317420621],[-51.05257867548288,-27.8909673979832],[-51.05190941090717,-27.89139675930024],[-51.0508675685211,-27.89190943014672],[-51.05026571273964,-27.89195924709417],[-51.04972786532114,-27.89207273294215],[-51.0492848167988,-27.89210643971175]]]]}";

	@Test
	public void delta() throws IOException {
		TMSMap tmsMap = new TMSMap();

//		tmsMap.addLayer(TMSLayer.from(new URL("http://a.tile.osm.org/{z}/{x}/{y}.png"), false));
//		tmsMap.addLayer(TMSLayer.from(new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.jpg"), false));
//		tmsMap.addLayer(TMSLayer.from(new URL("https://b.tiles.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6IlpIdEpjOHcifQ.Cldl4wq_T5KOgxhLvbjE-w"), false));
		tmsMap.addLayer(TMSLayer.from(new URL("http://www.car.gov.br/mosaicos/{z}/{x}/{y}.jpg")));
		Geometry geometry = JTSTest.geomFromGeoJSON(GeoJSON01);

		Style style = new PolygonStyle()
			.fillColor(new Color(0x97FF33))
			.fillOpacity(0.6f)
			.color(new Color(0xFF4921))
			.opacity(0.6f);

		tmsMap.addLayer(JTSLayer.from(DefaultGeographicCRS.WGS84, style, geometry));

		Envelope env = geometry.getEnvelopeInternal();
		env.expandBy(0.01D);

		tmsMap.zoom(env, 15);

		tmsMap.render(1000, 1000, File.createTempFile("TMS-Error01-delta", ".png"));
	}
}
