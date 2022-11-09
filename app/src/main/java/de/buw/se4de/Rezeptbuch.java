package de.buw.se4de;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

public class Rezeptbuch {

    private static final String path = "src/main/resources/rezeptbuch.csv";
    private ArrayList<Rezept> rezepte = new ArrayList<Rezept>();

    private JFrame rezeptBuchWindow;
    private JFrame rezeptWindow;

    private boolean running = false;
    private boolean rezept_offen = false;

    private boolean is_editable = false;

    JFrame init() {
        if (running) return rezeptBuchWindow;
        running = true;
        System.out.println("Rezeptbuch geöffnet");

        // Rezepte laden
        rezepte.addAll(load(path));

        initWindow();

        return rezeptBuchWindow;
    }

    ArrayList<Rezept> load(String pfad) {
        ArrayList<Rezept> temp = new ArrayList<Rezept>();
		try (Reader reader = Files.newBufferedReader(Paths.get(pfad), StandardCharsets.UTF_8);
			@SuppressWarnings("deprecation")
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
                for (CSVRecord csvRecord : csvParser) {
                    String name = csvRecord.get("name");
                    String zutatenVoll = csvRecord.get("zutaten");
                    String personen = csvRecord.get("personen");
                    String kategorienVoll = csvRecord.get("kategorien");
                    String zeit = csvRecord.get("zeit");
                    String zubereitung = csvRecord.get("zubereitung").replaceAll(";", "\n");

                    String[] zutaten = zutatenVoll.split(";");
                    String[] kategorien = kategorienVoll.split(";");

                    Rezept rezept = new Rezept(name, zutaten, personen, kategorien, zeit, zubereitung);
                    temp.add(rezept);
                }
		} catch (IOException e) {
			e.printStackTrace();
		}
        return temp;
    } 

