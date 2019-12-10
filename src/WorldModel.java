import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{
   public final int numRows;
   public final int numCols;
   private final Background background[][];
   private final Entity occupancy[][];
   public final Set<Entity> entities;

   private static final int PROPERTY_KEY = 0;

   private static final String BGND_KEY = "background";
   private static final int BGND_NUM_PROPERTIES = 4;
   private static final int BGND_ID = 1;
   private static final int BGND_COL = 2;
   private static final int BGND_ROW = 3;

   private static final String EGGMAN_KEY = "eggman";
   private static final int EGGMAN_NUM_PROPERTIES = 5;
   private static final int EGGMAN_ID = 1;
   private static final int EGGMAN_COL = 2;
   private static final int EGGMAN_ROW = 3;
   private static final int EGGMAN_ACTION_PERIOD = 4 ;

   private static final String SONIC_KEY = "sonic";
   private static final int SONIC_NUM_PROPERTIES = 6;
   private static final int SONIC_ID = 1;
   private static final int SONIC_COL = 2;
   private static final int SONIC_ROW = 3;
   private static final int SONIC_ACTION_PERIOD = 4;
   private static final int SONIC_ANIMATION_PERIOD = 5;

   private static final String SHADOW_KEY = "shadow";
   private static final int SHADOW_NUM_PROPERTIES = 6;
   private static final int SHADOW_ID = 1;
   private static final int SHADOW_COL = 2;
   private static final int SHADOW_ROW = 3;
   private static final int SHADOW_ACTION_PERIOD = 4;
   private static final int SHADOW_ANIMATION_PERIOD = 5;


   private static final String SPIKE_KEY = "spike";
   private static final int SPIKE_NUM_PROPERTIES = 5;
   private static final int SPIKE_ID = 1;
   private static final int SPIKE_COL = 2;
   private static final int SPIKE_ROW = 3;
   private static final int SPIKE_ACTION_PERIOD = 4;
   private static final int SPIKE_REACH = 1;

   private static final String TAILS_KEY = "tails";
   private static final int TAILS_NUM_PROPERTIES = 4;
   private static final int TAILS_ID = 1;
   private static final int TAILS_COL = 2;
   private static final int TAILS_ROW = 3;


   private static final String RING_KEY = "ring";
   private static final int RING_NUM_PROPERTIES = 5;
   private static final int RING_ID = 1;
   private static final int RING_COL = 2;
   private static final int RING_ROW = 3;
   private static final int RING_ACTION_PERIOD = 4;
   private long endTime;
   private final long startTime;

   public boolean level1 = false;
   public boolean level2 = false;
   public boolean level3 = false;

   public int currentLevel = 0;


   public void setEndTime(long t){
      this.endTime = t;
   }

   public long getEndTime(){
      return this.endTime;
   }
   public long getStartTime(){
      return this.startTime;
   }


   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();
      this.startTime = System.currentTimeMillis();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
              pos.x >= 0 && pos.x < this.numCols;
   }

   private Entity getOccupancyCell(Point pos)
   {
      return this.occupancy[pos.y][pos.x];
   }

   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         this.entities.add(entity);
      }
   }

   private void tryAddEntity(Entity entity)
   {
      if (this.isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   private void setOccupancyCell(Point pos, Entity entity)
   {
      this.occupancy[pos.y][pos.x] = entity;
   }

   private Background getBackgroundCell(Point pos)
   {
      return this.background[pos.y][pos.x];
   }

   private void setBackgroundCell(Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   private void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         this.entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   private void setBackground(Point pos, Background background)
   {
      if (this.withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public  Optional<Entity> getOccupant(Point pos)
   {
      if (this.isOccupied(pos))
      {
         return Optional.of(this.getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public void load(Scanner in, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   private boolean processLine(String line, ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties, imageStore);
            case SONIC_KEY:
               return parseSonic(properties, imageStore);
            case EGGMAN_KEY:
               return parseEGGMAN(properties, imageStore);
            case SPIKE_KEY:
               return parseSPIKE(properties, imageStore);
            case TAILS_KEY:
               return parseTAILS(properties, imageStore);
            case RING_KEY:
               return parseRing(properties, imageStore);
            case SHADOW_KEY:
               return parseSHADOW(properties, imageStore);
         }

      }

      return false;
   }

   private boolean parseBackground(String [] properties, ImageStore imageStore)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                 Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         setBackground(pt, new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   private boolean parseSonic(String [] properties, ImageStore imageStore)
   {
      if (properties.length == SONIC_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[SONIC_COL]),
                 Integer.parseInt(properties[SONIC_ROW]));
         Entity entity = Sonic.createSonic(pt,
                 Integer.parseInt(properties[SONIC_ACTION_PERIOD]),
                 Integer.parseInt(properties[SONIC_ANIMATION_PERIOD]),
                 imageStore.getImageList(SONIC_KEY));
         tryAddEntity(entity);
      }

      return properties.length == SONIC_NUM_PROPERTIES;
   }

   private boolean parseSHADOW(String [] properties, ImageStore imageStore)
   {
      if (properties.length == SHADOW_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[SHADOW_COL]),
                 Integer.parseInt(properties[SHADOW_ROW]));
         Entity entity = Shadow.createShadow(pt,
                 Integer.parseInt(properties[SHADOW_ACTION_PERIOD]),
                 Integer.parseInt(properties[SHADOW_ANIMATION_PERIOD]),
                 imageStore.getImageList(SHADOW_KEY));
         tryAddEntity(entity);
      }

      return properties.length == SHADOW_NUM_PROPERTIES;
   }

   private boolean parseEGGMAN(String [] properties, ImageStore imageStore)
   {
      if (properties.length == EGGMAN_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[EGGMAN_COL]),
                 Integer.parseInt(properties[EGGMAN_ROW]));
         Entity entity = Eggman.createEggman(pt,
                  imageStore.getImageList(EGGMAN_KEY),Integer.parseInt(properties[EGGMAN_ACTION_PERIOD]));
         tryAddEntity(entity);
      }

      return properties.length == EGGMAN_NUM_PROPERTIES;
   }

   private boolean parseSPIKE(String [] properties, ImageStore imageStore)
   {
      if (properties.length == SPIKE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[SPIKE_COL]),
                 Integer.parseInt(properties[SPIKE_ROW]));
         Entity entity = Spike.createSpike(pt,
                  Integer.parseInt(properties[SPIKE_ACTION_PERIOD]),
                 imageStore.getImageList(SPIKE_KEY));
         tryAddEntity(entity);
      }

      return properties.length == SPIKE_NUM_PROPERTIES;
   }

   private boolean parseTAILS(String [] properties,
                                       ImageStore imageStore)
   {
      if (properties.length == TAILS_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[TAILS_COL]),
                 Integer.parseInt(properties[TAILS_ROW]));
         Entity entity = Tails.createTails(pt,
                 imageStore.getImageList(TAILS_KEY));
         tryAddEntity(entity);
      }

      return properties.length == TAILS_NUM_PROPERTIES;
   }

   private boolean parseRing(String [] properties,ImageStore imageStore)
   {
      if (properties.length == RING_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[RING_COL]),
                 Integer.parseInt(properties[RING_ROW]));
         Entity entity = Ring.createRing(pt,
                 Integer.parseInt(properties[RING_ACTION_PERIOD]),
                 imageStore.getImageList(RING_KEY));
         tryAddEntity(entity);
      }

      return properties.length == RING_NUM_PROPERTIES;
   }

   public  Optional<Entity> findNearest(Point pos,
                                        Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : this.entities)
      {
         if (kind.isInstance(entity))
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }


   private Optional<Entity> nearestEntity(List<Entity> entities,
                                         Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = nearest.getPosition().distanceSquared(pos);

         for (Entity other : entities)
         {
            int otherDistance = other.getPosition().distanceSquared(pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public Optional<Point> findOpenAround( Point pos)
   {
      for (int dy = -SPIKE_REACH; dy <= SPIKE_REACH; dy++)
      {
         for (int dx = -SPIKE_REACH; dx <= SPIKE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (this.withinBounds(newPt) &&
                    !this.isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public void create_background_event(Point p, ImageStore imageStore){
      String id = "lava";
      Point p1,p2,p3,p4,p5,p6,p7,p8;
      p1 = new Point(p.x+1,p.y);
      p2 = new Point(p.x-1,p.y);
      p3 = new Point(p.x+1,p.y+1);
      p4 = new Point(p.x-1,p.y-1);
      p5 = new Point(p.x+1,p.y-1);
      p6 = new Point(p.x-1,p.y+1);
      p7 = new Point(p.x,p.y+1);
      p8 = new Point(p.x,p.y-1);

      setBackground(p, new Background(id, imageStore.getImageList(id)));
      setBackground(p1, new Background(id, imageStore.getImageList(id)));
      setBackground(p2, new Background(id, imageStore.getImageList(id)));
      setBackground(p3, new Background(id, imageStore.getImageList(id)));
      setBackground(p4, new Background(id, imageStore.getImageList(id)));
      setBackground(p5, new Background(id, imageStore.getImageList(id)));
      setBackground(p6, new Background(id, imageStore.getImageList(id)));
      setBackground(p7, new Background(id, imageStore.getImageList(id)));
      setBackground(p8, new Background(id, imageStore.getImageList(id)));

   }




}
