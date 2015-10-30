# TMSMap


## Examples

### Import

```java
  import br.ufla.tmsmap.*;
```

### TMSLayer

```java
TMSMap map = new TMSMap();

map.addLayer(TMSLayer.from(new File("layer/{z}/{x}/{y}.png")));
map.zoom(-45.28374, -45.18135, -21.13236, -21.04297, 12);

File imageFile = new File("TMSMap-Example-01.01.png");
OutputStream imageStream = new FileOutputStream(new File("TMSMap-Example-01.02.png"));

map.render(500, 500, imageFile);
map.render(500, 500, TMSMap.PNG, imageStream);
```
