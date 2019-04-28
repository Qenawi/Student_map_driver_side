package com.panda.student_map_driver_side.data;

public class PickupRequst
{
    public PickupRequst()
    {

    }
    public PickupRequst(String state, String Location)
    {
        setState(state);
        setLocation(Location);
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
       this.state = state;
    }
    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }


    private String state;
    private String location;

}
