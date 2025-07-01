package com.filecontr.service.virtual_files;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.filecontr.service.server_data.IIdStrategy;
import com.filecontr.utils.adapters.logger.AdapterLoggerFactory;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestVirtualFile {

  IdFactory factory = IdFactory.createTestFactory();

  IIdentificator getTestId(IIdStrategy strategy) {
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
    Function<IIdentificator[], List<Optional<IVirtualFile>>> searcher = (a) -> {
      check.set(true);
      return List.of(Optional.empty());
    };
    var array = new ArrayList<>(List.of(searcher));
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, array, getTestGson());
    vfFactory.createVirtualFileById(getTestId(IIdStrategy.getTestStrategy()));
    Assertions.assertTrue(check.get());  
  }

  @Test
  void testFindMany() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var check1 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check1.set(true);   
        return List.of(Optional.empty());
      }
    );
    var check2 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check2.set(true);
        return List.of(Optional.empty());
      }
    );
    vfFactory.createVirtualFileById(getTestId(IIdStrategy.getTestStrategy()));
    Assertions.assertTrue(check1.get() && check2.get());
  }

  @Test
  void testFindAndStop() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var check1 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check1.set(true);   
        return List.of(Optional.empty());
      }
    );
    var check2 = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check2.set(true);
        return List.of(VirtualFileFactory.createTestVirtualFile());
      }
    );
    vfFactory.addSearcher(
      (a) -> {
        Assertions.fail();
        return List.of(Optional.empty());
      }
    );
    vfFactory.createVirtualFileById(getTestId(IIdStrategy.getTestStrategy()));
    Assertions.assertTrue(check1.get() && check2.get());
  }

  @Test
  void testaddSearcher() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    AtomicBoolean check = new AtomicBoolean(false);
    vfFactory.addSearcher(
      (a) -> {
        check.set(true);   
        return List.of(Optional.empty());
      }
    );
    vfFactory.createVirtualFileById(getTestId(IIdStrategy.getTestStrategy()));
    Assertions.assertTrue(check.get());
  }

  @Test
  void testremoveSearcher() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    AtomicBoolean check = new AtomicBoolean(false);
    var id = vfFactory.addSearcher(
      (a) -> {
        check.set(true);   
        return List.of(Optional.empty());
      }
    );
    vfFactory.removeSearcher(id);
    vfFactory.createVirtualFileById(getTestId(IIdStrategy.getTestStrategy()));
    Assertions.assertFalse(check.get());
  }

  @Test
  void testCreateNewFileDefault() {
    var vfFactory = new VirtualFileFactory(this::getTestId, AdapterLoggerFactory::getTestLogger, new ArrayList<>(), getTestGson());
    var optionalVirtualFile = vfFactory.createNewFileDefault(IIdStrategy.getTestStrategy());
    if (optionalVirtualFile.isEmpty()) {
      Assertions.fail();
    }
    var virtualFile = optionalVirtualFile.get();
    Assertions.assertNotNull(virtualFile.getContent());
    Assertions.assertNotNull(virtualFile.getId());
  }
}
