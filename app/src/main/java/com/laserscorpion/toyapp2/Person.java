package com.laserscorpion.toyapp2;

public class Person {
    public int id;
    public String name;
    public String uni;

    public String toString() {
        return name + ": " + uni;
    }

    @Override
    public boolean equals(Object e) {
        if (! (e instanceof  Person))
            return false;
        Person other = (Person)e;
        return id == other.id;
    }
}

