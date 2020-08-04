package com.l900.master.tools;


import android.util.Log;

public class EmergencyContact {

    private String name;
    private String tel;

    public EmergencyContact(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "EmergencyContact{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        EmergencyContact emergencyContact = (EmergencyContact) o;
        Log.e("1900","equals "+emergencyContact.toString());
        if ((emergencyContact.name.equals(name))&& (emergencyContact.tel.equals(tel)))
            return true;
       return true;
    }



}
