package com.salem.backend.mapper;

import com.salem.backend.dto.NoteResponse;
import com.salem.backend.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Makes it a Spring Bean (@Component)
public interface NoteMapper {

    // We only need to map the image URL explicitly because the Entity has 'imageKey'
    // but the DTO expects 'imageUrl'.
    @Mapping(target = "imageUrl", expression = "java(imageUrl)")
    NoteResponse toResponse(Note note, String imageUrl);
}