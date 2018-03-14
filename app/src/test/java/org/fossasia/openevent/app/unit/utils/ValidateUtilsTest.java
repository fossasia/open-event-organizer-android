package org.fossasia.openevent.app.unit.utils;

import org.fossasia.openevent.app.common.utils.core.ValidateUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ValidateUtilsTest {

    @Test
    public void testValidateUrl() {
        assertTrue(ValidateUtils.validateUrl("https://www.test.com"));
        assertTrue(ValidateUtils.validateUrl("http://www.test.com"));
        assertTrue(ValidateUtils.validateUrl("ftp://www.test.com"));
        assertTrue(ValidateUtils.validateUrl("file://www.test.com"));
        assertTrue(ValidateUtils.validateUrl("https://test.com"));
        assertTrue(ValidateUtils.validateUrl("https://www.TEST.co"));
        assertTrue(ValidateUtils.validateUrl("https://test"));
        assertTrue(ValidateUtils.validateUrl("https://www.TEST.c@!34o"));
        assertTrue(ValidateUtils.validateUrl("https://www.test5465uy.co.in"));
        assertTrue(ValidateUtils.validateUrl("https://www.te/**-st.co.in"));
        assertTrue(ValidateUtils.validateUrl("https://www.test.co.in"));
        assertTrue(ValidateUtils.validateUrl("https://www.test.co"));
        assertTrue(ValidateUtils.validateUrl("https://www.test.in"));
        assertTrue(ValidateUtils.validateUrl("https://54652"));
        assertTrue(ValidateUtils.validateUrl("https://t.co"));
        assertTrue(ValidateUtils.validateUrl("https://tby.test.com"));

        assertFalse(ValidateUtils.validateUrl(""));
        assertFalse(ValidateUtils.validateUrl("ht://www.test.com"));
        assertFalse(ValidateUtils.validateUrl("http/www.test.com"));
        assertFalse(ValidateUtils.validateUrl("http://"));
        assertFalse(ValidateUtils.validateUrl("http://."));
        assertFalse(ValidateUtils.validateUrl("http://...."));
        assertFalse(ValidateUtils.validateUrl("http56://www.test.com"));
        assertFalse(ValidateUtils.validateUrl("http//www.test."));
        assertFalse(ValidateUtils.validateUrl("http//www."));
        assertFalse(ValidateUtils.validateUrl("httpstest.com"));
        assertFalse(ValidateUtils.validateUrl("test"));
        assertFalse(ValidateUtils.validateUrl("test.co.in"));
        assertFalse(ValidateUtils.validateUrl(".com"));
    }

    @Test
    public void testValidateEmail() {
        assertTrue(ValidateUtils.validateEmail("abcd@efg.com"));
        assertTrue(ValidateUtils.validateEmail("ABCD@efg.com"));
        assertTrue(ValidateUtils.validateEmail("abc898d@efg.com"));
        assertTrue(ValidateUtils.validateEmail("abcd@efg.COM"));
        assertTrue(ValidateUtils.validateEmail("abc@EFG.co"));
        assertTrue(ValidateUtils.validateEmail("abc@efg.co.in"));
        assertTrue(ValidateUtils.validateEmail("abcd@efg.com"));
        assertTrue(ValidateUtils.validateEmail("abcd@efg.in"));
        assertTrue(ValidateUtils.validateEmail("abcd@efg.bhj"));
        assertTrue(ValidateUtils.validateEmail("abcd@ef56g.bhj"));
        assertTrue(ValidateUtils.validateEmail("abc+-%d@abc.com"));

        assertFalse(ValidateUtils.validateEmail(""));
        assertFalse(ValidateUtils.validateEmail("abcdefg.com"));
        assertFalse(ValidateUtils.validateEmail("abc@.com"));
        assertFalse(ValidateUtils.validateEmail("ab*-c@defg.com"));
        assertFalse(ValidateUtils.validateEmail("abcd"));
        assertFalse(ValidateUtils.validateEmail("abc@abc"));
        assertFalse(ValidateUtils.validateEmail("abc@@hjg.com"));
        assertFalse(ValidateUtils.validateEmail("abc@abc.co++-"));
        assertFalse(ValidateUtils.validateEmail("abc@ab/*c"));
        assertFalse(ValidateUtils.validateEmail("abc@ab/*c.com"));
        assertFalse(ValidateUtils.validateEmail("abcd@efg.co565m"));
        assertFalse(ValidateUtils.validateEmail("abcd@ghs.l"));
        assertFalse(ValidateUtils.validateEmail("abcd@efg.cojjjhjhjhjhjhjhjhm"));
    }
}
