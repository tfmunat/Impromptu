package com.laserscorpion.impromptu;

public class Person {
    public int id;
    public String name;

    public String toString() {
        return name + ": " + id;
    }

    @Override
    public boolean equals(Object e) {
        if (! (e instanceof  Person))
            return false;
        Person other = (Person)e;
        return id == other.id;
    }
}

