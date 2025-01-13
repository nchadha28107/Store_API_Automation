package com.store.steps;

import com.store.utils.MockServer;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

  @Before()
  public void beforeScenario() {
    MockServer.startMockServer();
  }

  @After()
  public void afterScenario() {
    MockServer.stopMockServer();
  }
}
