package com.filecontr.service.virtual_files;

import java.util.Optional;

import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.content.IContent;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;

public interface IVirtualFile {
  IIdentificator getId();
  Optional<IIdentificator> getParentId();
  IContent getContent();

  public static IVirtualFile getTestVF(IdFactory factory) {
    var id = factory.getNextId();
    var parent = factory.getNextId();
    return new IVirtualFile() {
      @Override
      public IIdentificator getId() {
        return id;
      }

      @Override
      public Optional<IIdentificator> getParentId() {
        return Optional.of(parent);
      }

      @Override
      public IContent getContent() {
        return ContentFactory.createTestContent();
      }
      
    };
  }
}
