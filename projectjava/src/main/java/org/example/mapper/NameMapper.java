package org.example.mapper;

import org.example.dto.NameDTO;
import org.example.entity.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NameMapper {
    @Mapping(target = "name", source = "name")
    NameDTO mapWithoutId(Name name);

    @Mapping(target = "name", source = "name")
    Name mapGenerated(org.example.generated.Name generated);

}
