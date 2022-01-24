package de.vkd.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.vkd.auxiliary.Auxiliary;
import de.vkd.auxiliary.ComparatorChain;
import de.vkd.database.DatabaseReturnType;
import de.vkd.gui.Console;
import de.vkd.gui.TextAreaOutputStream;


public class Framework<E> {
    public final Logger log = Logger.getLogger(Framework.class.getName());
    private final Map<String, String> stringValMap = new HashMap<String, String>();
    private Console console;

    private List<Variable> sysVarList;

    private VarSet varSet;
//    private String systemVariableMarker;
//    private String localVariableMarker;
//    private String argSplitter;
//    private String xml_var;
//    private String xml_var_name;
//    private String xml_var_value;
//    private String xml_var_args;
//    private String xml_var_argument;
//    private String xml_value;
//    private String xml_value_name;
//    private String xml_value_text;
    private List<E> database;
    private String version;

    public Framework(Level loggerLevel){
        this(loggerLevel, null, true);
    }
    public Framework(Level loggerLevel, boolean openConsole) {
        this(loggerLevel, null, openConsole);
    }
    public Framework(Level loggerLevel, String consoleIconPath) {
        this(loggerLevel, consoleIconPath, true);
    }
    public Framework(Level loggerLevel, String consoleIconPath, boolean openConsole) {
        if(openConsole){
            console = new Console();
            System.setErr(new PrintStream(new TextAreaOutputStream(console.getTextPane())));
        }
        loadLogger(loggerLevel);
    }

