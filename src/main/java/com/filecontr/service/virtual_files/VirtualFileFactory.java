package com.filecontr.service.virtual_files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.filecontr.service.server_data.IIdStrategy;
import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.filecontr.utils.functional_classes.pathes.file_data.FileData;
import com.google.gson.Gson;

public class VirtualFileFactory {
  private final List<Function<IIdentificator[], List<Optional<IVirtualFile>>>> searcherFunctions; 
  private final Function<IIdStrategy, IIdentificator> idSupplier;
  private final ILogger logger;
  private final Function<String, IVirtualFile> deserializer;
  private final Function<IVirtualFile, String> serilizer;

  public VirtualFileFactory(
    Function<IIdStrategy, IIdentificator> idSupplier,
    Function<Class<?>, ILogger> loggerProducer,
    List<Function<IIdentificator[], List<Optional<IVirtualFile>>>> searcherFunctions,
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

  public Optional<String> processVirtualFileToJson(IVirtualFile virtualFile) {
    try {
      return Optional.of(this.serilizer.apply(virtualFile));
    } catch (RuntimeException e) {
      logger.warn("JSON processing error");
      return Optional.empty();
    }
  }

  public Optional<IVirtualFile> createVirtualFileFromJson(String json) {
    try{
      return Optional.of(this.deserializer.apply(json));
    } catch (RuntimeException e) {
      logger.warn("JSON parsing error");
      return Optional.empty();
    }
  }

  public int addSearcher(Function<IIdentificator[], List<Optional<IVirtualFile>>> searcher) {
    searcherFunctions.add(searcher);
    return searcherFunctions.size()-1;
  }

  public void removeSearcher(int id) {
    searcherFunctions.remove(id);
  }

  public Optional<IVirtualFile> createVirtualFileFromSql(Map<String, String> sqlMap) {
    try {
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
      return Optional.of(new SimpleVirtualFile(id, content));
    } catch (RuntimeException e) {
      logger.warn("Virtual File parsing error: " + e.getMessage());
    }
    return Optional.empty();
  }

  public List<Optional<IVirtualFile>> createVirtualFileListFromSql(List<Map<String, String>> sqlMapList) {
    logger.debug("Process SQL request");
    return sqlMapList.stream().map(this::createVirtualFileFromSql).toList();
  }

  public Map<IIdentificator, Optional<IVirtualFile>> createVirtualFileById(IIdentificator... ids) {
    var remainingIds = new HashSet<IIdentificator>(List.of(ids));
    var retArray = new ArrayList<Optional<IVirtualFile>>(ids.length);
    var ret = new HashMap<IIdentificator, Optional<IVirtualFile>>();
    for (var searcher : searcherFunctions) {
      List<Optional<IVirtualFile>> result = searcher.apply(
          remainingIds.toArray(IIdentificator[]::new)
        ).stream()
        .filter(a -> a.isPresent())
        .toList();
      remainingIds.removeAll(result.stream().map(a -> a.get().getId()).toList());
      retArray.addAll(result);
      if (remainingIds.size() == 0) {
        break;
      } 
    }
    remainingIds.stream().peek(a -> logger.debug(String.format("File ID %s not found", Long.toHexString(a.toLong())))).close();
    for (var element : retArray) {
      ret.put(element.get().getId(), element);
    }
    for (var element : remainingIds) {
      ret.put(element, Optional.empty());
    }
    return ret;
  }

  public Optional<IVirtualFile> createNewFileDefault(IIdStrategy request) {
    try {
      var id = idSupplier.apply(request);
      var parentId = request.getParent();
      var type = request.getType();
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
