package amber.io;

import java.io.IOException;
import java.util.Arrays;
import java.awt.Robot;
import java.util.List;
import java.util.ArrayList;


public class InputRecorder {

    private InputEditor editor;

    public InputRecorder(InputEditor editor){
        this.editor = editor;
    }


    private final List<Integer> pastKeyCodes;

    public void advanceFrame(){
        String[] values = new String[editor.getTable().getColumnCount()];
        for (int i = 0; i < editor.getTable().getColumnCount(); i++){
            long x = editor.getCurrentFrame();
            if (x >= editor.getTable().getRowCount()) x--;
            Object value = editor.getTable().getValueAt((int) x, i);
            if (value != null) values[i] = "" + value;
        }
        if (pastKeyCodes == null){
            pastKeyCodes = new ArrayList<>();
        }

        bringGameToFront();
        Robot bot = new Robot();
        List<Integer> copy = pastKeyCodes;
        List<Integer> updatePast = new ArrayList<>();
        for (String string : values){

            int keycode = keyCodeFromString(string)
            if (keycode == 0){
                System.out.println("Something went wrong when reading inputs on frame: " + frame);
                return;
            }

            if (!pastKeyCodes.contains(keycode)){
                bot.keyPress(keycode);
            } else {
                copy.remove((Integer) keycode);
            }
            updatePast.add((Integer) keycode);
        };
        for (Integer i : copy){
            bot.keyRelease((int) i);
        }
        pastKeyCodes = updatePast;
    }




    public void bringGameToFront(){
        CompletableFuture.runAsync(() -> {
            String[] array = {
                "powershell",
                "Add-Type @\"",
                "using System;",
                "using System.Runtime.InteropServices;",
                    "public class Win32 {
                    ",/"[DllImport(\"user32.dll\")]",
                    "public static extern bool SetForegroundWindow(IntPtr hWnd);",
                    "}",
                    "\"@",
                    "$process = Get-Process -Name \"BattleBlockTheater\" -ErrorAction SilentlyContinue",
                    "if ($process) {",
                    "$hwnd = $process.MainWindowHandle",
                    "[Win32]::SetForegroundWindow($hwnd)",
                    "}"
            };
            ProcessBuilder builder = new ProcessBuilder(array);
            try {
                Process powershell = builder.start();
                powershell.waitFor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int keyCodeFromString(String input){
        return switch(input){
            case "Left": 37
            case "Right": 39
            case "Up": 38
            case "Down": 40
            case "Jump": 32
            case "Weapon": 65
            case "Swap Weapon": 219
            case "Shove": 68
            case "Grab": 87
            case "Pause": 27
            case "Call": 81
            case "Interact": 162
            default: 0
        }
    }
}
