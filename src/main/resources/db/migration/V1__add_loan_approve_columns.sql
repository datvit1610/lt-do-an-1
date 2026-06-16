-- Thêm các cột phục vụ duyệt phiếu mượn
ALTER TABLE loans ADD COLUMN approve_status INTEGER DEFAULT 0;
ALTER TABLE loans ADD COLUMN approved_by VARCHAR;
ALTER TABLE loans ADD COLUMN approved_date TIMESTAMP;