    private void loadLogger(Level loggerLevel){
        log.setLevel(Level.OFF);
        log.setUseParentHandlers(false);
        Handler handler = new ConsoleHandler();
        handler.setLevel( Level.ALL );
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                if(console!=null)console.scrollToBottom();
                boolean printStackTrace = false;
                StringBuilder sb = new StringBuilder();
                if(record.getLevel().equals(Level.INFO)||record.getLevel().equals(Level.WARNING)||record.getLevel().equals(Level.SEVERE))sb.append("[" + record.getLevel() + "] ");
                sb.append(new SimpleDateFormat("dd.MM.yyyy kk:mm").format(record.getMillis()));

//                LOG JAVA CLASSES
//                sb.append(" in ");
//                sb.append(record.getSourceClassName());
//                if(record.getSourceMethodName() != null)
//                sb.append(", " + record.getSourceMethodName());
                sb.append(": ");
                if(record.getThrown()!=null){
                    sb.append(record.getThrown().getClass().getName() + ": " + record.getThrown().getMessage());
                    printStackTrace = true;
                }else{
                    sb.append(record.getMessage());
                }
                sb.append("\n");
                if(printStackTrace){
                    for(StackTraceElement st: record.getThrown().getStackTrace()){
                        sb.append("\tat " + st.getClassName() + "." + st.getMethodName() + "(" + st.getFileName() + ":" + st.getLineNumber() + ")\n");
                    }
                }
                return sb.toString();
            }
        });
        log.addHandler( handler );
        log.setLevel( loggerLevel );
    }

    public void initVars(VarSet varSet) {
        this.varSet = varSet;
    }
    /*public void initVars(
            String systemVariableMarker, String localVariableMarker, String argSplitter,
            String xml_var, String xml_var_name, String xml_var_value, String xml_var_args, String xml_var_argument,
            String xml_value, String xml_value_name, String xml_value_text
            ){
        this.systemVariableMarker = systemVariableMarker;
        this.localVariableMarker = localVariableMarker;
        this.argSplitter = argSplitter;
        this.xml_var = xml_var;
        this.xml_var_name = xml_var_name;
        this.xml_var_value = xml_var_value;
        this.xml_var_args = xml_var_args;
        this.xml_var_argument = xml_var_argument;
        this.xml_value = xml_value;
        this.xml_value_name = xml_value_name;
        this.xml_value_text = xml_value_text;
    }*/

    public void setSysVarList(List<Variable> sysVarList) {
        this.sysVarList = sysVarList;
    }

    /**
     * Loads the Strings into {@code enumMap} to be used in {@link Main#getString(StringValue)}
     * @param enumMap The map, the loaded data should be stored in
     * @param filedir The path of the file (.xml) from which the data should be loaded
     * @throws JDOMException Throws this Exception, if the XML-File has invalid lines of code.
     * @throws IOException Throws this Exception, if the File was not found.
     */
    public void loadStringValuesFromXML(String filedir, boolean loadFromJar) throws Exception{
        log.log(Level.FINER, "loading StringValues from " + filedir);
        Document doc;
        if(loadFromJar) {
            InputStream resource = Auxiliary.getResourceFromJAR(filedir, log);
            doc = new SAXBuilder().build(resource);
        } else {
            doc = new SAXBuilder().build(filedir);
        }

        Element lang = doc.getRootElement();

        //loading vars from XML-File
        List<Variable> localVarList = new ArrayList<Variable>();

        for(Element v: lang.getChildren(this.varSet.get("xml_var"))){
            String varName = v.getChildText(this.varSet.get("xml_var_name"));
            String varValue = v.getChildText(this.varSet.get("xml_var_value"));
            boolean b = false;
            for(Variable temp: localVarList)if(temp.getName().equals(varName))b=true;
            if(!b){
                Element temp = v.getChild(this.varSet.get("xml_var_args"));
                if(temp!=null){
                    List<Element> varArgs = temp.getChildren(this.varSet.get("xml_var_argument"));
                    String[] strArr = new String[varArgs.size()];
                    for(int i = 0; i < varArgs.size(); i++){
                        strArr[i] = varArgs.get(i).getText();
                    }
                    localVarList.add(new Variable(varName, varValue, strArr));
                }else localVarList.add(new Variable(varName, varValue));
            }
        }


        //loading Strings from XML-File
        for(Element v: lang.getChildren(this.varSet.get("xml_value"))){
            String valName = v.getAttributeValue(this.varSet.get("xml_value_name"));
            String valText = v.getChildText(this.varSet.get("xml_value_text"));
            //replacing vars set in the XML-File (local) and the ones set in the program (system)
            valText = replaceVars(valText, sysVarList, this.varSet.get("xml_sysVar_marker"), this.varSet.get("xml_func_arg_splitter"));
            valText = replaceVars(valText, localVarList, this.varSet.get("xml_localVar_marker"), this.varSet.get("xml_func_arg_splitter"));
            if(!containsString(valName))
                addStringValue(valName, valText);
        }
        log.log(Level.FINER, "StringValues loaded");
    }
    /**
     * The loading of the Strings from the XML-File supports a very basic kind of variables and "functions".
     * The variables are simple String stored in the XML-File itself and referred to in the entries which deFINER a specific String.
     * To refer to this variable in the String, one should sourround the variable name with the deFINERd variableMarker, e.g. {@code ?EL?} (? as the variableMarker)
     * The methods are basically a scheme with some placeholders and the args (only Strings) are set in place of the placeholders.
     * This code checks if the current String uses a variable using {@code vars} and checks then, if the variable has args attached to it by brackets.
     * If either is true, the variable/method is replaced accordingly and the method checks for the next variable/method.
     * If each known variable/method is checked, the String is returned.
     * @param input A String read from the XML-File. It could contain variables/methods potentially.
     * @param vars A List of all known variables/methods
     * @param variableMarker A marker which signifies the begin/end of a variable/method, e.g. {@code '?'} in {@code ?EL?}
     * @param argSplitter A marker which is used to differentiate between multiple arguments, e.g. {@code ','} in {@code ?METHOD?(ARG0,ARG1,ARG2)}
     * @return The processed input
     */
    private String replaceVars(String input, List<Variable> vars, String variableMarker, String argSplitter){
        if(vars == null) return input;
        for(Variable s: vars){
            boolean repeat;
            do{
                repeat = false;
                int index = input.indexOf(variableMarker + s.getName() + variableMarker);
                int varLength = (variableMarker + s.getName() + variableMarker).length();
                if(index > -1){
                    boolean replace = true;
                    if(index + varLength < input.length()){
                        char c = input.charAt(index + varLength);
                        //check for brackets
                        if(c == '('){
                            if(input.indexOf(')', index + varLength) > -1){
                                List<String> argList = new ArrayList<String>();
                                String bracketContent = input.substring(index + varLength + 1, input.indexOf(')', index + varLength));
                                String[] argArr = bracketContent.split("[" + argSplitter + "]");
                                for(String arg: argArr){
                                    arg = arg.trim();
                                    if(arg.isEmpty())continue;
                                    else{
                                        argList.add(arg);
                                    }
                                }
                                input = input.replaceFirst("\\" + variableMarker + s.getName() + "\\" + variableMarker + "\\(" + bracketContent + "\\)", s.getValue(this.varSet.get("xml_localVar_marker"), argList.toArray(new String[0])));
                                replace = false;
                                repeat = true;
                            }
                        //check for escape
                        }else if(c == '\\'){
                            if(index + varLength + 1 < input.length()){
                                if(input.charAt(index+varLength + 1) == '('){
                                    input = input.substring(0, index+varLength + 1).concat(input.substring(index+varLength+2));
                                }
                            }
                        }
                    }
                    //not a method - regular var
                    if(replace){
                        input = input.replaceAll("\\" + variableMarker + s.getName() + "\\" + variableMarker, s.getValue(this.varSet.get("xml_localVar_marker")));
                    }
                }
            }while(repeat);
        }
        return input;
    }

    public void addStringValue(String name, String value){
        stringValMap.put(name, value);
    }

    /**
     * Returns the String belonging to the {@link StringValue}
     * @param stringName The StringValue, whose  String should be returned
     * @return The String belonging to the StringValue. If the StringValue has no String, null is returned instead.
     * @throws StringNotContainedException
     */
    public String getString(String stringName){
        return stringValMap.get(stringName);
    }
    public boolean containsString(String stringName){
        return stringValMap.containsKey(stringName);
    }
    public Logger getLogger() {
        return log;
    }
    public Console getConsole() {
        return console;
    }
    public List<E> getDatabase() {
        return new ArrayList<E>(this.database);
    }
    public String getVersion() {
        return version;
    }
    public void setDatabase(DatabaseReturnType<E> databaseReturnType) {
        this.database = databaseReturnType.getReadData();
        this.version = databaseReturnType.getVersion();
    }
    /**
     * Sorts a List using a {@code ComparatorChain}. If comparatorChain is null, the original list is returned
     * @param l The List that should be sorted
     * @param chain The Chain that dictates the order used to sort the List
     * @return The sorted List or the original list, if comparatorChain is null
     */
    public static <T> List<T> sort(List<T> unsortedList, ComparatorChain<T> comparatorChain){
        List<T> temp = new ArrayList<T>(unsortedList);
        if(comparatorChain == null)return temp;
        Collections.sort(temp, comparatorChain);
        return temp;
    }
}
