package com.filecontr.service.server_data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;

public class SimpleServerData implements IServerData {
  final ConcurrentLinkedQueue<IIdentificator> reservedId;
  final Function<Integer, Long[]> reserverId;
  final Supplier<Integer> getterMinSize;
  final Supplier<Integer> getterRequestSize;
  final Supplier<Long> getterWaitingTime;
  final ILogger logger;
  final IServerId serverId;
  Boolean alreadySendRequest;

  public SimpleServerData(
      IServerId serverId,
      Supplier<Integer> getterMinSize,
      Supplier<Integer> getterRequestSize,
      Supplier<Long> getterWaitingTime,
      Function<Integer, Long[]> reserverId,
      Function<Class<?>, ILogger> loggerProducer
    ) {
    this.getterMinSize = getterMinSize;
    this.getterRequestSize = getterRequestSize;
    this.serverId = serverId;
    this.reserverId = reserverId;
    this.getterWaitingTime = getterWaitingTime;
    this.reservedId = new ConcurrentLinkedQueue<>();
    this.logger = loggerProducer.apply(this.getClass());
    requestForId();
  }

  private boolean shouldCallRequest() {
    return reservedId.size() < getterMinSize.get() && !alreadySendRequest;
  }

  private void requestForId() {
    Stream.of(reserverId.apply(getterRequestSize.get()))
      .map(IdFactory::createIdFromLong)
      .peek(reservedId::add).close();
    alreadySendRequest = false;
  }

  @Override
  public synchronized IIdentificator getNextId() {
    if (shouldCallRequest()) {
      alreadySendRequest = true;
      CompletableFuture.runAsync(this::requestForId);
    }
    var ret = reservedId.poll();
    if (ret != null) {
      return ret;
    }
    logger.warn(String.format("Server Data %d doesn't contains reserved Id", serverId.toLong()));
    try {
      Thread.sleep(getterWaitingTime.get());
      ret = reservedId.poll();
      if (ret != null) {
        return ret;
      }
      logger.warn(String.format("!!!Server Data %d doesn't contains reserved Id - Timeout passed", serverId.toLong()));
      Thread.sleep(2*getterWaitingTime.get());
      ret = reservedId.poll();
      if (ret != null) {
        return ret;
      }
      logger.error(String.format("Server %d wasn't able to ", serverId.toLong()));
      throw new RuntimeException("Unable to get Id");
    } catch (InterruptedException e) {
      logger.error(String.format("Undefined exception", serverId.toLong()));
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public IServerId getServerId() {
    return serverId;
  }

}
