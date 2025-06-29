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

import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.id.IIdentificator;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Repository
public class VirtualFileRedis {
  final Function<String, String> getter;
  final BiFunction<String, String, String> setter;
  final Function<String, Long> remover;
  final ILogger logger;
  @Setter @Getter Long ttl;

  @Autowired
  public VirtualFileRedis(
    Jedis jedis,
    @Value("redisTtl") Long ttl,
    Function<Class<?>, ILogger> loggerProducer
  ) {
    this.ttl = ttl;
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

  public Optional<String> getVirtualFileAsJson(IIdentificator id) {
    var ret = getter.apply("vf:" + id.toLong().toString());
    logger.debug(String.format("Getting Virtual File id %s from Redis", id.toLong().toString()));
    if (ret.equals("nil")) {
      return Optional.empty();
    }
    return Optional.of(ret);
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

  public Boolean addVirtualFileAsJson(IIdentificator id, String jsonVF) {
    var ret = setter.apply("vf:" + id.toLong().toString(), jsonVF);
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

  public Boolean removeVirtualFile(IIdentificator id) {
    remover.apply("vf:" + id.toLong().toString());
    logger.debug(String.format("Adding Virtual File id %s to Redis", id.toLong().toString()));
    return true;
  }

  public Boolean removeParent(IIdentificator id) {
    remover.apply("parent:" + id.toLong().toString());
    logger.debug(String.format("Adding Parent of %s to Redis", id.toLong().toString()));
    return true;
  }

  public Boolean removeChildren(IIdentificator id) {
    remover.apply("children:" + id.toLong().toString());
    logger.debug(String.format("Adding Virtual File id %s to Redis", id.toLong().toString()));
    return true;
  }
}
