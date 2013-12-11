package org.adiusframework.processmanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.adiusframework.processmanager.xml.Aspect;
import org.adiusframework.processmanager.xml.ProcessDefinition;
import org.adiusframework.processmanager.xml.TaskType;
import org.springframework.beans.factory.annotation.Required;

/**
 * The ParsedServiceProcessDefinition acts as a bridge to a ProcessDefinition
 * object which is loaded by the inserted parser from the definition file.
 */
public class ParsedServiceProcessDefinition implements ServiceProcessDefinition {

	private URL definitionURL;

	private ProcessDefinitionParser parser;

	private ProcessDefinition processDefinition;

	@PostConstruct
	public void init() throws IOException {
		InputStream stream = getDefinitionUrl().openStream();
		setProcessDefinition(getParser().parse(stream));
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	@Required
	public URL getDefinitionUrl() {
		return definitionURL;
	}

	public void setDefinitionUrl(URL definitionURL) {
		this.definitionURL = definitionURL;
	}

	@Required
	public ProcessDefinitionParser getParser() {
		return parser;
	}

	public void setParser(ProcessDefinitionParser parser) {
		this.parser = parser;
	}

	@Override
	public TaskType getNextTask(String taskName) {
		if (taskName == null)
			return getProcessDefinition().getTasks().getTask().get(0);
		int index = indexOf(taskName);
		if (index > -1 && index + 1 < getProcessDefinition().getTasks().getTask().size())
			return getProcessDefinition().getTasks().getTask().get(index + 1);
		return null;
	}

	@Override
	public boolean isFinalTask(String taskName) {
		return indexOf(taskName) == getProcessDefinition().getTasks().getTask().size() - 1;
	}

	protected int indexOf(String taskName) {
		int index = 0;
		for (TaskType taskType : getProcessDefinition().getTasks().getTask()) {
			if (taskType.getName().equals(taskName))
				return index;
			index++;
		}
		return -1;
	}

	@Override
	public boolean isDataAccessor() {
		if (getProcessDefinition().getType().getAspect() == null)
			throw new NullPointerException("Aspect has not been set correctly, null returned.");
		return getProcessDefinition().getType().getAspect().equals(Aspect.ACCESSOR);
	}

	@Override
	public String getType() {
		return getProcessDefinition().getType().getName();
	}

	@Override
	public String getDomain() {
		return getProcessDefinition().getType().getDomain();
	}

}
