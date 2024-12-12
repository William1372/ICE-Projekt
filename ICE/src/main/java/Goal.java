public class Goal {
    float distanceInKm;
    float distanceProgress;
    int minutes;

    public Goal (float distanceInKm, float distanceProgress){
        this.distanceInKm = distanceInKm;
        this.distanceProgress = distanceProgress;
    }

    public Goal (float distanceInKm, int minutes, float distanceProgress){
        this.distanceInKm = distanceInKm;
        this.distanceProgress = distanceProgress;
        this.minutes = minutes;
    }

    //Der skal rettes i den så den ikke printer det samme for alle goals :
    //0.0 km out of 10.0 km  goal1
    //
    //0.0 km out of 1000.0 km goal2
    //
    //0.0 km out of 30.0 km goal3
    public String toString(){
        return distanceProgress + " km out of " + distanceInKm + " km"+"\n";
    }

    public float getDistance() {
        return  distanceInKm;
    }

    public int getMinutes() {
        return minutes;
    }
}
