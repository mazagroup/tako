package org.springframework.roo.shell.itest;

import static org.junit.Assert.fail;

import org.amdatu.testing.configurator.TestConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExampleTest {

    @Before
    public void before() {
        TestConfigurator.configure(this)
            // Configure your test here
            .apply();
    }

    @After
    public void after() {
        TestConfigurator.cleanUp(this);
    }

    @Test
    public void test() {
        fail("Not implemented yet");
    }

}
