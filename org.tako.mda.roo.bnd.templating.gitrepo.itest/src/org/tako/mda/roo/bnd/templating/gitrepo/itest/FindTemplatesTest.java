package org.tako.mda.roo.bnd.templating.gitrepo.itest;

import static org.junit.Assert.fail;

import org.amdatu.testing.configurator.TestConfigurator;
import org.bndtools.templating.TemplateLoader;

import static org.amdatu.testing.configurator.TestConfigurator.createServiceDependency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FindTemplatesTest {

	private volatile TemplateLoader templateLoader;

    @Before
    public void before() {
        TestConfigurator.configure(this)
            // Configure your test here
        	.add(createServiceDependency().setService(TemplateLoader.class).setRequired(true))
            .apply();
    }

    @After
    public void after() {
        TestConfigurator.cleanUp(this);
    }

    @Test
    public void test() {
    	org.junit.Assert.assertNotNull(templateLoader);
        //fail("Not implemented yet");
    }

}
