package net.ttddyy.junit5.extension.wait;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExtensionContext;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 */
public class WaitExtension implements ParameterResolver, AfterTestExecutionCallback {
	private static final String DEFAULT_TIMEOUT_VALUE_PROPERTY_NAME = "junit.waitcontext.timeout.value";

	private static final String DEFAULT_TIMEOUT_UNIT_PROPERTY_NAME = "junit.waitcontext.timeout.unit";

	private static final int DEFAULT_TIMEOUT_VALUE = Integer.getInteger(DEFAULT_TIMEOUT_VALUE_PROPERTY_NAME, 10);

	private static final ChronoUnit DEFAULT_TIMEOUT_UNIT = ChronoUnit.valueOf(
			System.getProperty(DEFAULT_TIMEOUT_UNIT_PROPERTY_NAME, ChronoUnit.SECONDS.name()));

	// set default to 10sec
	private static final Duration DEFAULT_TIMEOUT = Duration.of(DEFAULT_TIMEOUT_VALUE, DEFAULT_TIMEOUT_UNIT);

	@Override
	public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return (parameterContext.getParameter().getType() == WaitContext.class);
	}

	@Override
	public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		// only works for test methods for now
		Method testMethod = extensionContext.getTestMethod().orElseThrow(() ->
				new ParameterResolutionException(String.format(
						"%s is only supported on test method", WaitContext.class.getSimpleName())));

		this.waitContexts.putIfAbsent(testMethod, new WaitContext(1, DEFAULT_TIMEOUT));
		return this.waitContexts.get(testMethod);
	}

	private Map<Method, WaitContext> waitContexts = new HashMap<>();

	@Override
	public void afterTestExecution(TestExtensionContext context) throws Exception {
		context.getTestMethod().ifPresent(testMethod -> {
			this.waitContexts.computeIfPresent(testMethod, (method, waitContext) -> {
				waitContext.await();
				return null;  // remove the entry from map
			});
		});
	}

}
