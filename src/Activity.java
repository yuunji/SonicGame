public class Activity extends Action{




    public Activity(activeEntity entity, WorldModel world, ImageStore imageStore){
        super(entity,world,imageStore,0);

    }
    public void executeAction(EventScheduler scheduler)
    {
        this.getEntity().executeActivity(this.getWorld(),this.getImageStore(),scheduler);
    }

    public static Activity createActivityAction(activeEntity entity, WorldModel world, ImageStore imageStore)
    {
        return new Activity(entity,world,imageStore);
    }
}
