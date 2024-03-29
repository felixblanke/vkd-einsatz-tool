package de.vkd.einsatz_tool.vkd;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.jdom2.JDOMException;

import de.vkd.auxiliary.Auxiliary;
import de.vkd.auxiliary.ComparatorChain;
import de.vkd.auxiliary.NamedComparator;
import de.vkd.database.DatabaseCSV;
import de.vkd.database.DatabaseEntryCreator;
import de.vkd.database.DatabaseReturnType;
import de.vkd.database.DatabaseType;
import de.vkd.database.ObjectCreator;
import de.vkd.database.ReadDataException;
import de.vkd.framework.Framework;
import de.vkd.framework.Variable;
import de.vkd.einsatz_tool.gui.Frame;


/**
 * @author Felix Blanke
 */
public class Main {
    public final static String JAR_PATH = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/";

    public final static String SETTINGS_PATH = JAR_PATH + "_cfg/settings.xml";
    public final static String PROPERTIES_FILE = "/application.properties";
    public final static String DB_VERSION_MARKER_CSV = "version:";
    public final static String DB_VERSION_MARKER_XML = "version";
    public final static String DB_DEFAULT_VERSION = "NO_VERSION";

    private final static BidiMap<Integer, String> GROUP_MAP = new TreeBidiMap<>();
    private final static BidiMap<Integer, String> POSITION_MAP = new TreeBidiMap<>();
    private final static BidiMap<Rank, String> RANK_MAP = new TreeBidiMap<>();

//CONSTANTS
    public final Settings settings;
    private final Framework<VK> framework;
//STATIC VARS

//NONSTATIC VARS
    //filters
    private final boolean[] filter_group = new boolean[8];
    private final boolean[] filter_pos = new boolean[4];
    private final boolean[] filter_rank = new boolean[9];

    //comparators:
    private final static List<NamedComparator<VK>> VK_COMPARATOR_LIST = new ArrayList<>();
    public static NamedComparator<VK> VK_GROUP_COMPARATOR;
    public static NamedComparator<VK> VK_POSITION_COMPARATOR;
    public static NamedComparator<VK> VK_RANK_COMPARATOR;
    public static NamedComparator<VK> VK_NAME_COMPARATOR;
    public static NamedComparator<VK> VK_SURNAME_COMPARATOR;

    private final static List<NamedComparator<Kuerzung>> KUERZUNG_COMPARATOR_LIST = new ArrayList<>();
    public static NamedComparator<Kuerzung> KUERZUNG_ID_COMPARATOR;
    public static NamedComparator<Kuerzung> KUERZUNG_PERCENTAGE_COMPARATOR;
    public static NamedComparator<Kuerzung> KUERZUNG_REASON_COMPARATOR;

    public static ComparatorChain<VK> VK_DEFAULT_COMPARATOR_CHAIN;
    public static ComparatorChain<VK> VK_DEFAULT_COMPARATOR_CHAIN_IGNORING_GROUPS;

    /**Loads the Settings used
     * @return The Settings that should be used
     */
    private Settings loadSettings() throws SettingsLoadingException, JDOMException, IOException{
        return new Settings(this, SETTINGS_PATH);
    }
    public Main(String[] args) throws Exception {
        Level log_lvl;

        if(args.length > 0) {
            log_lvl = Level.parse(args[0]);
        } else {
            log_lvl = Level.INFO;
        }

        // TODO allow setting via args
        boolean openConsole = false;

        framework = new Framework<>(log_lvl, openConsole);

        for(String str: args){
            getLogger().log(Level.INFO, str);
        }

        getLogger().log(Level.FINE, "Starting loading process");
        getLogger().log(Level.FINE, JAR_PATH);
        this.settings = loadSettings();

        if (framework.getConsole() != null) {
            try {
                getLogger().log(Level.FINEST, "Loading icon from " + settings.getIconPath());
                framework.getConsole().setIcon(settings.getIconPath(), true);
                getLogger().log(Level.FINEST, "Icon loaded");
            } catch(IOException ex) {
                getLogger().log(Level.WARNING, "", ex);
            }
        }

        getLogger().log(Level.FINEST, "Loading application properties from " + PROPERTIES_FILE);
        Properties properties = new Properties();
        properties.load(Auxiliary.getResourceURLFromJAR(PROPERTIES_FILE).openStream());

        getLogger().log(Level.FINEST, "Properties file loaded:");
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            getLogger().log(Level.FINEST, "\t" + key + " - " + properties.getProperty(key));
        }

