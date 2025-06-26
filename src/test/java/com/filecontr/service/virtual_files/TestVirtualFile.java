package com.filecontr.service.virtual_files;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.filecontr.utils.adapters.logger.AdapterLoggerFactory;
import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestVirtualFile {

  IdFactory factory = IdFactory.createTestFactory();

  IIdentificator getTestId() {
    return factory.getNextId(); 
  }

  Gson getTestGson() {
    var strategy = new VirtualFileConverter();
    var builder = new GsonBuilder();
    builder.registerTypeAdapter(IVirtualFile.class, strategy);
    return builder.create();
  }

  @Test
  void testFindOne() {
    AtomicBoolean check = new AtomicBoolean(false);
    Function<IIdentificator, Optional<IContent>> searcher = (a) -> {
      check.set(true);   
      return Optional.empty();
    };
    var array = new ArrayList<>(List.of(searcher));
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, array, getTestGson());
    vfFactory.getVirtualFileById(getTestId());
    Assertions.assertTrue(check.get());  
  }

  @Test
  void testFindMany() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var check1 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check1.set(true);   
        return Optional.empty();
      }
    );
    var check2 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check2.set(true);
        return Optional.empty();
      }
    );
    vfFactory.getVirtualFileById(getTestId());
    Assertions.assertTrue(check1.get() && check2.get());
  }

  @Test
  void testFindAndStop() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var check1 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check1.set(true);   
        return Optional.empty();
      }
    );
    var check2 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check2.set(true);
        return Optional.of(ContentFactory.createTestContent());
      }
    );
    vfFactory.addSearcher(
      (a) -> {
        Assertions.fail();
        return Optional.empty();
      }
    );
    vfFactory.getVirtualFileById(getTestId());
    Assertions.assertTrue(check1.get() && check2.get());
  }

  @Test
  void testaddSearcher() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    AtomicBoolean check = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check.set(true);   
        return Optional.empty();
      }
    );
    vfFactory.getVirtualFileById(getTestId());
    Assertions.assertTrue(check.get());
  }

  @Test
  void testremoveSearcher() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    AtomicBoolean check = new AtomicBoolean(false);
    var id = vfFactory.addSearcher(
      (a) -> {
        check.set(true);   
        return Optional.empty();
      }
    );
    vfFactory.removeSearcher(id);
    vfFactory.getVirtualFileById(getTestId());
    Assertions.assertFalse(check.get());
  }

  @Test
  void testCreateNewFileRoot() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var optionalVirtualFile = vfFactory.createNewFileRoot(Optional.of("testtype"));
    if (optionalVirtualFile.isEmpty()) {
      Assertions.fail();
    }
    var virtualFile = optionalVirtualFile.get();
    Assertions.assertNotNull(virtualFile.getContent());
    Assertions.assertNotNull(virtualFile.getId());
  }

  @Test
  void testCreateNewFileDefault() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var id = IdFactory.createTestFactory().getNextId();
    var optionalVirtualFile = vfFactory.createNewFileDefault(Optional.of(id), Optional.of("testtype"));
    if (optionalVirtualFile.isEmpty()) {
      Assertions.fail();
    }
    var virtualFile = optionalVirtualFile.get();
    Assertions.assertNotNull(virtualFile.getContent());
    Assertions.assertNotNull(virtualFile.getId());
  }
}
