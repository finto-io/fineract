package io.finto.integration.fineract.usecase.impl.customer;

import io.finto.domain.customer.OpeningCustomer;
import io.finto.integration.fineract.converter.FineractCustomerMapper;
import io.finto.integration.fineract.usecase.impl.SdkFineractUseCaseContext;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class SdkCreateCustomerUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractCustomerMapper customerMapper;
    private FindKeyValueDictionaryUseCase dictionaryUseCase;
    private SdkCreateCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        customerMapper = control.createMock(FineractCustomerMapper.class);
        dictionaryUseCase = control.createMock(FindKeyValueDictionaryUseCase.class);
        useCase = SdkCreateCustomerUseCase.builder()
                .context(context)
                .dictionaryUseCase(dictionaryUseCase)
                .customerMapper(customerMapper)
                .build();
    }


    /**
     * Method under test: {@link SdkCreateCustomerUseCase#createCustomer(OpeningCustomer)}
     */
    @Test
    void test_createCustomer_fail_npe() {
        assertThrows(NullPointerException.class, () -> useCase.createCustomer(null));
    }

    /**
     * Method under test: {@link SdkCreateCustomerUseCase#createCustomer(OpeningCustomer)}
     */
    @Test
    @Disabled(value = "https://fintoio.atlassian.net/browse/FIN2-22292")
    void test_createCustomer_success() {
        fail();
    }

}