# JUnit5 Jupiter Extension

## `WaitExtension` and `WaitContext`

Wait to finish the test method until `WaitContext` satisfies condition(`CountDownLatch`).


Once `WaitExtension` is enabled, `WaitContext` can be injected to test methods.


```java
@ExtendWith(WaitExtension.class)
public class WaitExtensionTests {

  @Test
  public void test(WaitContext waitContext) {
    ...
    // in some other thread or callback
    waitContext.complete();  // notify this test has satisfied finishing condition
    ...
  }

}
```


## TODO

- documentation
- unit tests