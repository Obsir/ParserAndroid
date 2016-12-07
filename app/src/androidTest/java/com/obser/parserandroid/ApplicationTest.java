package com.obser.parserandroid;

import android.app.Application;
import android.os.Message;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testApp(){
//        assertEquals();
        Message a = new Message();
        a.what = 0;
        Message b = a;
        b.what = 1;
        a.what = 3;
        assertEquals(b.what, a.what);
//        assertEquals(c=='\n', true);
//        assertEquals(Character.isWhitespace((int)' '), true);
//        assertEquals(Character.isWhitespace((int)'\t'), true);
    }
    public void testLine(){

    }
}