package com.laserscorpion.impromptu;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class PersonTest {
    /*@Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }*/

    @Test
    public void personEqualsSelf() {
        Person person1 = new Person();
        person1.id = 25;
        assertEquals(person1, person1);
    }

    @Test
    public void personDoesntEqualSomeoneElse() {
        Person person1 = new Person();
        Person person2 = new Person();
        person1.id = 25;
        person2.id = 100;
        assertNotEquals(person1, person2);
    }

}