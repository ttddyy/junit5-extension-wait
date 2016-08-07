package net.ttddyy.junit5.extension.wait;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.JUnitException;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tadaya Tsuyukubo
 */
public class WaitContextTests {

	@Test
	public void complete() {
		WaitContext waitContext = new WaitContext(1, Duration.of(1, ChronoUnit.SECONDS));
		waitContext.complete();
	}

	@Test
	public void completeMultiple() {
		WaitContext waitContext = new WaitContext(1, Duration.of(1, ChronoUnit.SECONDS));
		waitContext.setCount(2);
		waitContext.complete();
		waitContext.complete();
	}

	@Test
	public void setCountAfterUsed() {
		WaitContext waitContext = new WaitContext(1, Duration.of(1, ChronoUnit.SECONDS));
		waitContext.complete();

		assertThrows(JUnitException.class, () -> waitContext.setCount(2));
	}

	// TODO: more test with assertTimeout when upgrade to new version
}
