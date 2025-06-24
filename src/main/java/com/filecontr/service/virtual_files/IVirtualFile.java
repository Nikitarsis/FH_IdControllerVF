package com.filecontr.service.virtual_files;

import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;

public interface IVirtualFile {
  IIdentificator getId();
  IContent getContent();

  public static IVirtualFile getTestVF(IdFactory factory) {
    var id = factory.getNextId();
    return new IVirtualFile() {
      @Override
      public IIdentificator getId() {
        return id;
      }

      @Override
      public IContent getContent() {
        return ContentFactory.createTestContent();
      }
      
    };
  }
}
