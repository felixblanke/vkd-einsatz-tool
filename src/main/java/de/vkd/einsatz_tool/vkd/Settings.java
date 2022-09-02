package de.vkd.einsatz_tool.vkd;

import de.vkd.auxiliary.Auxiliary;
import de.vkd.database.DatabaseType;
import de.vkd.framework.VarSet;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Settings {

  //    private String dbVersion = "NO_VAL";
  private String exportPath;
  private String stringValuesPath;
  private String vkDataPath;
  private String iconPath;
  private Font remarkFont;
  private SimpleDateFormat dateFormat;
  private Charset vkDataEncoding;
  private DateTimeFormatter dateTimeFormatter;
  private String dateFormatExcel;
  private DatabaseType databaseType;
  private VarSet varSet;

  private String groupPrefix;

  private boolean readDatabaseFromJar;

  //TODO:
  //SUPPORT MULTIPLE CHARSETS

  public Settings(Main m, String filedir)
      throws SettingsLoadingException, JDOMException, IOException {
    m.getLogger().log(Level.FINER, "loading settings from " + filedir);

    Document doc = new SAXBuilder().build(filedir);
    Element root = doc.getRootElement();


    final String dateFormat = "dd.MM.yyyy kk:mm";
    final String dateFormat2 = "dd.MM.yyyy hh:mm";
    final String dateFormat3 = "dd.MM.yyyy";

    final String resPath = "/";
    final String exportPath = "_export";
    final Charset vkDataEncoding = StandardCharsets.UTF_8;

    final String XML_VK_DATA = "vk_data";
    final String vkDataPath = readString(XML_VK_DATA, root);
    final String XML_STRING_VALUES = "string_values";
    final String stringValuesPath = readString(XML_STRING_VALUES, root);
    final String XML_ICON = "icon";
    final String iconPath = readString(XML_ICON, root);
    final String XML_REMARK_FONT = "remark_font";
    final String remarkFontPath = readString(XML_REMARK_FONT, root);
    final String XML_GRP_PREFIX = "group_prefix";
    final String groupPrefix = readString(XML_GRP_PREFIX, root);
    final String XML_DATABASE_IN_JAR = "vk_data_in_jar";
    final String databaseInJar = readString(XML_DATABASE_IN_JAR, root);
    boolean readDatabaseFromJar = Boolean.parseBoolean(databaseInJar);

    final DatabaseType databaseType;

    if (vkDataPath.endsWith(".csv") || vkDataPath.endsWith(".CSV")) {
      databaseType = DatabaseType.CSV;
    } else if (vkDataPath.endsWith(".xml") || vkDataPath.endsWith(".XML")) {
      databaseType = DatabaseType.XML;
    } else {
      throw new SettingsLoadingException(
          "Unknown filetype of the '" + XML_VK_DATA + "' value ('" + vkDataPath + "')");
    }

    //varFlags
    final VarSet varSet = new VarSet();
    String XML_VAR_FLAG = "var_flag";
    for (Element e : root.getChildren(XML_VAR_FLAG)) {
      if (!e.hasAttributes()) {
        throw new SettingsLoadingException(
            "Exception loading a '" + XML_VAR_FLAG + "' tag: no attributes");
      }
      List<Attribute> attributes = e.getAttributes();
      if (attributes.size() > 1) {
        throw new SettingsLoadingException(
            "Exception loading a '" + XML_VAR_FLAG + "' tag: too many attributes");
      }
      String XML_ATTRIBUTE_NAME = "name";
      if (!attributes.get(0).getName().equals(XML_ATTRIBUTE_NAME)) {
        throw new SettingsLoadingException(
            "Exception loading a '" + XML_VAR_FLAG + "' tag: wrong attribute ('"
                + attributes.get(0)
                .getName() + "' instead of '" + XML_ATTRIBUTE_NAME + "')");
      }
      String elemVal = e.getValue();
      if (elemVal.trim().isEmpty()) {
        throw new SettingsLoadingException(
            "Exception loading a '" + XML_VAR_FLAG + "' tag: no value");
      }

      varSet.put(attributes.get(0).getValue(), elemVal);
    }

    switch (databaseType) {
      case CSV:
      case XML:
        initVars(m, resPath, exportPath, stringValuesPath, vkDataPath, iconPath,
            remarkFontPath, dateFormat, dateFormat2, dateFormat3, vkDataEncoding, databaseType,
            varSet, groupPrefix, readDatabaseFromJar);
        break;
      default:
        throw new SettingsLoadingException("Unknown enum type");
    }

    m.getLogger().log(Level.FINER, "settings loaded");

    //        HashMap<String, String> dirHM = new HashMap<String, String>();
    //        Charset charset = null;
    //
    //        DatabaseType databaseType = null;
    //
    //        VarSet varSet = new VarSet();
    //

    //
    //        /*
    //         * VERSION: CHECK HOW MANY -> Must be exactly one
    //         */
    //
    //        /*
    //         * DIR: load res, export
    //         */
    //        for(Element e: root.getChildren(XML_DIRECTORY)) {
    //            String elemVal = e.getValue();
    //            if(!e.hasAttributes())throw new SettingsLoadingException("Exception loading a '" +
    //            XML_DIRECTORY + "' tag: no attributes");
    //            List<Attribute> attributes= e.getAttributes();
    //            if(attributes.size() > 1)throw new SettingsLoadingException("Exception loading
    //            a '"
    //            + XML_DIRECTORY + "' tag: too many attributes");
    //            if(!attributes.get(0).getName().equals(XML_ATTRIBUTE_NAME))throw new
    //            SettingsLoadingException("Exception loading a '" + XML_DIRECTORY + "' tag: wrong
    //            attribute ('" + attributes.get(0).getName() + "' instead of '" +
    //            XML_ATTRIBUTE_NAME
    //            + "')");
    //            if(elemVal.trim().isEmpty())throw new SettingsLoadingException("Exception
    //            loading a
    //            '" + XML_DIRECTORY + "' tag: no value");
    //
    //            String attrName = attributes.get(0).getValue();
    //            dirHM.put(attrName, elemVal);
    ////            switch(attrName) {
    ////            case XML_DIR_RES:
    ////                dir_res = elemVal;
    ////                break;
    ////            case XML_DIR_EXPORT:
    ////                dir_export = elemVal;
    ////                break;
    ////            default:
    ////                throw new SettingsLoadingException("Exception loading a '" + XML_DIRECTORY +
    // "' tag: unknown value as " + XML_ATTRIBUTE_NAME + " attribute ('" + attrName + "')");
    ////            }
    //        }
    //
    //        for(Element e: root.getChildren(XML_FILE)) {
    //            String elemVal = e.getValue();
    //            if(!e.hasAttributes())throw new SettingsLoadingException("Exception loading a '" +
    //            XML_DIRECTORY + "' tag: no attributes");
    //            List<Attribute> attributes= e.getAttributes();
    //            if(attributes.size() > 1)throw new SettingsLoadingException("Exception loading
    //            a '"
    //            + XML_DIRECTORY + "' tag: too many attributes");
    //            if(!attributes.get(0).getName().equals(XML_ATTRIBUTE_NAME))throw new
    //            SettingsLoadingException("Exception loading a '" + XML_DIRECTORY + "' tag: wrong
    //            attribute ('" + attributes.get(0).getName() + "' instead of '" +
    //            XML_ATTRIBUTE_NAME
    //            + "')");
    //            if(elemVal.trim().isEmpty())throw new SettingsLoadingException("Exception
    //            loading a
    //            '" + XML_DIRECTORY + "' tag: no value");
    //
    //            String attrName = attributes.get(0).getValue();
    //            dirHM.put(attrName, elemVal);
    ////            switch(attrName) {
    ////            case XML_DIR_RES:
    ////                dir_res = elemVal;
    ////                break;
    ////            case XML_DIR_EXPORT:
    ////                dir_export = elemVal;
    ////                break;
    ////            default:
    ////                throw new SettingsLoadingException("Exception loading a '" + XML_DIRECTORY +
    // "' tag: unknown value as " + XML_ATTRIBUTE_NAME + " attribute ('" + attrName + "')");
    ////            }
    //        }
    //
    //        for(Element e: root.getChildren()) {
    //            final String elemVal = e.getValue();
    //            final String elemName = e.getName();
    //
    //            if(elemName.equals(XML_VERSION)) {
    //                if(elemVal.trim().isEmpty())throw new SettingsLoadingException("Exception
    //                loading a '" + XML_VERSION + "' tag: no value");
    //                this.VERSION = elemVal.trim();
    //            }else if(elemName.equals(XML_STRING)){
    //                if(!e.hasAttributes())throw new SettingsLoadingException("Exception loading a
    //                '" + XML_STRING + "' tag: no attributes");
    //                List<Attribute> attributes= e.getAttributes();
    //                if(attributes.size() > 1)throw new SettingsLoadingException("Exception loading
    //                a '" + XML_STRING + "' tag: too many attributes");
    //                if(!attributes.get(0).getName().equals(XML_ATTRIBUTE_NAME))throw new
    //                SettingsLoadingException("Exception loading a '" + XML_STRING + "' tag: wrong
    //                attribute ('" + attributes.get(0).getName() + "' instead of '" +
    //                XML_ATTRIBUTE_NAME + "')");
    //                if(elemVal.trim().isEmpty())throw new SettingsLoadingException("Exception
    //                loading a '" + XML_STRING + "' tag: no value");
    //
    //                String attrName = attributes.get(0).getValue();
    //                switch(attrName) {
    //                case XML_STRING_DATA_FORMAT_ONE:
    //                    dateFormat = elemVal;
    //                    break;
    //                case XML_STRING_DATA_FORMAT_TWO:
    //                    dateFormat2 = elemVal;
    //                    break;
    //                case XML_STRING_DATA_FORMAT_THREE:
    //                    dateFormat3 = elemVal;
    //                    break;
    //                default:
    //                    throw new SettingsLoadingException("Exception loading a '" + XML_STRING +
    //                    "' tag: unknown value as " + XML_ATTRIBUTE_NAME + " attribute ('" +
    //                    attrName + "')");
    //                }
    //            }else if(elemVal.equals(XML_OPTION)) {
    //                if(!e.hasAttributes())throw new SettingsLoadingException("Exception loading a
    //                '" + XML_OPTION + "' tag: no attributes");
    //                List<Attribute> attributes= e.getAttributes();
    //                if(attributes.size() > 1)throw new SettingsLoadingException("Exception loading
    //                a '" + XML_OPTION + "' tag: too many attributes");
    //                if(!attributes.get(0).getName().equals(XML_ATTRIBUTE_NAME))throw new
    //                SettingsLoadingException("Exception loading a '" + XML_OPTION + "' tag: wrong
    //                attribute ('" + attributes.get(0).getName() + "' instead of '" +
    //                XML_ATTRIBUTE_NAME + "')");
    //                if(elemVal.trim().isEmpty())throw new SettingsLoadingException("Exception
    //                loading a '" + XML_OPTION + "' tag: no value");
    //
    //                String attrName = attributes.get(0).getValue();
    //                switch(attrName) {
    ////                case XML_OPTION_STANDARD_CHARSETS:
    ////                    if(elemVal.equals(UTF8_VAL)
    ////                    break;
    //                case XML_OPTION_DATABASE_TYPE:
    //                    if(elemVal.trim().equalsIgnoreCase(FILETYPE_CSV)){
    //                        databaseType = DatabaseType.CSV;
    //                    }else if(elemVal.trim().equalsIgnoreCase(FILETYPE_XML)) {
    //                        databaseType = DatabaseType.XML;
    //                    }else {
    //                        throw new SettingsLoadingException("Exception loading a '" +
    //                        XML_OPTION
    //                        + "' tag with " + XML_OPTION_DATABASE_TYPE + " as value of " +
    //                        XML_ATTRIBUTE_NAME + " attribute: unknown element value ('" +
    //                        elemVal +
    //                        "')");
    //                    }
    //                    break;
    //                default:
    //                    throw new SettingsLoadingException("Exception loading a '" + XML_OPTION +
    //                    "' tag: unknown value as " + XML_ATTRIBUTE_NAME + " attribute ('" +
    //                    attrName + "')");
    //                }
    //            }else if(elemVal.equals(XML_VAR_FLAG)) {
    //                if(!e.hasAttributes())throw new SettingsLoadingException("Exception loading a
    //                '" + XML_VAR_FLAG + "' tag: no attributes");
    //                List<Attribute> attributes= e.getAttributes();
    //                if(attributes.size() > 1)throw new SettingsLoadingException("Exception loading
    //                a '" + XML_VAR_FLAG + "' tag: too many attributes");
    //                if(!attributes.get(0).getName().equals(XML_ATTRIBUTE_NAME))throw new
    //                SettingsLoadingException("Exception loading a '" + XML_VAR_FLAG + "' tag:
    //                wrong
    //                attribute ('" + attributes.get(0).getName() + "' instead of '" +
    //                XML_ATTRIBUTE_NAME + "')");
    //                if(elemVal.trim().isEmpty())throw new SettingsLoadingException("Exception
    //                loading a '" + XML_VAR_FLAG + "' tag: no value");
    //
    //                varSet.put(attributes.get(0).getValue(), elemVal);
    //            }
    //        }


    //        //loading vars from XML-File
    //        List<Variable> localVarList = new ArrayList<Variable>();
    //
    //        for(Element v: lang.getChildren(this.varSet.get("xml_var"))){
    //            String varName = v.getChildText(this.varSet.get("xml_var_name"));
    //            String varValue = v.getChildText(this.varSet.get("xml_var_value"));
    //            boolean b = false;
    ////            for(Variable temp: localVarList)if(temp.getName().equals(varName))b=true;
    //            if(!b){
    //                Element temp = v.getChild(this.varSet.get("xml_var_args"));
    //                if(temp!=null){
    //                    List<Element> varArgs = temp.getChildren(this.varSet.get
    //                    ("xml_var_argument"));
    //                    String[] strArr = new String[varArgs.size()];
    //                    for(int i = 0; i < varArgs.size(); i++){
    //                        strArr[i] = varArgs.get(i).getText();
    //                    }
    //                    localVarList.add(new Variable(varName, varValue, strArr));
    //                }else localVarList.add(new Variable(varName, varValue));
    //            }
    //        }
    //
    //
    //        //loading Strings from XML-File
    //        for(Element v: lang.getChildren(this.varSet.get("xml_value"))){
    //            String valName = v.getAttributeValue(this.varSet.get("xml_value_name"));
    //            String valText = v.getChildText(this.varSet.get("xml_value_text"));
    //            //replacing vars set in the XML-File (local) and the ones set in the program
    //            (system)
    //            valText = replaceVars(valText, sysVarList, this.varSet.get("xml_sysVar_marker"),
    //            this.varSet.get("xml_func_arg_splitter"));
    //            valText = replaceVars(valText, localVarList, this.varSet.get
    //            ("xml_localVar_marker")
    //            , this.varSet.get("xml_func_arg_splitter"));
    //            if(!containsString(valName))
    //                addStringValue(valName, valText);
    //        }
  }

  public Settings(Main m, String resPath, String exportPath,
                  String stringValuesPath, String vkDataPath, String iconPath,
                  String remarkFontPath, String dateFormat, String dateFormat2, String dateFormat3,
                  Charset vkDataEncoding, DatabaseType databaseType, VarSet varSet, String groupPrefix, boolean readDatabaseFromJar) {
    initVars(m, resPath, exportPath, stringValuesPath, vkDataPath, iconPath,
        remarkFontPath, dateFormat, dateFormat2, dateFormat3, vkDataEncoding, databaseType, varSet, groupPrefix, readDatabaseFromJar);
  }

  private String readString(String elemName, Element root) throws SettingsLoadingException {
    List<Element> elems = root.getChildren(elemName);
    if (elems.size() != 1) {
      throw new SettingsLoadingException(
          "There must be exactly one '" + elemName + "' tag, not " + elems.size());
    }
    return elems.get(0).getValue();
  }

  private void initVars(Main m, String resPath, String exportPath,
                        String stringValuesPath, String vkDataPath, String iconPath,
                        String remarkFontPath, String dateFormat, String dateFormat2,
                        String dateFormat3, Charset vkDataEncoding, DatabaseType databaseType,
                        VarSet varSet, String groupPrefix, boolean readDatabaseFromJar) {
    this.exportPath = exportPath;
    this.stringValuesPath = resPath + stringValuesPath;
    this.vkDataPath = resPath + vkDataPath;
    this.iconPath = resPath + iconPath;

    this.dateFormat = new SimpleDateFormat(dateFormat);
    this.dateFormatExcel = dateFormat2;
    this.vkDataEncoding = vkDataEncoding;

    this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat3);
    this.databaseType = databaseType;

    this.groupPrefix = groupPrefix;
    this.readDatabaseFromJar = readDatabaseFromJar;

    Font remarkFont = null;
    try {
      InputStream fontResource =
          Auxiliary.getResourceFromJAR(resPath + remarkFontPath, m.getLogger());
      remarkFont = Font.createFont(Font.TRUETYPE_FONT, fontResource);
    } catch (FontFormatException | IOException e) {
      m.getLogger().log(Level.WARNING, "", e);
    } finally {
      this.remarkFont = remarkFont;
    }

    this.varSet = varSet;
  }

  public DatabaseType getDatabaseType() {
    return databaseType;
  }

  public SimpleDateFormat getDateFormat() {
    return dateFormat;
  }

  public String getDateFormatExcel() {
    return dateFormatExcel;
  }

  public DateTimeFormatter getDateTimeFormatter() {
    return dateTimeFormatter;
  }

  public String getExportPath() {
    return exportPath;
  }

  public String getIconPath() {
    return iconPath;
  }

  public Font getRemarkFont() {
    return remarkFont;
  }

  public String getStringValuesPath() {
    return stringValuesPath;
  }

  public VarSet getVarSet() {
    return varSet;
  }

  public Charset getVkDataEncoding() {
    return vkDataEncoding;
  }

  public String getVkDataPath() {
    return vkDataPath;
  }

  public String getGroupPrefix() {
      return groupPrefix;
  }

  public boolean isReadDatabaseFromJar() {
      return readDatabaseFromJar;
  }
}
