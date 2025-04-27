package amber.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InputSave {

    private final InputEditor editor;
    private boolean LOADED;
    public String SAVEPATH;
    private String NAME;

    public InputSave(InputEditor editor){
        this.editor = editor;
    }


    public void prompt(){
        System.out.println("To load an existing file, paste the path below. To create a new one, press enter.");
        System.out.println("Example: D:\\TAS\\LVL1.bbt");
        Scanner scanner = new Scanner(System.in);
        String string = scanner.nextLine();
        addHook();
        if (!string.isEmpty()){
            LOADED = true;
            SAVEPATH = string;
            try {
                loadFile(string);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        System.out.println("Paste path to save the new file below.\nExample: D:\\TasFiles");
        SAVEPATH = scanner.nextLine();
        System.out.println("Give your new file a name! (No extension)");
        NAME = scanner.nextLine();
    }

    public void addHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            File file = new File("");
            if (LOADED) {
                file = new File(SAVEPATH);
            }
            if (!LOADED) {file = new File(SAVEPATH + "\\" + NAME + ".bbt");}


            try {
                if (!LOADED){file.createNewFile();}
                FileWriter writer = new FileWriter(file);
                for (int i = 0; i < editor.getMaxFrame(); i++){
                    for (int o = 1; o < editor.getModel().getColumnCount(); o++){
                        writer.write(String.format("%s,%s,%s\n", editor.getTable().getValueAt(i, o), i, o));
                    }
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void loadFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        List<String> list = new ArrayList<>();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        editor.getModel().setRowCount(Integer.parseInt(list.getLast().split(",")[1]));
        list.forEach(string -> {
            String[] strs = string.split(",");

            if (Integer.parseInt(strs[1]) < editor.getTable().getRowCount()) {
                if (!strs[0].equals("null")) {
                    editor.getTable().setValueAt(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
                }
                editor.getTable().setValueAt(Integer.parseInt(strs[1]), Integer.parseInt(strs[1]), 0);
            }
        });
        reader.close();
    }
}
