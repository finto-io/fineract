package io.finto.integration.fineract.usecase.impl.charge;

import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.fineract.sdk.models.PostChargesRequest;
import io.finto.integration.fineract.converter.FineractChargeMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.charge.CreateChargeUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@AllArgsConstructor
@Builder
public class SdkCreateChargeUseCase implements CreateChargeUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractChargeMapper chargeMapper;

    public static class SdkCreateChargeUseCaseBuilder {
        private FineractChargeMapper chargeMapper = FineractChargeMapper.INSTANCE;
    }

    @Override
    public ChargeId createCharge(ChargeCreate request) {
        PostChargesRequest fineractRequest = chargeMapper.chargeCreationFineractRequest(
                request
        );

        return ChargeId.of(
                Objects.requireNonNull(context
                        .getResponseBody(
                                context.chargeApi().createCharge(fineractRequest)
                        )
                        .getResourceId())
        );
    }

}
