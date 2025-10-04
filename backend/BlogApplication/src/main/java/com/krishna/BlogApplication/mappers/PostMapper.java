package com.krishna.BlogApplication.mappers;


import com.krishna.BlogApplication.domain.CreatePostRequest;
import com.krishna.BlogApplication.domain.UpdatePostRequest;
import com.krishna.BlogApplication.domain.dtos.CreatePostRequestDto;
import com.krishna.BlogApplication.domain.dtos.PostDto;
import com.krishna.BlogApplication.domain.dtos.UpdatePostRequestDto;
import com.krishna.BlogApplication.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "status", source = "status")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto dto);

    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto dto);

}