        List<Variable> sysVarList = new ArrayList<>();
        sysVarList.add(new Variable("VER", properties.getProperty("application.version")));
        sysVarList.add(new Variable("DESC", properties.getProperty("application.description")));
        sysVarList.add(new Variable("APPNAME", properties.getProperty("application.name")));
        sysVarList.add(new Variable("YEAR", Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        sysVarList.add(new Variable("GRP", settings.getGroupPrefix()));

        framework.setSysVarList(sysVarList);

        framework.initVars(settings.getVarSet());

        framework.loadStringValuesFromXML(settings.getStringValuesPath(), true);

        initVars();

        //read Data
        DatabaseReturnType<VK> databaseReturnType = readInData(settings.getVkDataPath(), settings.isReadDatabaseFromJar());
        framework.setDatabase(databaseReturnType);

        getLogger().log(Level.FINE, "Loading complete");
        printCopyright();

        assert databaseReturnType != null;
        getLogger().log(Level.INFO, "DB-Version: " + databaseReturnType.getVersion());
    }

    /**
     * Sets the initial values of a few fields, such as {@code filet_group} or {@code xml_var}.
     */
    private void initVars(){
        //init groupMap
        GROUP_MAP.put(1, getFramework().getString("GROUP_1"));
        GROUP_MAP.put(2, getFramework().getString("GROUP_2"));
        GROUP_MAP.put(3, getFramework().getString("GROUP_3"));
        GROUP_MAP.put(4, getFramework().getString("GROUP_4"));
        GROUP_MAP.put(5, getFramework().getString("GROUP_5"));
        GROUP_MAP.put(6, getFramework().getString("GROUP_6"));
        GROUP_MAP.put(-1, getFramework().getString("GROUP_L"));
        GROUP_MAP.put(-2, getFramework().getString("GROUP_ER"));
        //init positionMap
        POSITION_MAP.put(1, getFramework().getString("POS_GL"));
        POSITION_MAP.put(2, getFramework().getString("POS_1STV"));
        POSITION_MAP.put(3, getFramework().getString("POS_2STV"));
        POSITION_MAP.put(4, getFramework().getString("POS_MANNSCHAFT_TABLE"));
        //init rankMap
        RANK_MAP.put(Rank.VK, getFramework().getString("RANK_VK"));
        RANK_MAP.put(Rank.OVK, getFramework().getString("RANK_OVK"));
        RANK_MAP.put(Rank.HVK, getFramework().getString("RANK_HVK"));
        RANK_MAP.put(Rank.HVKA, getFramework().getString("RANK_HVKA"));
        RANK_MAP.put(Rank.UGL, getFramework().getString("RANK_UGL"));
        RANK_MAP.put(Rank.PGL, getFramework().getString("RANK_PGL"));
        RANK_MAP.put(Rank.GL, getFramework().getString("RANK_GL"));
        RANK_MAP.put(Rank.OGL, getFramework().getString("RANK_OGL"));
        RANK_MAP.put(Rank.HGL, getFramework().getString("RANK_HGL"));
        RANK_MAP.put(Rank.DEF, getFramework().getString("EMPTY_LIST"));

        //comparators
        VK_GROUP_COMPARATOR = new NamedComparator<>(
                getFramework().getString("VK_GROUP"),
                Comparator.comparing(VK::getGroup)
        );
        VK_POSITION_COMPARATOR = new NamedComparator<>(
                getFramework().getString("VK_POS"),
                Comparator.comparing(VK::getPosition)
        );
        VK_RANK_COMPARATOR = new NamedComparator<>(
                getFramework().getString("VK_RANK"),
                Comparator.comparing((VK vk) -> vk.getRank().getHierarchy()).reversed()
        );
        VK_NAME_COMPARATOR = new NamedComparator<>(
                getFramework().getString("VK_NAME"),
                Comparator.comparing(VK::getName)
        );
        VK_SURNAME_COMPARATOR = new NamedComparator<>(
                getFramework().getString("VK_SURNAME"),
                Comparator.comparing(VK::getSurname)
        );

        VK_COMPARATOR_LIST.add(VK_GROUP_COMPARATOR);
        VK_COMPARATOR_LIST.add(VK_POSITION_COMPARATOR);
        VK_COMPARATOR_LIST.add(VK_RANK_COMPARATOR);
        VK_COMPARATOR_LIST.add(VK_NAME_COMPARATOR);
        VK_COMPARATOR_LIST.add(VK_SURNAME_COMPARATOR);

        VK_DEFAULT_COMPARATOR_CHAIN = new ComparatorChain<>(
            VK_GROUP_COMPARATOR, VK_POSITION_COMPARATOR, VK_RANK_COMPARATOR, VK_NAME_COMPARATOR, VK_SURNAME_COMPARATOR
        );

        VK_DEFAULT_COMPARATOR_CHAIN_IGNORING_GROUPS = new ComparatorChain<>(
                VK_POSITION_COMPARATOR, VK_RANK_COMPARATOR, VK_NAME_COMPARATOR, VK_SURNAME_COMPARATOR
        );

        KUERZUNG_ID_COMPARATOR = new NamedComparator<>(
                getFramework().getString("KUERZUNG_ID_COMPARATOR"),
                Comparator.comparing(Kuerzung::getId)
        );
        KUERZUNG_PERCENTAGE_COMPARATOR = new NamedComparator<>(
                getFramework().getString("KUERZUNG_PERCENTAGE_COMPARATOR"),
                Comparator.comparing(Kuerzung::getPercentage).reversed()
        );
        KUERZUNG_REASON_COMPARATOR = new NamedComparator<>(
                getFramework().getString("KUERZUNG_REASON_COMPARATOR"),
                Comparator.comparing(Kuerzung::getReason)
        );

        KUERZUNG_COMPARATOR_LIST.add(KUERZUNG_ID_COMPARATOR);
        KUERZUNG_COMPARATOR_LIST.add(KUERZUNG_PERCENTAGE_COMPARATOR);
        KUERZUNG_COMPARATOR_LIST.add(KUERZUNG_REASON_COMPARATOR);

        //class Status:
        Status.AE.setName(getFramework().getString("STATUS_AE"), getFramework().getString("STATUS_AE_LONG"));
        Status.SE.setName(getFramework().getString("STATUS_SE"), getFramework().getString("STATUS_SE_LONG"));
        Status.UE.setName(getFramework().getString("STATUS_UE"), getFramework().getString("STATUS_UE_LONG"));
        Status.EINGETEILT.setName(getFramework().getString("STATUS_EING"));
        Status.ERSATZ.setName(getFramework().getString("STATUS_ERS"));
        Status.ZUSAETZLICH.setName(getFramework().getString("STATUS_ZUS"));
        Status.NONE.setName(getFramework().getString("STATUS_NONE"));
        Status.AUSGETRETEN.setName(getFramework().getString("STATUS_AUSG"));

        Status.AE.setDemandingAttendance(false);
        Status.SE.setDemandingAttendance(false);
        Status.UE.setDemandingAttendance(false);
        Status.AUSGETRETEN.setDemandingAttendance(false);
        Status.NONE.setDemandingAttendance(false);
        Status.EINGETEILT.setDemandingAttendance(true);
        Status.ZUSAETZLICH.setDemandingAttendance(true);
        Status.ERSATZ.setDemandingAttendance(true);


        Arrays.fill(filter_group, true);
        Arrays.fill(filter_pos, true);
        Arrays.fill(filter_rank, true);
        getLogger().log(Level.FINER, "Variables initialized");
    }

    private void printCopyright(){
        getLogger().log(Level.INFO, getFramework().getString("COPYRIGHT_NAME"));
        getLogger().log(Level.INFO, getFramework().getString("COPYRIGHT_DESC"));
        getLogger().log(Level.INFO, getFramework().getString("COPYRIGHT_AUTHOR"));
    }

    /**
     * The Main method
     * @param args not used
     */

    public static void main(String[] args){
        Main m = null;
        try {
            m = new Main(args);
            final Main fixScopeProblemMain = m;
            /* LOOKANDFEEL - NOT USED
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            */

            /* Create and display the GUI */
            java.awt.EventQueue.invokeLater(() -> {
                Frame f = new Frame(fixScopeProblemMain);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
                f.requestFocus();
            });
        } catch (Exception e) {
            if(m != null && m.getLogger() != null)m.getLogger().log(Level.SEVERE, "", e);
            else e.printStackTrace();
        }
    }
    /**
     * Returns a VK with a specific ID from the database. The ID should be a distinct property.
     * @param id The ID of the VK, which should be returned
     * @return The VK belonging to the specific ID.
     */
    public VK getVK(int id){
        return getVK(id, getDatabase());
    }
    /**
     * Returns a VK with a specific ID from the database. The ID should be a distinct property.
     * @param id The ID of the VK, which should be returned
     * @return The VK belonging to the specific ID.
     */
    public VK getVK(int id, List<VK> database){
        for (VK vk : database) {
            if (vk.getId() == id) return vk;
        }
        return null;
    }
    /**
     * Returns a VK with the same String representation from the database, under the assumption each String representation is unique.
     * @param stringRepresentation The String representation ({@code .getStringRepresentation}) of the VK, which should be returned
     * @return The VK belonging to the String representation.
     */
    public VK getVK(String stringRepresentation){
        return getVK(stringRepresentation, getDatabase());
    }
    /**
     * Returns a VK with the same String representation from the database, under the assumption each String representation is unique.
     * @param stringRepresentation The String representation ({@code .getStringRepresentation}) of the VK, which should be returned
     * @return The VK belonging to the String representation.
     */
    public VK getVK(String stringRepresentation, List<VK> database){
        for (VK vk : database) {
            if (vk.getStringRepresentation().equals(stringRepresentation.trim())) return vk;
        }
        return null;
    }
    /**
     * Filters the {@code List<VK> l} using the boolean-arrays {@code filter_group, filter_rank} and {@code filter_pos} and returns a {@code List<VK>} containing all the result of the filtering process.
     * @param list The List that should be filtered.
     * @return A List containing the result of the filtering process.
     */
    public List<VK> applyFilter(List<VK> list){
        List<VK> tmp = new ArrayList<>();
        for (VK vk : list) {
            if (filter_rank[vk.getRank().getHierarchy()]) {
                if (filter_pos[vk.getPosition() - 1]) {
                    switch (vk.getGroup()) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            if (filter_group[vk.getGroup() - 1]) tmp.add(vk);
                            break;
                        case -1:
                            if (filter_group[6]) tmp.add(vk);
                            break;
                        case -2:
                            if (filter_group[7]) tmp.add(vk);
                            break;
                    }
                }
            }
        }
        return tmp;
    }
    /**
     * Clones the database and returns the clone.
     * @return A clone of the Database.
     */
    public List<VK> getDatabase(){
        return getFramework().getDatabase();
    }
    /**
     * Creates a completely clean Database and returns it
     * @return the clean database
     */
    public List<VK> getCleanDatabase(){
        List<VK> database = getDatabase();
        List<VK> cleanDatabase = new ArrayList<>();
        for(VK vk: database) {
            cleanDatabase.add(new VK(vk));
        }
        return cleanDatabase;
    }

