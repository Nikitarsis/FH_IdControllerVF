package com.filecontr.service.virtual_files;

import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;

public interface IVirtualFile {
  IIdentificator getId();
  IContent getContent();
}