    boolean initWindow() {
        rezeptBuchWindow = new JFrame("Rezeptbuch");
		rezeptBuchWindow.setSize(1280, 720);
        rezeptBuchWindow.setResizable(false);

        rezeptBuchWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                save(path, rezepte);
                unload();
            }
        });
		
        JPanel auswahlPanel = new JPanel(new GridLayout(0, 5, 20, 20));

        for (Rezept rezept : rezepte) {
            JButton tempButton = new JButton(rezept.name);

            tempButton.addActionListener(e -> {
                setupButton(rezept, tempButton);
            });

            tempButton.setPreferredSize(new Dimension(0, 200));
            auswahlPanel.add(tempButton);
        }

        auswahlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JScrollPane auswahlScrollPanel = new JScrollPane(auswahlPanel);
        auswahlScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        auswahlScrollPanel.setPreferredSize(new Dimension(1280, 500));

        auswahlScrollPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel suchPanel = new JPanel();
        JLabel suchNameLabel = new JLabel("Name:");
        JLabel suchKategorieLabel = new JLabel("Kategorien:");
        JTextField suchNameTextField = new JTextField();
        JTextField suchKategorieTextField = new JTextField();
        JButton suchButton = new JButton("Suchen");
        JButton importButton = new JButton("Importieren");
        JButton exportButton = new JButton("Exportieren");
        JButton neuButton = new JButton("Neues Rezept");

        suchNameTextField.setColumns(30);
        suchKategorieTextField.setColumns(30);

        suchKategorieTextField.setToolTipText("Man kann mehrere Kategorien mit \",\" angeben");

        suchPanel.add(suchNameLabel);
        suchPanel.add(suchNameTextField);
        suchPanel.add(suchKategorieLabel);
        suchPanel.add(suchKategorieTextField);
        suchPanel.add(suchButton);
        suchPanel.add(importButton);
        suchPanel.add(exportButton);
        suchPanel.add(neuButton);
        
        rezeptBuchWindow.getContentPane().add(suchPanel, BorderLayout.NORTH);
        rezeptBuchWindow.getContentPane().add(auswahlScrollPanel);
        
        rezeptBuchWindow.validate();
		rezeptBuchWindow.setVisible(true);

        suchButton.addActionListener(e -> {
            showSearch(auswahlPanel, suchNameTextField.getText(),suchKategorieTextField.getText());
        });

        importButton.addActionListener(e -> {
            ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>();
            JFileChooser chooser = new JFileChooser("src/main/resources");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Rezepte", "csv");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(rezeptBuchWindow);
            if(returnVal == JFileChooser.APPROVE_OPTION) {

                rezepte_temp = load(chooser.getSelectedFile().getPath());
            }
            rezepte.addAll(rezepte_temp);
            addRezepte(auswahlPanel, rezepte_temp);
            save("src/main/resources/rezeptebuch_LIVE.csv", rezepte);
        });

        exportButton.addActionListener(e -> {
            JFrame exportFrame = new JFrame("Exportieren");
            exportFrame.setSize(600, 600);
            exportFrame.setResizable(false);

            DefaultListModel<Rezept> listMod = new DefaultListModel<Rezept>();

            listMod.addAll(rezepte);

            JList<Rezept> list = new JList<Rezept>(listMod);
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(600, 600));

            JButton eButton = new JButton("Exportieren");

            exportFrame.getContentPane().add(listScroller);
            exportFrame.getContentPane().add(eButton, BorderLayout.SOUTH);

            exportFrame.validate();
		    exportFrame.setVisible(true);

            eButton.addActionListener(f -> {
                ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>(list.getSelectedValuesList());
                JFileChooser chooser = new JFileChooser("src/main/resources");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Rezepte", "csv");
                chooser.setFileFilter(filter);
                chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                chooser.setSelectedFile(new File("export.csv"));
                int returnVal = chooser.showSaveDialog(exportFrame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    save(chooser.getSelectedFile().getPath(), rezepte_temp);
                    exportFrame.dispatchEvent(new WindowEvent(exportFrame, WindowEvent.WINDOW_CLOSING));
                }
            });
        });

        neuButton.addActionListener(e -> {
            Rezept neu = new Rezept("neu", new String[]{"Zutaten"}, "0", new String[]{"Kategorien"}, "00:00:00", "Zubereitung");

            ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>();
            rezepte_temp.add(neu);
            rezepte.add(neu);
            addRezepte(auswahlPanel, rezepte_temp);
            save("src/main/resources/rezeptebuch_LIVE.csv", rezepte);
        });

        return true;
    }

    void setupButton(Rezept rezept, JButton button) {
        if (rezept_offen) {
            rezeptWindow.toFront();
            rezeptWindow.requestFocus();
            return;
        }
        rezept_offen = true;
        is_editable = false;
        
        rezeptWindow = new JFrame(rezept.name);
        rezeptWindow.setSize(500, 720);
        rezeptWindow.setResizable(false);

        rezeptWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rezept_offen = false;
                save("src/main/resources/rezeptebuch_LIVE.csv", rezepte);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel rezeptPanel = new JPanel();
        rezeptPanel.setLayout(new BoxLayout(rezeptPanel, BoxLayout.Y_AXIS));
        rezeptPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        JTextField nameField = new JTextField();
        JLabel kategorienLabel = new JLabel("Kategorien", SwingConstants.CENTER);
        JTextArea kategorienArea = new JTextArea();
        JLabel zutatenLabel = new JLabel("Zutaten", SwingConstants.CENTER);
        JTextArea zutatenArea = new JTextArea();
        JLabel personenLabel = new JLabel("Personen Anzahl", SwingConstants.CENTER);
        JTextField personenField = new JTextField();
        JLabel zeitLabel = new JLabel("Zubereitungs Dauer (Format DD:HH:MM)", SwingConstants.CENTER);
        JTextField zeitField = new JTextField();
        JLabel zubereitungLabel = new JLabel("Zubereitung", SwingConstants.CENTER);
        JTextArea zubereitungArea = new JTextArea();

        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
        personenField.setMaximumSize(new Dimension(Integer.MAX_VALUE, personenField.getPreferredSize().height));
        zeitField.setMaximumSize(new Dimension(Integer.MAX_VALUE, zeitField.getPreferredSize().height));

        kategorienArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, kategorienArea.getPreferredSize().height));
        zutatenArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, zutatenArea.getPreferredSize().height));

        nameField.setEditable(false);
        kategorienArea.setEditable(false);
        zutatenArea.setEditable(false);
        personenField.setEditable(false);
        zeitField.setEditable(false);
        zubereitungArea.setEditable(false);

        nameField.setText(rezept.name);
        personenField.setText(rezept.personen);
        zeitField.setText(rezept.zeit);
        zubereitungArea.setText(rezept.zubereitung);
        zutatenArea.setText(String.join("\n", rezept.zutaten));
        kategorienArea.setText(String.join(", ", rezept.kategorien));

        rezeptPanel.add(nameLabel);
        rezeptPanel.add(nameField);
        rezeptPanel.add(kategorienLabel);
        rezeptPanel.add(kategorienArea);
        rezeptPanel.add(zutatenLabel);
        rezeptPanel.add(zutatenArea);
        rezeptPanel.add(personenLabel);
        rezeptPanel.add(personenField);
        rezeptPanel.add(zeitLabel);
        rezeptPanel.add(zeitField);
        rezeptPanel.add(zubereitungLabel);
        rezeptPanel.add(zubereitungArea);


        JPanel funktionenPanel = new JPanel();
        JButton editButton = new JButton("Editiermodus aktivieren");
        JButton saveButton = new JButton("Speichern");
        JButton exportButton = new JButton("Exportieren");

        funktionenPanel.add(editButton);
        funktionenPanel.add(saveButton);
        funktionenPanel.add(exportButton);

        mainPanel.add(rezeptPanel);
        mainPanel.add(funktionenPanel, BorderLayout.NORTH);

        rezeptWindow.getContentPane().add(mainPanel);

        rezeptWindow.setVisible(true);
        rezeptWindow.toFront();
        rezeptWindow.requestFocus();

        editButton.addActionListener(e -> {
			if(is_editable) {
                nameField.setEditable(false);
                kategorienArea.setEditable(false);
                zutatenArea.setEditable(false);
                personenField.setEditable(false);
                zeitField.setEditable(false);
                zubereitungArea.setEditable(false);
                editButton.setText("Editiermodus aktivieren");
                is_editable = !is_editable;
            } else {
                nameField.setEditable(true);
                kategorienArea.setEditable(true);
                zutatenArea.setEditable(true);
                personenField.setEditable(true);
                zeitField.setEditable(true);
                zubereitungArea.setEditable(true);
                editButton.setText("Editiermodus deaktivieren");
                is_editable = !is_editable;
            }
		});

        saveButton.addActionListener(e -> {
            rezept.name = nameField.getText();
            button.setText(rezept.name);

            String per = personenField.getText();
            if (per.matches("[0-9]+")) {
                rezept.personen = per;
            } else {
                personenField.setText(rezept.personen);
            }

            String zeit = zeitField.getText();
            if (per.matches("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]")) {
                rezept.zeit = zeit;
            } else {
                zeitField.setText(rezept.zeit);
            }

            rezept.zubereitung = zubereitungArea.getText();

            rezept.kategorien = kategorienArea.getText().split(", ");
            
            rezept.zutaten = zutatenArea.getText().split("\n");

            save("src/main/resources/rezeptebuch_LIVE.csv", rezepte);
        });

        exportButton.addActionListener(e -> {
            ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>();
            rezepte_temp.add(rezept);
            JFileChooser chooser = new JFileChooser("src/main/resources");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Rezepte", "csv");
            chooser.setFileFilter(filter);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setSelectedFile(new File(rezept.name + ".csv"));
            int returnVal = chooser.showSaveDialog(rezeptWindow);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                save(chooser.getSelectedFile().getPath(), rezepte_temp);
            }
        });
    }

    void addRezepte(JPanel panel, ArrayList<Rezept> rezepte_temp) {
        for (Rezept rezept : rezepte_temp) {
            JButton tempButton = new JButton(rezept.name);

            tempButton.addActionListener(e -> {
                setupButton(rezept, tempButton);
            });

            tempButton.setPreferredSize(new Dimension(0, 200));
            panel.add(tempButton);
        }
        rezeptBuchWindow.revalidate();
        rezeptBuchWindow.repaint();
    }

    boolean find_kategorie(Rezept rezept, String kategorien) {
        for (String kategorie : kategorien.split(",")) {
            for (String gibt : rezept.kategorien) {
                if (kategorie.toLowerCase().equals(gibt.toLowerCase()))
                    return true;
            }
        }
        
        return false;
    }

    void showSearch(JPanel panel, String text, String kategorie) {
        panel.removeAll();
        panel.revalidate();

        ArrayList<Rezept> temp = new ArrayList<Rezept>();
        for (Rezept rezept : rezepte) {
            boolean found_name = rezept.name.toLowerCase().contains(text.toLowerCase()) || text.length() == 0;
            boolean found_kategorie = find_kategorie(rezept, kategorie) || kategorie.length() == 0;
            if (found_name && found_kategorie)
                temp.add(rezept);
        }

        addRezepte(panel, temp);
    }

    boolean save(String pfad, ArrayList<Rezept> rez) {

        CSVFormat format = CSVFormat.DEFAULT;
        format.builder().setHeader("name", "zutaten", "personen", "kategorien", "zeit", "zubereitung");
        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(pfad), StandardCharsets.UTF_8) ;
            CSVPrinter printer = new CSVPrinter(writer, format);
        ) {
            printer.printRecords(getRecords(rez));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    void unload() {
        rezepte.clear();
        if (rezeptWindow != null)
            rezeptWindow.dispatchEvent(new WindowEvent(rezeptWindow, WindowEvent.WINDOW_CLOSING));
        running = false;
        rezeptBuchWindow = null;
        rezeptWindow = null;
    }

    ArrayList<String[]> getRecords(ArrayList<Rezept> rez) {
        ArrayList<String[]> records = new ArrayList<String[]>();

        String[] header = {"name", "zutaten", "personen", "kategorien", "zeit", "zubereitung"};
        records.add(header);

        for (Rezept rezept : rez) {
            String[] record = {rezept.name, String.join(";", rezept.zutaten), rezept.personen, String.join(";", rezept.kategorien), rezept.zeit, rezept.zubereitung.replaceAll("\n", ";")};
            records.add(record);
        }

        return records;
    }
}
