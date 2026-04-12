-- =============================
-- FIX created_at FROM business date
-- =============================

-- BORROW_RECORDS
UPDATE borrow_records
SET
    created_at = borrowed_at,
    updated_at = COALESCE(updated_at, borrowed_at)
WHERE created_at IS NULL;

-- BORROW_ITEMS (nếu có due_date hoặc created logic riêng thì chỉnh lại)
UPDATE borrow_items
SET
    created_at = NOW(),
    updated_at = COALESCE(updated_at, NOW())
WHERE created_at IS NULL;

-- BOOKS
UPDATE books
SET
    created_at = NOW(),
    updated_at = COALESCE(updated_at, NOW())
WHERE created_at IS NULL;

-- USERS
UPDATE users
SET
    created_at = NOW(),
    updated_at = COALESCE(updated_at, NOW())
WHERE created_at IS NULL;

-- FINES
UPDATE fines
SET
    created_at = NOW(),
    updated_at = COALESCE(updated_at, NOW())
WHERE created_at IS NULL;

-- =============================
-- FIX created_by (fallback)
-- =============================

UPDATE borrow_records SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE borrow_items SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE books SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE users SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE fines SET created_by = 'SYSTEM' WHERE created_by IS NULL;

-- =============================
-- OPTIONAL: FIX updated_by
-- =============================

UPDATE borrow_records SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;
UPDATE borrow_items SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;
UPDATE books SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;
UPDATE users SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;
UPDATE fines SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;