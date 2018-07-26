package com.eventyay.organizer.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SearchUtilsTest {

    @Test
    public void shouldSuccessfullyMatch() {
        assertFalse(SearchUtils.filter("jaob.prser", "Jack", "Parsar"));
        assertFalse(SearchUtils.filter(" kirk", "Bob", "Kirk"));
        assertFalse(SearchUtils.filter("bagula", "Harry", "Daniels", "manas.bagula"));
        assertFalse(SearchUtils.filter("gm", "Henry", "Parrish", "henry.parish@gmail.com"));
        assertFalse(SearchUtils.filter("prry", "Henry", "Parrish", "henry.parish@gmail.com"));
        assertFalse(SearchUtils.filter("prri", "Henry", "Porish", "henry.parish@gmail.com"));
        assertFalse(SearchUtils.filter("hnl", "Henry", "Parrish", "henry.parish@gmail.com"));
        assertFalse(SearchUtils.filter("hnr", "Hank", "Parrish", "Hank.parish@gmail.com"));
        assertFalse(SearchUtils.filter("hnr", "Handly", "Parrish", "handly.parish@gmail.com"));
    }

    @Test
    public void shouldNotSuccessfullyMatch() {
        assertTrue(SearchUtils.filter("jaob.prser", "Jackes", "Pastor"));
        assertTrue(SearchUtils.filter(" kirk", "Bob", "Shark"));
        assertTrue(SearchUtils.filter("bagula", "Harry", "Daniels", "manas.baggs"));
        assertTrue(SearchUtils.filter("gm", "Henry", "Parrish", "henry.parish@ymail.com"));
        assertTrue(SearchUtils.filter("prry", "Henry", "Porish", "henry.parish@gmail.com"));
        assertTrue(SearchUtils.filter("prri", "Henry", "Poish", "henry.poish@gmail.com"));
        assertTrue(SearchUtils.filter("hnr", "Hadley", "Parrish", "handly.chief@gmail.com"));
    }

}
