package com.filecontr.utils.functional_classes.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestIdFactory {
  @Test
  void testFactory() {
    var testFactory = IdFactory.createTestFactory();
    Assertions.assertNotNull(testFactory.getNextId());
  }

  @Test
  void testId() {
    var testFactory = IdFactory.createTestFactory();
    Assertions.assertNotNull(testFactory.getNextId().toLong());
  }
}
