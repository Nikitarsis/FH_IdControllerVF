package com.filecontr.service.virtual_files;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;
import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public class VirtualFileFactory {
  private final ArrayList<Function<IIdentificator, Optional<IContent>>> searcherFunctions; 
  Supplier<IIdentificator> idSupplier;
  ILogger logger;

  public VirtualFileFactory(
    Supplier<IIdentificator> idSupplier,
    Function<Class<?>, ILogger> loggerProducer,
    ArrayList<Function<IIdentificator, Optional<IContent>>> searcherFunctions
  ) {
    this.logger = loggerProducer.apply(this.getClass());
    this.idSupplier = idSupplier;
    this.searcherFunctions = searcherFunctions;
  }

  public int addSercher(Function<IIdentificator, Optional<IContent>> searcher) {
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
        logger.trace(String.format("File ID %d found", id.toLong()));
        return Optional.of(new SimpleVirtualFile(id, content.get()));
      }
    }
    logger.debug(String.format("File ID %d not found"));
    return Optional.empty();
  }

  public Optional<IVirtualFile> createNewFileRoot(Optional<String> type) {
    try {
      var id = idSupplier.get();
      String relativePath;
      FilePath filePath;
      if (type.isPresent()) {
        relativePath = String.format("./%s.%s", Long.toHexString(id.toLong()), type.get());
        filePath = FilePath.createFilePathWithType(relativePath, type.get());
      } else {
        relativePath = String.format("./%s", Long.toHexString(id.toLong()));
        filePath = FilePath.createSimpleFilePath(relativePath);
      }
      var path = new ServerPath(
        id.getData().getURL(),
        filePath
      );
      var content = ContentFactory.createEmptyContent(path);
      logger.debug(String.format("File with ID %d created", id.toLong()));
      return Optional.of(new SimpleVirtualFile(id, content));
    } catch (Exception e) {
      logger.warn(String.format("File with ID %d wasn't created. Exception: %s", e.getMessage()));
      return Optional.empty();
    }
  }

  public Optional<IVirtualFile> createNewFileDefault(String directoryPath, Optional<String> type) {
    try {
      var id = idSupplier.get();
      var relativePath = String.format("%s%d", directoryPath, id.toLong());
      FilePath filePath;
      if (type.isPresent()) {
        filePath = FilePath.createFilePathWithType(relativePath, type.get());
      } else {
        filePath = FilePath.createSimpleFilePath(relativePath);
      }
      var path = new ServerPath(
        id.getData().getURL(),
        filePath
      );
      var content = ContentFactory.createEmptyContent(path);
      logger.debug(String.format("File with ID %d created", id.toLong()));
      return Optional.of(new SimpleVirtualFile(id, content));
    } catch (Exception e) {
      logger.warn(String.format("File with ID %d wasn't created. Exception: %s", e.getMessage()));
      return Optional.empty();
    }
  }

  public Optional<IVirtualFile> createNewFilePseudonym(String relativePath, String type) {
    try {
      var id = idSupplier.get();
      var path = new ServerPath(
        id.getData().getURL(),
        FilePath.createFilePathWithType(relativePath, type)
      );
      var content = ContentFactory.createEmptyContent(path);
      logger.debug(String.format("File with ID %d created", id.toLong()));
      return Optional.of(new SimpleVirtualFile(id, content));
    } catch (Exception e) {
      logger.warn(String.format("File with ID %d wasn't created. Exception: %s", e.getMessage()));
      return Optional.empty();
    }
  }
}
