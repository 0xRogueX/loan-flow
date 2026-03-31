-- ============================================================
-- LoanFlow Database Schema
-- ============================================================

-- ── Sequences for human-readable business keys ──────────────
CREATE SEQUENCE IF NOT EXISTS application_number_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS loan_number_seq        START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS payment_receipt_seq    START 1 INCREMENT 1;
