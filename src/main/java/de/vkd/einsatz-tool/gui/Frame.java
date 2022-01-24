package de.vkd.einsatz_tool.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

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
import javax.swing.JPopupMenu;
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

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import de.vkd.auxiliary.ComparatorChain;
import de.vkd.database.DatabaseReturnType;
import de.vkd.framework.Framework;
import de.vkd.gui.CustomButton;
import de.vkd.gui.CustomTable;
import de.vkd.gui.CustomTableModel;
import de.vkd.gui.Dialog;
import de.vkd.gui.HintTextField;
import de.vkd.gui.SearchHintTextField;
import de.vkd.einsatz_tool.vkd.Einsatzbericht;
import de.vkd.einsatz_tool.vkd.EinsatzberichtLoadingException;
import de.vkd.einsatz_tool.vkd.Main;
import de.vkd.einsatz_tool.vkd.Status;
import de.vkd.einsatz_tool.vkd.VK;

@SuppressWarnings("serial")
public class Frame extends JFrame{

    private static final int WIDTH = 770;
    private static final int HEIGHT = 636;
    private static final int HEIGHT_OF_PNLCONTROL = 150;
    private static final int HEIGHT_OF_PNLCONTROLTWO = 220;
    private static final int INSETS_PNLCONTROL = 3;
    private static final int INSETS_PNLCONTROL2 = 5;
    private static final int INSETS_PNLCONTROL2_SIDES = 5;
    private static final int INSETS_PNLCONTROL2_RIGHT = 5;

    private static final int NAV_BTN_PREF_WIDTH = 100;

    private ComparatorChain<VK> defaultChainEL;
    private ComparatorChain<VK> defaultChainAL;
    private ComparatorChain<VK> defaultChainBus;


//COMPONENTS
    private JPanel pnlMain;
    private JPanel pnlPageOne;
    private JPanel pnlPageTwo;

    private CardLayout cl;

//PAGE ONE:
    private JPanel pnlControl;

//SELECTIONS:
    private JPanel pnlFilter;
//Buttons
    private CustomButton btnSelectAll;
    private CustomButton btnDeSelectAll;
    private JToggleButton btnShowSelected;
    private JToggleButton btnShowDriver;
    private CustomButton btnPrevPanelPageOne;
    private CustomButton btnNextPanelPageOne;
    private CustomButton btnOtherActions;
//Table (showing the VK)
    private CustomTable<VK> table_pageOne;
    private JScrollPane scrollPane_pageOne;
//Labels

//Menu
    private JPopupMenu menuOtherActions;
    private JMenuItem menuItemOpenFile;
    private JMenuItem menuItemRemarkDialog;
    private JCheckBoxMenuItem menuItemBussePutzen;
    private JCheckBoxMenuItem menuItemOnlyFahrdienst;


    private SearchHintTextField htfSearchPageOne;
//PAGE TWO:
    private JPanel pnlControlTwo;
//Buttons
    private CustomButton btnPrevPanelPageTwo;
    private CustomButton btnNextPanelPageTwo;

    private DateTimePicker beginPicker;
    private DateTimePicker endPicker;
    private CustomButton btnEL;
    private JLabel lblEL;
    private CustomButton btnAL;
    private JLabel lblAL;
    private CustomButton btnBus;
    private JLabel lblBus;

    private CustomButton btnAddRemark;
    private CustomButton btnDefaultStatus;

    private HintTextField htfName;
    private SearchHintTextField htfSearchPageTwo;

    private List<VK> listEL = new ArrayList<VK>();
    private List<VK> listAL = new ArrayList<VK>();
    private List<VK> listBus = new ArrayList<VK>();


    private CustomTable<VK> table_pageTwo;
    private JScrollPane scrollPane_pageTwo;

    private Main m;
    private boolean showSelected = false;
    private boolean showDriver = false;
    private boolean showMenuBar = false;

    private String remark = "";

    private HashMap<String, Integer[]> filterCheckBoxMap = new HashMap<String, Integer[]>();
    {
        /*
         * Integer[2]:
         * first pos determines used array:
         * 0: filter_group
         * 1: filter_pos
         * 2: filter_rank
         * 3: filter_func
         * sec pos determines array index
         */
        filterCheckBoxMap.put("GROUP_1", new Integer[]{0, 0});
        filterCheckBoxMap.put("GROUP_2", new Integer[]{0, 1});
        filterCheckBoxMap.put("GROUP_3", new Integer[]{0, 2});
        filterCheckBoxMap.put("GROUP_4", new Integer[]{0, 3});
        filterCheckBoxMap.put("GROUP_5", new Integer[]{0, 4});
        filterCheckBoxMap.put("GROUP_6", new Integer[]{0, 5});
        filterCheckBoxMap.put("GROUP_L", new Integer[]{0, 6});
        filterCheckBoxMap.put("GROUP_ER", new Integer[]{0, 7});
        filterCheckBoxMap.put("POS_GL", new Integer[]{1, 0});
        filterCheckBoxMap.put("POS_1STV", new Integer[]{1, 1});
        filterCheckBoxMap.put("POS_2STV", new Integer[]{1, 2});
        filterCheckBoxMap.put("POS_MANNSCHAFT_CHECKBOX", new Integer[]{1, 3});
        filterCheckBoxMap.put("RANK_VK", new Integer[]{2, 0});
        filterCheckBoxMap.put("RANK_OVK", new Integer[]{2, 1});
        filterCheckBoxMap.put("RANK_HVK", new Integer[]{2, 2});
        filterCheckBoxMap.put("RANK_HVKA", new Integer[]{2, 3});
        filterCheckBoxMap.put("RANK_UGL", new Integer[]{2, 4});
        filterCheckBoxMap.put("RANK_PGL", new Integer[]{2, 5});
        filterCheckBoxMap.put("RANK_GL", new Integer[]{2, 6});
        filterCheckBoxMap.put("RANK_OGL", new Integer[]{2, 7});
        filterCheckBoxMap.put("RANK_HGL", new Integer[]{2, 8});
    }

