package net.ttddyy.junit5.extension.wait;

import org.junit.jupiter.api.Executable;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.ExceptionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Tadaya Tsuyukubo
 */
public class WaitContext {

	private CountDownLatch latch;

	private Duration interval = Duration.ofSeconds(1);

	private Duration duration;

	private volatile Throwable error;

	private boolean started;

	public WaitContext(int count, Duration duration) {
		this.latch = new CountDownLatch(count);
		this.duration = duration;
	}

	public void complete() {
		this.started = true;
		this.latch.countDown();
	}

	public void fail(Throwable e) {
		this.started = true;
		this.error = e;
		this.latch.countDown();
	}

	public void setCount(int count) {
		if (this.started) {
			throw new JUnitException(String.format(
					"%s has already started. count=%d", WaitContext.class.getSimpleName(), this.latch.getCount()));
		}
		this.latch = new CountDownLatch(count);
	}

	public void setTimeout(Duration duration) {
		if (this.started) {
			throw new JUnitException(String.format(
					"%s has already started. count=%d", WaitContext.class.getSimpleName(), this.latch.getCount()));
		}
		this.duration = duration;
	}

	public void setCheckInterval(Duration interval) {
		this.interval = interval;
	}

	public void await() {

		Executable executable = () -> {
			if (this.latch == null) {
				throw new JUnitException("This context is already used");
			}

			LocalDateTime expireAt = LocalDateTime.now().plus(duration);
			// keep polling latch every interval
			while (expireAt.isAfter(LocalDateTime.now())) {
				if (this.latch.await(this.interval.toNanos(), TimeUnit.NANOSECONDS)) {
					break;
				}
			}

			LocalDateTime now = LocalDateTime.now();
			if (expireAt.isBefore(now)) {
				Duration difference = Duration.between(expireAt, now);

				// or maybe throw TimeoutException
				throw new JUnitException(String.format(
						"%s has expired. [duration=%s, count=%d]", WaitContext.class.getSimpleName(), difference,
						this.latch.getCount()));
			}

			// prevent this latch from being reused
			this.latch = null;

			if (this.error != null) {
				throw this.error;
			}
		};

		try {
			executable.execute();
		}
		catch (Throwable throwable) {
			throw ExceptionUtils.throwAsUncheckedException(throwable);
		}
	}
}
