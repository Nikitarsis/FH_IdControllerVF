package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class DefaultContent implements IContent {
  Long creationTime;
  ServerPath serverPath;
}
