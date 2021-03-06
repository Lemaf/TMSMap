package br.ufla.tmsmap.test;

import br.ufla.tmsmap.LineStringStyle;
import br.ufla.tmsmap.PolygonStyle;
import br.ufla.tmsmap.Style;

import java.awt.*;

/**
 * Created by rthoth on 30/10/15.
 */
public class Helper {
	public static Style getStyle01() {
		return new PolygonStyle()
				  .fillColor(new Color(255, 236, 46))
				  .fillOpacity(0.7f)
				  .color(new Color(255, 127, 17))
				  .opacity(0.5f)
				  .width(2)
				  .dashArray(5, 10, 20);
	}

	public static Style getLineStyle01() {
		return new LineStringStyle()
				  .color(new Color(74, 179, 255))
				  .opacity(0.9f)
				  .width(3)
				  .dashArray(10, 1, 1, 1, 5);
	}
}
