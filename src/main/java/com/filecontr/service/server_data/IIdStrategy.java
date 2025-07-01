package com.filecontr.service.server_data;

import java.util.Optional;

import com.filecontr.utils.functional_classes.id.IIdentificator;

public interface IIdStrategy {
  Optional<IIdentificator> getParent();
  Optional<String> getType();
}
