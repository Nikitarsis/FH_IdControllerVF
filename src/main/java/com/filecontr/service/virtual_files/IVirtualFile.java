package com.filecontr.service.virtual_files;

import java.util.Optional;

import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;

public interface IVirtualFile {
  IIdentificator getId();
  Optional<IIdentificator> getParentId();
  IContent getContent();
}
