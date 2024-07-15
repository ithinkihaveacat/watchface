package com.google.wear.watchface.dfx.memory;

import static com.google.wear.watchface.dfx.memory.EvaluationSettings.parseFromArguments;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

@RunWith(JUnit4.class)
public class EvaluationSettingsTest {

    // 10 MB
    private static final long DEFAULT_MEMORY_LIMIT_BYTES = 10 * 1024 * 1024;

    @Test
    public void parseFromArguments_parsesRequiredArgs() {
        Optional<EvaluationSettings> evaluationSettings =
                parseFromArguments(
                        "--watch-face", "path/to/watchface.apk", "--schema-version", "1");

        assertTrue(evaluationSettings.isPresent());
        assertEquals("path/to/watchface.apk", evaluationSettings.get().getWatchFacePath());
        assertEquals("1", evaluationSettings.get().getSchemaVersion());
        // assert default values
        assertFalse(evaluationSettings.get().isVerbose());
        assertEquals(DEFAULT_MEMORY_LIMIT_BYTES, evaluationSettings.get().getAmbientLimitBytes());
        assertEquals(DEFAULT_MEMORY_LIMIT_BYTES, evaluationSettings.get().getActiveLimitBytes());
    }

    @Test
    public void parseFromArguments_parsesOptionalArgs() {
        Optional<EvaluationSettings> evaluationSettings =
                parseFromArguments(
                        "--watch-face",
                        "path/to/watchface.apk",
                        "--schema-version",
                        "1",
                        "--ambient-limit-mb",
                        "50",
                        "--active-limit-mb",
                        "100",
                        "--verbose",
                        "--disable-old-style-clocks",
                        "--disable-ambient-deduplication",
                        "--apply-v1-offload-limitations");

        assertTrue(evaluationSettings.isPresent());
        assertEquals("path/to/watchface.apk", evaluationSettings.get().getWatchFacePath());
        assertEquals("1", evaluationSettings.get().getSchemaVersion());
        assertTrue(evaluationSettings.get().isVerbose());
        assertEquals(50 * 1024 * 1024, evaluationSettings.get().getAmbientLimitBytes());
        assertEquals(100 * 1024 * 1024, evaluationSettings.get().getActiveLimitBytes());
        assertFalse(evaluationSettings.get().supportOldStyleAnalogOrDigitalClock());
        assertFalse(evaluationSettings.get().deduplicateAmbient());
        assertTrue(evaluationSettings.get().applyV1OffloadLimitations());
    }

    @Test
    public void parseFromArguments_returnsNoneWhenLimitIsNotNumber() {
        Optional<EvaluationSettings> evaluationSettings =
                parseFromArguments(
                        "--watch-face",
                        "path/to/watchface.apk",
                        "--schema-version",
                        "1",
                        "--ambient-limit-mb",
                        "lorem");

        assertFalse(evaluationSettings.isPresent());
    }

    @Test
    public void parseFromArguments_returnsNoneWhenMissingRequiredArg() {
        Optional<EvaluationSettings> evaluationSettings = parseFromArguments();
        assertFalse(evaluationSettings.isPresent());
    }
}
