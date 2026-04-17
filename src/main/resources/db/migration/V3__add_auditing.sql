-- =============================
-- 1. ADD AUDITING COLUMNS
-- =============================

-- USERS
ALTER TABLE users
ADD COLUMN created_by VARCHAR(50) NULL,
ADD COLUMN updated_at DATETIME(6) NULL,
ADD COLUMN updated_by VARCHAR(50) NULL;

-- BOOKS
ALTER TABLE books
ADD COLUMN created_at DATETIME(6) NULL,
ADD COLUMN created_by VARCHAR(50) NULL,
ADD COLUMN updated_at DATETIME(6) NULL,
ADD COLUMN updated_by VARCHAR(50) NULL;

-- BORROW_RECORDS
ALTER TABLE borrow_records
ADD COLUMN created_at DATETIME(6) NULL,
ADD COLUMN created_by VARCHAR(50) NULL,
ADD COLUMN updated_at DATETIME(6) NULL,
ADD COLUMN updated_by VARCHAR(50) NULL;

-- BORROW_ITEMS
ALTER TABLE borrow_items
ADD COLUMN created_at DATETIME(6) NULL,
ADD COLUMN created_by VARCHAR(50) NULL,
ADD COLUMN updated_at DATETIME(6) NULL,
ADD COLUMN updated_by VARCHAR(50) NULL;

-- FINES
ALTER TABLE fines
ADD COLUMN created_by VARCHAR(50) NULL,
ADD COLUMN updated_at DATETIME(6) NULL,
ADD COLUMN updated_by VARCHAR(50) NULL;

-- 3. OPTIONAL: DEFAULT VALUE FIX (SAFE)
-- Set default created_at nếu cần (tránh null data cũ)
UPDATE users SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE books SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE borrow_records SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE borrow_items SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE fines SET created_by = 'SYSTEM' WHERE created_by IS NULL;