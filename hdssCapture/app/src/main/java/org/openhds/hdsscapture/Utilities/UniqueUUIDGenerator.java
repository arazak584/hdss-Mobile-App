package org.openhds.hdsscapture.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import java.security.MessageDigest;
import java.util.UUID;

public class UniqueUUIDGenerator {
    private static final String PREFS_NAME = "uuid_generator";
    private static final String KEY_DEVICE_PREFIX = "device_prefix";
    private static final String KEY_COUNTER = "counter";
    private static long counter = 0;
    private static String devicePrefix = null;

    /**
     * Initialize the generator with app context
     */
    public static void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Get or create persistent device prefix
        devicePrefix = prefs.getString(KEY_DEVICE_PREFIX, null);
        if (devicePrefix == null) {
            devicePrefix = generateDevicePrefix(context);
            prefs.edit().putString(KEY_DEVICE_PREFIX, devicePrefix).apply();
        }

        // Load counter
        counter = prefs.getLong(KEY_COUNTER, 0);
    }

    /**
     * Generate a unique ID with pattern: ff(2) + PREFIX(6) + TIMESTAMP(8) + COUNTER(8) + RANDOM(8) = 32 chars
     */
    public static synchronized String generate(Context context) {
        if (devicePrefix == null) {
            init(context);
        }

        StringBuilder uuid = new StringBuilder(32);

        // Fixed prefix "ff" (2 chars)
        uuid.append("ff");

        // Device prefix (6 chars) - force exactly 6 chars
        String devPrefix = devicePrefix.length() > 6
                ? devicePrefix.substring(0, 6)
                : String.format("%-6s", devicePrefix).replace(' ', '0');
        uuid.append(devPrefix);

        // Timestamp (8 chars) - force exactly 8 chars
        long timestamp = System.currentTimeMillis();
        String timeStr = String.format("%08x", (int)(timestamp & 0xFFFFFFFFL));
        uuid.append(timeStr.substring(Math.max(0, timeStr.length() - 8)));

        // Counter (8 chars) - force exactly 8 chars
        String counterStr = String.format("%08x", (int)(counter & 0xFFFFFFFFL));
        uuid.append(counterStr.substring(Math.max(0, counterStr.length() - 8)));

        // Random component (8 chars) - force exactly 8 chars
        String randomStr = String.format("%08x", UUID.randomUUID().hashCode() & 0xFFFFFFFFL);
        uuid.append(randomStr.substring(Math.max(0, randomStr.length() - 8)));

        // Increment and persist counter
        counter++;
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_COUNTER, counter)
                .apply();

        // Ensure exactly 32 characters
        String result = uuid.toString().toLowerCase();
        if (result.length() > 32) {
            result = result.substring(0, 32);
        }

        return result;
    }

    /**
     * Generate a unique 6-char prefix for this device
     */
    private static String generateDevicePrefix(Context context) {
        try {
            // Use Android ID (unique per app install)
            String androidId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );

            // Hash it to get consistent 6-char hex
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(androidId.getBytes());

            // Take first 3 bytes (6 hex chars)
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                hex.append(String.format("%02x", digest[i] & 0xFF));
            }

            return hex.toString();

        } catch (Exception e) {
            // Fallback: generate random prefix and persist it
            return String.format("%06x", UUID.randomUUID().hashCode() & 0xFFFFFF);
        }
    }

    /**
     * Get the device prefix (for debugging/logging)
     */
    public static String getDevicePrefix(Context context) {
        if (devicePrefix == null) {
            init(context);
        }
        return "ff" + devicePrefix;
    }

    /**
     * Reset counter (use carefully, only for testing)
     */
    public static void resetCounter(Context context) {
        counter = 0;
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_COUNTER, 0)
                .apply();
    }
}
