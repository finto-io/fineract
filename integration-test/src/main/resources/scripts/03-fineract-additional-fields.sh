psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$FINERACT_TENANT_DEFAULT_DB_NAME" <<-EOSQL

INSERT INTO public.x_registered_table(registered_table_name, application_table_name, entity_subtype, category)
VALUES('account_fields', 'm_savings_account', 'additional_fields', 200);

CREATE TABLE public.account_fields (
  savings_account_id bigint NOT NULL,
  iban varchar(50) DEFAULT NULL,
  external_integration_success boolean DEFAULT NULL,
  external_source varchar(50) DEFAULT NULL,
  integration_failure_type varchar(255) DEFAULT NULL,
  swift varchar(255) DEFAULT NULL,
  external_account_number varchar(255) DEFAULT NULL,
  external_account_name varchar(255) DEFAULT NULL,
  external_branch varchar(255) DEFAULT NULL,
  PRIMARY KEY (savings_account_id),
  CONSTRAINT fk_account_fields_savings_account_id FOREIGN KEY (savings_account_id) REFERENCES m_savings_account (id)  ON DELETE CASCADE
);

ALTER TABLE public.account_fields OWNER TO postgres;


EOSQL
