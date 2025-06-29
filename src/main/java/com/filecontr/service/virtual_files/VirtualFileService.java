package com.filecontr.service.virtual_files;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.filecontr.repository.virtual_file.postgres.VirtualFilePostgres;
import com.filecontr.repository.virtual_file.redis.VirtualFileRedis;
import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class VirtualFileService {
  final VirtualFilePostgres postgres;
  final VirtualFileRedis redis;
  final VirtualFileFactory fileFactory;
  final ILogger logger;

  private Gson getGson() {
    var gsonBuilder = new GsonBuilder();
    var strategy = new VirtualFileConverter();
    gsonBuilder.registerTypeAdapter(IVirtualFile.class, strategy);
    return gsonBuilder.create(); 
  }

  protected Optional<IVirtualFile> findVirtualFileInSql(IIdentificator id) {
    var sqlResponse = postgres.getVirtualFileById(id);
    if (sqlResponse.size() > 1) {
      logger.warn(String.format("Ambiguous situation: database return %d of id %s", sqlResponse.size(), id.toLong().toString()));
      return Optional.empty();
    }
    if (sqlResponse.isEmpty()) {
      logger.debug(String.format("Virtual File %s didn't find in SQL", id.toLong().toString()));
      return Optional.empty();
    }
    var list = fileFactory.createVirtualFileFromSql(sqlResponse);
    if (sqlResponse.isEmpty()) {
      logger.warn("Sql parsing error");
      return Optional.empty();
    }
    if (list.get().size() != 1) {
      logger.warn(String.format("Ambiguous situation: VirtualFileFactory parsing incorrectly"));
      return Optional.empty();
    }
    return Optional.of(list.get().get(0));
  }

  protected Optional<IVirtualFile> findInRedis(IIdentificator id) {
    return Optional.empty();
  }

  private List<Function<IIdentificator, Optional<IVirtualFile>>> getSearcherFunctions() {
    return List.of(this::findInRedis, this::findVirtualFileInSql);
  }

  @Autowired
  public VirtualFileService(
    VirtualFilePostgres postgres,
    VirtualFileRedis redis,
    Function<Class<?>, ILogger> loggerProducer
  ) {
    this.postgres = postgres;
    this.redis = redis;
    this.logger = loggerProducer.apply(this.getClass());   

    this.fileFactory = new VirtualFileFactory(null, loggerProducer, getSearcherFunctions(), getGson());
  }

  public Optional<String> getVirtualFile(Long idLong) {
    var id = IdFactory.createIdFromLong(idLong);
    var virtualFile = fileFactory.createVirtualFileById(id);
    if (virtualFile.isEmpty()) {
      return Optional.empty();
    }
    var ret = fileFactory.processVirtualFileToJson(virtualFile.get());
    redis.addVirtualFileAsJson(id, ret);
    return Optional.of(ret);
  }
}
