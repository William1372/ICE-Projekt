public class Goal {
    float distanceInKm;
    float distanceProgress;


    public Goal (float distanceInKm, float distanceProgress){
        this.distanceInKm = distanceInKm;
        this.distanceProgress = distanceProgress;
        //Hvad skal er goal være? vægt? km-mål?
    }

    public String toString(){
        return distanceProgress + " km out of " + distanceInKm + " km";
    }

}
