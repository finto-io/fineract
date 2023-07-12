package io.finto.integration.fineract.usecase.impl;

import io.finto.exceptions.core.FintoApiException;
import io.finto.integration.fineract.converter.FineractDictionaryMapper;
import io.finto.usecase.customer.FindKeyValueDictionaryUseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
public class SdkFindCodeValuesUseCase implements FindKeyValueDictionaryUseCase {

    @NonNull
    private final SdkFineractUseCaseContext context;
    @NonNull
    private final FineractDictionaryMapper dictionaryMapper;

    public static class SdkFindCodeValuesUseCaseBuilder {
        private FineractDictionaryMapper dictionaryMapper = FineractDictionaryMapper.INSTANCE;
    }

    @Override
    public Map<Long, String> findKeyValuesByDictionaryId(Long dictionaryId) {
        var call = context.codeValuesApi().retrieveAllCodeValues(dictionaryId);
        return dictionaryMapper.toKeyValueMap(context.getResponseBody(call));
    }

    @Override
    public String getValueByKey(Long dictionaryId, Long key) {
        var call = context.codeValuesApi().retrieveCodeValue(dictionaryId, key);
        return context.getResponseBody(call).getName();
    }

    @Override
    public List<Long> findKeysByValue(Long dictionaryId, String value) {
        return findKeyValuesByDictionaryId(dictionaryId).entrySet().stream()
                .filter(x -> x.getValue().equalsIgnoreCase(value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Long> findOneKeyByValue(Long dictionaryId, String value) {
        var res = findKeysByValue(dictionaryId, value);
        if (res == null || res.isEmpty()) {
            return Optional.empty();
        }
        if (res.size() > 1){
            throw new FintoApiException(String.format("Dictionary with id=%d contains non-unique value=%s", dictionaryId, value));
        }
        return Optional.of(res.get(0));
    }

    @Override
    public Long getOneKeyByValue(Long dictionaryId, String value) {
        return findOneKeyByValue(dictionaryId, value)
                .orElseThrow(() -> new FintoApiException(String.format("Dictionary with id=%d does not contain value=%s", dictionaryId, value)));
    }

}
