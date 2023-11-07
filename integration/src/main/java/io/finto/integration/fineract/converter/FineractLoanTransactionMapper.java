package io.finto.integration.fineract.converter;

import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactionsTransactionIdResponse;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static io.finto.fineract.sdk.Constants.DATE_FORMATTER;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractLoanTransactionMapper {

    FineractLoanTransactionMapper INSTANCE = Mappers.getMapper(FineractLoanTransactionMapper.class);

    @Named("fromLocalDate")
    default String fromLocalDate(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.format(DATE_FORMATTER);
    }

    @Mapping(target = "transactionDate", source = "date", qualifiedByName = "fromLocalDate")
    @Mapping(target = "transactionAmount", source = "amount")
    @Mapping(target = "paymentTypeId", source = "paymentTypeId")
    @Mapping(target = "locale", constant = "en")
    @Mapping(target = "dateFormat", expression = "java(io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN)")
    PostLoansLoanIdTransactionsRequest loanTransactionSubmissionOther(TransactionSubmit request);

    @Mapping(target = "transactionDate", source = "date", qualifiedByName = "fromLocalDate")
    @Mapping(target = "locale", constant = "en")
    @Mapping(target = "dateFormat", expression = "java(io.finto.fineract.sdk.Constants.DATE_FORMAT_PATTERN)")
    @Mapping(target = "paymentTypeId", expression = "java(null)")
    PostLoansLoanIdTransactionsRequest loanTransactionSubmissionForeclosure(TransactionSubmit request);

    @Mapping(target = "type", source = "type.value")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "otherIncomePortion", source = "unrecognizedIncomePortion")
    @Mapping(target = "isReversed", source = "manuallyReversed")
    @Mapping(target = "reversalDate", source = "reversedOnDate")
    Transaction toDomain(GetLoansLoanIdTransactionsTransactionIdResponse loanTransaction);

}