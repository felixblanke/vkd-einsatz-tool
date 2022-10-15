package de.vkd.einsatz_tool.gui;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import de.vkd.auxiliary.Auxiliary;
import de.vkd.auxiliary.ComparatorChain;
import de.vkd.database.DatabaseReturnType;
import de.vkd.einsatz_tool.vkd.Einsatzbericht;
import de.vkd.einsatz_tool.vkd.EinsatzberichtLoadingException;
import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.Status;
import de.vkd.einsatz_tool.vkd.VK;
import de.vkd.framework.Framework;
import de.vkd.gui.CustomButton;
import de.vkd.gui.CustomTable;
import de.vkd.gui.CustomTableModel;
import de.vkd.gui.Dialog;
import de.vkd.gui.HintTextField;
import de.vkd.gui.SearchHintTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class Frame extends JFrame {
  private static final int INSETS_PNLCONTROL = 3;
  private static final int INSETS_PNLCONTROL2 = 5;
  private static final int INSETS_PNLCONTROL2_SIDES = 5;
  private static final int INSETS_PNLCONTROL2_RIGHT = 5;

  private static final int NAV_PANEL_VSTRUT = 30;

  private final ComparatorChain<VK> defaultChainEL;
  private final ComparatorChain<VK> defaultChainAL;
  private final ComparatorChain<VK> defaultChainBus;
  private final Main main;
  private final HashMap<String, Integer[]> filterCheckBoxMap = new HashMap<>();
  //COMPONENTS
  private JPanel pnlMain;
  private JPanel pnlPageTwo;
  private CardLayout cl;
  private JToggleButton btnShowSelected;
  //Table (showing the VK)
  private CustomTable<VK> tablePageOne;
  private TableColumn tableColumnDriver;
  //Menu
  private JMenuBar menuBar;
  private JMenu menuFiles;
  private JMenu menuDriverActions;
  private JMenuItem menuItemExportPath;
  private JMenuItem menuItemOpen;

  private JCheckBoxMenuItem menuItemBussePutzen;
  private JCheckBoxMenuItem menuItemOnlyFahrdienst;
  private JCheckBoxMenuItem menuItemShowDriverColumn;
  private JCheckBoxMenuItem menuItemShowOnlyDrivers;
  private CustomButton btnNextPanelPageTwo;
  private DateTimePicker beginPicker;
  private DateTimePicker endPicker;
  private JLabel lblSelectionCounter;
  private JLabel lblEL;
  private JLabel lblAL;
  private JLabel lblBus;
  private HintTextField htfName;
  private List<VK> listEL = new ArrayList<>();
  private List<VK> listAL = new ArrayList<>();
  private List<VK> listBus = new ArrayList<>();
  private CustomTable<VK> tablePageTwo;
  private boolean showSelected = false;
  private boolean showDriver = false;
  private String remark = "";

  private final void initializeFilterCheckboxMap() {
    /*
     * Integer[2]:
     * first pos determines used array:
     * 0: filter_group
     * 1: filter_pos
     * 2: filter_rank
     * 3: filter_func
     * sec pos determines array index
     */
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_1"), new Integer[] {0, 0});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_2"), new Integer[] {0, 1});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_3"), new Integer[] {0, 2});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_4"), new Integer[] {0, 3});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_5"), new Integer[] {0, 4});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_6"), new Integer[] {0, 5});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_L"), new Integer[] {0, 6});
    filterCheckBoxMap.put(main.getFramework().getString("GROUP_ER"), new Integer[] {0, 7});
    filterCheckBoxMap.put(main.getFramework().getString("POS_GL"), new Integer[] {1, 0});
    filterCheckBoxMap.put(main.getFramework().getString("POS_1STV"), new Integer[] {1, 1});
    filterCheckBoxMap.put(main.getFramework().getString("POS_2STV"), new Integer[] {1, 2});
    filterCheckBoxMap.put(main.getFramework().getString("POS_MANNSCHAFT_CHECKBOX"), new Integer[] {1, 3});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_VK"), new Integer[] {2, 0});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_OVK"), new Integer[] {2, 1});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_HVK"), new Integer[] {2, 2});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_HVKA"), new Integer[] {2, 3});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_UGL"), new Integer[] {2, 4});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_PGL"), new Integer[] {2, 5});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_GL"), new Integer[] {2, 6});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_OGL"), new Integer[] {2, 7});
    filterCheckBoxMap.put(main.getFramework().getString("RANK_HGL"), new Integer[] {2, 8});
  }

  public Frame(Main main) {
    this.main = main;
    initializeFilterCheckboxMap();
    this.defaultChainEL =
        new ComparatorChain<>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR,
            Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR);
    this.defaultChainAL =
        new ComparatorChain<>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR,
            Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR);
    this.defaultChainBus =
        new ComparatorChain<>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR,
            Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR);
    initComponents();

    boolean tryLoading = JOptionPane.showOptionDialog(
                Frame.this,
                main.getFramework().getString("DIALOG_LOADING_MESSAGE"),
                main.getFramework().getString("DIALOG_LOADING_TITLE"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{
                  main.getFramework().getString("DIALOG_LOADING_MESSAGE_OPT_NEW"),
                  main.getFramework().getString("DIALOG_LOADING_MESSAGE_OPT_LOAD"),
                },
                main.getFramework().getString("DIALOG_LOADING_MESSAGE_OPT_NEW")
              ) == JOptionPane.NO_OPTION;

    while (tryLoading) {
        boolean loadingResult = loadEinsatzbericht();
        if (loadingResult) break;
        else {
          tryLoading = JOptionPane.showOptionDialog(Frame.this,
                main.getFramework().getString("DIALOG_RETRY_LOADING_MESSAGE"),
                main.getFramework().getString("DIALOG_RETRY_LOADING_TITLE"),
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Ja", "Nein"},
                "Nein") == JOptionPane.YES_OPTION;
        }
    }
  }

  //TODO: Inverse Sorting (e.g. Z-A instead of A-Z)
  //TODO: UserConfig entwickeln.
  //TODO: RestoreDefault Möglichkeit aus der Config entwickeln
  //TODO: Lösung für 2 Vks mit selben Namen und Rang
  //TODO: MenuBar Hinzufügen
  //TODO: Default Sortierreihenfolge über Menubar festlegen (User Config?)
  //TODO: generell Eingabe von .csv in .cfg ändern, Einstellungen in dieser speichern
  //      (Oder SQLLite, JSON?)
  //TODO: Code kommentieren und aufräumen, Javadoc schreiben
  //TODO: ADD UNDO/REDO
  private void initComponents() {
    main.getLogger().log(Level.FINE, "Initializing Frame");
    //general:
    setTitle(main.getFramework().getString("FRAME_TITLE"));
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setResizable(true);
    try {
      setIconImage(new ImageIcon(
          ImageIO.read(Auxiliary.getResourceURLFromJAR(main.settings.getIconPath()))).getImage());
    } catch (IOException ex) {
      main.getLogger().log(Level.WARNING, "", ex);
    }
    setLayout(new BorderLayout());

    boolean showMenuBar = true;
    if (showMenuBar) {
      menuBar = new JMenuBar();
      menuFiles = new JMenu("Datei");
      menuItemExportPath = new JMenuItem();
      refreshSelectExportMenuItem();
      menuItemExportPath.addActionListener(e -> selectExportFolder());
      menuFiles.add(menuItemExportPath);

      menuItemOpen = new JMenuItem("Einsatzbericht öffnen");
      menuFiles.add(menuItemOpen);
      menuItemOpen.addActionListener(e -> loadEinsatzbericht());

      menuBar.add(menuFiles);

      menuDriverActions = new JMenu("Fahrdienst & Busse");

      menuItemBussePutzen = new JCheckBoxMenuItem(main.getFramework().getString("MENU_CHECK_BP"));
      menuDriverActions.add(menuItemBussePutzen);

      menuItemOnlyFahrdienst =
          new JCheckBoxMenuItem(main.getFramework().getString("MENU_CHECK_ONLY_FD"), true);
      menuDriverActions.add(menuItemOnlyFahrdienst);

      menuItemShowDriverColumn =
          new JCheckBoxMenuItem(main.getFramework().getString("MENU_CHECK_FD_COL"), false);
      menuDriverActions.add(menuItemShowDriverColumn);

      menuItemShowOnlyDrivers =
          new JCheckBoxMenuItem(main.getFramework().getString("BUTTON_SHOWDRIVER"), false);
      menuDriverActions.add(menuItemShowOnlyDrivers);

      menuBar.add(menuDriverActions);

      //menuItemBussePutzen.setMnemonic('B');
      //menuItemOnlyFahrdienst.setMnemonic('F');

      add(menuBar, "North");
    }
    pnlMain = new JPanel(cl = new CardLayout());

    //page one:
    JPanel pnlPageOne = new JPanel(new GridBagLayout());

    //PAGE ONE:
    JPanel pnlControl = new JPanel(new BorderLayout());

    JPanel pnlTemp = new JPanel();
    pnlTemp.setLayout(new BoxLayout(pnlTemp, BoxLayout.Y_AXIS));

    CustomButton btnPrevPanelPageOne = new CustomButton();
    btnPrevPanelPageOne.setText(main.getFramework().getString("BUTTON_PREVPANEL"));
    btnPrevPanelPageOne.setFocusPainted(false);
    btnPrevPanelPageOne.setAlignmentX(0);
    btnPrevPanelPageOne.setEnabled(false);

    CustomButton btnNextPanelPageOne = new CustomButton();
    btnNextPanelPageOne.setText(main.getFramework().getString("BUTTON_NEXTPANEL"));
    btnNextPanelPageOne.setFocusPainted(false);
    btnNextPanelPageOne.setAlignmentX(0);

    JPanel navPanelPageOne = new JPanel();
    navPanelPageOne.setLayout(new BoxLayout(navPanelPageOne, BoxLayout.Y_AXIS));

    navPanelPageOne.add(Box.createVerticalStrut(NAV_PANEL_VSTRUT));
    navPanelPageOne.add(btnPrevPanelPageOne);
    navPanelPageOne.add(Box.createVerticalStrut(NAV_PANEL_VSTRUT));
    navPanelPageOne.add(btnNextPanelPageOne);
    navPanelPageOne.add(Box.createVerticalGlue());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);

    JPanel pnlNavButtonsAuxOne = new JPanel(new GridBagLayout());
    pnlNavButtonsAuxOne.add(navPanelPageOne, gbc);
    pnlControl.add(pnlNavButtonsAuxOne, "East");

    //SELECTIONS:
    JPanel pnlFilter = new JPanel(new GridBagLayout());

    gbc = new GridBagConstraints();
    gbc.insets =
        new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
    gbc.anchor = GridBagConstraints.FIRST_LINE_START;

    new FilterCheckBox(main.getFramework().getString("POS_GL"), pnlFilter, gbc, 0, 0, 3, true);
    new FilterCheckBox(main.getFramework().getString("POS_1STV"), pnlFilter, gbc, 0, 1, 3, true);
    new FilterCheckBox(main.getFramework().getString("POS_2STV"), pnlFilter, gbc, 0, 2, 3, true);
    new FilterCheckBox(main.getFramework().getString("POS_MANNSCHAFT_CHECKBOX"), pnlFilter, gbc, 0, 3, 3, true);

    new FilterCheckBox(main.getFramework().getString("RANK_VK"), pnlFilter, gbc, 3, 0, 1, true);
    new FilterCheckBox(main.getFramework().getString("RANK_OVK"), pnlFilter, gbc, 3, 1, 1, true);
    new FilterCheckBox(main.getFramework().getString("RANK_HVK"), pnlFilter, gbc, 3, 2, 1, true);
    new FilterCheckBox(main.getFramework().getString("RANK_UGL"), pnlFilter, gbc, 3, 3, 1, true);

    new FilterCheckBox(main.getFramework().getString("RANK_PGL"), pnlFilter, gbc, 4, 0, 1, true);
    new FilterCheckBox(main.getFramework().getString("RANK_GL"), pnlFilter, gbc, 4, 1, 1, true);
    new FilterCheckBox(main.getFramework().getString("RANK_OGL"), pnlFilter, gbc, 4, 2, 1, true);
    new FilterCheckBox(main.getFramework().getString("RANK_HGL"), pnlFilter, gbc, 4, 3, 1, true);

    // get group numbers occuring in database, split into regular groups and special groups (i.e. Leitung and Einsatzreserve)
    Set<Integer> specialSet = new HashSet<>();
    Set<Integer> regularSet = new HashSet<>();
    for (VK vk: main.getDatabase()){
      if (vk.getGroup() <= 0) {
        specialSet.add(vk.getGroup());
      } else {
        regularSet.add(vk.getGroup());
      }
    }

    int regularGroupFilterRows = regularSet.size() % 2 == 0 ?  regularSet.size() / 2 : regularSet.size() / 2 + 1;

    List<Integer> regularList = new ArrayList<>(regularSet);
    Collections.sort(regularList);

    List<Integer> specialList = new ArrayList<>(specialSet);
    specialList.sort(Collections.reverseOrder());

    // fill up the first column in order, then the second.
    int index;
    for(index = 0; index < regularList.size(); index++) {
      new FilterCheckBox(Main.outputGroup(regularList.get(index)), pnlFilter, gbc, 5 + 3 * (index / regularGroupFilterRows), index % regularGroupFilterRows, 3, true);
    }

    // fill up from left to right
    for(int j = 0; j < specialList.size(); j++) {
      new FilterCheckBox(Main.outputGroup(specialList.get(j)), pnlFilter, gbc, 5 + 3 * ((j + regularList.size()) % 2), (j + regularList.size()) / 2, 3, true);
    }

    JPanel pnlTemp2 = new JPanel(new GridBagLayout());

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets =
        new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
    gbc.anchor = GridBagConstraints.CENTER;

    gbc.gridy = 0;
    //Buttons
    CustomButton btnSelectAll = new CustomButton();
    btnSelectAll.setText(main.getFramework().getString("BUTTON_SELECTALL"));
    btnSelectAll.setFocusPainted(false);
    pnlTemp2.add(btnSelectAll, gbc);

    gbc.insets =
        new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.gridy = 1;
    CustomButton btnDeSelectAll = new CustomButton();
    btnDeSelectAll.setText(main.getFramework().getString("BUTTON_DESELECTALL"));
    btnDeSelectAll.setFocusPainted(false);
    pnlTemp2.add(btnDeSelectAll, gbc);

    gbc.anchor = GridBagConstraints.CENTER;
    gbc.gridy = 2;
    btnShowSelected = new JToggleButton();
    btnShowSelected.setText(main.getFramework().getString("BUTTON_SHOWSELECTED"));
    btnShowSelected.setFocusPainted(false);
    pnlTemp2.add(btnShowSelected, gbc);

    gbc.anchor = GridBagConstraints.CENTER;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, 0, INSETS_PNLCONTROL);

    lblSelectionCounter = new JLabel();
    pnlTemp2.add(lblSelectionCounter, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
    gbc.gridy = 0;
    gbc.gridx = 11;
    gbc.weighty = 1;
    gbc.gridwidth = 3;
    gbc.gridheight = 5;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 0, 0, 0);
    pnlFilter.add(pnlTemp2, gbc);

    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridheight = 1;
    gbc.gridwidth = 11;
    gbc.insets = new Insets(0, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);

    JPanel subPanelSearch = new JPanel(new GridBagLayout());

    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.fill = GridBagConstraints.BOTH;
    gbc2.weightx = 1;
    gbc2.insets = new Insets(0, 0, 0, INSETS_PNLCONTROL);

    SearchHintTextField htfSearchPageOne =
        new SearchHintTextField(main.getFramework().getString("TF_SEARCH"), 1, 2, 3, 4, 5);
    subPanelSearch.add(htfSearchPageOne, gbc2);

    gbc2.insets = new Insets(0, INSETS_PNLCONTROL, 0, 0);
    gbc2.weightx = 0;

    pnlFilter.add(subPanelSearch, gbc);

    pnlControl.add(pnlFilter, "West");

    gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.BOTH;

    pnlPageOne.add(pnlControl, gbc);

    tablePageOne = new CustomTable<>(main.getFramework(), this, Main.getVKComparatorList());
    tablePageOne.setModel(new TableModelPageOne(
        new String[] {main.getFramework().getString("TABLE_SELECTION"),
            main.getFramework().getString("TABLE_GROUP"),
            main.getFramework().getString("TABLE_RANK"),
            main.getFramework().getString("TABLE_NAME"),
            main.getFramework().getString("TABLE_SURNAME"),
            main.getFramework().getString("TABLE_POS"),
            main.getFramework().getString("TABLE_DRIVER"),
            main.getFramework().getString("TABLE_ID")},
        new ComparatorChain<>(Main.VK_GROUP_COMPARATOR, Main.VK_POSITION_COMPARATOR,
            Main.VK_RANK_COMPARATOR, Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR),
        tablePageOne,
        lblSelectionCounter));

    tableColumnDriver = tablePageOne.getColumnModel().getColumn(6);
    // Hide Fahrdienst and ID columns
    tablePageOne.getColumnModel().removeColumn(tablePageOne.getColumnModel().getColumn(6));
    tablePageOne.getColumnModel().removeColumn(tablePageOne.getColumnModel().getColumn(6));
    tablePageOne.refreshTable();

    htfSearchPageOne.setTable(tablePageOne);

    tablePageOne.resizeColumnWidth();
    tablePageOne.setFillsViewportHeight(true);
    gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 1;
    JScrollPane scrollPanePageOne = new JScrollPane(tablePageOne);
    pnlPageOne.add(scrollPanePageOne, gbc);

    //Listener:
    btnSelectAll.addActionListener(e -> {
      for (int i = 0; i < tablePageOne.getRowCount(); i++) {
        tablePageOne.setValueAt(true, i, 0);
        main.getVK((int) tablePageOne.getModel().getValueAt(i, 7))
            .setSelected((boolean) tablePageOne.getModel().getValueAt(i, 0));
      }
      tablePageOne.refreshTable();
    });
    btnDeSelectAll.addActionListener(e -> {
      for (int i = 0; i < tablePageOne.getRowCount(); i++) {
        tablePageOne.setValueAt(false, i, 0);
        main.getVK((int) tablePageOne.getModel().getValueAt(i, 7))
            .setSelected((boolean) tablePageOne.getModel().getValueAt(i, 0));
      }
      tablePageOne.refreshTable();
    });

    btnShowSelected.addActionListener(e -> {
      menuItemShowOnlyDrivers.setSelected(false);
      showDriver = menuItemShowOnlyDrivers.isSelected();
      showSelected = btnShowSelected.isSelected();
      tablePageOne.refreshTable();
    });
    btnPrevPanelPageOne.addActionListener(e -> {
      cl.first(pnlMain);
      tablePageOne.refreshTable();
      tablePageTwo.refreshTable();
    });
    btnNextPanelPageOne.addActionListener(e -> {
      cl.last(pnlMain);

      menuItemShowDriverColumn.setEnabled(false);
      menuItemShowOnlyDrivers.setEnabled(false);

      tablePageOne.refreshTable();
      tablePageTwo.refreshTable();
    });

    menuItemShowDriverColumn.addActionListener(e -> {
      if (menuItemShowDriverColumn.isSelected()){
        tablePageOne.getColumnModel().addColumn(tableColumnDriver);
      } else {
        tablePageOne.getColumnModel().removeColumn(tableColumnDriver);
      }
      tablePageOne.refreshTable();
    });

    menuItemShowOnlyDrivers.addActionListener(e -> {
      btnShowSelected.setSelected(false);
      showDriver = menuItemShowOnlyDrivers.isSelected();
      showSelected = btnShowSelected.isSelected();
      tablePageOne.refreshTable();
    });

    pnlMain.add(pnlPageOne);
    //end of page one

    //page two:
    pnlPageTwo = new JPanel(new BorderLayout());

    //PAGE TWO:
    JPanel pnlControlTwo = new JPanel(new BorderLayout());

    //PrevPanel/NextPanel Buttons
    JPanel navPanelPageTwo = new JPanel();
    navPanelPageTwo.setLayout(new BoxLayout(navPanelPageTwo, BoxLayout.Y_AXIS));

    //Buttons
    CustomButton btnPrevPanelPageTwo = new CustomButton();
    btnPrevPanelPageTwo.setText(main.getFramework().getString("BUTTON_PREVPANEL"));
    btnPrevPanelPageTwo.setFocusPainted(false);
    btnPrevPanelPageTwo.setAlignmentX(0);

    btnNextPanelPageTwo = new CustomButton();
    btnNextPanelPageTwo.setText(main.getFramework().getString("BUTTON_NEXTPANEL"));
    btnNextPanelPageTwo.setFocusPainted(false);
    btnNextPanelPageTwo.setAlignmentX(0);

    navPanelPageTwo.add(Box.createVerticalStrut(NAV_PANEL_VSTRUT));
    navPanelPageTwo.add(btnPrevPanelPageTwo);
    navPanelPageTwo.add(Box.createVerticalStrut(NAV_PANEL_VSTRUT));
    navPanelPageTwo.add(btnNextPanelPageTwo);
    navPanelPageTwo.add(Box.createVerticalGlue());

    JPanel pnlNavButtonsAuxTwo = new JPanel(new GridBagLayout());

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
    pnlNavButtonsAuxTwo.add(navPanelPageTwo, gbc);
    pnlControlTwo.add(pnlNavButtonsAuxTwo, "East");

    JPanel pnlEinsatzInfo = new JPanel(new GridBagLayout());

    addHintTextFieldToPageTwo(htfName = new HintTextField(main.getFramework().getString("TF_NAME")),
        main.getFramework().getString("LABEL_NAME"), pnlEinsatzInfo);

    //pnlTime

    JPanel pnlTime = new JPanel(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.insets = new Insets(0, 0, 0, 2 * INSETS_PNLCONTROL2_SIDES);
    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
    gbc.weighty = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    DatePickerSettings dateSettings1 = new DatePickerSettings();
    TimePickerSettings timeSettings1 = new TimePickerSettings();

    dateSettings1.setAllowEmptyDates(false);
    timeSettings1.setAllowEmptyTimes(false);

    DatePickerSettings dateSettings2 = new DatePickerSettings();
    TimePickerSettings timeSettings2 = new TimePickerSettings();

    dateSettings2.setAllowEmptyDates(false);
    timeSettings2.setAllowEmptyTimes(false);

    beginPicker = new DateTimePicker(dateSettings1, timeSettings1);
    endPicker = new DateTimePicker(dateSettings2, timeSettings2);

    pnlTime.add(beginPicker, gbc);
    gbc.insets = new Insets(0, 2 * INSETS_PNLCONTROL2_SIDES, 0, 2 * INSETS_PNLCONTROL2_SIDES);
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.gridx = 1;
    gbc.weightx = 0;
    pnlTime.add(new JLabel(main.getFramework().getString("LABEL_BETWEEN")), gbc);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 2 * INSETS_PNLCONTROL2_SIDES, 0, 0);
    gbc.gridx = 2;
    pnlTime.add(endPicker, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.gridheight = 1;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_RIGHT);

    pnlEinsatzInfo.add(pnlTime, gbc);

    //Buttons

    lblEL = new JLabel();
    lblAL = new JLabel();
    lblBus = new JLabel();

    CustomButton btnEL = new CustomButton();
    addCustomButtonToPageTwo(main.getFramework().getString("BUTTON_EL"),
        main.getFramework().getString("LABEL_EL"), pnlEinsatzInfo, btnEL, lblEL, 3, listEL);

    CustomButton btnAL = new CustomButton();
    addCustomButtonToPageTwo(main.getFramework().getString("BUTTON_AL"),
        main.getFramework().getString("LABEL_AL"), pnlEinsatzInfo, btnAL, lblAL, 4, listAL);

    CustomButton btnBus = new CustomButton();
    addCustomButtonToPageTwo(main.getFramework().getString("BUTTON_BUS"),
        main.getFramework().getString("LABEL_BUS"), pnlEinsatzInfo, btnBus, lblBus, 5, listBus);


    pnlTemp = new JPanel(new GridBagLayout());

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_RIGHT);
    gbc.gridheight = 1;
    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 6;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0;
    SearchHintTextField htfSearchPageTwo;
    pnlEinsatzInfo.add(htfSearchPageTwo = new SearchHintTextField(
        main.getFramework().getString("TF_SEARCH"), 0, 1, 2, 4), gbc
    );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 0, 0, 5);
    gbc.weightx = 1;

    CustomButton btnDefaultStatus =
        new CustomButton(main.getFramework().getString("BUTTON_DEFAULT_STATUS"));
    btnDefaultStatus.setFocusPainted(false);
    pnlTemp.add(btnDefaultStatus, gbc);


    gbc.insets = new Insets(0, 5, 0, 0);
    gbc.gridx = 1;
    CustomButton btnAddRemark = new CustomButton(main.getFramework().getString("BUTTON_ADDREMARK"));
    btnAddRemark.setFocusPainted(false);
    pnlTemp.add(btnAddRemark, gbc);
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_RIGHT);
    gbc.gridheight = 1;
    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 6;
    gbc.gridx = 1;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 0;
    pnlEinsatzInfo.add(pnlTemp, gbc);

    pnlControlTwo.add(pnlEinsatzInfo, "Center");

    beginPicker.addDateTimeChangeListener(e -> {checkTimePicker(true);});
    endPicker.addDateTimeChangeListener(e -> {checkTimePicker(false);});

    btnPrevPanelPageTwo.addActionListener(e -> {
      cl.first(pnlMain);

      menuItemShowDriverColumn.setEnabled(true);
      menuItemShowOnlyDrivers.setEnabled(true);

      tablePageOne.refreshTable();
      tablePageTwo.refreshTable();
    });
    btnNextPanelPageTwo.addActionListener(e -> {
      if (pnlPageTwo.isVisible()) {
        Container c = btnNextPanelPageTwo.getParent();
        while (true) {
          if (c instanceof Frame) {
            break;
          } else {
            c = c.getParent();
          }
        }

        List<VK> selectedVK = new ArrayList<>();
        for (VK vk : main.getDatabase()) {
          if (vk.isSelected()) {
            selectedVK.add(vk);
          }
        }

        List<String> errorMsgList = new ArrayList<>();

        String begin =
            beginPicker.getDateTimePermissive().format(main.settings.getDateTimeFormatter()) + " "
                + beginPicker.getTimePicker().getTimeStringOrEmptyString();
        String end = endPicker.getDateTimePermissive().format(main.settings.getDateTimeFormatter())
            + " " + endPicker.getTimePicker().getTimeStringOrEmptyString();
        if (begin.equals(end)) {
          errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_SAME_BEGIN_END"));
        }
        if (htfName.getText().isEmpty()) {
          errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_NO_NAME"));
        }
        try {
          if (main.settings.getDateFormat().parse(begin).getTime()
              > main.settings.getDateFormat().parse(end).getTime()) {
            errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_BEGIN_AFTER_END"));
          }
        } catch (ParseException ex) {
          errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_BEGIN_AFTER_END"));
        }
        if (listEL.isEmpty() && listAL.isEmpty()) {
          errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_NO_EL_NOR_AL"));
        }
        if (selectedVK.isEmpty()) {
          errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_NO_VK"));
        }

        for (VK vk : listEL) {
          if (!vk.isSelected()) {
            errorMsgList.add(
                main.getFramework().getString("CREATION_EXCEPTION_EL_NOT_SELECTED") + " ("
                    + vk.getStringRepresentation() + ")");
          }
        }
        for (VK vk : listAL) {
          if (!vk.isSelected()) {
            errorMsgList.add(
                main.getFramework().getString("CREATION_EXCEPTION_AL_NOT_SELECTED") + " ("
                    + vk.getStringRepresentation() + ")");
          }
        }
        for (VK vk : listBus) {
          if (!vk.isSelected()) {
            errorMsgList.add(
                main.getFramework().getString("CREATION_EXCEPTION_BUS_NOT_SELECTED") + " ("
                    + vk.getStringRepresentation() + ")");
          }
        }

        for (VK vk : listEL) {
          if (!vk.hasAttendedEinsatz()) {
            errorMsgList.add(
                main.getFramework().getString("CREATION_EXCEPTION_EL_NOT_ASSIGNED") + " ("
                    + vk.getStringRepresentation() + " ist als " + vk.getStatus().getShortName()
                    + " eingetragen).");
          }
        }
        for (VK vk : listAL) {
          if (!vk.hasAttendedEinsatz()) {
            errorMsgList.add(
                main.getFramework().getString("CREATION_EXCEPTION_AL_NOT_ASSIGNED") + " ("
                    + vk.getStringRepresentation() + " ist als " + vk.getStatus().getShortName()
                    + " eingetragen).");
          }
        }
        for (VK vk : selectedVK) {
          if (vk.getStatus().equals(Status.NONE) && !listBus.contains(vk)) {
            errorMsgList.add(main.getFramework().getString("CREATION_EXCEPTION_VK_NO_STATUS") + " ("
                + vk.getStringRepresentation() + ").");
          }
        }
        if (!errorMsgList.isEmpty()) {
          String s = main.getFramework().getString("CREATION_EXCEPTION") + " \n";
          for (String str : errorMsgList) {
            s = s.concat("- " + str + "\n");
          }
          JOptionPane.showMessageDialog(c, s);
        } else {
          //Check, whether user is sure, that a driver who is not in Fahrdienst should drive a bus
          boolean ret = true;
          for (VK vk : listBus) {
            if (!vk.isDriver()) {
              ret = JOptionPane.showConfirmDialog(Frame.this,
                  main.getFramework().getString("OPTION_CONFIRM_NO_FD"),
                  main.getFramework().getString("OPTION_CONFIRM_NO_FD_TITLE"),
                  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
              break;
            }
          }
          if (!ret) {
            return;
          }

          //check if a driver without status was intentional

          //list of all drivers without a status
          List<VK> driverWithoutStatusList = new ArrayList<>();
          for (VK vk : listBus) {
            if (vk.getStatus().equals(Status.NONE)) {
              driverWithoutStatusList.add(vk);
            }
          }

          if (!driverWithoutStatusList.isEmpty()) {
            String listAsString = main.getStringFromVKList(driverWithoutStatusList, "\n");
            //ask user whether intentional
            ret = JOptionPane.showConfirmDialog(Frame.this,
                main.getFramework().getString("OPTION_CONFIRM_FD_NO_STATUS") + "\n" + listAsString,
                main.getFramework().getString("OPTION_CONFIRM_FD_NO_STATUS_TITLE"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
            if (!ret) {
              return;
            }
          }

          try {
            new Einsatzbericht(main, main.getFramework().getVersion(), htfName.getText(),
                main.settings.getDateFormat().parse(begin),
                main.settings.getDateFormat().parse(end),
                remark, selectedVK, listEL, listAL, listBus).createEinsatzbericht(
                menuItemBussePutzen.isSelected());
            JOptionPane.showMessageDialog(c, main.getFramework().getString("CREATION_SUCCESS"));
          } catch (FileNotFoundException ex) {
            main.getLogger().log(Level.WARNING, "", ex);
            JOptionPane.showMessageDialog(c, main.getFramework().getString("CREATION_EXCEPTION")
                + main.getFramework().getString("CREATION_EXCEPTION_FILE_NOT_FOUND"));
          } catch (ParseException | IOException ex) {
            main.getLogger().log(Level.SEVERE, "", ex);
          }
        }
      }
      cl.last(pnlMain);
      tablePageOne.refreshTable();
      tablePageTwo.refreshTable();
    });

    btnEL.addActionListener(createActionListener(btnEL,
        new String[] {main.getFramework().getString("TABLE_EL"),
            main.getFramework().getString("TABLE_RANK"),
            main.getFramework().getString("TABLE_NAME"),
            main.getFramework().getString("TABLE_SURNAME"),
            main.getFramework().getString("TABLE_POS"),
            main.getFramework().getString("TABLE_ID")},
        "LABEL_EL",
        lblEL, defaultChainEL, false, new ListHolder<VK>() {
          @Override
          List<VK> getWorkingList() {
            return listEL;
          }
        }));
    btnAL.addActionListener(createActionListener(btnAL,
        new String[] {main.getFramework().getString("TABLE_EL"),
            main.getFramework().getString("TABLE_RANK"),
            main.getFramework().getString("TABLE_NAME"),
            main.getFramework().getString("TABLE_SURNAME"),
            main.getFramework().getString("TABLE_POS"),
            main.getFramework().getString("TABLE_ID")},
        "LABEL_AL",
        lblAL, defaultChainAL, false, new ListHolder<VK>() {
          @Override
          List<VK> getWorkingList() {
            return listAL;
          }
        }));
    btnBus.addActionListener(createActionListener(btnBus,
        new String[] {main.getFramework().getString("TABLE_BUS"),
            main.getFramework().getString("TABLE_RANK"),
            main.getFramework().getString("TABLE_NAME"),
            main.getFramework().getString("TABLE_SURNAME"),
            main.getFramework().getString("TABLE_POS"),
            main.getFramework().getString("TABLE_ID")},
        "LABEL_BUS",
        lblBus, defaultChainBus, true, new ListHolder<VK>() {
          @Override
          List<VK> getWorkingList() {
            return listBus;
          }
        }));

    btnAddRemark.addActionListener(e -> {
      Container c = btnNextPanelPageTwo.getParent();
      while (true) {
        if (c instanceof Frame) {
          break;
        } else {
          c = c.getParent();
        }
      }
      Dialog d = new Dialog((Frame) c, main.getFramework().getString("FRAME_TITLE"));
      d.setLayout(new GridBagLayout());
      GridBagConstraints localGbc = new GridBagConstraints();
      localGbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2,
          INSETS_PNLCONTROL2);
      localGbc.anchor = GridBagConstraints.FIRST_LINE_START;
      JLabel lblRemark = new JLabel("Bemerkung: ");
      d.add(lblRemark, localGbc);

      localGbc.gridy = 1;
      localGbc.fill = GridBagConstraints.HORIZONTAL;

      CustomButton btnAccept = new CustomButton(main.getFramework().getString("BUTTON_ACCEPT"));
      btnAccept.setFocusPainted(false);
      d.add(btnAccept, localGbc);

      localGbc.gridy = 2;

      CustomButton btnCancel = new CustomButton(main.getFramework().getString("BUTTON_CANCEL"));
      btnCancel.setFocusPainted(false);
      d.add(btnCancel, localGbc);

      localGbc.gridy = 0;
      localGbc.fill = GridBagConstraints.BOTH;
      localGbc.gridx = 1;
      localGbc.weightx = 1;
      localGbc.weighty = 1;
      localGbc.gridheight = GridBagConstraints.REMAINDER;

      JTextPane remarkHintTextPane = new JTextPane();

      StyleContext context = new StyleContext();
      StyledDocument document = new DefaultStyledDocument(context);
      Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
      StyleConstants.setAlignment(style, StyleConstants.ALIGN_JUSTIFIED);
      remarkHintTextPane.setDocument(document);
      if (main.settings.getRemarkFont() != null) {
        remarkHintTextPane.setFont(main.settings.getRemarkFont().deriveFont(11.5f));
      }

      remarkHintTextPane.setText(remark);

      JScrollPane htaScrollPane = new JScrollPane(remarkHintTextPane);
      d.add(htaScrollPane, localGbc);

      btnAccept.addActionListener(e1 -> {
        remark = remarkHintTextPane.getText();
        d.dispose();
      });
      btnCancel.addActionListener(e1 -> d.dispose());

      d.setPreferredSize(new Dimension(600, 300));
      d.pack();
      d.setLocationRelativeTo(c);
      d.setVisible(true);
    });

    pnlPageTwo.add(pnlControlTwo, "North");

    tablePageTwo = new CustomTable<>(main.getFramework(), this, Main.getVKComparatorList());

    tablePageTwo.setModel(new TableModelPageTwo(
        new String[] {main.getFramework().getString("TABLE_RANK"),
            main.getFramework().getString("TABLE_NAME"),
            main.getFramework().getString("TABLE_SURNAME"),
            main.getFramework().getString("TABLE_DRIVER"),
            main.getFramework().getString("TABLE_STATUS"),
            main.getFramework().getString("TABLE_KUERZUNG"),
            main.getFramework().getString("TABLE_REMARK"),
            main.getFramework().getString("TABLE_ID")},
        new ComparatorChain<>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR,
            Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR), tablePageTwo));

    tablePageTwo.getColumnModel().removeColumn(tablePageTwo.getColumnModel().getColumn(7));

    //initializing column 4 with JComboBoxes
    TableColumn col = tablePageTwo.getColumnModel().getColumn(4);
    JComboBox<String> statusBox = new JComboBox<>();
    for (Status s : Status.values()) {
      statusBox.addItem(s.getShortName());
    }
    col.setCellEditor(new DefaultCellEditor(statusBox));

    tablePageTwo.refreshTable();
    JScrollPane scrollPanePageTwo = new JScrollPane(tablePageTwo);

    tablePageTwo.resizeColumnWidth();
    tablePageTwo.setFillsViewportHeight(true);
    htfSearchPageTwo.setTable(tablePageTwo);

    tablePageTwo.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
          if (e.getSource() instanceof CustomTable<?>) {
            @SuppressWarnings("unchecked") CustomTable<VK> target = (CustomTable<VK>) e.getSource();
            int column = target.columnAtPoint(e.getPoint());
            if (column == 5) {
              int row = target.rowAtPoint(e.getPoint());
              if (row >= 0) {
                Container c = tablePageTwo.getParent();
                while (true) {
                  if (c instanceof Frame) {
                    break;
                  } else {
                    c = c.getParent();
                  }
                }
                VK vk = main.getVK((int) target.getModel().getValueAt(row, 7));
                KuerzungDialog d = new KuerzungDialog(main.getFramework(), (Frame) c,
                    main.getFramework().getString("FRAME_TITLE"), vk);
                d.setVisible(true);
                tablePageTwo.refreshTable();
              }
            } else if (column == 6) {
              int row = target.rowAtPoint(e.getPoint());
              if (row >= 0) {
                Container c = tablePageTwo.getParent();
                while (true) {
                  if (c instanceof Frame) {
                    break;
                  } else {
                    c = c.getParent();
                  }
                }
                VK vk = main.getVK((int) target.getModel().getValueAt(row, 7));
                RemarkDialog d = new RemarkDialog(main.getFramework(), (Frame) c,
                    main.getFramework().getString("FRAME_TITLE"), vk);
                d.setVisible(true);
                tablePageTwo.refreshTable();
              }
            }
          }
        }
      }
    });

    JMenuItem menuItemStatusDialog = new JMenuItem(main.getFramework().getString("MNU_ADD_STATUS"));
    JMenuItem menuItemRemarkDialog = new JMenuItem(main.getFramework().getString("MNU_ADD_REMARK"));
    JMenuItem menuItemKuerzungDialog = new JMenuItem(main.getFramework().getString("MNU_ADD_KUERZUNG"));
    menuItemStatusDialog.addActionListener(e -> {
      if (tablePageTwo.getSelectedRow() >= 0) {
        Container c = tablePageTwo.getParent();
        while (true) {
          if (c instanceof Frame) {
            break;
          } else {
            c = c.getParent();
          }
        }
        VK vk = main.getVK((int) tablePageTwo.getModel().getValueAt(tablePageTwo.getSelectedRow(), 7));
        StatusDialog d =
            new StatusDialog(main.getFramework(), (Frame) c,
                main.getFramework().getString("FRAME_TITLE"),
                vk);
        d.setVisible(true);
        tablePageTwo.refreshTable();
      }
    });

    menuItemKuerzungDialog.addActionListener(e -> {
      if (tablePageTwo.getSelectedRow() >= 0) {
        Container c = tablePageTwo.getParent();
        while (true) {
          if (c instanceof Frame) {
            break;
          } else {
            c = c.getParent();
          }
        }
        VK vk = main.getVK((int) tablePageTwo.getModel().getValueAt(tablePageTwo.getSelectedRow(), 7));
        KuerzungDialog d =
            new KuerzungDialog(main.getFramework(), (Frame) c,
                main.getFramework().getString("FRAME_TITLE"),
                vk);
        d.setVisible(true);
        tablePageTwo.refreshTable();
      }
    });

    menuItemRemarkDialog.addActionListener(e -> {
      if (tablePageTwo.getSelectedRow() >= 0) {
        Container c = tablePageTwo.getParent();
        while (true) {
          if (c instanceof Frame) {
            break;
          } else {
            c = c.getParent();
          }
        }
        VK vk = main.getVK((int) tablePageTwo.getModel().getValueAt(tablePageTwo.getSelectedRow(), 7));
        RemarkDialog d =
            new RemarkDialog(main.getFramework(), (Frame) c,
                main.getFramework().getString("FRAME_TITLE"),
                vk);
        d.setVisible(true);
        tablePageTwo.refreshTable();
      }
    });
    tablePageTwo.addMenuItem(menuItemStatusDialog);
    tablePageTwo.addMenuItem(menuItemKuerzungDialog);
    tablePageTwo.addMenuItem(menuItemRemarkDialog);

    btnDefaultStatus.addActionListener(e -> {
      for (VK vk : main.getDatabase()) {
        if (vk.isSelected()) {
          if (vk.getStatus().equals(Status.NONE)) {
            vk.setStatus(Status.EINGETEILT);
            tablePageTwo.refreshTable();
          }
        }
      }
    });
    pnlPageTwo.add(scrollPanePageTwo, "Center");
    pnlMain.add(pnlPageTwo);

    add(pnlMain, "Center");
    pack();
    setMinimumSize(getPreferredSize());
    main.getLogger().log(Level.FINE, "Frame initialized");
  }

  private void checkTimePicker(boolean beginWasChanged) {
    if (beginPicker.getDatePicker().getDate().compareTo(endPicker.getDatePicker().getDate()) > 0) {
      if (beginWasChanged) {
        endPicker.getDatePicker().setDate(beginPicker.getDatePicker().getDate());
      } else {
        beginPicker.getDatePicker().setDate(endPicker.getDatePicker().getDate());
      }
    }

    if (beginPicker.getDatePicker().getDate().compareTo(endPicker.getDatePicker().getDate()) == 0 && beginPicker.getTimePicker().getTime().compareTo(endPicker.getTimePicker().getTime()) > 0) {
      if (beginWasChanged) {
        endPicker.getTimePicker().setTime(beginPicker.getTimePicker().getTime());
      } else {
        beginPicker.getTimePicker().setTime(endPicker.getTimePicker().getTime());
      }
    }
  }

  private void refreshSelectExportMenuItem() {
    this.menuItemExportPath.setText(
      main.getFramework().getString("MENU_SELECT_EXPORT_PREFIX") +
      main.settings.getExportPath() +
      main.getFramework().getString("MENU_SELECT_EXPORT_SUFFIX")
    );
  }

  private void addHintTextFieldToPageTwo(HintTextField htf, String lblText, JPanel parent) {
    //suppose parent uses GridBagLayout
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.FIRST_LINE_START;
    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_SIDES);
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.weighty = 0;

    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    JLabel lbl = new JLabel();
    lbl.setText(lblText);
    parent.add(lbl, gbc);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_RIGHT);

    parent.add(htf, gbc);
  }

  private void addCustomButtonToPageTwo(String btnTxt, String lblText, JPanel parent,
                                        CustomButton btn, JLabel lbl, int y, List<VK> list) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_RIGHT);
    gbc.gridheight = 1;
    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.BOTH;

    gbc.gridy = y;

    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0;

    btn.setText(btnTxt);
    btn.setFocusPainted(false);
    parent.add(btn, gbc);

    gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2,
        INSETS_PNLCONTROL2_RIGHT);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;

    lbl.setText(lblText + main.getStringFromVKList(list));
    lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    parent.add(lbl, gbc);
  }

  private boolean loadEinsatzbericht() {
    JFileChooser fileChooser = new JFileChooser(".");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setFileFilter(new FileFilter() {
      @Override
      public String getDescription() {
        return ".xls Dateien";
      }

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".xls");
      }
    });
    int status = fileChooser.showOpenDialog(this);
    if (status == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      Container c = btnNextPanelPageTwo.getParent();
      while (true) {
        if (c instanceof Frame) {
          break;
        } else {
          c = c.getParent();
        }
      }
      try {
        //TODO:
        //BUG: report throws Exception, if the underlying Einsatzbericht an older GuZ uses.
        //TODO: maybe create a unique code for each GuZ, so that it can be distinguished,
        // which report uses what
        Einsatzbericht report = new Einsatzbericht(main, selectedFile);
        beginPicker.setDateTimePermissive(
            LocalDateTime.ofInstant(report.getBegin().toInstant(), ZoneId.systemDefault()));
        endPicker.setDateTimePermissive(
            LocalDateTime.ofInstant(report.getEnd().toInstant(), ZoneId.systemDefault()));

        List<VK> database = main.getCleanDatabase();
        for (VK vkReport : report.getSelectedVK()) {
          boolean vkFound = false;
          for (VK vk : database) {
            if (vk.getId() == vkReport.getId()) {
              database.remove(vk);
              database.add(vkReport);
              vkFound = true;
              break;
            }
          }
          if (!vkFound) {
            throw new EinsatzberichtLoadingException(selectedFile.getAbsolutePath(),
                "The Einsatzbericht could not be loaded correctly.", -1, -1);
          }
        }
        main.getFramework().setDatabase(new DatabaseReturnType<>(database, report.getVersion()));

        remark = report.getRemark();
        listEL = report.getListEL();
        listAL = report.getListAL();
        listBus = report.getListBus();

        listEL = Framework.sort(listEL, defaultChainEL);
        lblEL.setText(
            main.getFramework().getString("LABEL_EL").concat(main.getStringFromVKList(listEL)));

        listAL = Framework.sort(listAL, defaultChainAL);
        lblAL.setText(
            main.getFramework().getString("LABEL_AL").concat(main.getStringFromVKList(listAL)));

        listBus = Framework.sort(listBus, defaultChainBus);
        lblBus.setText(
            main.getFramework().getString("LABEL_BUS").concat(main.getStringFromVKList(listBus)));

        htfName.setText(report.getName());

        tablePageOne.refreshTable();
        tablePageTwo.refreshTable();
        JOptionPane.showMessageDialog(c, main.getFramework().getString("LOADING_SUCCESS"));
        return true;
      } catch (Exception ex) {
        main.getLogger().log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(c, main.getFramework().getString("LOADING_EXCEPTION"));
      }
    }
    return false;
  }

  private boolean selectExportFolder() {
    JFileChooser fileChooser = new JFileChooser(main.settings.getExportPath());
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int status = fileChooser.showOpenDialog(this);
    if (status == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      try {
        assert selectedFile.exists() && selectedFile.isDirectory();
        main.settings.setExportPath(selectedFile.getPath());
        refreshSelectExportMenuItem();
        return true;
      } catch (Exception ex) {
        main.getLogger().log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, main.getFramework().getString("SELECT_EXPORT_EXCEPTION"));
      }
    }
    return false;
  }

  private ActionListener createActionListener(CustomButton dialogCause,
                                              String[] columnNames, /*String userErrorStringVal,*/
                                              String labelStartStringVal,
                                              //List<VK> workingList,
                                              JLabel outputLabel, ComparatorChain<VK> defaultChain,
                                              boolean isDriverDialog, ListHolder<VK> listHolder) {
    return e -> {
      Container parentWindow = dialogCause.getParent();
      while (true) {
        if (parentWindow instanceof Window) {
          break;
        } else {
          parentWindow = parentWindow.getParent();
        }
      }
      ButtonDialog d =
          new ButtonDialog(main, (Window) parentWindow,
              main.getFramework().getString("FRAME_TITLE"),
              columnNames, /*userErrorStringVal,*/ labelStartStringVal, listHolder.getWorkingList(),
              outputLabel, new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2,
              INSETS_PNLCONTROL2), defaultChain,
              isDriverDialog && menuItemOnlyFahrdienst.getState()); //State true: Only Fahrdienst
      d.setVisible(true);
    };
  }

  DateTimePicker getBeginPicker() {
      return beginPicker;
  }

  DateTimePicker getEndPicker() {
      return endPicker;
  }

  private abstract static class ListHolder<T> { //No (Function) Pointer in Java
    abstract List<T> getWorkingList();
  }

  class FilterCheckBox extends JCheckBox {
    public FilterCheckBox(String stringName, JPanel parent, GridBagConstraints gbc, int x, int y,
                          int width, boolean selectedAtStart) {
      super(stringName);
      this.setFocusPainted(false);
      this.setSelected(selectedAtStart);
      gbc.gridx = x;
      gbc.gridy = y;
      gbc.gridwidth = width;

      addActionListener(e -> {
        filterCheckBoxSelected(filterCheckBoxMap.get(stringName), isSelected());
        if (stringName.equals("RANK_HVK")) {
          filterCheckBoxSelected(filterCheckBoxMap.get("RANK_HVKA"), isSelected());
        }
      });
      parent.add(this, gbc);
    }

    private void filterCheckBoxSelected(Integer[] filterInformation, boolean isSelected) {
      switch (filterInformation[0]) {
        case 0:
          main.setFilter_Group(filterInformation[1], isSelected);
          break;
        case 1:
          main.setFilter_Pos(filterInformation[1], isSelected);
          break;
        case 2:
          main.setFilter_Rank(filterInformation[1], isSelected);
          break;
        default:
          main.getLogger()
              .log(Level.WARNING, "Cannot parse filter information" + filterInformation[0]);
          break;
      }
      tablePageOne.refreshTable();
    }
  }

  //++++++++++++
  //TABLE MODELS
  //++++++++++++

  class TableModelPageOne extends CustomTableModel<VK> {

    private JLabel lblSelectionCounter;
    public TableModelPageOne(String[] columnNames, ComparatorChain<VK> defaultChain,
                             CustomTable<VK> table, JLabel lblSelectionCounter) {
      super(columnNames, defaultChain, table);
      this.lblSelectionCounter = lblSelectionCounter;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0 || columnIndex == 6) {
        return Boolean.class;
      }
      if (columnIndex == 7) {
        return Integer.class;
      }
      return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return column == 0;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void setValueAt(Object value, int row, int column) {
      if (value instanceof Boolean && column == 0) {
        Vector rowData = (Vector) getDataVector().get(row);
        rowData.set(column, value);
        main.getVK((int) getValueAt(row, 7)).setSelected((boolean) value);
        refreshTable();
      }
    }

    @Override
    public void refreshTable() {
      List<VK> l;
      if (showSelected || showDriver) {
        l = new ArrayList<>();
        for (VK vk : main.getDatabase()) {
          if (showSelected && vk.isSelected() || showDriver && vk.isDriver()) {
            l.add(vk);
          }
        }
        l = sort(l);
      } else {
        l = sort(main.applyFilter(main.getDatabase()));
      }

      this.setRowCount(0);

      int selectedCount = 0;
      Object[][] rowData = new Object[l.size()][this.getColumnCount()];
      for (int i = 0; i < l.size(); i++) {
        VK vk = l.get(i);
        rowData[i][0] = vk.isSelected();
        rowData[i][1] = Main.outputGroup(vk.getGroup());
        rowData[i][2] = Main.getRankString(vk.getRank());
        rowData[i][3] = vk.getName();
        rowData[i][4] = vk.getSurname();
        rowData[i][5] = Main.outputPosition(vk.getPosition());
        rowData[i][6] = vk.isDriver();
        rowData[i][7] = vk.getId();
      }
      for (VK vk : main.getDatabase()) {
        if(vk.isSelected()) selectedCount++;
      }
      for (Object[] o : rowData) {
        this.addRow(o);
      }
      this.lblSelectionCounter.setText(main.getFramework().getString("LABEL_CURRENTLY_SELECTED") + selectedCount);
      super.refreshTable();
    }
  }

  class TableModelPageTwo extends CustomTableModel<VK> {
    //"Rang", "Vorname", "Nachname", "Fahrdienst", "Teilgenommen", "Bemerkung", "ID"
    public TableModelPageTwo(String[] s, ComparatorChain<VK> defaultChain, CustomTable<VK> table) {
      super(s, defaultChain, table);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 3) {
        return Boolean.class;
      }
      if (columnIndex == 7) {
        return Integer.class;
      }
      return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return column == 4;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void setValueAt(Object value, int row, int column) {
      if (value instanceof String && column == 4) {
        Vector rowData = (Vector) getDataVector().get(row);
        rowData.set(column, value);
        main.getVK((int) getValueAt(row, 7)).setStatus(Status.getStatusByShortName((String) value));
        refreshTable();
        if (Status.getStatusByShortName((String) value) == Status.ERSATZ) {
          Container c = tablePageTwo.getParent();
          while (true) {
            if (c instanceof Frame) {
              break;
            } else {
              c = c.getParent();
            }
          }
          VK vk = main.getVK((int) getValueAt(row, 7));
          StatusDialog d =
              new StatusDialog(main.getFramework(), (Frame) c,
                  main.getFramework().getString("FRAME_TITLE"),
                  vk);
          d.setVisible(true);
          refreshTable();
        }
      }
    }

    @Override
    public void refreshTable() {
      List<VK> l = new ArrayList<>();
      for (VK vk : main.getDatabase()) {
        if (vk.isSelected()) {
          l.add(vk);
        }
      }
      l = sort(l);

      this.setRowCount(0);
      Object[][] rowData = new Object[l.size()][this.getColumnCount()];
      for (int i = 0; i < l.size(); i++) {
        VK vk = l.get(i);
        rowData[i][0] = Main.getRankString(vk.getRank());
        rowData[i][1] = vk.getName();
        rowData[i][2] = vk.getSurname();
        rowData[i][3] = vk.isDriver();
        rowData[i][4] = vk.getStatus().getShortName();
        rowData[i][5] = vk.getKuerzungsListe().isEmpty() ? "" : vk.getKuerzungsListe().size();
        if (vk.hasIndividualTimes()) {
          String remarkString = "";
          if (!vk.getRemark().trim().isEmpty()) {
            remarkString += vk.getRemark() + ", ";
          }

          String beginDate = vk.getBeginDateTime().format(main.settings.getDateTimeFormatter());
          String endDate = vk.getEndDateTime().format(main.settings.getDateTimeFormatter());

          String beginTime = vk.getBeginDateTime().toLocalTime().toString();
          String endTime = vk.getEndDateTime().toLocalTime().toString();

          if (beginDate.equals(endDate)) {
            rowData[i][6] = remarkString + beginDate + " " + beginTime + "–" + endTime;
          } else {
            rowData[i][6] = remarkString + beginDate + " " + beginTime + " – " + endDate + " " + endTime;
          }
        } else {
          rowData[i][6] = vk.getRemark();
        }
        rowData[i][7] = vk.getId();
      }
      for (Object[] o : rowData) {
        this.addRow(o);
      }
      super.refreshTable();
    }
  }
}
