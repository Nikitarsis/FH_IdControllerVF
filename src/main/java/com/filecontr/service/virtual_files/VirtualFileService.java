package com.filecontr.service.virtual_files;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.filecontr.repository.virtual_file.postgres.VirtualFilePostgres;
import com.filecontr.repository.virtual_file.redis.VirtualFileRedis;
import com.filecontr.service.server_data.IIdStrategy;
import com.filecontr.service.server_data.IServerData;
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

  protected List<Optional<IVirtualFile>> findVirtualFileInSql(IIdentificator... ids) {
    var list = postgres.getVirtualFileById(fileFactory::createVirtualFileFromSql, ids);
    for (var element : list) {
      if (element.isEmpty()) {
        logger.debug(String.format(
          "Virtual File %s didn't find in SQL",
          element.get().getId().toLong().toString())
        );
      }
    }
    return list;
  }

  protected List<Optional<IVirtualFile>> findVirtualFileInRedis(IIdentificator... ids) {
    var list = redis.getVirtualFileById(fileFactory::createVirtualFileFromJson, ids);
    for (var element : list) {
      if (element.isEmpty()) {
        logger.debug(String.format(
          "Virtual File %s didn't find in Redis",
          element.get().getId().toLong().toString())
        );
      }
    }
    return list;
  }

  private List<Function<IIdentificator[], List<Optional<IVirtualFile>>>> getSearcherFunctions() {
    return List.of(this::findVirtualFileInRedis, this::findVirtualFileInSql);
  }

  @Autowired
  public VirtualFileService(
    @Value("LongtimeDatabase") VirtualFilePostgres postgres,
    @Value("ColdCache") VirtualFileRedis redis,
    IServerData idServer,
    Function<Class<?>, ILogger> loggerProducer
  ) {
    this.postgres = postgres;
    this.redis = redis;
    this.logger = loggerProducer.apply(this.getClass());   

    this.fileFactory = new VirtualFileFactory(idServer::getNextId, loggerProducer, getSearcherFunctions(), getGson());
  }

  public List<Optional<String>> getVirtualFile(Long... idLong) {
    var ids = Arrays.stream(idLong)
      .map(IdFactory::createIdFromLong)
      .toArray(IIdentificator[]::new);
    var virtualFileMap = fileFactory.createVirtualFileById(ids);
    var ret = virtualFileMap.entrySet().stream()
      .filter(a -> a.getValue().isEmpty())
      .map(a -> a.getValue().get())
      .toArray(IVirtualFile[]::new);
    redis.addVirtualFile(ret);
    return Arrays.stream(ret)
      .map(a -> fileFactory.processVirtualFileToJson(a))
      .toList();
  }

  public List<Optional<String>> createVirtualFile(IIdStrategy... requests) {
    var vf = Arrays.stream(requests)
      .map(fileFactory::createNewFileDefault)
      .toArray(IVirtualFile[]::new);
    CompletableFuture.runAsync(() -> postgres.addVirtualFile(vf));
    CompletableFuture.runAsync(() -> redis.addVirtualFile(vf));
    var ret = Arrays.stream(vf).map(a -> fileFactory.processVirtualFileToJson(a)).toList();
    return ret;
  }

  public Boolean setType(Long idLong, String newType) {
    var id = IdFactory.createIdFromLong(idLong);
    CompletableFuture.runAsync(() -> postgres.setType(id, newType));
    CompletableFuture.runAsync(() -> redis.setType(id, newType));
    return true;
  }

  public Boolean deleteVirtualFile(Long... idLong) {
    var ids = Arrays.stream(idLong)
      .map(IdFactory::createIdFromLong)
      .toArray(IIdentificator[]::new);
    CompletableFuture.runAsync(() -> postgres.deleteVirtualFile(ids));
    CompletableFuture.runAsync(() -> redis.deleteVirtualFile(ids));
    return true;
  }
}
