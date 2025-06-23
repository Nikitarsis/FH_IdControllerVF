package com.filecontr.utils.functional_classes.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;

public class TestContent {
  @Test
  void testCompleteFactoryMethod() {
    var filePath = FilePath.createTestFilePath();
    var time = 1488l;
    var content = ContentFactory.createContent(time, filePath);
    Assertions.assertEquals(time, content.getCreationTime());
    Assertions.assertEquals(filePath, content.getFilePath());   
  }

  @Test
  void testSimpleMethod() {
    var filePath = FilePath.createTestFilePath();
    var content = ContentFactory.createEmptyContent(filePath);
    Assertions.assertEquals(filePath, content);
  }

  @Test
  void testCreationTestContent() {
    var content = ContentFactory.createTestContent();
    Assertions.assertNotNull(content);
    Assertions.assertNotNull(content.getFilePath());
    Assertions.assertNotNull(content.getCreationTime());
  }
}
