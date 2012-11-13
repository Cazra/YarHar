package yarhar.map;

import java.util.LinkedList;
import java.util.Collections;
import yarhar.*;
import java.awt.*;
import org.json.*;
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
    
    public Layer(JSONObject layerJ, SpriteLibrary lib) {
        loadJSON(layerJ, lib);
    }
    
    
    /** Creates a JSON string respresenting this layer. */
    public String toJSON() {
        String result = "{";
        
        result += "\"name\":\"" + name + "\",";
        result += "\"i\":" + order + ",";
        result += "\"sprites\":[";
        boolean isFirst = true;
        for(SpriteInstance sprite : sprites) {
            if(!isFirst)
                result += ",";
            else
                isFirst = false;
                
            result += sprite.toJSON();
        }
        result += "],";
        result += "\"opacity\":" + opacity + ",";
        result += "\"visible\":" + isVisible;
        
        result += "}";
        return result;
    }
    
    
    /** Loads this layer from JSON. */
    public void loadJSON(JSONObject layerJ, SpriteLibrary lib) {
        try {
            name = layerJ.getString("name");
            order = layerJ.getInt("i");
            
            JSONArray spriteSetJ = layerJ.getJSONArray("sprites");
            for(int i = 0; i < spriteSetJ.length(); i++) {
                JSONObject spriteJ = spriteSetJ.getJSONObject(i);
                SpriteInstance sprite = new SpriteInstance(spriteJ, lib);
                addSprite(sprite);
            }
            
            opacity = layerJ.getDouble("opacity");
            isVisible = layerJ.getBoolean("visible");
        }
        catch (Exception e) {
            System.err.println("Error reading JSON for layer.");
        }
    }
    
    
    
    /** Rendering a layer causes all the sprites contained inside it to be rendered. */
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
        sprite.zIndex = sprites.size();
        sprites.add(sprite);
    }
    
    /** Removes a sprite from this layer. Returns true if the sprite was contained in this layer. */
    public boolean removeSprite(SpriteInstance sprite) {
        return sprites.remove(sprite);
    }
    
    
    /** Uses a SpriteType to drop an instance of a sprite at a point in the layer. */
    public SpriteInstance dropSpriteType(SpriteType spriteType, Point mouseWorld) {
        SpriteInstance sprite = spriteType.createInstance(mouseWorld.x, mouseWorld.y);
        addSprite(sprite);
        return sprite;
    }
    
    /** Layers are only equal by address. */
    public boolean equals(Object o) {
        return (this == o);
    }
    
    /** Checks if a sprite in this layer has been clicked, with topmost sprites having priority. If one has, that sprite is returned. Else null is returned. */
    public SpriteInstance tryClickSprite(Point mouseScr) {
        LinkedList<SpriteInstance> revList = (LinkedList<SpriteInstance>) sprites.clone();
        Collections.reverse(revList);
        
        for(SpriteInstance sprite : revList) {
            if(sprite.isClicked(mouseScr))
                return sprite;
        }
        
        return null;
    }
    
    /** Selects or unselects all sprites in this layer. */
    public void selectAll(boolean flag) {
        for(SpriteInstance sprite : sprites)
            sprite.isSelected = flag;
    }
}

