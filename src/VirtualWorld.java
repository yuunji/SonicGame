import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;

   public static boolean redraw = false;

   private static final int VIEW_WIDTH = 960;
   private static final int VIEW_HEIGHT = 640;
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   private static final int WORLD_WIDTH_SCALE = 2;
   private static final int WORLD_HEIGHT_SCALE = 2;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS =  30;
   private static final int WORLD_ROWS =  20;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_NAME = "world.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;

   public static long coin_time;

   private ImageStore imageStore;
   public WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;

   private long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void increase_speed_helper(Level level, long levelTime){
      Entity shadow = null;
      if(levelTime >= 30 * 1000){
         for(Entity entity : world.entities){
            if(entity.getClass() == Shadow.class){
               shadow = entity;
            }
         }
      }
      if(shadow != null) {
         Point pos = shadow.getPosition();
         world.removeEntity(shadow);
         scheduler.unscheduleAllEvents(shadow);
         Shadow fasterShadow = Shadow.createShadow(pos, level.getNewActionP(),
                 150, imageStore.getImageList("shadow"));

         world.addEntity(fasterShadow);
         fasterShadow.scheduleActions(scheduler, world, imageStore);
      }
   }

   public void draw()
   {

      long time = System.currentTimeMillis();
      long levelTime = System.currentTimeMillis() - world.getStartTime();
      DifficultyFactory difficulty = new DifficultyFactory();
      Level level = difficulty.getLevel(levelTime);
      if (time >= next_time)
      {
         this.scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;

         if(System.currentTimeMillis() - coin_time > 5500){
            Entity coin = null;
            for(Entity entity : world.entities){
               if(entity.getClass() == Coin.class){
                coin = entity;
               }
            }
            if(coin != null){
               world.removeEntity(coin);
            }
            coin_time = System.currentTimeMillis();
         }
      }

      view.drawViewport();
      if(redraw) {
         textSize(50);
         fill(255, 0, 100, 255);
         background(170);
         text("Game Over", 10 * 32, 10 * 32);
         long end = world.getEndTime();
         double totalTime = (end - world.getStartTime())/1000.0;
         text("You Lasted: "+ totalTime + " seconds", 4 * 32, 12 * 32);
         text("Level: "+ world.currentLevel, 4 * 32, 15 * 32);
         //redraw = false;
      }


      if(!redraw) {
         textSize(25);
         fill(255, 255, 255, 255);
         text("Level: " + level.getLevel(), 0, 19 * 32);
         text("Time: " + (double) levelTime / 1000 + "s", 0, 20 * 32);
      }


      if(level.getClass() == Level1.class && !world.level1 & !redraw){
         increase_speed_helper(level,levelTime);
         world.level1 = true;
         world.currentLevel = 1;
      }
      if(level.getClass() == Level2.class && !world.level2 & !redraw){
         increase_speed_helper(level,levelTime);
         world.level2 = true;
         world.currentLevel = 2;

      }
      if(level.getClass() == Level3.class && !world.level3 & !redraw){
         increase_speed_helper(level,levelTime);
         world.level3 = true;
         world.currentLevel = 3;

      }

   }



   public void keyPressed()
   {
      if (key == CODED)
      {
         switch (keyCode)
         {
            case UP:
               for(Entity entity: world.entities){
                  if (entity.getClass() == Sonic.class || entity.getClass() == Tails.class){
                     Point newPos = new Point(entity.getPosition().x,entity.getPosition().y-1);
                     if (world.withinBounds(newPos)){
                        if (!entity.getPosition().equals(newPos)) {
                           Optional<Entity> occupant = world.getOccupant(newPos);
                           if (occupant.isPresent()) {

                           }
                           else if(world.getBackgroundImage(newPos).equals
                                   (Optional.of(imageStore.getImageList("lava").get(0)))){
                           }
                           else{
                              world.moveEntity(entity,newPos);

                           }
                        }
                     }
                  }
               }
               break;
            case DOWN:
               for(Entity entity: world.entities){
                  if (entity.getClass() == Sonic.class || entity.getClass() == Tails.class){
                     Point newPos = new Point(entity.getPosition().x,entity.getPosition().y +1);
                     if (world.withinBounds(newPos)){
                        //((OctoNotFull)entity).setPosition(newPos);
                        if (!entity.getPosition().equals(newPos)) {
                           Optional<Entity> occupant = world.getOccupant(newPos);
                           if (occupant.isPresent()) {
                              //scheduler.unscheduleAllEvents(occupant.get());
                           }
                           else if(world.getBackgroundImage(newPos).equals
                                   (Optional.of(imageStore.getImageList("lava").get(0)))){
                           }
                           else{
                              world.moveEntity(entity,newPos);

                           }
                        }
                     }
                  }
               }
               break;
            case LEFT:
               for(Entity entity: world.entities){
                  if (entity.getClass() == Sonic.class || entity.getClass() == Tails.class){
                     Point newPos = new Point(entity.getPosition().x-1,entity.getPosition().y);
                     if (world.withinBounds(newPos)){
                        //((OctoNotFull)entity).setPosition(newPos);
                        if (!entity.getPosition().equals(newPos)) {
                           Optional<Entity> occupant = world.getOccupant(newPos);
                           if (occupant.isPresent()) {
                              //scheduler.unscheduleAllEvents(occupant.get());
                           }
                           else if(world.getBackgroundImage(newPos).equals
                                   (Optional.of(imageStore.getImageList("lava").get(0)))){
                           }
                           else{
                              world.moveEntity(entity,newPos);

                           }
                        }
                        //world.moveEntity(entity,newPos);
                     }
                  }
               }
               break;
            case RIGHT:
               for(Entity entity: world.entities){
                  if (entity.getClass() == Sonic.class || entity.getClass() == Tails.class){
                     Point newPos = new Point(entity.getPosition().x+1,entity.getPosition().y);
                     if (world.withinBounds(newPos)){
                        if (!entity.getPosition().equals(newPos)) {
                           Optional<Entity> occupant = world.getOccupant(newPos);
                           if (occupant.isPresent()) {
                              //scheduler.unscheduleAllEvents(occupant.get());
                           }
                           else if(world.getBackgroundImage(newPos).
                                   equals(Optional.of(imageStore.getImageList("lava").get(0)))){
                           }
                           else{
                              world.moveEntity(entity,newPos);
                           }
                        }
                     }
                  }
               }
               break;
         }
      }

   }

   public void mousePressed() {
      Point mouseClick = new Point(mouseX/32, mouseY/32);

      int ringX = 0;
      int ringY = 0;
      Entity ring = null;
      for (Entity entity : world.entities) {
         if (entity.getClass() == Ring.class) {
            ringX = entity.getPosition().x;
            ringY = entity.getPosition().y;
            ring = entity;
         }
      }
      Entity sonic = null;

      if (ringX == mouseClick.x && ringY == mouseClick.y) {
         world.removeEntity(ring);
         scheduler.unscheduleAllEvents(ring);
         for(Entity entity : world.entities){
            if (entity.getClass() == Sonic.class){
               sonic = entity;
            }
         }
         if(sonic != null) {
            Point pos = sonic.getPosition();
            animatedEntity tails = Tails.createTails(pos, imageStore.getImageList("tails"));
            activeEntity coin1 = Coin.createCoin(new Point(0,0),1500, imageStore.getImageList("coin"));
            world.removeEntity(sonic);
            scheduler.unscheduleAllEvents(sonic);
            world.addEntity(tails);
            world.addEntity(coin1);
            coin_time = System.currentTimeMillis();
            tails.scheduleActions(scheduler, world, imageStore);
            coin1.scheduleActions(scheduler,world,imageStore);
            world.create_background_event(new Point(ringX,ringY),imageStore);

         }
      }
   }
   private static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   private static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         world.load(in, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void scheduleActions(WorldModel world,
                                      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.entities)
      {
         //Only start actions for entities that include action (not those with just animations)
         if (true) {
            activeEntity c = (activeEntity) entity;
            if (c.getActionPeriod() > 0) {
               c.scheduleActions(scheduler, world, imageStore);
            }
         }
      }
   }

   private static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }


   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);

   }
}
