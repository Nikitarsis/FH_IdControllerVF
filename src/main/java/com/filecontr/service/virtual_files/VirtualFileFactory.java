package com.filecontr.service.virtual_files;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.pathes.file_data.FileData;
import com.google.gson.Gson;

public class VirtualFileFactory {
  private final ArrayList<Function<IIdentificator, Optional<IContent>>> searcherFunctions; 
  Supplier<IIdentificator> idSupplier;
  ILogger logger;
  Function<String, IVirtualFile> deserializator;
  Function<IVirtualFile, String> serilizator;

  public VirtualFileFactory(
    Supplier<IIdentificator> idSupplier,
    Function<Class<?>, ILogger> loggerProducer,
    ArrayList<Function<IIdentificator, Optional<IContent>>> searcherFunctions,
    Gson gson
  ) {
    this.logger = loggerProducer.apply(this.getClass());
    this.idSupplier = idSupplier;
    this.searcherFunctions = searcherFunctions;
    this.deserializator = (str) -> {
      return gson.fromJson(str, IVirtualFile.class);
    };
    this.serilizator = (src) -> {
      return gson.toJson(src, IVirtualFile.class);
    };
  }

  public String toJson(IVirtualFile virtualFile) {
    return this.serilizator.apply(virtualFile);
  }

  public IVirtualFile fromJson(String json) {
    return this.deserializator.apply(json);
  }

  public int addSearcher(Function<IIdentificator, Optional<IContent>> searcher) {
    searcherFunctions.add(searcher);
    return searcherFunctions.size()-1;
  }

  public void removeSearcher(int id) {
    searcherFunctions.remove(id);
  }

  public Optional<IVirtualFile> getVirtualFileById(IIdentificator id) {
    for (var searcher : searcherFunctions) {
      Optional<IContent> content = Optional.empty();
      try {
        content = searcher.apply(id);
      } catch (Exception e) {
        logger.warn("Unexpected exception catched: " + e.getMessage());
      }
      if (content.isPresent()) {
        logger.trace(String.format("File ID %s found", Long.toHexString(id.toLong())));
        return Optional.of(new SimpleVirtualFile(id, content.get()));
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
}