    public Frame(Main m) {
        this.m = m;
        this.defaultChainEL = new ComparatorChain<VK>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR, Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR);
        this.defaultChainAL = new ComparatorChain<VK>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR, Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR);
        this.defaultChainBus = new ComparatorChain<VK>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR, Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR);
        initComponents();
    }
    //TODO: Inverse Sorting (e.g. Z-A instead of A-Z)
    //TODO: UserConfig entwickeln.
    //TODO: RestoreDefault M�glichkeit aus der Config entwickeln
    //TODO: L�sung f�r 2 Vks mit selben Namen und Rang
    //TODO: MenuBar Hinzuf�gen
    //TODO: Default Sortierreihenfolge �ber Menubar festlegen (User Config?)
    //TODO: generell Eingabe von .csv in .cfg �ndern, Einstellungen in dieser speichern (Oder SQLLite, JSON?)
    //TODO: Code kommentieren und aufr�umen, Javadoc schreiben
    //TODO: ADD UNDO/REDO
    private void initComponents() {
        m.getLogger().log(Level.FINE, "Initializing Frame");
        //general:
        setTitle(m.getFramework().getString("FRAME_TITLE"));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setIconImage(new ImageIcon(m.settings.getIconPath()).getImage());
        setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        if(showMenuBar){
            JMenuBar menuBar = new JMenuBar();
            JMenu menu1 = new JMenu("Datei");
            JMenuItem item1 = new JMenuItem("Speicherort");
            menu1.add(item1);
            menuBar.add(menu1);

            add(menuBar, "North");
        }
        pnlMain = new JPanel();
        cl = new CardLayout();
        pnlMain.setLayout(cl);

        JPanel navPanelPageOne;
        JPanel navPanelPageTwo;


        //page one:
        pnlPageOne = new JPanel();
        pnlPageOne.setLayout(new GridBagLayout());//(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        pnlPageOne.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        pnlControl = new JPanel();
        //TODO: CLEAN UP CODE: REMOVE PREFERRED SIZE
        pnlControl.setPreferredSize(new Dimension((int) getContentPane().getPreferredSize().getWidth(), HEIGHT_OF_PNLCONTROL));
        pnlControl.setLayout(new BorderLayout());

        JPanel pnlTemp = new JPanel();
        pnlTemp.setLayout(new BoxLayout(pnlTemp, BoxLayout.Y_AXIS));

        btnPrevPanelPageOne = new CustomButton();
        btnPrevPanelPageOne.setText(m.getFramework().getString("BUTTON_PREVPANEL"));
        btnPrevPanelPageOne.setFocusPainted(false);
        btnPrevPanelPageOne.setAlignmentX(0);

        btnNextPanelPageOne = new CustomButton();
        btnNextPanelPageOne.setText(m.getFramework().getString("BUTTON_NEXTPANEL"));
        btnNextPanelPageOne.setFocusPainted(false);
        btnNextPanelPageOne.setAlignmentX(0);

        navPanelPageOne = new JPanel();
        navPanelPageOne.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weighty = 1;
        gbc.weightx = 1;
        navPanelPageOne.add(btnPrevPanelPageOne, gbc);

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        navPanelPageOne.add(btnNextPanelPageOne, gbc);
        int h = (HEIGHT_OF_PNLCONTROL - (btnPrevPanelPageOne.getPreferredSize().height + btnNextPanelPageOne.getPreferredSize().height))/3;

        navPanelPageOne.setPreferredSize(new Dimension(NAV_BTN_PREF_WIDTH, navPanelPageOne.getPreferredSize().height));

        pnlTemp.add(Box.createVerticalStrut(h));
        pnlTemp.add(navPanelPageOne);
        pnlTemp.add(Box.createVerticalStrut(h));

        pnlControl.add(pnlTemp, "East");

        pnlFilter = new JPanel();
        pnlFilter.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        new FilterCheckBox("GROUP_1", pnlFilter, gbc, 0, 0, 3, true);
        new FilterCheckBox("GROUP_2", pnlFilter, gbc, 0, 1, 3, true);
        new FilterCheckBox("GROUP_3", pnlFilter, gbc, 0, 2, 3, true);
        new FilterCheckBox("GROUP_L", pnlFilter, gbc, 0, 3, 3, true);


        new FilterCheckBox("GROUP_4", pnlFilter, gbc, 3, 0, 3, true);
        new FilterCheckBox("GROUP_5", pnlFilter, gbc, 3, 1, 3, true);
        new FilterCheckBox("GROUP_6", pnlFilter, gbc, 3, 2, 3, true);
        new FilterCheckBox("GROUP_ER", pnlFilter, gbc, 3, 3, 3, true);

        new FilterCheckBox("POS_GL", pnlFilter, gbc, 6, 0, 3, true);
        new FilterCheckBox("POS_1STV", pnlFilter, gbc, 6, 1, 3, true);
        new FilterCheckBox("POS_2STV", pnlFilter, gbc, 6, 2, 3, true);
        new FilterCheckBox("POS_MANNSCHAFT_CHECKBOX", pnlFilter, gbc, 6, 3, 3, true);

        new FilterCheckBox("RANK_VK", pnlFilter, gbc, 9, 0, 1, true);
        new FilterCheckBox("RANK_OVK", pnlFilter, gbc, 9, 1, 1, true);
        new FilterCheckBox("RANK_HVK", pnlFilter, gbc, 9, 2, 1, true);
        new FilterCheckBox("RANK_UGL", pnlFilter, gbc, 9, 3, 1, true);

        new FilterCheckBox("RANK_PGL", pnlFilter, gbc, 10, 0, 1, true);
        new FilterCheckBox("RANK_GL", pnlFilter, gbc, 10, 1, 1, true);
        new FilterCheckBox("RANK_OGL", pnlFilter, gbc, 10, 2, 1, true);
        new FilterCheckBox("RANK_HGL", pnlFilter, gbc, 10, 3, 1, true);



        JPanel pnlTemp2 = new JPanel();
        pnlTemp2.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        btnSelectAll = new CustomButton();
        btnSelectAll.setText(m.getFramework().getString("BUTTON_SELECTALL"));
        btnSelectAll.setFocusPainted(false);
        pnlTemp2.add(btnSelectAll, gbc);

        gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 1;
        btnDeSelectAll = new CustomButton();
        btnDeSelectAll.setText(m.getFramework().getString("BUTTON_DESELECTALL"));
        btnDeSelectAll.setFocusPainted(false);
        pnlTemp2.add(btnDeSelectAll, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 2;
        btnShowSelected = new JToggleButton();
        btnShowSelected.setText(m.getFramework().getString("BUTTON_SHOWSELECTED"));
        btnShowSelected.setFocusPainted(false);
        pnlTemp2.add(btnShowSelected, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 3;
        gbc.insets = new Insets(INSETS_PNLCONTROL, INSETS_PNLCONTROL, 0, INSETS_PNLCONTROL);

        btnShowDriver = new JToggleButton();
        btnShowDriver.setText(m.getFramework().getString("BUTTON_SHOWDRIVER"));
        btnShowDriver.setFocusPainted(false);
        pnlTemp2.add(btnShowDriver, gbc);


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
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.gridwidth = 11;
        gbc.insets = new Insets(0, INSETS_PNLCONTROL, INSETS_PNLCONTROL, INSETS_PNLCONTROL);
        htfSearchPageOne = new SearchHintTextField(m.getFramework().getString("TF_SEARCH"), 1, 2, 3, 4, 5);

        JPanel subPanelSearch = new JPanel();
        subPanelSearch.setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 1;
        gbc2.insets = new Insets(0, 0, 0, INSETS_PNLCONTROL);

        subPanelSearch.add(htfSearchPageOne, gbc2);

        gbc2.insets = new Insets(0, INSETS_PNLCONTROL, 0, 0);
        gbc2.weightx = 0;

        btnOtherActions = new CustomButton("Sonstiges");

        menuOtherActions = new JPopupMenu();
        menuItemOpenFile = new JMenuItem(m.getFramework().getString("MENU_OPEN_FILE"));
        menuOtherActions.add(menuItemOpenFile);

        menuItemBussePutzen = new JCheckBoxMenuItem(m.getFramework().getString("MENU_CHECK_BP"));
        menuOtherActions.add(menuItemBussePutzen);

        menuItemOnlyFahrdienst = new JCheckBoxMenuItem(m.getFramework().getString("MENU_CHECK_ONLY_FD"), true);
        menuOtherActions.add(menuItemOnlyFahrdienst);

//        menuItemOpenFile.setMnemonic('O');
//        menuItemBussePutzen.setMnemonic('B');
//        menuItemOnlyFahrdienst.setMnemonic('F');

        subPanelSearch.add(btnOtherActions, gbc2);

        pnlFilter.add(subPanelSearch, gbc);

        pnlControl.add(pnlFilter, "West");

        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;

        pnlPageOne.add(pnlControl, gbc);

        table_pageOne = new CustomTable<VK>(m.getFramework(), this, Main.getVKComparatorList());
        table_pageOne.setModel(
                new TableModelPageOne(
                        new String[]{
                                m.getFramework().getString("TABLE_SELECTION"),
                                m.getFramework().getString("TABLE_GROUP"),
                                m.getFramework().getString("TABLE_RANK"),
                                m.getFramework().getString("TABLE_POS"),
                                m.getFramework().getString("TABLE_NAME"),
                                m.getFramework().getString("TABLE_SURNAME"),
                                m.getFramework().getString("TABLE_DRIVER"),
                                m.getFramework().getString("TABLE_ID")},
                        new ComparatorChain<VK>(Main.VK_GROUP_COMPARATOR, Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR, Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR),
                        table_pageOne));
        table_pageOne.refreshTable();
        scrollPane_pageOne = new JScrollPane(table_pageOne);

        htfSearchPageOne.setTable(table_pageOne);

        table_pageOne.resizeColumnWidth();
        table_pageOne.setFillsViewportHeight(true);
//        scrollPane_pageOne.setPreferredSize(new Dimension((int)getContentPane().getPreferredSize().getWidth(),
//                (int) (getContentPane().getPreferredSize().getHeight()-pnlControl.getPreferredSize().getHeight())));
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        pnlPageOne.add(scrollPane_pageOne, gbc);

        //Listener:
        btnSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < table_pageOne.getRowCount(); i++){
                    table_pageOne.setValueAt(true, i, 0);
                    m.getVK((int)table_pageOne.getValueAt(i, 7)).setSelected((boolean)table_pageOne.getValueAt(i, 0));
                }
                table_pageOne.refreshTable();
            }
        });
        btnDeSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < table_pageOne.getRowCount(); i++){
                    table_pageOne.setValueAt(false, i, 0);
                    m.getVK((int)table_pageOne.getValueAt(i, 7)).setSelected((boolean)table_pageOne.getValueAt(i, 0));
                }
                table_pageOne.refreshTable();
            }
        });
        btnShowDriver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnShowSelected.setSelected(false);
                showDriver = btnShowDriver.isSelected();
                showSelected = btnShowSelected.isSelected();
                table_pageOne.refreshTable();
            }
        });
        btnShowSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnShowDriver.setSelected(false);
                showDriver = btnShowDriver.isSelected();
                showSelected = btnShowSelected.isSelected();
                table_pageOne.refreshTable();
            }
        });
        btnPrevPanelPageOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.first(pnlMain);
                table_pageOne.refreshTable();
                table_pageTwo.refreshTable();
            }
        });
        btnNextPanelPageOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.last(pnlMain);

                table_pageOne.refreshTable();
                table_pageTwo.refreshTable();
            }
        });
        btnOtherActions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuOtherActions.show(btnOtherActions, btnOtherActions.getWidth()/2, btnOtherActions.getHeight()/2);
            }
        });

        pnlMain.add(pnlPageOne);
        //end of page one

        //page two:
        pnlPageTwo = new JPanel();
        pnlPageTwo.setLayout(new BorderLayout());

        pnlControlTwo = new JPanel();
        pnlControlTwo.setPreferredSize(new Dimension((int) getContentPane().getPreferredSize().getWidth(), HEIGHT_OF_PNLCONTROLTWO));
        pnlControlTwo.setLayout(new BorderLayout());

        //PrevPanel/NextPanel Buttons
        navPanelPageTwo = new JPanel();
        navPanelPageTwo.setLayout(new BoxLayout(navPanelPageTwo, BoxLayout.Y_AXIS));

        btnPrevPanelPageTwo = new CustomButton();
        btnPrevPanelPageTwo.setText(m.getFramework().getString("BUTTON_PREVPANEL"));
        btnPrevPanelPageTwo.setFocusPainted(false);
        btnPrevPanelPageTwo.setAlignmentX(0);

        btnNextPanelPageTwo = new CustomButton();
        btnNextPanelPageTwo.setText(m.getFramework().getString("BUTTON_NEXTPANEL"));
        btnNextPanelPageTwo.setFocusPainted(false);
        btnNextPanelPageTwo.setAlignmentX(0);


        JPanel pnlNavButtonsAux = new JPanel();
        pnlNavButtonsAux.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weighty = 1;
        gbc.weightx = 1;
        pnlNavButtonsAux.add(btnPrevPanelPageTwo, gbc);

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        pnlNavButtonsAux.add(btnNextPanelPageTwo, gbc);


        int marginNavButtons = (HEIGHT_OF_PNLCONTROL - (btnPrevPanelPageTwo.getPreferredSize().height + btnNextPanelPageTwo.getPreferredSize().height))/3;

        navPanelPageTwo.add(Box.createVerticalStrut(marginNavButtons));
        navPanelPageTwo.add(pnlNavButtonsAux);
        navPanelPageTwo.add(Box.createVerticalStrut(marginNavButtons + HEIGHT_OF_PNLCONTROLTWO - HEIGHT_OF_PNLCONTROL));

        navPanelPageTwo.setPreferredSize(new Dimension(NAV_BTN_PREF_WIDTH, navPanelPageTwo.getPreferredSize().height));

        pnlControlTwo.add(navPanelPageTwo, "East");

        JPanel pnlEinsatzInfo = new JPanel();
        pnlEinsatzInfo.setLayout(new GridBagLayout());

        pnlEinsatzInfo.setPreferredSize(new Dimension(WIDTH,//-74,
                HEIGHT_OF_PNLCONTROL));


        addHintTextFieldToPageTwo(htfName = new HintTextField(m.getFramework().getString("TF_NAME")), m.getFramework().getString("LABEL_NAME"),  pnlEinsatzInfo, 0);

        //pnlTime

        JPanel pnlTime = new JPanel();
        pnlTime.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 2*INSETS_PNLCONTROL2_SIDES);
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
        gbc.insets = new Insets(0, 2*INSETS_PNLCONTROL2_SIDES, 0, 2*INSETS_PNLCONTROL2_SIDES);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 1;
        gbc.weightx = 0;
        pnlTime.add(new JLabel(m.getFramework().getString("LABEL_BETWEEN")), gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 2*INSETS_PNLCONTROL2_SIDES, 0, 0);
        gbc.gridx = 2;
        pnlTime.add(endPicker, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);
        gbc.gridheight = 1;
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);

        pnlEinsatzInfo.add(pnlTime, gbc);

        //Buttons
        btnEL = new CustomButton();
        btnAL = new CustomButton();
        btnBus = new CustomButton();
        lblEL = new JLabel();
        lblAL = new JLabel();
        lblBus = new JLabel();

        addCustomButtonToPageTwo(m.getFramework().getString("BUTTON_EL"),
                m.getFramework().getString("LABEL_EL"),
                pnlEinsatzInfo, btnEL, lblEL, 3, listEL);

        addCustomButtonToPageTwo(m.getFramework().getString("BUTTON_AL"),
                m.getFramework().getString("LABEL_AL"),
                pnlEinsatzInfo, btnAL, lblAL, 4, listAL);

        addCustomButtonToPageTwo(m.getFramework().getString("BUTTON_BUS"),
                m.getFramework().getString("LABEL_BUS"),
                pnlEinsatzInfo, btnBus, lblBus, 5, listBus);



        pnlTemp = new JPanel();
        pnlTemp.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        pnlEinsatzInfo.add(htfSearchPageTwo = new SearchHintTextField(m.getFramework().getString("TF_SEARCH"), 0, 1, 2, 4), gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.weightx = 1;

        btnDefaultStatus = new CustomButton(m.getFramework().getString("BUTTON_DEFAULT_STATUS"));
        btnDefaultStatus.setFocusPainted(false);
        pnlTemp.add(btnDefaultStatus, gbc);


        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.gridx = 1;
        btnAddRemark = new CustomButton(m.getFramework().getString("BUTTON_ADDREMARK"));
        btnAddRemark.setFocusPainted(false);
        pnlTemp.add(btnAddRemark, gbc);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        pnlEinsatzInfo.add(pnlTemp, gbc);

        pnlControlTwo.add(pnlEinsatzInfo, "Center");


        btnPrevPanelPageTwo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.first(pnlMain);
                table_pageOne.refreshTable();
                table_pageTwo.refreshTable();
            }
        });
        btnNextPanelPageTwo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pnlPageTwo.isVisible()){
                    Container c = btnNextPanelPageTwo.getParent();
                    while(true){
                        if(c instanceof Frame)break;
                        else c = c.getParent();
                    }
                    String begin = "";
                    String end = "";
                    begin = beginPicker.getDateTimePermissive().format(m.settings.getDateTimeFormatter()) + " " +
                            beginPicker.getTimePicker().getTimeStringOrEmptyString();

                    end = endPicker.getDateTimePermissive().format(m.settings.getDateTimeFormatter()) + " " +
                            endPicker.getTimePicker().getTimeStringOrEmptyString();

                    List<VK> selectedVK = new ArrayList<VK>();
                    for(VK vk: m.getDatabase()){
                        if(vk.isSelected())selectedVK.add(vk);
                    }

                    List<String> errorMsgList = new ArrayList<String>();

                    if(htfName.getText().isEmpty()) errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_NO_NAME"));
                    if(begin.isEmpty()) errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_NO_BEGIN"));
                    if(end.isEmpty()) errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_NO_END"));
                    if(begin.equals(end)) errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_SAME_BEGIN_END"));
                    try {
                        if(m.settings.getDateFormat().parse(begin).getTime() > m.settings.getDateFormat().parse(end).getTime())
                            errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_BEGIN_AFTER_END"));
                    } catch (ParseException ex) {
                        errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_BEGIN_AFTER_END"));
                    }
                    if(listEL.isEmpty() && listAL.isEmpty()) {
                        errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_NO_EL_NOR_AL"));
                    }
                    if(selectedVK.isEmpty()) errorMsgList.add(m.getFramework().getString("CREATION_EXCEPTION_NO_VK"));

                    for(VK vk : listEL){
                        if(!vk.isSelected())errorMsgList.add(
                                m.getFramework().getString("CREATION_EXCEPTION_EL_NOT_SELECTED")
                                + " (" + vk.getStringRepresentation() + ")");
                    }
                    for(VK vk : listAL){
                        if(!vk.isSelected())errorMsgList.add(
                                m.getFramework().getString("CREATION_EXCEPTION_AL_NOT_SELECTED")
                                + " (" + vk.getStringRepresentation() + ")");
                    }
                    for(VK vk : listBus){
                        if(!vk.isSelected())errorMsgList.add(
                                m.getFramework().getString("CREATION_EXCEPTION_BUS_NOT_SELECTED")
                                + " (" + vk.getStringRepresentation() + ")");
                    }

                    for(VK vk : listEL){
                        if(!vk.hasAttendedEinsatz())errorMsgList.add(
                                m.getFramework().getString("CREATION_EXCEPTION_EL_NOT_ASSIGNED")
                                + " (" + vk.getStringRepresentation() + " ist als " + vk.getStatus().getShortName() + " eingetragen).");
                    }
                    for(VK vk : listAL){
                        if(!vk.hasAttendedEinsatz())errorMsgList.add(
                                m.getFramework().getString("CREATION_EXCEPTION_AL_NOT_ASSIGNED")
                                + " (" + vk.getStringRepresentation() + " ist als " + vk.getStatus().getShortName() + " eingetragen).");
                    }
                    for(VK vk: selectedVK){
                        if(vk.getStatus().equals(Status.NONE) && !listBus.contains(vk))
                            errorMsgList.add(
                                    m.getFramework().getString("CREATION_EXCEPTION_VK_NO_STATUS")
                                    + " (" + vk.getStringRepresentation() + ").");
                    }
                    if(!errorMsgList.isEmpty()){
                        String s = m.getFramework().getString("CREATION_EXCEPTION") + " \n";
                        for(String str: errorMsgList)s = s.concat("- " + str + "\n");
                        JOptionPane.showMessageDialog(c, s);
                    }else{
                        //Check, whether user is sure, that a driver who is not in the Fahrdienst should drive a bus
                        boolean ret = true;
                        for(VK vk: listBus) {
                            if(!vk.isDriver()) {
                                ret = JOptionPane.showConfirmDialog(Frame.this, m.getFramework().getString("OPTION_CONFIRM_NO_FD"),
                                        m.getFramework().getString("OPTION_CONFIRM_NO_FD_TITLE"),
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
                                break;
                            }
                        }
                        if(!ret)return;

                        //check if a driver without status was intentional

                        //list of all drivers without a status
                        List<VK> driverWithoutStatusList = new ArrayList<VK>();
                        for(VK vk: listBus) {
                            if(vk.getStatus().equals(Status.NONE)) driverWithoutStatusList.add(vk);
                        }

                        if(!driverWithoutStatusList.isEmpty()) {
                            String listAsString = m.getStringFromVKList(driverWithoutStatusList, "\n");
                            //ask user whether intentional
                            ret = JOptionPane.showConfirmDialog(Frame.this, m.getFramework().getString("OPTION_CONFIRM_FD_NO_STATUS") + "\n" + listAsString,
                                    m.getFramework().getString("OPTION_CONFIRM_FD_NO_STATUS_TITLE"),
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
                            if(!ret)return;
                        }

                        try {

                            new Einsatzbericht(
                                    m,
                                    m.getFramework().getVersion(),
                                    htfName.getText(),
                                    m.settings.getDateFormat().parse(begin),
                                    m.settings.getDateFormat().parse(end),
                                    remark,
                                    selectedVK,
                                    listEL,
                                    listAL,
                                    listBus)
                                .createEinsatzbericht(menuItemBussePutzen.isSelected());
                            JOptionPane.showMessageDialog(c, m.getFramework().getString("CREATION_SUCCESS"));
                        } catch (ParseException ex) {
                            m.getLogger().log(Level.SEVERE, "", ex);
                        } catch(FileNotFoundException ex){
                            m.getLogger().log(Level.WARNING, "", ex);
                            JOptionPane.showMessageDialog(c, m.getFramework().getString("CREATION_EXCEPTION")
                                    + m.getFramework().getString("CREATION_EXCEPTION_FILE_NOT_FOUND"));
                        } catch (IOException ex) {
                            m.getLogger().log(Level.SEVERE, "", ex);
                        }
                    }
                }
                cl.last(pnlMain);
                table_pageOne.refreshTable();
                table_pageTwo.refreshTable();
            }
        });

        btnEL.addActionListener(createActionListener(
                btnEL,
                new String[]{
                        m.getFramework().getString("TABLE_EL"),
                        m.getFramework().getString("TABLE_RANK"),
                        m.getFramework().getString("TABLE_POS"),
                        m.getFramework().getString("TABLE_NAME"),
                        m.getFramework().getString("TABLE_SURNAME"),
                        m.getFramework().getString("TABLE_ID"),
                        },
//                "USER_ERROR_NO_EL_SELECTED",
                "LABEL_EL",
//                listEL,
                lblEL,
                defaultChainEL,
                false,
                new ListHolder<VK>() {
                    @Override
                    List<VK> getWorkingList() {
                        return listEL;
                    }
                }));
        btnAL.addActionListener(createActionListener(
                btnAL,
                new String[]{
                        m.getFramework().getString("TABLE_EL"),
                        m.getFramework().getString("TABLE_RANK"),
                        m.getFramework().getString("TABLE_POS"),
                        m.getFramework().getString("TABLE_NAME"),
                        m.getFramework().getString("TABLE_SURNAME"),
                        m.getFramework().getString("TABLE_ID"),
                },
//                "USER_ERROR_NO_AL_SELECTED",
                "LABEL_AL",
//                listAL,
                lblAL,
                defaultChainAL,
                false,
                new ListHolder<VK>() {
                    @Override
                    List<VK> getWorkingList() {
                        return listAL;
                    }
                }));
        btnBus.addActionListener(createActionListener(
                btnBus,
                new String[]{
                        m.getFramework().getString("TABLE_BUS"),
                        m.getFramework().getString("TABLE_RANK"),
                        m.getFramework().getString("TABLE_POS"),
                        m.getFramework().getString("TABLE_NAME"),
                        m.getFramework().getString("TABLE_SURNAME"),
                        m.getFramework().getString("TABLE_ID"),
                },
//                "USER_ERROR_NO_DRIVER_SELECTED",
                "LABEL_BUS",
//                listBus,
                lblBus,
                defaultChainBus,
                true,
                new ListHolder<VK>() {
                    @Override
                    List<VK> getWorkingList() {
                        return listBus;
                    }
                }));

        btnAddRemark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container c = btnNextPanelPageTwo.getParent();
                while(true){
                    if(c instanceof Frame)break;
                    else c = c.getParent();
                }
                Dialog d = new Dialog((Frame) c, m.getFramework().getString("FRAME_TITLE"));
                d.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2);
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                JLabel lblRemark = new JLabel("Bemerkung: ");
                d.add(lblRemark, gbc);

                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                CustomButton btnOK = new CustomButton(m.getFramework().getString("BUTTON_ACCEPT"));
                btnOK.setFocusPainted(false);
                d.add(btnOK, gbc);

                gbc.gridy = 2;

                CustomButton btnCancel = new CustomButton(m.getFramework().getString("BUTTON_CANCEL"));
                btnCancel.setFocusPainted(false);
                d.add(btnCancel, gbc);

                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = 1;
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.gridheight = GridBagConstraints.REMAINDER;


                JTextPane remarkHintTextPane = new JTextPane();
