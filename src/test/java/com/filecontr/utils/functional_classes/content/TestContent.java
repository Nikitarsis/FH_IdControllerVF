package com.filecontr.utils.functional_classes.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public class TestContent {
  @Test
  void testCompleteFactoryMethod() {
    var serverPath = ServerPath.createTestServerPath();
    var time = 1488l;
    var content = ContentFactory.createContent(time, serverPath);
    Assertions.assertEquals(time, content.getCreationTime());
    Assertions.assertEquals(serverPath, content.getServerPath());   
  }

  @Test
  void testSimpleMethod() {
    var serverPath = ServerPath.createTestServerPath();
    var content = ContentFactory.createEmptyContent(serverPath);
    Assertions.assertEquals(serverPath, content.getServerPath());
  }

  @Test
  void testCreationTestContent() {
    var content = ContentFactory.createTestContent();
    Assertions.assertNotNull(content);
    Assertions.assertNotNull(content.getServerPath());
    Assertions.assertNotNull(content.getCreationTime());
  }
}
