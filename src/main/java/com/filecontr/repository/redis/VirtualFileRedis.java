package com.filecontr.repository.redis;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.id.IIdentificator;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class VirtualFileRedis {
  final Function<String, String> getterVirtualFile;
  final Function<String, String> getterParents;
  final BiFunction<String, String, String> setterVirtualFile;
  final BiFunction<String, String, String> setterParents;
  final ILogger logger;
  Long ttl;

  public VirtualFileRedis(
    Jedis jedis,
    Long ttl,
    Function<Class<?>, ILogger> loggerProducer
  ) {
    this.ttl = ttl;
    var params = new SetParams();
    params.pxAt(ttl);
    this.getterVirtualFile = (String key) -> {
      return jedis.get("vf:" + key);
    };
    this.getterParents = (String key) -> {
      return jedis.get("parent:" + key);
    };
    this.setterVirtualFile = (String key, String value) -> {
      return jedis.set("vf:" + key, value, params);
    };
    this.setterParents = (String key, String value) -> {
      return jedis.set("parent:" + key, value, params);
    };
    this.logger = loggerProducer.apply(this.getClass());
  }

  public Optional<String> getVirtualFileAsJson(IIdentificator id) {
    var ret = getterVirtualFile.apply(id.toLong().toString());
    logger.debug(String.format("Getting Virtual File id %s from Redis", id.toLong().toString()));
    if (ret.equals("nil")) {
      return Optional.empty();
    }
    return Optional.of(ret);
  }

  public Boolean addVirtualFileAsJson(IIdentificator id, String jsonVF) {
    var ret = setterVirtualFile.apply(id.toLong().toString(), jsonVF);
    logger.debug(String.format("Adding Virtual File id %s to Redis", id.toLong().toString()));
    if (ret == null) {
      return false;
    }
    if (ret == "OK") {
      return true;
    }
    logger.warn("Incorrect return of redis set");
    return false;
  }
}