    /**
     * Reads the VK-data from an .csv or .xml file and creates a {@code List<VK>} containing this data.
     * @param filedir The filepath, from which the data should be read.
     * @return The {@code List<VK>} containing the data.
     * @throws JDOMException Throws this Exception, if the reading process from the XML-file was not successful, e.g. because the XML-code is corrupt.
     * @throws IOException Throws this Exception, if the reading process from the file was not successful, e.g. because the file could not be opened
     * @throws ReadDataException Throws this Exception, if the read data is not valid.
     */
    private DatabaseReturnType<VK> readInData(String filedir, boolean readFromJar) throws Exception {
        getLogger().log(Level.FINER, "Loading VK data from " + filedir + (settings.getDatabaseType() == DatabaseType.XML?" (XML-Mode)":settings.getDatabaseType() == DatabaseType.CSV?" (CSV-Mode)":""));
        if(settings.getDatabaseType() == DatabaseType.XML){
            throw new RuntimeException("XML data files currently not supported due to work on the backend. Try a CSV Sheet.");
            /*String[] colNames = new String[]{
                    "vk_group",
                    "vk_rank",
                    "vk_name",
                    "vk_surname",
                    "vk_pos",
                    "vk_func"
            };
            DatabaseXML<VK> databaseXML = new DatabaseXML<VK>(filedir);
            DatabaseReturnType<VK> readData = databaseXML.readData(
                    "vk",
                    new DatabaseEntryCreator<VK>(
                            new ObjectCreator<VK>(colNames) {

                        @Override
                        public VK create(String... arr) {
                            boolean driver = false;
                            if(arr[getColumnIndex("vk_func")] != null && arr[getColumnIndex("vk_func")].equals("Fahrdienst"))driver = true;
                            return new VK(
                                    getGroupNumber(arr[getColumnIndex("vk_group")]),
                                    parseRank(arr[getColumnIndex("vk_rank")]),
                                    arr[getColumnIndex("vk_name")],
                                    arr[getColumnIndex("vk_surname")],
                                    Integer.valueOf(arr[getColumnIndex("vk_pos")]),
                                    driver);
                        }

                    },
                    colNames),
                    DB_VERSION_MARKER_XML,
                    DB_DEFAULT_VERSION
                            );
            getLogger().log(Level.FINER, "VK data loaded");
            //TODO: !!!
            return readData;*/
        }else if(settings.getDatabaseType() == DatabaseType.CSV){
            String[] colNames = new String[]{
                    "Gruppe",
                    "Rang",
                    "Vorname",
                    "Nachname",
                    "Position",
                    "Funktion"
            };
            DatabaseCSV<VK> databaseCSV = new DatabaseCSV<>(
                    filedir,
                    settings.getVkDataEncoding().name(),
                    new DatabaseEntryCreator<>(
                            new ObjectCreator<VK>(colNames) {
                                @Override
                                public VK create(String... arr) {
                                    boolean driver = arr[getColumnIndex("Funktion")] != null && arr[getColumnIndex("Funktion")].equals("Fahrdienst");
                                    return new VK(
                                            getGroupNumber(arr[getColumnIndex("Gruppe")]),
                                            parseRank(arr[getColumnIndex("Rang")]),
                                            arr[getColumnIndex("Vorname")],
                                            arr[getColumnIndex("Nachname")],
                                            Integer.parseInt(arr[getColumnIndex("Position")]),
                                            driver);
                                }
                            },
                        colNames
                    ),
                    DB_VERSION_MARKER_CSV,
                    DB_DEFAULT_VERSION,
                    readFromJar
                    );
            getLogger().log(Level.FINER, "VK data loaded");
            return new DatabaseReturnType<>(databaseCSV.getReadData(), databaseCSV.getVersion());
        }else
            return null;
    }


