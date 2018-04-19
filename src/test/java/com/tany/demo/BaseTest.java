package com.tany.demo;

import com.tany.demo.config.TestApplicationConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

@ContextConfiguration(classes = {TestApplicationConfig.class})
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
}
