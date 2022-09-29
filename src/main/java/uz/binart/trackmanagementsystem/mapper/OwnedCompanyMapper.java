package uz.binart.trackmanagementsystem.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import uz.binart.trackmanagementsystem.dto.OwnedCompanyListDto;
import uz.binart.trackmanagementsystem.model.OwnedCompany;

public interface OwnedCompanyMapper {

    @Mappings({
            @Mapping(target = "id", source = "ownedCompany.id"),
            @Mapping(target = "name", source = "ownedCompany.name"),
            @Mapping(target = "abbreviation", source = "ownedCompany.abbreviation"),
            @Mapping(target = "city", source = "ownedCompany.city"),
            @Mapping(target = "state", source = "ownedCompany.city"),
    })
    OwnedCompanyListDto fromEntity(OwnedCompany ownedCompany);
}
