import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


public abstract class Entity
{
    private Point position;
    private final List<PImage> images;
    private int imageIndex;

    public Entity(Point position, List<PImage> images, int imageIndex) {
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;

    }

    public PImage getCurrentImage(){
        return this.images.get(this.imageIndex);
    }

    public Point getPosition(){
        return this.position;
    }

    public void setPosition(Point pos){
        this.position = pos;
    }
    public void setImageIndex(int imageIndex){
        this.imageIndex = imageIndex;
    }
    public int getImageIndex(){
        return this.imageIndex;
    }
    public List<PImage> getImages(){
        return this.images;
    }


}
