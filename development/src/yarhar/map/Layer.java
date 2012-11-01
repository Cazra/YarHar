package yarhar.map;

import yarhar.*;
import java.awt.*;
import pwnee.*;

public class Layer {
    /** An optional name for this layer displayed in the editor. If no name is given, its order value is displayed. */
    public String name = "";
    
    /** This layer's index in the list of layers for this map. Layers are rendered from last to first 
    with the layer whose order is 0 being rendered last. */
    public int order = 0;
    
    /** The list of sprites populating this layer. */
    // public ArrayList<YHSprite> sprites = new ArrayList<YHSprite>();
    
    /** The opacity for this layer */
    public double opacity = 1.0;
    
    /** If false, this layer is invisible. */
    public boolean isVisible = true;
    
    
    public Layer() {
        
    }
    
    public Layer(String n) {
        name = n;
    }
    
    
    public void render(Graphics2D g) {
        if(!isVisible || opacity == 0.0)
            return;
        
        Composite oldComp = g.getComposite();
        
        // Use an AlphaComposite to apply semi-transparency to the Sprite's image.
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
        
        // TODO: loop over sprites and render them.
        
        g.setComposite(oldComp);
    }
    
    /** Safely sets the opacity for this layer. */
    public void setOpacity(double op) {
        if(op > 1.0)
            op = 1.0;
        if(op < 0.0)
            op = 0.0;
        opacity = op;
    }
}

