package com.filecontr.service.virtual_files;

import java.lang.reflect.Type;
import java.util.Optional;

import com.filecontr.utils.functional_classes.content.ContentFactory;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;
import com.filecontr.utils.functional_classes.pathes.file_data.FileData;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class VirtualFileConverter implements JsonSerializer<IVirtualFile>, JsonDeserializer<IVirtualFile> {
  final static String idPseudonym = "id";
  final static String parentIdPseudonym = "parent_id";
  final static String typePseudonym = "type";
  final static String creationTimePseudonym = "creation_time";

  @Override
  public IVirtualFile deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
    var object = json.getAsJsonObject();
    var id = IdFactory.createIdFromLong(object.get(idPseudonym).getAsLong());
    var creationTime = object.get(creationTimePseudonym).getAsLong();
    Optional<String> fileType = Optional.empty();
    Optional<IIdentificator> parentId = Optional.empty();
    if (!object.get(typePseudonym).isJsonNull()) {
      fileType = Optional.of(object.get(typePseudonym).getAsString());
    }
    if (!object.get(parentIdPseudonym).isJsonNull()) {
      parentId = Optional.of(IdFactory.createIdFromLong(object.get(parentIdPseudonym).getAsLong()));
    }
    var fileData = FileData.createFileDataFull(fileType, parentId);
    var content = ContentFactory.createContent(creationTime, fileData);
    return new SimpleVirtualFile(id, content);
  }

  @Override
  public JsonElement serialize(IVirtualFile src, Type type, JsonSerializationContext context) {
    var object = new JsonObject();
    var content = src.getContent();
    object.addProperty(idPseudonym, src.getId().toLong());
    if (content.getFileData().parentId().isPresent()) {
      object.addProperty(parentIdPseudonym, content.getFileData().parentId().get().toLong());
    }
    if (content.getFileData().type().isPresent()) {
      object.addProperty(typePseudonym, content.getFileData().type().get());
    }
    object.addProperty(creationTimePseudonym, content.getCreationTime());
    return object;
  }
  
}
