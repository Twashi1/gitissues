package gitissues.native

/**
 * Represents a UUID7 (Universally Unique Identifier version 7).
 * UUID7 is a 128-bit timestamp-based UUID format.
 *
 * This is a lightweight representation using a 16-byte array.
 * For hex string conversion and other operations, use the utility functions below.
 */
object UUID7 {
    /** Constant: UUID7 must have exactly 16 bytes */
    private val UUID7_SIZE = 16

    /** Constant for UUID7 hex string length */
    private val HEX_STRING_LENGTH = 32

    /**
     * Creates a UUID7 from a 16-byte array.
     *
     * @param bytes 16 bytes representing the UUID7
     * @return A byte array representing the UUID7 (same input, copied for safety)
     */
    fun fromBytes(bytes: ByteArray): ByteArray {
        if (bytes.size != UUID7_SIZE) {
            throw IllegalArgumentException("UUID7 must have exactly $UUID7_SIZE bytes, got ${bytes.size}")
        }
        return bytes.copyOf(UUID7_SIZE)
    }

    /**
     * Converts a 16-byte UUID7 array to a hexadecimal string (32 lowercase hex characters).
     *
     * @param bytes 16 bytes representing the UUID7
     * @return 32-character lowercase hex string
     */
    fun toHexString(bytes: ByteArray): String {
        if (bytes.size != UUID7_SIZE) {
            throw IllegalArgumentException("UUID7 must have exactly $UUID7_SIZE bytes")
        }
        val sb = StringBuilder(HEX_STRING_LENGTH)
        for (b in bytes) {
            sb.append("%02x".format(b))
        }
        return sb.toString()
    }

    /**
     * Converts a hexadecimal string (32 characters) to a 16-byte UUID7 array.
     *
     * @param hex 32-character lowercase hex string
     * @return 16-byte array representing the UUID7
     */
    fun fromHexString(hex: String): ByteArray {
        if (hex.length != HEX_STRING_LENGTH) {
            throw IllegalArgumentException("UUID7 hex string must be $HEX_STRING_LENGTH characters, got ${hex.length}")
        }
        val bytes = ByteArray(UUID7_SIZE)
        for (i in 0 until HEX_STRING_LENGTH step 2) {
            // Manual hex parsing: each pair of hex chars becomes one byte
            var high: Int = 0
            var low: Int = 0
            if (hex[i] in '0'..'9') high = hex[i].code - '0'.code
            else if (hex[i] in 'a'..'f') high = hex[i].code - 'a'.code + 10
            else if (hex[i] in 'A'..'F') high = hex[i].code - 'A'.code + 10
            else throw IllegalArgumentException("Invalid hex character at position $i")

            if (hex[i + 1] in '0'..'9') low = hex[i + 1].code - '0'.code
            else if (hex[i + 1] in 'a'..'f') low = hex[i + 1].code - 'a'.code + 10
            else if (hex[i + 1] in 'A'..'F') low = hex[i + 1].code - 'A'.code + 10
            else throw IllegalArgumentException("Invalid hex character at position $i + 1")

            bytes[i / 2] = (high shl 4 or low and 0xFF).toByte()
        }
        return bytes
    }

    /**
     * Generates a new UUID7 based on the current timestamp and random bits.
     * This delegates to the native library's UUID7 generation.
     *
     * @param schema The native schema handle
     * @param issue The issue to associate the UUID7 with
     * @return 16-byte array representing the newly generated UUID7
     */
    fun generate(schema: Schema, issue: NativeIssue): ByteArray {
        return schema.getOrCreateUUID(issue)
    }
}