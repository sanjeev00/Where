package com.barebrains.where;

public class Users {
    private String Name;
    private String Email;
    private Friend Friends;
    private Friend FrReq;
    private String Location;
    private String Avatar;

    public Users()
    {

    }
    public Users(String Name,String Email)
    {
        this.Email = Email;
        this.Name = Name;
    }
    Users(String Name,String Email,String Avatar,String avatar,String location)
    {
        this.Email = Email;
        this.Name = Name;
        this.Location = location;
        this.Avatar = avatar;

    }
    public String getLocation() {
        return this.Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getAvatar() {
        return this.Avatar;
    }

    public void setAvatar(String avatar) {
        this.Avatar = avatar;
    }



    public String getName()
    {
        return this.Name;
    }

    public String getEmail() {
        return  this.Email;
    }

    public Friend getFriends() {
        return this.Friends;
    }
    public Friend getFrReq()
    {
        return this.FrReq;
    }
}
class Friend {
    private String S;
    public Friend()
    {

    }
    public Friend(String s)
    {

    }
    public String getS()
    {
        return this.S;
    }
}

