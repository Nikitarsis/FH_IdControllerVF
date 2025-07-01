package com.filecontr.repository.virtual_file;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.filecontr.service.virtual_files.IVirtualFile;
import com.filecontr.utils.functional_classes.id.IIdentificator;

public interface IVirtualFileRepository<T> {
  public List<Optional<IVirtualFile>> getVirtualFileById(Function<T, Optional<IVirtualFile>> converter, IIdentificator... ids);
  public Boolean addVirtualFile(IVirtualFile... files);
  public Boolean setType(IIdentificator id, String type);
  public Boolean deleteVirtualFile(IIdentificator... ids);
}
