package br.ufla.tmsmap;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;

public class LabelLayer implements Layer {

	private Font font;
	private Color color;
	private final Coordinate coordinate;
	private Integer xOffset = null;
	private Integer yOffset = null;
	private String label;
	private Alignment alignment;

	public enum Alignment{RIGHT, LEFT, CENTER};

	public LabelLayer(String label, Coordinate point, Alignment alignment, Font font, Color color) {
		this.label = label;
		this.coordinate = point;
		this.font = font;
		this.color = color;
		this.alignment = alignment;
	}

	public LabelLayer(String label, Coordinate point, Font font, Color color) {
		this.label = label;
		this.coordinate = point;
		this.font = font;
		this.color = color;
		this.alignment = Alignment.CENTER;
	}

	public LabelLayer setYOffset(Integer offset) {
		yOffset = offset;
		return this;
	}

	public LabelLayer setXOffset(Integer offset) {
		xOffset = offset;
		return this;
	}

	@Override
	public org.geotools.map.Layer createMapLayer(MapViewport mapViewport, int i, ColorModel colorModel) {
		return new LabelImageLayer(mapViewport.getBounds());
	}

	private class LabelImageLayer extends DirectLayer {

		private final ReferencedEnvelope bounds;

		public LabelImageLayer(ReferencedEnvelope bounds) {
			this.bounds = bounds;
		}

		/**
		 * Referência: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com
		 * /javase/tutorial/2d/text/examples/LineBreakSample.java
		 */
		@Override
		public void draw(Graphics2D graphics2D, MapContent mapContent, MapViewport mapViewport) {

			AffineTransform transform = mapViewport.getWorldToScreen();

			double[] coordinates = new double[]{coordinate.x, coordinate.y, 0, 0};

			transform.transform(coordinates, 0, coordinates, 2, 1);

			int paragraphStart, paragraphEnd;

			AttributedString vanGogh = new AttributedString(label, new Hashtable<TextAttribute, Object>());
			vanGogh.addAttribute(TextAttribute.FONT, font);
			vanGogh.addAttribute(TextAttribute.FOREGROUND, color);

			AttributedCharacterIterator paragraph = vanGogh.getIterator();
			paragraphStart = paragraph.getBeginIndex();
			paragraphEnd = paragraph.getEndIndex();
			FontRenderContext frc = graphics2D.getFontRenderContext();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

			float breakWidth = (float)(mapViewport.getScreenArea().getMaxX() - mapViewport.getScreenArea().getMinX());
			float textwidth = 0;

			// Set position to the index of the first character in the paragraph.
			lineMeasurer.setPosition(paragraphStart);

			// Get max of width in lineMeasure
			while (lineMeasurer.getPosition() < paragraphEnd) {
				TextLayout layout = lineMeasurer.nextLayout(breakWidth);
				float v = layout.getAdvance();
				if(v > textwidth)
					textwidth = v;
			}

			if(textwidth > breakWidth) {
				textwidth = breakWidth;
			}

			int _xOffset = (xOffset != null) ? xOffset : 0;
			int _yOffset = (yOffset != null) ? yOffset : 0;

			float x = (float)(coordinates[2] + _xOffset);
			float y = (float)(coordinates[3] - _yOffset);
			float minX = x - (textwidth/2);

			if(minX < 0)
				minX = 0;

			float maxX = x + (textwidth/2);

			if(maxX < 0)
				maxX = 0;

			// Set break width to width of Component.
			float drawPosY = y;

			lineMeasurer.setPosition(paragraphStart);

			// Get lines until the entire paragraph has been displayed.
			while (lineMeasurer.getPosition() < paragraphEnd) {

				// Retrieve next layout. A cleverer program would also cache
				// these layouts until the component is re-sized.
				TextLayout layout = lineMeasurer.nextLayout(maxX);

				float drawPosX;
				switch (alignment){
					case RIGHT:
						drawPosX = maxX - layout.getAdvance();
						break;
					case CENTER:
						drawPosX = minX + ((maxX - minX) - layout.getAdvance())/2;
						break;
					default:
						drawPosX = minX;
				}

				// Move y-coordinate by the ascent of the layout.
				drawPosY += layout.getAscent();

				// Draw the TextLayout at (drawPosX, drawPosY).
				layout.draw(graphics2D, drawPosX, drawPosY);

				// Move y-coordinate in preparation for next layout.
				drawPosY += layout.getDescent() + layout.getLeading();
			}

		}

		@Override
		public ReferencedEnvelope getBounds() {
			return bounds;
		}
	}

}
