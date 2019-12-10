import java.util.List;
import processing.core.PImage;

final class Background
{
   public final String id;
   public final List<PImage> images;
   public int imageIndex;

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
   }

   public PImage getCurrentImage() {

         return this.images
                 .get(this.imageIndex);
   }
}
