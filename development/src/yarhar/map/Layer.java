package yarhar.map;

import java.util.LinkedList;
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
    public LinkedList<SpriteInstance> sprites = new LinkedList<SpriteInstance>();
    
    /** The opacity for this layer */
    public double opacity = 1.0;
    
    /** If false, this layer is invisible. */
    public boolean isVisible = true;
    
    
    public Layer() {
        this("Untitled");
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
        
        for(SpriteInstance sprite : sprites) {
            sprite.render(g);
        }
        
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
    
    
    /** Adds a sprite to this layer. */
    public void addSprite(SpriteInstance sprite) {
        sprites.add(sprite);
    }
    
    /** Uses a SpriteType to drop an instance of a sprite at a point in the layer. */
    public void dropSpriteType(SpriteType spriteType, Point mouseWorld) {
        SpriteInstance sprite = spriteType.createInstance(mouseWorld.x, mouseWorld.y);
        addSprite(sprite);
    }
}

