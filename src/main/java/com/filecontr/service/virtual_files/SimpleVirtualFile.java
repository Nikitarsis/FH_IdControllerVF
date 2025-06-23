package com.filecontr.service.virtual_files;

import java.util.Optional;

import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class SimpleVirtualFile implements IVirtualFile {
  IIdentificator id;
  Optional<IIdentificator> parentId;
  IContent content;
}