package amber.io;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static boolean PAUSED;
    public static final Runtime RUNTIME = Runtime.getRuntime();
    public static String PATH;
    private static long saveframe = 0;

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        File file = new File("./");
        PATH = (file.getAbsolutePath().replace("\\.", "\\src\\main\\resources\\"));
        Scanner scanner = new Scanner(System.in);
        System.setProperty("java.library.path", PATH + "\\Integration");

        boolean inputs = true;
        String path = "";
        while (inputs){
            System.out.println("Enter the absolute path to your BattleBlock Theater folder!");
            System.out.println("Example: D:\\SteamLibrary\\steamapps\\common\\BattleBlock Theater");
            String t = scanner.nextLine();
            if (!new File(t).exists()) {
                System.out.println("Not a file!");
            } else {
                path = t;
                inputs = false;
            }
        }
        File gameDirectory = new File(path);
        File gameFile = new File(gameDirectory + "\\BattleBlockTheater");
        System.out.println(gameFile.getAbsoluteFile());

        ProcessBuilder processBuilder = new ProcessBuilder(gameFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true);

        PAUSED = false;
        final long[] cachedframe = {0};

        InputEditor editor = new InputEditor(saveframe);
        editor.addTable();
        editor.getSave().prompt();

        try {
            Process process = processBuilder.start();
            Thread countthread = new Thread(() -> {
                long frame = 0;
                long start = System.nanoTime();
                JFrame display = new JFrame("Frame Counter");
                display.setSize(200, 75);
                display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                display.setLocationRelativeTo(null);
                display.setVisible(true);
                JLabel counter = new JLabel("Frame");
                display.add(counter);
                while (true){
                    if (cachedframe[0] > 0){
                        frame += cachedframe[0];
                        cachedframe[0] = 0;
                        editor.getRecorder().advanceFrame();
                    }
                    saveframe = frame;
                    editor.updateFrame(frame);
                    String seconds;
                    if ((frame / 60 % 60) < 10){
                        seconds = "0" + frame / 60 % 60;
                    } else {
                        seconds = "" + frame / 60 % 60;
                    }
                    String millis = "" + (double) (frame % 60) * 0.016666666666;
                    millis = millis.substring(millis.indexOf("."));
                    if (millis.length() > 3){
                        millis = millis.substring(millis.indexOf("."), millis.indexOf(".") + 3);
                    }
                    counter.setText(frame + "             " + String.format("%s:%s%s", ((int) frame / 60 / 60), seconds, millis));

                    if (!PAUSED){
                        if (System.nanoTime() - start >= 16666660){
                            editor.getRecorder().advanceFrame();
                            frame++;
                            start = System.nanoTime();
                        }
                    } else {
                        start = System.nanoTime();
                    }
                }
            });

            AtomicBoolean advancing = new AtomicBoolean(false);

            GlobalKeyListener listener = new GlobalKeyListener() {
                @Override
                public void keyPressed(GlobalKeyEvent globalKeyEvent) {
                    if (globalKeyEvent.getVirtualKeyCode() == 186){
                        if (!PAUSED){
                                pause();
                                // System.out.println("Paused game.");
                                PAUSED = true;
                        } else {
                                unpause();
                                // System.out.println("Unpaused game.");
                                PAUSED = false;
                        }
                    }
                    else if (globalKeyEvent.getVirtualKeyCode() == 86){
                        CompletableFuture.runAsync(() -> {
                            if (!advancing.get()) {
                                advancing.set(true);
                                PAUSED = true;
                                unpause();
                                try {
                                    Thread.sleep(16, 666660);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                editor.getRecorder().advanceFrame();
                                pause();

                                cachedframe[0]++;
                                // System.out.println("Advanced 1 frame");
                            }
                        });
                    }
                }

                @Override
                public void keyReleased(GlobalKeyEvent globalKeyEvent) {
                    if (globalKeyEvent.getVirtualKeyCode() == 86){
                        advancing.set(false);
                    }
                }
            };
            GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook();
            keyboardHook.addKeyListener(listener);
            System.out.println("Game opened with exit code " + process.waitFor());
            RUNTIME.addShutdownHook(new Thread(() -> {
                try {
                    RUNTIME.exec("taskkill /F /IM BattleBlockTheater.exe");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
            boolean bool = true;
            while (bool){
                ProcessBuilder builder = new ProcessBuilder("wmic", "process", "where", "name='BattleBlockTheater.exe'", "get", "processId");
                builder.redirectErrorStream(true);
                Process procee = builder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(procee.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null){
                    if (line.contains("ProcessId")){
                        bool = false;
                    }
                }
            }
            countthread.start();
            editor.displayEditor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error launching or running game: " + e.getMessage());
        }
    }

    private static void pause(){
        try {
            RUNTIME.exec(PATH + "\\pssuspend.exe BattleBlockTheater.exe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void unpause(){
        try {
            RUNTIME.exec(PATH + "\\pssuspend.exe -r BattleBlockTheater.exe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void endOfEditor(){
        try {
            RUNTIME.exec(PATH + "\\pssuspend.exe BattleBlockTheater.exe");
            PAUSED = true;
            System.out.println("Reached end of input editor. Paused game.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}