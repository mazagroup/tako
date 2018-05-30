package org.bndtools.templating;

import java.util.List;
import java.util.Map;


public interface TemplateEngine {

    Map<String, String> getTemplateParameters(ResourceMap inputs, IProgressMonitor monitor) throws Exception;

    ResourceMap generateOutputs(ResourceMap inputs, Map<String, List<Object>> parameters, IProgressMonitor monitor) throws Exception;

}
