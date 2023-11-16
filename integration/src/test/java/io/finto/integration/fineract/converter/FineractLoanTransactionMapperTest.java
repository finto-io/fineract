package io.finto.integration.fineract.converter;

import io.finto.domain.bnpl.enums.LoanTransactionType;
import io.finto.domain.bnpl.transaction.PaymentTypeOption;
import io.finto.domain.bnpl.transaction.Transaction;
import io.finto.domain.bnpl.transaction.TransactionSubmit;
import io.finto.domain.bnpl.transaction.TransactionTemplate;
import io.finto.domain.id.fineract.TransactionId;
import io.finto.fineract.sdk.models.GetLoanTransactionRelation;
import io.finto.fineract.sdk.models.GetLoansCurrency;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactionsTemplateResponse;
import io.finto.fineract.sdk.models.GetLoansLoanIdTransactionsTransactionIdResponse;
import io.finto.fineract.sdk.models.GetLoansType;
import io.finto.fineract.sdk.models.GetPaymentTypesPaymentTypeIdResponse;
import io.finto.fineract.sdk.models.PaymentDetailData;
import io.finto.fineract.sdk.models.PaymentType;
import io.finto.fineract.sdk.models.PostLoansLoanIdTransactionsRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FineractLoanTransactionMapperTest {

    private final FineractLoanTransactionMapper mapper = FineractLoanTransactionMapper.INSTANCE;

    @Test
    void testFromLocalDate() {
        var localDate = LocalDate.now();
        assertEquals(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("en"))), mapper.fromLocalDate(localDate));
        assertNull(mapper.fromLocalDate(null));
    }

    @Test
    void testLoanTransactionSubmissionOther() {
        var localDate = LocalDate.now();
        assertEquals(generatePostLoansLoanIdTransactionsRequestForOthers(localDate),
                mapper.loanTransactionSubmissionOther(generateTransactionSubmit(localDate)));
    }

    private PostLoansLoanIdTransactionsRequest generatePostLoansLoanIdTransactionsRequestForOthers(LocalDate localDate) {
        return PostLoansLoanIdTransactionsRequest.builder()
                .transactionDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("en"))))
                .transactionAmount(new BigDecimal("1"))
                .paymentTypeId(2L)
                .locale("en")
                .dateFormat("yyyy-MM-dd")
                .build();
    }

    @Test
    void testLoanTransactionSubmissionForeclosure() {
        var localDate = LocalDate.now();
        assertEquals(generatePostLoansLoanIdTransactionsRequestForForeclosure(localDate),
                mapper.loanTransactionSubmissionForeclosure(generateTransactionSubmit(localDate)));
    }

    private PostLoansLoanIdTransactionsRequest generatePostLoansLoanIdTransactionsRequestForForeclosure(LocalDate localDate) {
        return PostLoansLoanIdTransactionsRequest.builder()
                .transactionDate(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("en"))))
                .locale("en")
                .dateFormat("yyyy-MM-dd")
                .build();
    }

    private TransactionSubmit generateTransactionSubmit(LocalDate localDate) {
        return TransactionSubmit.builder()
                .type(LoanTransactionType.PREPAY_LOAN)
                .date(localDate)
                .amount(new BigDecimal("1"))
                .paymentTypeId(2L)
                .build();
    }

    @Test
    void test_toDomain_transaction() {
        GetLoansCurrency getLoansCurrency = GetLoansCurrency.builder()
                .code("code") 
                .decimalPlaces(3) 
                .displayLabel("displayLabel") 
                .displaySymbol("displaySymbol") 
                .name("name") 
                .nameCode("nameCode") 
                .build();
        PaymentType paymentType = PaymentType.builder()
                .codeName("codeName") 
                .description("description") 
                .id(12L) 
                .isCashPayment(false) 
                .isSystemDefined(true) 
                .name("name") 
                ._new(false) 
                .position(3887L) 
                .build();

        PaymentDetailData paymentDetailData = PaymentDetailData.builder()
                .accountNumber("accountNumber") 
                .bankNumber("bankNumber") 
                .checkNumber("checkNumber") 
                .id(74L) 
                .paymentType(paymentType) 
                .receiptNumber("receiptNumber") 
                .routingCode("routingCode") 
                .build();
        GetLoanTransactionRelation getLoanTransactionRelation = GetLoanTransactionRelation.builder()
                .amount(123.1) 
                .fromLoanTransaction(199L) 
                .paymentType("paymentType") 
                .relationType("relationType") 
                .toLoanCharge(43L) 
                .toLoanTransaction(57L) 
                .build();
        GetLoansType getLoansType = GetLoansType.builder()
                .code("code") 
                .contra(false) 
                .value("value") 
                .description("description") 
                .disbursement(true) 
                .externalId("externalId") 
                .externalLoanId("externalLoanId") 
                .id(132L) 
                .loanId(3112L) 
                .recoveryRepayment(false) 
                .repayment(true) 
                .repaymentAtDisbursement(false) 
                .waiveCharges(true) 
                .waiveInterest(false) 
                .writeOff(true)
                .build();
        GetLoansLoanIdTransactionsTransactionIdResponse getLoansLoanIdTransactionsTransactionIdResponse = GetLoansLoanIdTransactionsTransactionIdResponse.builder()
                .amount(new BigDecimal("123.000")) 
                .currency(getLoansCurrency) 
                .date(LocalDate.of(2023, 1, 1)) 
                .externalId("externalId") 
                .feeChargesPortion(new BigDecimal("321.001")) 
                .id(7L) 
                .interestPortion(new BigDecimal("22.776")) 
                .loanChargePaidByList(null) 
                .manuallyReversed(false) 
                .netDisbursalAmount(new BigDecimal("554.001")) 
                .outstandingLoanBalance(new BigDecimal("98.001")) 
                .overpaymentPortion(new BigDecimal("87.001")) 
                .paymentDetailData(paymentDetailData) 
                .penaltyChargesPortion(new BigDecimal("31.001")) 
                .possibleNextRepaymentDate(LocalDate.of(2023, 1, 2)) 
                .principalPortion(new BigDecimal("93.1")) 
                .reversalExternalId("reversalExternalId") 
                .reversedOnDate(LocalDate.of(2023, 1, 6)) 
                .submittedOnDate(LocalDate.of(2023, 1, 10)) 
                .transactionRelations(Set.of(getLoanTransactionRelation)) 
                .type(getLoansType) 
                .unrecognizedIncomePortion(new BigDecimal("2.1")) 
                .build();

        Transaction expected = Transaction.builder()
                .type("value") 
                .currency("code") 
                .otherIncomePortion(new BigDecimal("2.1")) 
                .isReversed(false) 
                .reversalDate(LocalDate.of(2023, 1, 6)) 
                .id(TransactionId.of(7L)) 
                .date(LocalDate.of(2023, 1, 1)) 
                .amount(new BigDecimal("123.000")) 
                .principalPortion(new BigDecimal("93.1")) 
                .interestPortion(new BigDecimal("22.776")) 
                .feeChargesPortion(new BigDecimal("321.001")) 
                .penaltyChargesPortion(new BigDecimal("31.001")) 
                .overpaymentPortion(new BigDecimal("87.001")) 
                .outstandingLoanBalance(new BigDecimal("98.001")) 
                .build();

        Transaction actual = mapper.toDomainBnplTransaction(getLoansLoanIdTransactionsTransactionIdResponse);

        assertEquals(expected, actual);
        assertEquals("ac767cbad41840893a61feac69f89ec9", DigestUtils.md5Hex(actual.toString()), "Model change detected");
    }

    @Test
    void testToCommand() {
        assertEquals("prepayLoan", mapper.toCommand(LoanTransactionType.PREPAY_LOAN));
        assertEquals("repayment", mapper.toCommand(LoanTransactionType.REPAYMENT));
        assertEquals("foreclosure", mapper.toCommand(LoanTransactionType.FORECLOSURE));
        assertNull(mapper.toCommand(null));
    }

    @Test
    void testToDomainBnplTransactionTemplate() {
        var localDate = LocalDate.now();
        assertEquals(generateTransactionTemplate(localDate),
                mapper.toDomainBnplTransactionTemplate(generateGetLoansLoanIdTransactionsTemplateResponse(localDate)));
    }

    private TransactionTemplate generateTransactionTemplate(LocalDate localDate) {
        return TransactionTemplate.builder()
                .date(localDate)
                .amount(new BigDecimal("1"))
                .currency("code")
                .principalPortion(new BigDecimal("2"))
                .interestPortion(new BigDecimal("3"))
                .feeChargesPortion(new BigDecimal("4"))
                .penaltyChargesPortion(new BigDecimal("5"))
                .paymentTypeOptions(List.of(PaymentTypeOption.builder()
                        .paymentTypeId(7L)
                        .paymentTypeName("name")
                        .paymentTypeDescription("description")
                        .isCashPayment(true)
                        .build()))
                .build();
    }

    private GetLoansLoanIdTransactionsTemplateResponse generateGetLoansLoanIdTransactionsTemplateResponse(LocalDate localDate) {
        return GetLoansLoanIdTransactionsTemplateResponse.builder()
                .date(localDate)
                .currency(GetLoansCurrency.builder()
                        .code("code")
                        .build())
                .amount(new BigDecimal("1"))
                .principalPortion(new BigDecimal("2"))
                .interestPortion(new BigDecimal("3"))
                .feeChargesPortion(new BigDecimal("4"))
                .penaltyChargesPortion(new BigDecimal("5"))
                .paymentTypeOptions(List.of(GetPaymentTypesPaymentTypeIdResponse.builder()
                                .codeName("codeName")
                                .name("name")
                                .description("description")
                                .position(6)
                                .id(7L)
                                .isSystemDefined(true)
                                .isCashPayment(true)
                        .build()))
                .build();
    }

}