    public String getStringFromVKList(List<VK> list, String delimiter) {
        if(list.isEmpty())return getFramework().getString("EMPTY_LIST");

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < list.size(); i++){
            VK vk = list.get(i);
            String s = "";
            s = s.concat(Main.getRankString(vk.getRank())).concat(" ")
                    .concat(vk.getName()).concat(" ")
                    .concat(vk.getSurname());
            sb.append(s);
            if(i<list.size()-1)sb.append(delimiter);
        }
        return sb.toString();
    }
    public String getStringFromVKList(List<VK> list) {
        return getStringFromVKList(list, ", ");
    }
    /**
     * Sets the boolean value of the {@code filter_Group} boolean-array with the index i. The array is used in the process of filtering the shown List of VK using the CheckBoxes on Page One of the GUI.
     * @param i The index of the boolean value in the {@code filter_Group} boolean-array, which should be set.
     * @param b The boolean value, to which the entry in the {@code filter_Group} boolean-array should be set to.
     */
    public void setFilter_Group(int i, boolean b){
        filter_group[i] = b;
    }
    /**
     * Sets the boolean value of the {@code filter_Rank} boolean-array with the index i. The array is used in the process of filtering the shown List of VK using the CheckBoxes on Page One of the GUI.
     * @param i The index of the boolean value in the {@code filter_Rank} boolean-array, which should be set.
     * @param b The boolean value, to which the entry in the {@code filter_Rank} boolean-array should be set to.
     */
    public void setFilter_Rank(int i, boolean b){
        filter_rank[i] = b;
    }
    /**
     * Sets the boolean value of the {@code filter_Pos} boolean-array with the index i. The array is used in the process of filtering the shown List of VK using the CheckBoxes on Page One of the GUI.
     * @param i The index of the boolean value in the {@code filter_Pos} boolean-array, which should be set.
     * @param b The boolean value, to which the entry in the {@code filter_Pos} boolean-array should be set to.
     */
    public void setFilter_Pos(int i, boolean b){
        filter_pos[i] = b;
    }
    public Logger getLogger(){
        return framework.getLogger();
    }
    public Framework<VK> getFramework(){
        return framework;
    }
    //methods dealing with groupMap
    public static String outputGroup(int group){
        return GROUP_MAP.get(group);
    }
    public int getGroupNumber(String group){
        return (GROUP_MAP.getKey(group)==0)? 0 : GROUP_MAP.getKey(group);
    }
    //methods dealing with positionMap
    public static String outputPosition(int position){
        // Position is GL
        if (position == 1) {
            return POSITION_MAP.get(1);
        } else {
            return POSITION_MAP.get(4);
        }
    }
    public static int getPositionNumber(String position){
        return (POSITION_MAP.getKey(position)==0)? 0 : POSITION_MAP.getKey(position);
    }
    //methods dealing with rankMap
    public static Rank parseRank(String s){
        Rank r = RANK_MAP.getKey(s);
        if(r==null)return Rank.DEF;
        return r;
    }
    //returns the name of a specific rank
    public static String getRankString(Rank r){
        return RANK_MAP.get(r);
    }
    public static List<NamedComparator<VK>> getVKComparatorList() {
        return VK_COMPARATOR_LIST;
    }
    public static List<NamedComparator<Kuerzung>> getKuerzungComparatorList() {
        return KUERZUNG_COMPARATOR_LIST;
    }
}
