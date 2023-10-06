package io.finto.integration.fineract.usecase.impl.charge;

import io.finto.domain.charge.ChargeCreate;
import io.finto.domain.id.fineract.ChargeId;
import io.finto.fineract.sdk.api.ChargesApi;
import io.finto.fineract.sdk.models.PostChargesRequest;
import io.finto.fineract.sdk.models.PostChargesResponse;
import io.finto.integration.fineract.converter.FineractChargeMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

class SdkCreateChargeUseCaseTest {
    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private ChargesApi chargesApi;
    private FineractChargeMapper chargeMapper;
    private SdkCreateChargeUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        chargeMapper = control.createMock(FineractChargeMapper.class);
        chargesApi = control.createMock(ChargesApi.class);
        useCase = SdkCreateChargeUseCase.builder()
                .context(context)
                .chargeMapper(chargeMapper)
                .build();
    }

    /**
     * Method under test: {@link SdkCreateChargeUseCase#createCharge(ChargeCreate)}
     */
    @Test
    void test_createCharge() {
        ChargeCreate request = control.createMock(ChargeCreate.class);
        PostChargesRequest fineractRequest = control.createMock(PostChargesRequest.class);
        Call<PostChargesResponse> response = control.createMock(Call.class);
        PostChargesResponse responseBody = control.createMock(PostChargesResponse.class);

        expect(chargeMapper.chargeCreationFineractRequest(request)).andReturn(fineractRequest);
        expect(context.chargeApi()).andReturn(chargesApi);
        expect(chargesApi.createCharge(fineractRequest)).andReturn(response);
        expect(context.getResponseBody(response)).andReturn(responseBody);
        expect(responseBody.getResourceId()).andReturn(10L);

        control.replay();

        ChargeId actual = useCase.createCharge(request);

        control.verify();

        assertThat(actual).isEqualTo(ChargeId.of(10L));
    }

}