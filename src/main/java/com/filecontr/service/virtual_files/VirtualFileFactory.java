package com.filecontr.service.virtual_files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.filecontr.utils.functional_classes.pathes.file_data.FileData;
import com.google.gson.Gson;

public class VirtualFileFactory {
  private final ArrayList<Function<IIdentificator, Optional<IVirtualFile>>> searcherFunctions; 
  private final Supplier<IIdentificator> idSupplier;
  private final ILogger logger;
  private final Function<String, IVirtualFile> deserializer;
  private final Function<IVirtualFile, String> serilizer;

  public VirtualFileFactory(
    Supplier<IIdentificator> idSupplier,
    Function<Class<?>, ILogger> loggerProducer,
    ArrayList<Function<IIdentificator, Optional<IVirtualFile>>> searcherFunctions,
    Gson gson
  ) {
    this.logger = loggerProducer.apply(this.getClass());
    this.idSupplier = idSupplier;
    this.searcherFunctions = searcherFunctions;
    this.deserializer = (str) -> {
      return gson.fromJson(str, IVirtualFile.class);
    };
    this.serilizer = (src) -> {
      return gson.toJson(src, IVirtualFile.class);
    };
  }

  public String processVirtualFileToJson(IVirtualFile virtualFile) {
    return this.serilizer.apply(virtualFile);
  }

  public IVirtualFile createVirtualFileFromJson(String json) {
    return this.deserializer.apply(json);
  }

  public int addSearcher(Function<IIdentificator, Optional<IVirtualFile>> searcher) {
    searcherFunctions.add(searcher);
    return searcherFunctions.size()-1;
  }

  public void removeSearcher(int id) {
    searcherFunctions.remove(id);
  }

  public Optional<List<IVirtualFile>> createVirtualFileFromSql(List<Map<String, String>> sqlMapList) {
    ArrayList<IVirtualFile> array = new ArrayList<>();
    logger.debug("Process SQL request");
    try{
      for (var sqlMap: sqlMapList) {
        var rawId = Long.parseLong(sqlMap.get("id"));
        var rawParentId = Long.parseLong(sqlMap.get("parentId"));
        var rawCreationTime = Long.parseLong(sqlMap.get("creationTime"));
        var rawType = sqlMap.get("type");
      
        Optional<IIdentificator> parentId = Optional.empty();
        if (rawId != rawParentId) {
          parentId = Optional.of(IdFactory.createIdFromLong(rawParentId));
        }
        Optional<String> type = Optional.empty();
        if (!rawType.equals("/NONE")) {
          type = Optional.of(rawType);
        }
        var fileData = new FileData(type, parentId);
        var content = ContentFactory.createContent(rawCreationTime, fileData);
        var id = IdFactory.createIdFromLong(rawId);
        array.add(new SimpleVirtualFile(id, content));
      }
    } catch (NumberFormatException e) {
      logger.warn("Virtual File parsing error");
      return Optional.empty();
    } 
    if (array.size() == 0) {
      return Optional.empty();
    }
    return Optional.of(array);
  }

  public Optional<IVirtualFile> createVirtualFileById(IIdentificator id) {
    for (var searcher : searcherFunctions) {
      Optional<IVirtualFile> virtualFile = Optional.empty();
      try {
        virtualFile = searcher.apply(id);
      } catch (Exception e) {
        logger.warn("Unexpected exception catched: " + e.getMessage());
      }
      if (virtualFile.isPresent()) {
        logger.trace(String.format("File ID %s found", Long.toHexString(id.toLong())));
        return virtualFile;
      }
    }
    logger.debug(String.format("File ID %s not found", Long.toHexString(id.toLong())));
    return Optional.empty();
  }

  public Optional<IVirtualFile> createNewFileRoot(Optional<String> type) {
    try {
      var id = idSupplier.get();
      FileData fileData;
      if (type.isPresent()) {
        fileData = FileData.createFileDataWithType(type.get());
      } else {
        fileData = FileData.createSimpleFileData();
      }
      var content = ContentFactory.createEmptyContent(fileData);
      logger.debug(String.format("File with ID %d created", id.toLong()));
      return Optional.of(new SimpleVirtualFile(id, content));
    } catch (Exception e) {
      logger.warn(String.format("File with ID %d wasn't created. Exception: %s", e.getMessage()));
      return Optional.empty();
    }
  }

  public Optional<IVirtualFile> createNewFileDefault(Optional<IIdentificator> parentId, Optional<String> type) {
    try {
      var id = idSupplier.get();
      FileData fileData = FileData.createFileDataFull(type, parentId);
      var content = ContentFactory.createEmptyContent(fileData);
      logger.debug(String.format("File with ID %d created", id.toLong()));
      return Optional.of(new SimpleVirtualFile(id, content));
    } catch (Exception e) {
      logger.warn(String.format("File with ID %d wasn't created. Exception: %s", e.getMessage()));
      return Optional.empty();
    }
  }

  public static Optional<IVirtualFile> createTestVirtualFile() {
    var id = IdFactory.createTestFactory().getNextId();
    var content = ContentFactory.createTestContent();
    var vf = new SimpleVirtualFile(id, content);
    return Optional.of(vf);
  }
}
