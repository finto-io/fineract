package io.finto.integration.fineract.converter;

import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.loanproduct.FeeCalcType;
import io.finto.domain.loanproduct.FeeType;
import io.finto.fineract.sdk.models.PostChargesRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractChargeMapper {

    FineractChargeMapper INSTANCE = Mappers.getMapper(FineractChargeMapper.class);

    @Named("toChargeTimeType")
    default Integer toChargeTimeType(FeeType value) {
        switch (value) {
            case FEES:
                return 1;
            case LATE_PAYMENT:
                return 9;
            default:
                return 2;
        }
    }

    @Named("toChargeCalculationType")
    default Integer toChargeCalculationType(FeeCalcType value) {
        if (value == FeeCalcType.FIXED) {
            return 1;
        } else {
            return 2;
        }
    }

    @Named("toPenalty")
    default Boolean toPenalty(FeeType value) {
        return value == FeeType.LATE_PAYMENT;
    }

    @Mapping(target = "chargeAppliesTo", constant = "1")
    @Mapping(target = "amount", source = "feeAmount")
    @Mapping(target = "minCap", source = "fromRange")
    @Mapping(target = "maxCap", source = "toRange")
    @Mapping(target = "chargeTimeType", source = "feeType", qualifiedByName = "toChargeTimeType")
    @Mapping(target = "chargeCalculationType", source = "feeCalcType", qualifiedByName = "toChargeCalculationType")
    @Mapping(target = "penalty", source = "feeType", qualifiedByName = "toPenalty")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "locale", constant = "en")
    PostChargesRequest chargeCreationFineractRequest(ChargeCreate request);
}