/*
Action: ideally what our various entities might do in our virutal world
 */

import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;



abstract public class Action
{
    private final activeEntity entity;
    private final WorldModel world;
    private final ImageStore imageStore;
    private final int repeatCount;

    public Action(activeEntity entity,WorldModel world, ImageStore imageStore, int repeatCount){
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    abstract void executeAction(EventScheduler scheduler);

    public activeEntity getEntity(){
        return this.entity;
    }

    public int getRepeatCount(){
        return this.repeatCount;
    }

    public WorldModel getWorld(){
        return this.world;
    }

    public ImageStore getImageStore(){
        return this.imageStore;
    }




}
