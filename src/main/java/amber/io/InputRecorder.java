package amber.io;

import java.io.IOException;
import java.util.Arrays;

public class InputRecorder {

    private InputEditor editor;

    public InputRecorder(InputEditor editor){
        this.editor = editor;
    }

      private static final int[] KeyCodes = {
            37, // Left
            39, // Right
            38, // Up
            40, // Down
            32, // Jump
            65, // Weapon
            219, // Swap Weapon
            68, // Shove
            87, // Grab
            27, // Pause
            65, // Rock
            81, // Call
            162 // Interact
    };

    public void advanceFrame(){
        String[] values = new String[editor.getTable().getColumnCount()];
        for (int i = 0; i < editor.getTable().getColumnCount(); i++){
            long x = editor.getCurrentFrame();
            if (x >= editor.getTable().getRowCount()) x--;
            values[i] = "" + editor.getTable().getValueAt((int) x, i);
        }
//        String[] array = {
//                "powershell",
//                "Add-Type @\"",
//                "using System;",
//                "using System.Runtime.InteropServices;",
//                "public class Win32 {",
//                "[DllImport(\"user32.dll\")]",
//                "public static extern bool SetForegroundWindow(IntPtr hWnd);",
//                "}",
//                "\"@",
//
//                "$process = Get-Process -Name \"BattleBlockTheater\" -ErrorAction SilentlyContinue",
//                "if ($process) {",
//                "$hwnd = $process.MainWindowHandle",
//                "[Win32]::SetForegroundWindow($hwnd)",
//                "}"
//        };
//        ProcessBuilder builder = new ProcessBuilder(array);
//        try {
//            builder.start();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        Arrays.stream(values).forEach(string -> {

        });
    }
}
