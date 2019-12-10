import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Shadow extends animatedEntity{

    private static final String QUAKE_KEY = "quake";
    private boolean upgrade = false;

    public Shadow(Point position, List<PImage> images,
                  int actionPeriod, int animationPeriod) {
        super(position,images,0,actionPeriod,animationPeriod);

    }

        private Point nextPosition(WorldModel world, Point destPos) {
            Predicate<Point> canPassThrough = p -> (world.withinBounds(p) && !world.isOccupied(p));
            BiPredicate<Point,Point> withinReach = (p1, p2) -> p1.adjacent(p2);

            PathingStrategy strategy = new AStarPathingStrategy();
            List<Point> path = strategy.computePath(this.getPosition(),destPos,canPassThrough,withinReach,
                    PathingStrategy.CARDINAL_NEIGHBORS);
            if(path.size() == 0){
                return this.getPosition();
            }
            return path.get(0);
        }



    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> crabTarget_sonic = world.findNearest(
                this.getPosition(), Sonic.class);
        Optional<Entity> crabTarget_tails = world.findNearest(
                this.getPosition(), Tails.class);
        long nextPeriod = this.getActionPeriod();

        if (crabTarget_sonic.isPresent()) {
            Point tgtPos = crabTarget_sonic.get().getPosition();

            if (moveToTarget(world, crabTarget_sonic.get(), scheduler)) {
                Quake quake = Quake.createQuake(tgtPos,
                        imageStore.getImageList(QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
        if (crabTarget_tails.isPresent()) {
            Point tgtPos = crabTarget_tails.get().getPosition();

            if (moveToTarget(world, crabTarget_tails.get(), scheduler)) {
                Quake quake = Quake.createQuake(tgtPos,
                        imageStore.getImageList(QUAKE_KEY));
                //VirtualWorld.redraw = true;
                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent( this,
                Activity.createActivityAction( this,world, imageStore),
                nextPeriod);
    }


    private boolean moveToTarget(WorldModel world,
                               Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition()))
        {
            world.removeEntity( target);
            world.setEndTime(System.currentTimeMillis());
            VirtualWorld.redraw = true;
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (!occupant.isPresent())
                {
                    world.moveEntity(this, nextPos);

                }

            }
            return false;
        }
    }


    public static Shadow createShadow(Point pos, int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new Shadow(pos, images, actionPeriod, animationPeriod);
    }



    }
