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
map.render(500, 500, TMSMap.PNG, imageStream);
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
