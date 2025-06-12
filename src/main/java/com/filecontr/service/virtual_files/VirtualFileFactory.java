package com.filecontr.service.virtual_files;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.filecontr.utils.adapters.logger.AdapterLoggerFactory;
import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;
import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

@Service
public class VirtualFileFactory {
  private final Deque<Function<IIdentificator, Optional<IContent>>> finderFunctions = new ArrayDeque<Function<IIdentificator, Optional<IContent>>>(); 
  @Autowired IdFactory idProducer;
  ILogger logger = AdapterLoggerFactory.getLogger(this.getClass());

  public Optional<IVirtualFile> getVirtualFileById(IIdentificator id) {
    for (var finder : finderFunctions) {
      Optional<IContent> content = Optional.empty();
      try {
        content = finder.apply(id);
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
      var id = idProducer.getNextId();
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
      var id = idProducer.getNextId();
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
      var id = idProducer.getNextId();
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
