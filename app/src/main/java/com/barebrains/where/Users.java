package com.barebrains.where;

public class Users {
    private String Name;
    private String Email;
    private Friend Friends;
    private Friend FrReq;

    public Users()
    {

    }
    public Users(String Name,String Email)
    {
        this.Email = Email;
        this.Name = Name;
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

