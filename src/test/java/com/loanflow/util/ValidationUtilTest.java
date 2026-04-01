package com.loanflow.util;

import com.loanflow.entity.EmiSchedule;
import com.loanflow.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class ValidationUtilTest {

    // ── ensureEmisPaidInSequence ───────────────────────────────────────

    @Test
    void ensureEmisPaidInSequence_allowsPayment_whenNoPriorUnpaidEmiExists() {
        EmiSchedule emi = new EmiSchedule();
        emi.setInstallmentNumber(3);

        // hasPriorUnpaidEmi = false  →  no exception expected
        assertThatNoException()
                .isThrownBy(() -> ValidationUtil.ensureEmisPaidInSequence(emi, false));
    }

    @Test
    void ensureEmisPaidInSequence_throwsBusinessRuleException_whenPriorUnpaidEmiExists() {
        EmiSchedule emi = new EmiSchedule();
        emi.setInstallmentNumber(3);

        // hasPriorUnpaidEmi = true  →  must reject payment
        assertThatThrownBy(() -> ValidationUtil.ensureEmisPaidInSequence(emi, true))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("3")
                .hasMessageContaining("previous installments must be paid first");
    }

    @Test
    void ensureEmisPaidInSequence_allowsFirstInstallment_whenNoPriorExists() {
        EmiSchedule emi = new EmiSchedule();
        emi.setInstallmentNumber(1);

        // First installment always has no prior EMIs → hasPriorUnpaid = false
        assertThatNoException()
                .isThrownBy(() -> ValidationUtil.ensureEmisPaidInSequence(emi, false));
    }
}
