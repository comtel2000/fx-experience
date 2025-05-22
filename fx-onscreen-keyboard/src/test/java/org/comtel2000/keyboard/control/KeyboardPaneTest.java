package org.comtel2000.keyboard.control;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.comtel2000.keyboard.control.KeyboardType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;


public class KeyboardPaneTest {

    private static final String TEST_LAYOUT_URL = "/xml/test/kb-layout.xml";
    private String originalLayoutUrl;

    @BeforeAll
    static void initJfxToolkit() {
        // Initializes JavaFX Toolkit, needed for things like Platform.runLater() and creating UI components
        new JFXPanel(); 
    }

    @BeforeEach
    void setUp() {
        // Store original system property and set the test one
        originalLayoutUrl = System.getProperty("keyboard.layout.url");
        System.setProperty("keyboard.layout.url", TEST_LAYOUT_URL);
    }

    @AfterEach
    void tearDown() {
        // Restore original system property
        if (originalLayoutUrl != null) {
            System.setProperty("keyboard.layout.url", originalLayoutUrl);
        } else {
            System.clearProperty("keyboard.layout.url");
        }
    }

    @Test
    void initialStateTest() {
        // This test now runs with the JFX toolkit initialized by @BeforeAll
        KeyboardPane keyboardPane = new KeyboardPane();

        assertEquals(1.0, keyboardPane.scaleProperty().get(), "Default scale should be 1.0");
        assertEquals(0.7, keyboardPane.minScaleProperty().get(), "Default minScale should be 0.7");
        assertEquals(5.0, keyboardPane.maxScaleProperty().get(), "Default maxScale should be 5.0");
        assertEquals(0.2, keyboardPane.scaleOffsetProperty().get(), "Default scaleOffset should be 0.2");
        assertEquals(DefaultLayer.DEFAULT, keyboardPane.layerProperty().get(), "Default layer should be DefaultLayer.DEFAULT");
        // Default locale can be tricky if it's based on KeyboardSettings.getLocale() which might read the layout
        // For a fresh KeyboardPane before load(), it should be Locale.getDefault()
        assertEquals(Locale.getDefault(), keyboardPane.localeProperty().get(), "Default locale before load should be Locale.getDefault()");
        assertTrue(keyboardPane.cacheLayoutProperty().get(), "Default cacheLayout should be true");
        assertFalse(keyboardPane.symbolProperty().get(), "Default symbol should be false");
        assertFalse(keyboardPane.shiftProperty().get(), "Default shift should be false");
        assertFalse(keyboardPane.controlProperty().get(), "Default control should be false");
        assertTrue(keyboardPane.spaceKeyMoveProperty().get(), "Default spaceKeyMove should be true");
        assertTrue(keyboardPane.capsLockProperty().get(), "Default capsLock should be true");
        // The style might not be loaded until it's on a scene, or after load().
        // Let's check if it's not null or empty, rather than specific content if it's loaded dynamically.
        assertNotNull(keyboardPane.keyBoardStyleProperty().get(), "keyBoardStyle should not be null");
        assertFalse(keyboardPane.keyBoardStyleProperty().get().isEmpty(),"keyBoardStyle should not be empty" );

    }

    @Test
    void testLoadLayout() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KeyboardPane keyboardPane = new KeyboardPane();

