package com.filecontr.repository.virtual_files;

import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class SimpleVirtualFile implements IVirtualFile {
  IIdentificator id;
  IContent content;
}