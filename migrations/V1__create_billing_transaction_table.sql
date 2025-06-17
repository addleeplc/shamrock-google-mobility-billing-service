/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

CREATE TABLE billing_transaction (
    booking_id UUID PRIMARY KEY,
    booking_date TIMESTAMP,
    booking_completed_date TIMESTAMP,
    response_code SMALLINT,
    response_message TEXT,
    retries SMALLINT,
    processed_ts TIMESTAMP,
    create_ts TIMESTAMP
)