        Platform.runLater(() -> {
            try {
                assertDoesNotThrow(() -> keyboardPane.load(), "keyboardPane.load() should not throw an exception");
                assertFalse(keyboardPane.getChildren().isEmpty(), "Children list should be populated after load");
                // My test kb-layout.xml has locale="test"
                assertEquals(new Locale("test"), keyboardPane.getActiveLocale(), "Active locale should be 'test' from test layout");
                // My test kb-layout.xml has type="TEXT" as default
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Active type should be TEXT from test layout");
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX task did not complete in time");
    }

    @Test
    void testSwitchKeyboardTypes() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KeyboardPane keyboardPane = new KeyboardPane();

        Platform.runLater(() -> {
            try {
                keyboardPane.load(); // Load the test layout first
                
                // Initial state (TEXT based on test-layout.xml)
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Initial type should be TEXT");
                long initialKeyCount = keyboardPane.lookupAll(".key-button").size(); // Assuming keys have 'key-button' style class
                assertTrue(initialKeyCount > 0, "Should have some keys for TEXT type");

                // Switch to SYMBOL
                keyboardPane.setKeyboardType(KeyboardType.SYMBOL);
                assertEquals(KeyboardType.SYMBOL, keyboardPane.getActiveType(), "Type should switch to SYMBOL");
                long symbolKeyCount = keyboardPane.lookupAll(".key-button").size();
                assertTrue(symbolKeyCount > 0, "Should have some keys for SYMBOL type");
                assertNotEquals(initialKeyCount, symbolKeyCount, "Key count should change for SYMBOL type");

                // Switch back to TEXT
                keyboardPane.setKeyboardType(KeyboardType.TEXT);
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Type should switch back to TEXT");
                long textKeyCount = keyboardPane.lookupAll(".key-button").size();
                assertEquals(initialKeyCount, textKeyCount, "Key count should revert for TEXT type");

            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX task for switching types did not complete in time");
    }

    @Test
    void testSwitchLocale() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KeyboardPane keyboardPane = new KeyboardPane();
        Locale testLocale = new Locale("test");
        Locale testUsLocale = new Locale("test", "US");

        Platform.runLater(() -> {
            try {
                keyboardPane.load(); // Load the test layout first (defaults to "test" locale)

                // Initial state (Locale "test", Type TEXT)
                assertEquals(testLocale, keyboardPane.getActiveLocale(), "Initial locale should be 'test'");
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Initial type should be TEXT");
                long initialKeyCountTextLocale = keyboardPane.lookupAll(".key-button").size();
                assertEquals(3, initialKeyCountTextLocale, "Key count for 'test' locale, TEXT type should be 3");

                // Switch to Locale "test_US"
                keyboardPane.switchLocale(testUsLocale);
                assertEquals(testUsLocale, keyboardPane.getActiveLocale(), "Locale should switch to 'test_US'");
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Type should reset to TEXT after locale switch");
                long usKeyCountTextLocale = keyboardPane.lookupAll(".key-button").size();
                assertEquals(4, usKeyCountTextLocale, "Key count for 'test_US' locale, TEXT type should be 4");
                assertNotEquals(initialKeyCountTextLocale, usKeyCountTextLocale, "Key count should change for 'test_US' locale");

                // Switch back to Locale "test"
                keyboardPane.switchLocale(testLocale);
                assertEquals(testLocale, keyboardPane.getActiveLocale(), "Locale should switch back to 'test'");
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Type should reset to TEXT after locale switch");
                long revertedKeyCountTextLocale = keyboardPane.lookupAll(".key-button").size();
                assertEquals(initialKeyCountTextLocale, revertedKeyCountTextLocale, "Key count should revert for 'test' locale");

            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX task for switching locales did not complete in time");
    }

    @Test
    void testSwitchLayer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KeyboardPane keyboardPane = new KeyboardPane();
        Locale testLocale = new Locale("test");

        Platform.runLater(() -> {
            try {
                // Initial load - should use /xml/test/kb-layout.xml due to system property
                keyboardPane.load();

                // Verify initial state (Default Layer)
                assertEquals(DefaultLayer.DEFAULT, keyboardPane.getLayer(), "Initial layer should be DEFAULT");
                assertEquals(testLocale, keyboardPane.getActiveLocale(), "Initial locale should be 'test'");
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Initial type should be TEXT");
                long initialKeyCountDefaultLayer = keyboardPane.lookupAll(".key-button").size();
                assertEquals(3, initialKeyCountDefaultLayer, "Key count for DEFAULT layer, 'test' locale, TEXT type should be 3");

                // Switch to SYMBOL Layer
                // This should load /xml/symbol/kb-layout.xml internally
                keyboardPane.switchLayer(DefaultLayer.SYMBOL);
                assertEquals(DefaultLayer.SYMBOL, keyboardPane.getLayer(), "Layer should switch to SYMBOL");
                assertEquals(testLocale, keyboardPane.getActiveLocale(), "Locale should remain 'test' after layer switch");
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Type should reset to TEXT after layer switch");
                long symbolLayerKeyCount = keyboardPane.lookupAll(".key-button").size();
                assertEquals(2, symbolLayerKeyCount, "Key count for SYMBOL layer, 'test' locale, TEXT type should be 2");
                assertNotEquals(initialKeyCountDefaultLayer, symbolLayerKeyCount, "Key count should change for SYMBOL layer");

                // Switch back to DEFAULT Layer
                keyboardPane.switchLayer(DefaultLayer.DEFAULT);
                assertEquals(DefaultLayer.DEFAULT, keyboardPane.getLayer(), "Layer should switch back to DEFAULT");
                assertEquals(testLocale, keyboardPane.getActiveLocale(), "Locale should remain 'test' after switching back to DEFAULT layer");
                assertEquals(KeyboardType.TEXT, keyboardPane.getActiveType(), "Type should reset to TEXT after switching back to DEFAULT layer");
                long revertedKeyCountDefaultLayer = keyboardPane.lookupAll(".key-button").size();
                assertEquals(initialKeyCountDefaultLayer, revertedKeyCountDefaultLayer, "Key count should revert for DEFAULT layer");

            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX task for switching layers did not complete in time");
    }

    @Test
    void testKeyPressEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        KeyboardPane keyboardPane = new KeyboardPane();
        org.comtel2000.keyboard.robot.MockRobot mockRobot = new org.comtel2000.keyboard.robot.MockRobot();

        Platform.runLater(() -> {
            try {
                // Setup KeyboardPane with MockRobot
                keyboardPane.getRobotHandlers().clear();
                keyboardPane.getRobotHandlers().add(mockRobot);
                keyboardPane.load(); // Load /xml/test/kb-layout.xml

                // Create a KeyButtonNode and event to simulate a key press for 'a'
                // This 'a' key (code 97) is defined in the modified /xml/test/kb-layout.xml
                org.comtel2000.keyboard.layout.KeyboardKey keyData = new org.comtel2000.keyboard.layout.KeyboardKey();
                keyData.setCode(97); // ASCII for 'a'
                keyData.setLabel("a");
                keyData.setType(org.comtel2000.keyboard.layout.KeyboardKeyType.TEXT);

                // Assuming KeyButtonNode is the correct class for keys.
                // If it's KeyButton, adjust class name.
                org.comtel2000.control.KeyButtonNode keyButton = new org.comtel2000.control.KeyButtonNode(keyData);
                // keyButton.setFocusTraversable(false); // Often set

                org.comtel2000.keyboard.event.KeyButtonEvent event = 
                    new org.comtel2000.keyboard.event.KeyButtonEvent(keyButton, org.comtel2000.keyboard.event.KeyButtonEvent.KEY_PRESSED);
                
                keyboardPane.handle(event);

                // Verify MockRobot received the event
                assertFalse(mockRobot.receivedKeyPresses.isEmpty(), "MockRobot should have received key presses");
                assertEquals(1, mockRobot.receivedKeyPresses.size(), "MockRobot should have received one key press");
                
                org.comtel2000.keyboard.robot.MockRobot.KeyPress press = mockRobot.receivedKeyPresses.get(0);
                assertEquals('a', press.character, "Character should be 'a'");
                assertFalse(press.isCtrlPressed, "Ctrl should not be pressed");
                // Check if the sender is the original KeyButtonNode.
                // The KeyboardPane passes the event source (KeyButtonNode) to the robot.
                assertEquals(keyButton, mockRobot.lastSender, "Sender should be the KeyButtonNode instance");

            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX task for key press event did not complete in time");
    }
}
