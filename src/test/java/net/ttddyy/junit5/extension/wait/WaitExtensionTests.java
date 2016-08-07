package net.ttddyy.junit5.extension.wait;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.ClassFilter.includeClassNamePattern;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(JUnitPlatform.class)
public class WaitExtensionTests {

	@Nested
	@ExtendWith(WaitExtension.class)
	class Basic {

		@Test
		public void complete(WaitContext waitContext) {
			assertNotNull(waitContext);
			waitContext.complete();
		}

		@Test
		public void completeMultiple(WaitContext waitContext) {
			assertNotNull(waitContext);
			waitContext.setCount(2);
			waitContext.complete();
			waitContext.complete();
		}

		@Test
		public void setCountAfterUsed(WaitContext waitContext) {
			assertNotNull(waitContext);
			waitContext.complete();

			assertThrows(JUnitException.class, () -> waitContext.setCount(2));
		}

		@Test
		public void withDifferentThread(WaitContext waitContext) {
			new Thread(() -> waitContext.complete()).start();
		}

	}

	//	class TestResultListener implements TestExecutionListener {
	//
	//		int failed;
	//
	//		@Override
	//		public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
	//			if (testExecutionResult.getStatus() == Status.FAILED) {
	//				failed++;
	//			}
	//		}
	//	}
	//
	//	@Nested
	//	public class Basica {
	//
	//		// TODO: timeout case
	//		// set count after started
	//		// set count
	//
	//		@ExtendWith(WaitExtension.class)
	//		public class ParameterInBeforeAll {
	//
	//			//			@BeforeAll
	//			//			@ExtendWith(WaitExtension.class)
	//			//			void beforeAll(WaitContext waitContext){
	//			void beforeAll() {
	//				System.out.println("called");
	//				fail("This line of code should not be executed");
	//			}
	//
	//			@Test
	//			public void test() {
	//				System.out.println("AAA");
	//			}
	//		}
	//
	//		@Test
	//		public void a() {
	//			LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
	//					.selectors(selectClass(ParameterInBeforeAll.class))
	////					.selectors(selectMethod(ParameterInBeforeAll.class, "test"))
	//					.build();
	//
	//			Launcher launcher = LauncherFactory.create();
	//
	//			// Register a listener of your choice
	//			TestResultListener listener = new TestResultListener();
	//			launcher.registerTestExecutionListeners(listener);
	//
	//			launcher.execute(request);
	//
	//			assertEquals(1, listener.failed, "Test execution should be failed");
	//		}
	//
	//	}

}
