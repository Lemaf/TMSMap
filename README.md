# TMSMap


## Examples

### Import

```java
  import br.ufla.tmsmap.*;
```

### TMSLayer

```java
TMSMap map = new TMSMap();

// Local tiles
map.addLayer(TMSLayer.from(new File("layer/{z}/{x}/{y}.png")));
map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

// Write a file
File imageFile = new File("TMSMap-Example-01.01.png");
map.render(500, 500, imageFile);

// Or write on stream
OutputStream imageStream = new FileOutputStream(new File("TMSMap-Example-01.02.png"));
map.render(500, 500, Format.PNG, imageStream);
```

### Shapefile

```java
TMSMap map = new TMSMap();

// External tiles
URL url = new URL("http://localhost/layer/{z}/{x}/{y}.png");
map.addLayer(TMSLayer.from(url));

// Shapefile layer

// Shapefile style
Style style = new LineStringStyle()
				  .color(new Color(74, 179, 255))
				  .opacity(0.9f)
				  .width(3)
				  .dashArray(10, 1, 1, 1, 5);

map.addLayer(ShapefileLayer.from("hidro.shp", style));

map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

File imageFile = new File("TMSMap-Example-02.jpg")
map.render(1000, 1250, imageFile);
```

### Images

```java
TMSMap map = new TMSMap();

map.addLayer(TMSLayer.from(new File("layer/{z}/{x}/{y}.png")));
map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

map.addLayer(ImageLayer.from(new File("compass-rose.png")).right(40).top(20));
map.padding(50, 25);

File imageFile = new File("TMSMap-Example-03.jpg");
map.render(500, 500, imageFile);
```

### Scale Bar
```java
TMSMap map = new TMSMap();

Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
Color color = new Color(255, 255, 255);

map.addLayer(TMSLayer.from(new File("layer/{z}/{x}/{y}.png")));
map.addLayer(ScaleBar.Simple.from(font, color).bottom(10).left(10).height(10));

map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

map.render(600, 600, new File("TMSMap-Example-04.png"));
```


### JTS Geometries

```java
TMSMap map = new TMSMap();

map.addLayer(TMSLayer.from(new File("layer/{z}/{x}/{y}.png")));

// TMSMap needs a CRS
CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;

Style polygonStyle = new PolygonStyle()
      .fillColor(new Color(157, 255, 105))
      .fillOpacity(0.5f)
      .color(new Color(255, 102, 0))
      .opacity(0.9f);

// JTS Geometries
Geometry geometry1 = ...;
Geometry geometry2 = ...;

// JTSLayer.from is varying
map.addLayer(JTSLayer.from(crs, polygonStyle, geometry1, geometry2));

// Zoom to
Envelope envelope = geometry1.getEnvelopeInternal();
envelope.expandToInclude(geometry2.getEnvelopeInternal());

map.zoom(envelope.getMinX(), envelope.getMaxX(), envelope.getMinY(), envelope.getMaxY(), 12);

map.padding(200, 200);

map.render(500, 500, new File("TMSMap-Example-05.png"));
```
