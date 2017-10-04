package com.laserscorpion.toyapp2;

import org.junit.Test;

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
}