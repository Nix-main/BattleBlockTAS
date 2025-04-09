package amber.io;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;

public class InputEditor {


    private long frame;
    private final JFrame jframe;
    private final JTable table;
    private final JScrollPane pane;
    private final DefaultTableModel model;
    private final InputRecorder recorder;
    private final ColorRenderHandler renderer;

    public JFrame getJFrame(){return jframe;}
    public long getCurrentFrame(){return frame;}
    public JTable getTable(){return table;}
    public JScrollPane getPane(){return pane;}
    public DefaultTableModel getModel(){return model;}


    public InputEditor(long frame){
        this.frame = frame;
        jframe = new JFrame("Input Editor");
        renderer = new ColorRenderHandler();
        model = new DefaultTableModel(new Object[]{"Frame", "Left", "Right", "Up", "Down", "Jump", "Weapon", "Swap Weapon", "Shove", "Grab", "Pause", "Call", "Interact"}, 200){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        for (int i = 0; i < model.getRowCount(); i++){
            model.setValueAt(i+1, i, 0);
        }
        MouseListener listener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3 && !e.isShiftDown()){
                    System.out.print("\n# of frames to add: ");
                    int x = new Scanner(System.in).nextInt();
                    for (int i = 0; i < x; i++){
                        addRow();
                    }
                } else if (e.getButton() == 3){
                    System.out.println("\n# of frames to remove: ");
                    int x = new Scanner(System.in).nextInt();
                    if (x > model.getRowCount()){
                        System.out.println("Too many frames!");
                        return;
                    }
                    removeRow(x);
                } else {
                    int row = table.rowAtPoint(e.getPoint());
                    int column = table.columnAtPoint(e.getPoint());
                    if (!(column == 0)) {
                        if (model.getValueAt(row, column) == null) {
                            model.setValueAt(table.getColumnName(column), row, column);
                        } else {
                            model.setValueAt(null, row, column);
                        }
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {

            }
            @Override
            public void mouseEntered(MouseEvent e) {

            }
            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        table = new JTable(model);
        table.addMouseListener(listener);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.getColumnModel().getColumns().asIterator().forEachRemaining(c -> c.setMinWidth(50));
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, renderer);
        pane = new JScrollPane(table);
        pane.setLayout(new ScrollPaneLayout());
        JScrollBar bar = new JScrollBar();
        pane.add(bar);

        jframe.setLocationRelativeTo(null);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 500);
        recorder = new InputRecorder(this);

    }

    public void addTable(){
        jframe.add(pane);
    }

    private void addRow(){
        model.addRow(new Object[]{});
        for (int i = 0; i < model.getRowCount(); i++){
            model.setValueAt(i+1, i, 0);
        }
    }

    private void removeRow(int amount){
        for (int i = 1; i < amount; i++){
            model.removeRow(model.getRowCount() - i);
        }
        for (int i = 0; i < model.getRowCount(); i++){
            model.setValueAt(i+1, i, 0);
        }
    }

    public void displayEditor(){
        jframe.setVisible(true);
    }

    public void updateFrame(long update){
        if (frame > table.getRowCount()){
            addRow();
            Main.endOfEditor();
        }
        if (frame < update) {
            frame = update;
            recorder.advanceFrame();
            jframe.repaint();
        }
    }

    class ColorRenderHandler extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color nextFrame = new Color(0, 50, 200, 30);
            Color pastFrame = new Color(0, 200, 50, 30);
            if (row < frame) {
                cell.setBackground(pastFrame);
            } else if (row == frame){
                cell.setBackground(nextFrame);
            } else {
                cell.setBackground(Color.WHITE);
            }
            return cell;
        }
    }
}
