public class DifficultyFactory {

    public Level getLevel(long time){

        if(time < 30*1000){
            return new Level0();
        }
        else if( time >= 30*1000 && time <= 60*1000){
            return new Level1();
        }
        else if(time >= 60*1000 && time <= 90 * 1000){
            return new Level2();
        }
        else{
            return new Level3();
        }
    }
}