//                    hta.setText(WordUtils.wrap(remark, 100));



//                    hta.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
//                    SimpleAttributeSet attribs = new SimpleAttributeSet();
//                    StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_JUSTIFIED);
//                    hta.setParagraphAttributes(attribs, true);
//                    StyleConstants.setFontFamily(attribs, Main.f.getFamily());
//                    StyleConstants.setFontSize(attribs, 11);

                StyleContext context = new StyleContext();
                StyledDocument document = new DefaultStyledDocument(context);
                Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_JUSTIFIED);
                remarkHintTextPane.setDocument(document);
                if(m.settings.getRemarkFont() != null)remarkHintTextPane.setFont(m.settings.getRemarkFont().deriveFont(11.5f));

                remarkHintTextPane.setText(remark);

                JScrollPane hta_scrollPane = new JScrollPane(remarkHintTextPane);
                d.add(hta_scrollPane, gbc);

                btnOK.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        remark = remarkHintTextPane.getText();
                        d.dispose();
                    }
                });
                btnCancel.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        d.dispose();
                    }
                });

                d.setPreferredSize(new Dimension(600, 300));
                d.pack();
                d.setLocationRelativeTo(c);
                d.setVisible(true);

            }
        });

        pnlPageTwo.add(pnlControlTwo, "North");

        table_pageTwo = new CustomTable<VK>(m.getFramework(), this, Main.getVKComparatorList());

        table_pageTwo.setModel(
                new TableModelPageTwo(
                        new String[]{
                                m.getFramework().getString("TABLE_RANK"),
                                m.getFramework().getString("TABLE_NAME"),
                                m.getFramework().getString("TABLE_SURNAME"),
                                m.getFramework().getString("TABLE_DRIVER"),
                                m.getFramework().getString("TABLE_STATUS"),
                                m.getFramework().getString("TABLE_KUERZUNG"),
                                m.getFramework().getString("TABLE_ID")
                                },
                        new ComparatorChain<VK>(Main.VK_POSITION_COMPARATOR, Main.VK_RANK_COMPARATOR, Main.VK_NAME_COMPARATOR, Main.VK_SURNAME_COMPARATOR),
                        table_pageTwo));
        //initializing column 4 with JComboBoxes
        TableColumn col = table_pageTwo.getColumnModel().getColumn(4);
        JComboBox<String> statusBox = new JComboBox<String>();
        for(Status s: Status.values()){
            statusBox.addItem(s.getShortName());
        }
        col.setCellEditor(new DefaultCellEditor(statusBox));

        table_pageTwo.refreshTable();
        scrollPane_pageTwo = new JScrollPane(table_pageTwo);

        table_pageTwo.resizeColumnWidth();
        table_pageTwo.setFillsViewportHeight(true);
        scrollPane_pageTwo.setPreferredSize(new Dimension((int)getContentPane().getPreferredSize().getWidth(),
                (int) (getContentPane().getPreferredSize().getHeight()-pnlControlTwo.getPreferredSize().getHeight())));
        htfSearchPageTwo.setTable(table_pageTwo);
        table_pageTwo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if(e.getSource() instanceof CustomTable<?>){
                        @SuppressWarnings("unchecked")
                        CustomTable<VK> target = (CustomTable<VK>)e.getSource();
                        int column = target.columnAtPoint(e.getPoint());
                        if(column == 5){
                            int row = target.rowAtPoint(e.getPoint());
                            if(row>= 0){
                                Container c = table_pageTwo.getParent();
                                while(true){
                                    if(c instanceof Frame)break;
                                    else c = c.getParent();
                                  }
                                VK vk = m.getVK((int) target.getValueAt(row, 6));
                                  RemarkDialog d = new RemarkDialog(m.getFramework(), (Frame)c, m.getFramework().getString("FRAME_TITLE"), vk);
                                  d.setVisible(true);
                                  table_pageTwo.refreshTable();
                            }
                        }
                    }
                }
              }
            });

        menuItemRemarkDialog = new JMenuItem(m.getFramework().getString("MNU_ADD_REMARK"));
        menuItemRemarkDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table_pageTwo.getSelectedRow() >= 0) {
                    Container c = table_pageTwo.getParent();
                    while(true){
                        if(c instanceof Frame)break;
                        else c = c.getParent();
                        }
                    VK vk = m.getVK((int) table_pageTwo.getValueAt(table_pageTwo.getSelectedRow(), 6));
                    RemarkDialog d = new RemarkDialog(m.getFramework(), (Frame)c, m.getFramework().getString("FRAME_TITLE"), vk);
                    d.setVisible(true);
                    table_pageTwo.refreshTable();
                }
            }
        });
        table_pageTwo.addMenuItem(menuItemRemarkDialog);

        btnDefaultStatus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for(VK vk: m.getDatabase()){
                    if(vk.isSelected()){
                        if(vk.getStatus().equals(Status.NONE)){
                            if(!listBus.contains(vk)){
                                vk.setStatus(Status.EINGETEILT);
                                table_pageTwo.refreshTable();
                            }
                        }
                    }
                }

            }
        });
        pnlPageTwo.add(scrollPane_pageTwo, "Center");
        pnlMain.add(pnlPageTwo);

        menuItemOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                int status = fileChooser.showOpenDialog(menuItemOpenFile);
                if(status == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    Container c = btnNextPanelPageTwo.getParent();
                    while(true){
                        if(c instanceof Frame)break;
                        else c = c.getParent();
                    }
                    try {
                        //TODO:
                        //BUG: report throws Exception, if the underlying Einsatzbericht an older GuZ uses.
                        //TODO: maybe create a unique code for each GuZ, so that it can be distinguished, which report uses what
                        Einsatzbericht report = new Einsatzbericht(m, selectedFile);
                        beginPicker.setDateTimePermissive(LocalDateTime.ofInstant(report.getBegin().toInstant(), ZoneId.systemDefault()));
                        endPicker.setDateTimePermissive(LocalDateTime.ofInstant(report.getEnd().toInstant(), ZoneId.systemDefault()));

                        List<VK> database = m.getCleanDatabase();
                        for(VK vk_report: report.getSelectedVK()) {
                            boolean vkFound = false;
                            for(VK vk: database) {
                                if(vk.getID() == vk_report.getID()) {
                                    database.remove(vk);
                                    database.add(vk_report);
                                    vkFound = true;
                                    break;
                                }
                            }
                            if(!vkFound)throw new EinsatzberichtLoadingException(selectedFile.getAbsolutePath(), "The Einsatzbericht could not be loaded correctly.", -1, -1);
                        }
                        m.getFramework().setDatabase(new DatabaseReturnType<>(database, report.getVersion()));

                        remark = report.getRemark();
                        listEL = report.getListEL();
                        listAL = report.getListAL();
                        listBus = report.getListBus();

                        listEL = Framework.sort(listEL, defaultChainEL);
                        lblEL.setText(m.getFramework().getString("LABEL_EL")
                                .concat(m.getStringFromVKList(listEL)));

                        listAL = Framework.sort(listAL, defaultChainAL);
                        lblAL.setText(m.getFramework().getString("LABEL_AL")
                                .concat(m.getStringFromVKList(listAL)));

                        listBus = Framework.sort(listBus, defaultChainBus);
                        lblBus.setText(m.getFramework().getString("LABEL_BUS")
                                .concat(m.getStringFromVKList(listBus)));

                        htfName.setText(report.getName());

                        JOptionPane.showMessageDialog(c, m.getFramework().getString("LOADING_SUCCESS") );

                        //TODO
                    } catch (Exception ex) {
                        m.getLogger().log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(c, m.getFramework().getString("LOADING_EXCEPTION"));
                    }
                }
            }
        });


        add(pnlMain, "Center");
        pack();
        setMinimumSize(getPreferredSize());
        m.getLogger().log(Level.FINE, "Frame initialized");
    }



    private void addHintTextFieldToPageTwo(HintTextField htf, String lblText, JPanel parent, int y){
        //suppose parent uses GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES);
        gbc.gridy = y;
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;

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
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);

        parent.add(htf, gbc);
    }
    private void addCustomButtonToPageTwo(String btnTxt, String lblText, JPanel parent, CustomButton btn, JLabel lbl, int y, List<VK> list) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);
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

        gbc.insets = new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_SIDES, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2_RIGHT);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.weightx = 1;

        lbl.setText(lblText + m.getStringFromVKList(list));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        parent.add(lbl, gbc);
    }

    private ActionListener  createActionListener(
                CustomButton dialogCause, String[] columnNames, /*String userErrorStringVal,*/ String labelStartStringVal,
                //List<VK> workingList,
                JLabel outputLabel, ComparatorChain<VK> defaultChain, boolean isDriverDialog,
                ListHolder<VK> listHolder){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parentWindow = dialogCause.getParent();
                while(true){
                    if(parentWindow instanceof Window)break;
                    else parentWindow = parentWindow.getParent();
                }
                ButtonDialog d = new ButtonDialog(
                        m, (Window) parentWindow, m.getFramework().getString("FRAME_TITLE"),
                        columnNames, /*userErrorStringVal,*/ labelStartStringVal,
                        listHolder.getWorkingList(), outputLabel, new Insets(INSETS_PNLCONTROL2, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2, INSETS_PNLCONTROL2),
                        defaultChain,
                        isDriverDialog && menuItemOnlyFahrdienst.getState()); //State true: Only Fahrdienst
                d.setVisible(true);
            }
        };
    }

    abstract private class ListHolder<T>{ //No (Function) Pointer in Java
        abstract List<T> getWorkingList();
    }

    class FilterCheckBox extends JCheckBox{
        public FilterCheckBox(String stringName, JPanel parent, GridBagConstraints gbc, int x, int y, int width, boolean selectedAtStart) {
            super(m.getFramework().getString(stringName));
            setAutoRequestFocus(false);
            setFocusPainted(false);
            setSelected(selectedAtStart);
            gbc.gridx = x;
            gbc.gridy = y;
            gbc.gridwidth = width;

            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    filterCheckBoxSelected(filterCheckBoxMap.get(stringName), isSelected());
                    if(stringName.equals("RANK_HVK")){
                        filterCheckBoxSelected(filterCheckBoxMap.get("RANK_HVKA"), isSelected());
                    }
                }
            });
            parent.add(this, gbc);
        }
        private void filterCheckBoxSelected(Integer[] filterInformation, boolean isSelected){
            switch(filterInformation[0]){
            case 0:
                m.setFilter_Group(filterInformation[1], isSelected);
                break;
            case 1:
                m.setFilter_Pos(filterInformation[1], isSelected);
                break;
            case 2:
                m.setFilter_Rank(filterInformation[1], isSelected);
                break;
            }
            table_pageOne.refreshTable();
        }
    }

    //++++++++++++
    //TABLE MODELS
    //++++++++++++

    class TableModelPageOne extends CustomTableModel<VK>{

        public TableModelPageOne(String[] columnNames, ComparatorChain<VK> defaultChain, CustomTable<VK> table) {
            super(columnNames, defaultChain, table);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if(columnIndex == 0 || columnIndex == 6)return Boolean.class;
            if(columnIndex == 7) return Integer.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if(aValue instanceof Boolean && column == 0){
//                if((boolean)aValue)counter++;
//                else counter--;
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(column, (boolean) aValue);
                m.getVK((int) getValueAt(row, 7)).setSelected((boolean)aValue);
                refreshTable();

//                amountOfSelectedVK.setText(StringValues.getString(StringValues.LABEL_AMOUNT_OF_SELECTED_VK).concat(String.valueOf(counter)));

            }
        }

        @Override
        public void refreshTable() {
            List<VK> l;
            if(showSelected){
                l = new ArrayList<VK>();
                for(VK vk: m.getDatabase()){
                    if(vk.isSelected())l.add(vk);
                }
                l = sort(l);
            }else if(showDriver){
                l = new ArrayList<VK>();
                for(VK vk: m.getDatabase()){
                    if(vk.isDriver())l.add(vk);
                }
                l = sort(l);
            }else{
                l = sort(m.applyFilter(m.getDatabase()));
            }

            this.setRowCount(0);

            Object[][] rowData = new Object[l.size()][this.getColumnCount()];
            for(int i = 0; i < l.size(); i++){
                VK vk = l.get(i);
                rowData[i][0] = vk.isSelected();
                rowData[i][1] = Main.outputGroup(vk.getGroup());
                rowData[i][2] = Main.getRankString(vk.getRank());
                rowData[i][3] = Main.outputPosition(vk.getPosition());
                rowData[i][4] = vk.getName();
                rowData[i][5] = vk.getSurname();
                rowData[i][6] = vk.isDriver();
                rowData[i][7] = vk.getID();
            }
            for(Object[] o:rowData){
                this.addRow(o);
            }
            super.refreshTable();
        }
    }
    class TableModelPageTwo extends CustomTableModel<VK>{
        //"Rang", "Vorname", "Nachname", "Fahrdienst", "Teilgenommen", "Bemerkung", "ID"
        public TableModelPageTwo(String[] s, ComparatorChain<VK> defaultChain, CustomTable<VK> table) {
            super(s, defaultChain, table);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if(columnIndex == 3)return Boolean.class;
            if(columnIndex == 6) return Integer.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4;// || column == 6;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if(aValue instanceof String && column == 4){
                Vector rowData = (Vector) getDataVector().get(row);
                rowData.set(column, (String) aValue);
                m.getVK((int) getValueAt(row, 6)).setStatus(Status.getStatusByShortName((String) aValue));
            }
        }

        @Override
        public void refreshTable() {
            List<VK> l = new ArrayList<VK>();
            for(VK vk: m.getDatabase()){
                if(vk.isSelected())l.add(vk);
            }
            l = sort(l);

            this.setRowCount(0);
            Object[][] rowData = new Object[l.size()][this.getColumnCount()];
            for(int i = 0; i < l.size(); i++){
                VK vk = l.get(i);
                rowData[i][0] = Main.getRankString(vk.getRank());
                rowData[i][1] = vk.getName();
                rowData[i][2] = vk.getSurname();
                rowData[i][3] = vk.isDriver();
                rowData[i][4] = vk.getStatus().getShortName();
                rowData[i][5] = vk.getKuerzungsListe().isEmpty()?"":vk.getKuerzungsListe().size();
                rowData[i][6] = vk.getID();
            }
            for(Object[] o:rowData){
                this.addRow(o);
            }
            super.refreshTable();
        }
    }
}
