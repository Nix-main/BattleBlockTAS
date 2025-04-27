package amber.io;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class InputRecorder {

    private final InputEditor editor;

    public InputRecorder(InputEditor editor) {
        this.editor = editor;
    }


    private List<Integer> pastKeyCodes = new ArrayList<>();

    public void advanceFrame() {
        String[] values = new String[editor.getTable().getColumnCount()];
        for (int i = 0; i < editor.getTable().getColumnCount(); i++) {
            long x = editor.getCurrentFrame();
            if (x >= editor.getTable().getRowCount()) {
                x--;
            }
            if (x < editor.getTable().getRowCount()) {
                Object value = editor.getTable().getValueAt((int) x, i);
                if (value != null) values[i] = "" + value;
            }
        }

        Robot bot;
        try {
            bot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        List<Integer> copy = pastKeyCodes;
        List<Integer> updatePast = new ArrayList<>();
        for (String string : values) {
            if (string == null) {
                continue;
            }

            int keycode = switch (string) {
                case "Left" -> KeyEvent.VK_LEFT;
                case "Right" -> KeyEvent.VK_RIGHT;
                case "Up" -> KeyEvent.VK_UP;
                case "Down" -> KeyEvent.VK_DOWN;
                case "Jump" -> KeyEvent.VK_SPACE;
                case "Weapon" -> KeyEvent.VK_A;
                case "Swap Weapon" -> KeyEvent.VK_OPEN_BRACKET;
                case "Shove" -> KeyEvent.VK_D;
                case "Grab" -> KeyEvent.VK_W;
                case "Pause" -> KeyEvent.VK_ESCAPE;
                case "Call" -> KeyEvent.VK_Q;
                case "Interact" -> KeyEvent.VK_CONTROL;
                default -> 0;
            };
            if (keycode != 0) {
                if (!pastKeyCodes.contains(keycode)) {
                    System.out.println(string);
                    System.out.println(keycode);
                    bringGameToFront();
                    bot.keyPress(keycode);
                } else {
                    bringGameToFront();
                    copy.remove((Integer) keycode);
                    updatePast.add(keycode);
                }
            }
        }
        for (Integer i : copy) {
            if (i != 0) {
                bot.keyRelease(i);
                System.out.println("Released: " + i);
            }
        }
        updatePast.forEach(System.out::println);
        pastKeyCodes = updatePast;
    }


    public void bringGameToFront() {
            ProcessBuilder builder = new ProcessBuilder(Main.PATH + "BringBBT.exe");
            try {
                builder.start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
}