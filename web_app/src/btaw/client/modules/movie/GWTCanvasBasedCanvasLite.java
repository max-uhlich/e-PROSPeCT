package btaw.client.modules.movie;
import com.googlecode.gchart.client.GChartCanvasLite;

import com.google.gwt.widgetideas.graphics.client.GWTCanvas; 
import com.google.gwt.widgetideas.graphics.client.Color; 

public class GWTCanvasBasedCanvasLite
         extends GWTCanvas implements GChartCanvasLite {
     // GChartCanvasLite requires CSS/RGBA color strings, but
     // GWTCanvas uses its own Color class instead, so we wrap:
     public void setStrokeStyle(String cssColor) {
     // Sharp angles of default MITER can overwrite adjacent pie slices
        setLineJoin(GWTCanvas.ROUND);
        setStrokeStyle(new Color(cssColor));
     }
     public void setFillStyle(String cssColor) {
        setFillStyle(new Color(cssColor));
     }
     // Note: all other GChartCanvasLite methods (lineTo, moveTo,
     // arc, etc.) are directly inherited from GWTCanvas, so no
     // wrapper methods are needed.
}