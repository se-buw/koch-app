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
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

public class Rezeptbuch {

    private static final String path = "./src/main/resources/rezeptbuch.csv";
    private ArrayList<Rezept> rezepte = new ArrayList<>();

    private JFrame rezeptBuchWindow;
    private JFrame rezeptWindow;

    private boolean running = false;
    private boolean rezept_offen = false;

    private boolean is_editable = false;

    //neues Rezeptbuch-Window initialisieren
    JFrame init() {
        // wenn schon Rezeptbuch offen ist, wollen wir kein zweites
        if (running) return rezeptBuchWindow;
        running = true;

        // Rezepte laden
        rezepte.addAll(load(path));

        initWindow();

        return rezeptBuchWindow;
    }

    // nimmt den pfad zu einer CSV Datei und gibt eine Liste mit Rezepten zurück
    ArrayList<Rezept> load(String pfad) {
        ArrayList<Rezept> temp = new ArrayList<>();
        ArrayList<Ingredient> ing = new ArrayList<>();
		try (Reader reader = Files.newBufferedReader(Paths.get(pfad), StandardCharsets.UTF_8);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
                for (CSVRecord csvRecord : csvParser) {
                    String name = csvRecord.get("Name");
                    String zutatenVoll = csvRecord.get("Zutaten");
                    String personen = csvRecord.get("Personen");
                    String kategorienVoll = csvRecord.get("Kategorien");
                    String zeit = csvRecord.get("Zeit");
                    String zubereitung = csvRecord.get("Zubereitung").replaceAll(";", "\n");
                    String rating = csvRecord.get("Rating").trim();

                    String[] zutatenString = zutatenVoll.split(";");

                    String[] kategorien = kategorienVoll.split(";");
                    Rezept rezept = new Rezept(name, ing, personen, kategorien, zeit, zubereitung,rating);
                    parseRecipeIngredients(rezept, zutatenString);
                    temp.add(rezept);
                }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return temp;
    } 

    // Untermethode, dass Rezeptbuchfenster erstellt und Buttons
    void initWindow() {
        rezeptBuchWindow = new JFrame("Rezeptbuch");
		rezeptBuchWindow.setSize(1280, 720);
        rezeptBuchWindow.setResizable(false);

        // wenn das Rezeptbuch geschlossen wird speichern wir die geänderten Daten
        // und setzen das Rezeptbuch zu !running zurück
        rezeptBuchWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                save(path, rezepte);
                unload();
            }
        });
		
        JPanel auswahlPanel = new JPanel(new GridLayout(0, 5, 20, 20));

        //erstellt für jedes Rezept im Rezeptbuch einen neuen Button
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

        //Labels (angezeigte Schrift) und Buttons
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
        //zum Panel hinzufügen
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
        //sichtbar machen
        rezeptBuchWindow.validate();
		rezeptBuchWindow.setVisible(true);

        suchButton.addActionListener(e -> {
            showSearch(auswahlPanel, suchNameTextField.getText(),suchKategorieTextField.getText());
        });

        //wenn Import-Button gedrückt wird, öffnet sich Fenster zum Dateien öffnen
        importButton.addActionListener(e -> {
            ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>();
            // JFileChooser erlaubt es uns leicht einen Pfad zu erhalten
            JFileChooser chooser = new JFileChooser("./app/src/main/resources");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Rezepte", "csv");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(rezeptBuchWindow);
            if(returnVal == JFileChooser.APPROVE_OPTION) {

                rezepte_temp = load(chooser.getSelectedFile().getPath());
            }
            rezepte.addAll(rezepte_temp);
            addRezepte(auswahlPanel, rezepte_temp);
            // an mehereren Orten speichern wir in eine alternative Datei
            // diese wird derzeit nicht benutzt, war aber sehr hilfreich bei Fehlermeldungen
            save("./src/main/resources/rezeptebuch_LIVE.csv", rezepte);
        });

        /* wenn Export-Button gedrückt wird, öffnet sich Fenster wo der Speicherort vom csv file festgelegt wird */
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
                JFileChooser chooser = new JFileChooser("./app/src/main/resources");
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

        //wenn Neu-Button gedrückt wird, erstellt man ein neues Rezept im Rezeptbuch
        neuButton.addActionListener(e -> {
            Rezept neu = new Rezept("neu", new ArrayList<>(), "0", new String[]{"Kategorien"}, "00:00:00", "Zubereitung","Unbewertet");
            ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>();
            rezepte_temp.add(neu);
            rezepte.add(neu);
            addRezepte(auswahlPanel, rezepte_temp);
            save("./src/main/resources/rezeptebuch_LIVE.csv", rezepte);
        });
    }

    // lässt die Rezeptknöpfe ein extra Rezeptfenster öffnen
    /* Layout vom Rezeptfenster */
    void setupButton(Rezept rezept, JButton button) {
        if (rezept_offen) {
            rezeptWindow.toFront();
            rezeptWindow.requestFocus();
            return;
        }
        rezept_offen = true;
        is_editable = false;

        rezeptWindow = new JFrame(rezept.name);
        rezeptWindow.setSize(800, 900);
        rezeptWindow.setResizable(false);

        //wenn Fenster geschlossen wird, wird nochmal gespeichert
        rezeptWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rezept_offen = false;
                save("./src/main/resources/rezeptebuch_LIVE.csv", rezepte);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel rezeptPanel = new JPanel();
        rezeptPanel.setLayout(new BoxLayout(rezeptPanel, BoxLayout.Y_AXIS));
        rezeptPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        JTextField nameField = new JTextField();
        JLabel ratingLabel = new JLabel("Bewertung:", SwingConstants.CENTER);
        JTextField ratingField = new JTextField();
        JLabel kategorienLabel = new JLabel("Kategorien", SwingConstants.CENTER);
        JTextField kategorienArea = new JTextField();
        JLabel zutatenLabel = new JLabel("Zutaten (bitte in dem Muster eingeben: ZahlMenge Name)", SwingConstants.CENTER);
        JTextArea zutatenArea = new JTextArea();
        JLabel personenLabel = new JLabel("Personen Anzahl", SwingConstants.CENTER);
        JTextField personenField = new JTextField();
        JLabel zeitLabel = new JLabel("Zubereitungs Dauer (Format DD:HH:MM)", SwingConstants.CENTER);
        JTextField zeitField = new JTextField();
        JLabel zubereitungLabel = new JLabel("Zubereitung", SwingConstants.CENTER);
        JTextArea zubereitungArea = new JTextArea();

        JScrollPane zubScroll = new JScrollPane(zubereitungArea);
        JScrollPane zutScroll = new JScrollPane(zutatenArea);

        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
        ratingField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
        personenField.setMaximumSize(new Dimension(Integer.MAX_VALUE, personenField.getPreferredSize().height));
        zeitField.setMaximumSize(new Dimension(Integer.MAX_VALUE, zeitField.getPreferredSize().height));

        kategorienArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, kategorienArea.getPreferredSize().height));
        zutatenArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, zutatenArea.getPreferredSize().height));

        nameField.setEditable(false);
        ratingField.setEditable(false);
        kategorienArea.setEditable(false);
        zutatenArea.setEditable(false);
        personenField.setEditable(false);
        zeitField.setEditable(false);
        zubereitungArea.setEditable(false);

        nameField.setText(rezept.name);
        ratingField.setText(rezept.rating);
        personenField.setText(rezept.personen);
        zeitField.setText(rezept.zeit);
        zubereitungArea.setText(rezept.zubereitung);
        zutatenArea.setText(Arrays.toString(rezept.ingredients.toArray()).replace("[",
                "").replace("]", "").replace(",", "\n"));
        kategorienArea.setText(String.join(",", rezept.kategorien));

        rezeptPanel.add(nameLabel);
        rezeptPanel.add(nameField);
        rezeptPanel.add(ratingLabel);
        rezeptPanel.add(ratingField);
        rezeptPanel.add(kategorienLabel);
        rezeptPanel.add(kategorienArea);
        rezeptPanel.add(zutatenLabel);
        rezeptPanel.add(zutScroll);
        rezeptPanel.add(personenLabel);
        rezeptPanel.add(personenField);
        rezeptPanel.add(zeitLabel);
        rezeptPanel.add(zeitField);
        rezeptPanel.add(zubereitungLabel);
        rezeptPanel.add(zubScroll);


        JPanel funktionenPanel = new JPanel();
        JButton editButton = new JButton("Editiermodus aktivieren");
        JButton saveButton = new JButton("Speichern");
        JButton exportButton = new JButton("Exportieren");
        JButton ratingButton = new JButton("Bewerten");
        JButton adjustmentButton = new JButton("Portionen Anpassen");

        funktionenPanel.add(editButton);
        funktionenPanel.add(saveButton);
        funktionenPanel.add(exportButton);
        funktionenPanel.add(ratingButton);
        funktionenPanel.add(adjustmentButton);

        mainPanel.add(rezeptPanel);
        mainPanel.add(funktionenPanel, BorderLayout.NORTH);

        rezeptWindow.getContentPane().add(mainPanel);

        rezeptWindow.setVisible(true);
        rezeptWindow.toFront();
        rezeptWindow.requestFocus();

        //wenn edit button gedrückt wird, sollen die Werte editierbar sein
        editButton.addActionListener(e -> {
            if (is_editable) {
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

        //wenn Speichern gedrückt wird, sollen neue werte gespeichert werden
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
                zeitField.setText(rezept.zeit);
            } else {
                rezept.zeit = zeit;
            }

            rezept.zubereitung = zubereitungArea.getText();

            rezept.kategorien = kategorienArea.getText().split(",");

            String[] temp = zutatenArea.getText().split("\n"); //

            parseRecipeIngredients(rezept, temp);


            save("./src/main/resources/rezeptebuch_LIVE.csv", rezepte);
        });

        //wenn man Rezept exportieren möchte, export button drücken und es erscheint ein Speicherpfad-Window
        exportButton.addActionListener(e -> {
            ArrayList<Rezept> rezepte_temp = new ArrayList<Rezept>();
            rezepte_temp.add(rezept);
            JFileChooser chooser = new JFileChooser("./app/src/main/resources");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Rezepte", "csv");
            chooser.setFileFilter(filter);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setSelectedFile(new File(rezept.name + ".csv"));
            int returnVal = chooser.showSaveDialog(rezeptWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                save(chooser.getSelectedFile().getPath(), rezepte_temp);
            }
        });

        //Bewerten-Button wurde gedrückt und neues Bewertungsfenster öffnet sich
        ratingButton.addActionListener(e -> {
            Object[] possibilities = {"Schlecht", "So-so", "Gut", "Sehr gut", "Favorit"};
            String rating = (String)JOptionPane.showInputDialog(
                    rezeptBuchWindow,
                    "Bewerten Sie dieses Rezept:\n",
                    "Rezept Bewerten",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    "Gut");
            if ((rating != null) && (rating.length() > 0)) {
                //todo
            }
            rezept.rating = rating;
            ratingField.setText(rating);
        });

        adjustmentButton.addActionListener(e -> {
            var desiredPortions = JOptionPane.showInputDialog("Wie viele Portionen wollen Sie haben?");
            int portions = Integer.parseInt(desiredPortions);
            for (Ingredient ingredient : rezept.ingredients ) {
                ingredient.adjustAmount(portions, Integer.parseInt(rezept.personen));
            }
            rezeptBuchWindow.revalidate();
            rezeptBuchWindow.repaint();
        });
    }

    // fügt Rezeptknöpfe hinzu
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

    // Hilfsmethode um herauszufinden, ob sich eine Kategorie im Rezept befindet
    boolean find_kategorie(Rezept rezept, String kategorien) {
        for (String kategorie : kategorien.split(",")) {
            for (String gibt : rezept.kategorien) {
                if (kategorie.toLowerCase().equals(gibt.toLowerCase()))
                    return true;
            }
        }
        
        return false;
    }

    void parseRecipeIngredients(Rezept rezept, String[] temp) {
        rezept.ingredients.clear();
        for (String ingredientString : temp) {
            ingredientString = ingredientString.trim();
            String[] dissassemble = ingredientString.split(" ");

            if(dissassemble.length > 1) {
                String amountnunit = dissassemble[0];
                String name = dissassemble[1];

                int amount = 0;
                String unit = "";
                String c = "";
                for (int i=0; i < amountnunit.length(); i++) {
                    c += amountnunit.charAt(i);
                    if(!c.matches("[0-9]+")){
                        unit = amountnunit.substring(i);
                        try {
                            amount = Integer.parseInt(amountnunit.substring(0,i));
                        }
                        catch (Exception ignored){}
                        break;
                    }
                }
                if(amount > 0)
                    rezept.ingredients.add(new Ingredient(amount,unit,name));
            }
        }
    }

    // ersetzt die Rezeptknöpfe mit den die in der Suche gezeigt werden sollen
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

    // speichert die Liste der Rezepte als CSV Datei im angegebenen Pfad
    void save(String pfad, ArrayList<Rezept> rez) {

        CSVFormat format = CSVFormat.DEFAULT;
        format.builder().setHeader("Name", "Zutaten", "Personen", "Kategorien", "Zeit", "Zubereitung", "Rating");
        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(pfad), StandardCharsets.UTF_8) ;
            CSVPrinter printer = new CSVPrinter(writer, format);
        ) {
            printer.printRecords(getRecords(rez));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // setzt die Klasse zurück, um Probleme mit wiederholtem Öffnen zu vermeiden
    void unload() {
        rezepte.clear();
        if (rezeptWindow != null)
            rezeptWindow.dispatchEvent(new WindowEvent(rezeptWindow, WindowEvent.WINDOW_CLOSING));
        running = false;
        rezeptBuchWindow = null;
        rezeptWindow = null;
    }

    // Hilfsmethode um Rezept in Stringform zu verwandeln und abzuspeichern
    ArrayList<String[]> getRecords(ArrayList<Rezept> rez) {
        ArrayList<String[]> records = new ArrayList<String[]>();

        String[] header = {"Name", "Zutaten", "Personen", "Kategorien", "Zeit", "Zubereitung", "Rating"};
        records.add(header);

        for (Rezept rezept : rez) {
            String ingredientList = Arrays.toString(rezept.ingredients.toArray()).replace("[", "").replace("]", "").replace(",", ";");
            String[] record = {rezept.name, String.join(";", ingredientList) , rezept.personen, String.join(";", rezept.kategorien), rezept.zeit, rezept.zubereitung.replaceAll("\n", ";"), rezept.rating};
            records.add(record);
        }
        return records;
    }
}

