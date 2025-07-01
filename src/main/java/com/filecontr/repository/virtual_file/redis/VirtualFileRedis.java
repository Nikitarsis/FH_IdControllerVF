package com.filecontr.repository.virtual_file.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.filecontr.repository.virtual_file.IVirtualFileRepository;
import com.filecontr.service.virtual_files.IVirtualFile;
import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Repository("ColdCache")
public class VirtualFileRedis implements IVirtualFileRepository<String>{
  final Function<String, String> getter;
  final BiFunction<String, String, String> setter;
  final Function<String, Long> remover;
  final ILogger logger;
  final Gson gson;
  @Setter @Getter Long ttl;

  @Autowired
  public VirtualFileRedis(
    Jedis jedis,
    Gson gson,
    @Value("redisTtl") Long ttl,
    Function<Class<?>, ILogger> loggerProducer
  ) {
    this.ttl = ttl;
    this.gson = gson;
    var params = new SetParams();
    params.pxAt(ttl);
    this.getter = (String key) -> {
      return jedis.get(key);
    };
    this.setter = (String key, String value) -> {
      return jedis.set(key, value, params);
    };
    this.remover = (String key) -> {
      return jedis.del(key);
    };
    this.logger = loggerProducer.apply(this.getClass());
  }

  @Override
  public List<Optional<IVirtualFile>> getVirtualFileById(Function<String, Optional<IVirtualFile>> converter,IIdentificator... id) {
    return Arrays.stream(id)
      .peek(a -> logger.debug(String.format("Getting Virtual File id %s from Redis", a.toLong().toString())))
      .map(a -> getter.apply("vf:" + a.toLong().toString()))
      .map(converter::apply)
      .toList();
  }

  public Optional<String> getParent(IIdentificator id) {
    var ret = getter.apply("parent:" + id.toLong().toString());
    logger.debug(String.format("Getting Parent of %s from Redis", id.toLong().toString()));
    if (ret.equals("nil")) {
      return Optional.empty();
    }
    return Optional.of(ret);
  }

  public Optional<List<String>> getChildren(IIdentificator id) {
    var ret = getter.apply("children:" + id.toLong().toString()).split(",");
    logger.debug(String.format("Getting children of %s from Redis", id.toLong().toString()));
    if (ret[0].equals("nil")) {
      return Optional.empty();
    }
    return Optional.of(List.of(ret));
  }

  @Override
  public Boolean addVirtualFile(IVirtualFile... files) {
    return !Arrays.stream(files)
      .peek(a -> logger.debug(String.format("Adding Virtual File id %s to Redis", a.getId().toLong().toString())))
      .map(file -> setter.apply("vf:" + file.getId().toLong().toString(), gson.toJson(file)))
      .anyMatch(file -> file == null); 
  }

  public Boolean addParent(IIdentificator id, IIdentificator parent) {
    var ret = setter.apply("parent:" + id.toLong().toString(), parent.toLong().toString());
    logger.debug(String.format("Adding Parent of %s to Redis", id.toLong().toString()));
    if (ret == null) {
      return false;
    }
    if (ret == "OK") {
      return true;
    }
    logger.warn("Incorrect return of redis set");
    return false;
  }

  public Boolean addChildren(IIdentificator id, IIdentificator... children) {
    var childrenStr = Arrays.stream(children).map(a -> a.toLong().toString()).collect(Collectors.joining(","));
    var ret = setter.apply("children:" + id.toLong().toString(), childrenStr);
    logger.debug(String.format("Adding Children ids [%s] of file %s to Redis", childrenStr, id.toLong().toString()));
    if (ret == null) {
      return false;
    }
    if (ret == "OK") {
      return true;
    }
    logger.warn("Incorrect return of redis set");
    return false;
  }

  @Override
  public Boolean deleteVirtualFile(IIdentificator... ids) {
    for (var id: ids) {
      removeVirtualFile(id);
      removeChildren(id);
      removeParent(id);
    }
    return true;
  }

  public Boolean removeVirtualFile(IIdentificator id){
    remover.apply("vf:" + id.toLong().toString());
    logger.debug(String.format("Removing Virtual File of %s from Redis", id.toLong().toString()));
    return true;
  }

  public Boolean removeParent(IIdentificator id) {
    remover.apply("parent:" + id.toLong().toString());
    logger.debug(String.format("Removing Parent of id %s from Redis", id.toLong().toString()));
    return true;
  }

  public Boolean removeChildren(IIdentificator id) {
    remover.apply("children:" + id.toLong().toString());
    logger.debug(String.format("Removing Childrens of id %s to Redis", id.toLong().toString()));
    return true;
  }

  @Override
  public Boolean setType(IIdentificator id, String type) {
    deleteVirtualFile(id);
    logger.debug(String.format("Removing file id %s due to incorrect cache.", id.toLong().toString()));
    return false;
  }
}
