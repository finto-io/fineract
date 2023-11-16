package io.finto.integration.fineract.usecase.impl;

import io.finto.exceptions.core.FintoApiException;
import io.finto.fineract.sdk.api.CodeValuesApi;
import io.finto.fineract.sdk.models.GetCodeValuesDataResponse;
import io.finto.integration.fineract.converter.FineractDictionaryMapper;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SdkFindCodeValuesUseCaseTest {

    private IMocksControl control;
    private SdkFineractUseCaseContext context;
    private FineractDictionaryMapper dictionaryMapper;
    private SdkFindCodeValuesUseCase sdkFindCodeValuesUseCase;


    @BeforeEach
    void setup() {
        control = createStrictControl();
        context = control.createMock(SdkFineractUseCaseContext.class);
        dictionaryMapper = control.createMock(FineractDictionaryMapper.class);
        sdkFindCodeValuesUseCase = SdkFindCodeValuesUseCase.builder()
                .context(context)
                .dictionaryMapper(dictionaryMapper)
                .build();
    }

    /**
     * Method under test: {@link SdkFindCodeValuesUseCase#findKeyValuesByDictionaryId(Long)}
     */
    @Test
    void test_findKeyValuesByDictionaryId() {
        Long dictionaryId = 13L;
        CodeValuesApi apiMock = control.createMock(CodeValuesApi.class);
        expect(context.codeValuesApi())
                .andReturn(apiMock);
        Call<List<GetCodeValuesDataResponse>> callMock = control.createMock(Call.class);
        expect(apiMock.retrieveAllCodeValues(dictionaryId))
                .andReturn(callMock);
        List<GetCodeValuesDataResponse> responseMock = control.createMock(List.class);
        expect(context.getResponseBody(callMock))
                .andReturn(responseMock);

        Map<Long, String> expected = Map.of(1L, "value1", 7L, "value2");
        expect(dictionaryMapper.toKeyValueMap(responseMock))
                .andReturn(expected);

        control.replay();
        var actual = sdkFindCodeValuesUseCase.findKeyValuesByDictionaryId(dictionaryId);
        control.verify();

        assertEquals(expected, actual);
    }

    /**
     * Method under test: {@link SdkFindCodeValuesUseCase#getValueByKey(Long, Long)}
     */
    @Test
    void test_getValueByKey() {
        Long dictionaryId = 13L;
        Long key = 23L;

        CodeValuesApi apiMock = control.createMock(CodeValuesApi.class);
        expect(context.codeValuesApi())
                .andReturn(apiMock);
        Call<GetCodeValuesDataResponse> callMock = control.createMock(Call.class);
        expect(apiMock.retrieveCodeValue(dictionaryId, key))
                .andReturn(callMock);
        GetCodeValuesDataResponse responseMock = control.createMock(GetCodeValuesDataResponse.class);
        expect(context.getResponseBody(callMock))
                .andReturn(responseMock);

        String expected = "expected";
        expect(responseMock.getName())
                .andReturn(expected);
        control.replay();
        var actual = sdkFindCodeValuesUseCase.getValueByKey(dictionaryId, key);
        control.verify();

        assertEquals(expected, actual);
    }

    /**
     * Method under test: {@link SdkFindCodeValuesUseCase#findKeysByValue(Long, String)}
     */
    @Test
    void test_findKeysByValue() {
        sdkFindCodeValuesUseCase = createMockBuilder(SdkFindCodeValuesUseCase.class)
                .withConstructor(context, dictionaryMapper)
                .addMockedMethods(
                        "findKeyValuesByDictionaryId"
                )
                .createMock(control);
        Long dictionaryId = 13L;
        String value = "value";
        Long expectedKey1 = 1L;

        Map<Long, String> keyValues = Map.of(
                expectedKey1, value,
                13L, "foobar"
        );
        expect(sdkFindCodeValuesUseCase.findKeyValuesByDictionaryId(dictionaryId))
                .andReturn(keyValues);
        control.replay();

        List<Long> expected = List.of(expectedKey1);
        var actual = sdkFindCodeValuesUseCase.findKeysByValue(dictionaryId, value);
        control.verify();

        assertEquals(expected, actual);
    }

    /**
     * Method under test: {@link SdkFindCodeValuesUseCase#findOneKeyByValue(Long, String)}
     */
    @Test
    void test_findOneKeyByValue() {
        sdkFindCodeValuesUseCase = createMockBuilder(SdkFindCodeValuesUseCase.class)
                .withConstructor(context, dictionaryMapper)
                .addMockedMethods(
                        "findKeysByValue"
                )
                .createMock(control);
        Long dictionaryId = 13L;
        String value = "value";

        List<Long> list1 = null;
        expect(sdkFindCodeValuesUseCase.findKeysByValue(dictionaryId, value))
                .andReturn(list1);
        Optional<Long> expected1 = Optional.empty();
        control.replay();
        var actual1 = sdkFindCodeValuesUseCase.findOneKeyByValue(dictionaryId, value);
        control.verify();
        assertEquals(expected1, actual1);

        control.reset();

        List<Long> list2 = List.of();
        expect(sdkFindCodeValuesUseCase.findKeysByValue(dictionaryId, value))
                .andReturn(list2);
        Optional<Long> expected2 = Optional.empty();
        control.replay();
        var actual2 = sdkFindCodeValuesUseCase.findOneKeyByValue(dictionaryId, value);
        control.verify();
        assertEquals(expected2, actual2);

        control.reset();

        List<Long> list3 = List.of(1L, 3L);
        expect(sdkFindCodeValuesUseCase.findKeysByValue(dictionaryId, value))
                .andReturn(list3);
        control.replay();
        assertThrows(FintoApiException.class, () -> sdkFindCodeValuesUseCase.findOneKeyByValue(dictionaryId, value));
        control.verify();

        control.reset();

        List<Long> list4 = List.of(1L);
        expect(sdkFindCodeValuesUseCase.findKeysByValue(dictionaryId, value))
                .andReturn(list4);
        Optional<Long> expected4 = Optional.of(1L);
        control.replay();
        var actual4 = sdkFindCodeValuesUseCase.findOneKeyByValue(dictionaryId, value);
        control.verify();
        assertEquals(expected4, actual4);

    }

    /**
     * Method under test: {@link SdkFindCodeValuesUseCase#getOneKeyByValue(Long, String)}
     */
    @Test
    void test_getOneKeyByValue() {
        sdkFindCodeValuesUseCase = createMockBuilder(SdkFindCodeValuesUseCase.class)
                .withConstructor(context, dictionaryMapper)
                .addMockedMethods(
                        "findOneKeyByValue"
                )
                .createMock(control);
        Long dictionaryId = 13L;
        String value = "value";

        Long expected = 17L;
        expect(sdkFindCodeValuesUseCase.findOneKeyByValue(dictionaryId, value))
                .andReturn(Optional.of(expected));

        control.replay();
        var actual = sdkFindCodeValuesUseCase.getOneKeyByValue(dictionaryId, value);
        control.verify();

        assertEquals(expected, actual);

        control.reset();

        expect(sdkFindCodeValuesUseCase.findOneKeyByValue(dictionaryId, value))
                .andReturn(Optional.empty());
        control.replay();
        assertThrows(FintoApiException.class, () ->sdkFindCodeValuesUseCase.getOneKeyByValue(dictionaryId, value));
        control.verify();
    }


}