package org.example.mapper;

import javax.annotation.Generated;
import org.example.dto.NameDTO;
import org.example.entity.Name;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-31T19:19:29+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 19.0.1 (Oracle Corporation)"
)
@Component
public class NameMapperImpl implements NameMapper {

    @Override
    public NameDTO mapWithoutId(Name name) {
        if ( name == null ) {
            return null;
        }

        NameDTO nameDTO = new NameDTO();

        if ( name.getName() != null ) {
            nameDTO.setName( Byte.parseByte( name.getName() ) );
        }

        return nameDTO;
    }

    @Override
    public Name mapGenerated(org.example.generated.Name generated) {
        if ( generated == null ) {
            return null;
        }

        Name name = new Name();

        name.setName( generated.getName() );

        return name;
    }
}
