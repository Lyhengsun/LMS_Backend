package com.norton.lms_backend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class SequentialBillNumberGenerator {
    // AtomicLong is used to safely increment a counter in a multi-threaded environment
    // within a single JVM instance
    private static AtomicLong sequenceCounter = new AtomicLong(1001); // Start with a professional number

    /**
     * Generates a chronological and sequential bill number.
     * Format: YYYYMMDD-XXXX (e.g., 20251101-1001)
     * @return a unique bill number string
     */
    public static synchronized String generateChronologicalBillNumber() {
        // Use synchronized to ensure thread safety for the date format and counter
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());

        // Get the current sequence value and increment it
        long sequencePart = sequenceCounter.getAndIncrement();

        // Optional: Reset the counter daily if needed, but this requires more complex logic
        // to handle overlaps across application restarts/days.

        return datePart + "-" + sequencePart;
    }
}
