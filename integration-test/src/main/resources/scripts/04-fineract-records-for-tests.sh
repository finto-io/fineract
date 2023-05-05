psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$FINERACT_TENANT_DEFAULT_DB_NAME" <<-EOSQL

INSERT INTO public.m_savings_product
(id, name, short_name, description, deposit_type_enum, currency_code, currency_digits, currency_multiplesof, nominal_annual_interest_rate, interest_compounding_period_enum, interest_posting_period_enum, interest_calculation_type_enum, interest_calculation_days_in_year_type_enum, min_required_opening_balance, lockin_period_frequency, lockin_period_frequency_enum, accounting_type, withdrawal_fee_amount, withdrawal_fee_type_enum, withdrawal_fee_for_transfer, allow_overdraft, overdraft_limit, nominal_annual_interest_rate_overdraft, min_overdraft_for_interest_calculation, min_required_balance, enforce_min_required_balance, min_balance_for_interest_calculation, withhold_tax, tax_group_id, is_dormancy_tracking_active, days_to_inactive, days_to_dormancy, days_to_escheat, max_allowed_lien_limit, is_lien_allowed)
VALUES(1, 'Regular Account', 'RG01', 'Regular Account', 100, 'JOD', 3, 0, 0.000000, 1, 4, 1, 365, NULL, NULL, NULL, 1, NULL, NULL, true, false, 0.000000, 0.000000, 0.000000, 0.000000, false, NULL, false, NULL, NULL, NULL, NULL, NULL, NULL, false);

INSERT INTO public.m_client
(id, account_no, external_id, status_enum, sub_status, activation_date, office_joining_date, office_id, transfer_to_office_id, staff_id, firstname, middlename, lastname, fullname, display_name, mobile_no, is_staff, gender_cv_id, date_of_birth, image_id, closure_reason_cv_id, closedon_date, updated_by, updated_on, submittedon_date, activatedon_userid, closedon_userid, default_savings_product, default_savings_account, client_type_cv_id, client_classification_cv_id, reject_reason_cv_id, rejectedon_date, rejectedon_userid, withdraw_reason_cv_id, withdrawn_on_date, withdraw_on_userid, reactivated_on_date, reactivated_on_userid, legal_form_enum, reopened_on_date, reopened_by_userid, email_address, proposed_transfer_date, created_on_utc, created_by, last_modified_by, last_modified_on_utc)
VALUES(1, '000000002', NULL, 300, NULL, '2000-03-04', '2000-03-04', 1, NULL, NULL, NULL, NULL, NULL, NULL, 'Client of group', NULL, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2023-04-13 13:47:41.832', 1, 1, '2023-04-13 13:47:41.832');
EOSQL
