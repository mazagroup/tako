package org.tako.mda.roo.bnd.templating.gitrepo.itest;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.amdatu.testing.configurator.TestConfigurator;
import org.bndtools.templating.Template;
import org.bndtools.templating.TemplateLoader;

import static org.amdatu.testing.configurator.TestConfigurator.createServiceDependency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.osgi.util.promise.Promise;

import aQute.libg.reporter.ReporterAdapter;

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
    public void testFindWorkspaceTemplates() throws InvocationTargetException, InterruptedException {
    	org.junit.Assert.assertNotNull(templateLoader);
        //fail("Not implemented yet");
    	Promise<? extends Collection<Template>> res = templateLoader.findTemplates("workspace", new ReporterAdapter());
    	org.junit.Assert.assertEquals(2, res.getValue().size());
    }

}
