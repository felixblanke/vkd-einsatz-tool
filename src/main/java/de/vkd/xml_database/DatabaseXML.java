package de.vkd.xml_database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.vkd.database.DatabaseEntryCreator;
import de.vkd.database.DatabaseReturnType;
import de.vkd.database.ReadDataException;

public class DatabaseXML <E> {
    private File dataFile;
    public DatabaseXML(String filePath) {
        this.dataFile = new File(filePath);
    }
    /**
     * Reads data from the database and returns it.
     * @param elementName The
     * @param creator used to create an entry for the database from the read data
     * @param versionName Name of the tag that is used to specify the version in the XML document
     * @param defaultVersion If no version specified, this will be set as version
     * @return the read data
     * @throws ReadDataException thrown, if an error occured while reading from the xml file or if the {@link DatabaseEntryCreator} is not able to create an object from the read data
     * @throws Exception thrown, if the xml file cannot be opened
     */
    public DatabaseReturnType<E> readData(String elementName, ObjectCreatorXML<E> creator, String versionName, String defaultVersion) throws ReadDataException, Exception {
        Document doc = new SAXBuilder().build(dataFile);
        Element parentElement = doc.getRootElement();

        String version;

        List<Element> versionElements = parentElement.getChildren(versionName);

        if(versionElements.isEmpty())version = defaultVersion;
        else if(versionElements.size() == 1) {
            version = versionElements.get(0).getValue().trim();
            if(version.isEmpty())throw new ReadDataException("Empty version!");
        }else throw new ReadDataException("More than one version!");

        List<E> readData = new ArrayList<E>();
        //reading data
        for(Element v: parentElement.getChildren(elementName)){
            readData.add(creator.create(v));
        }

        return new DatabaseReturnType<E>(readData, version);
    }

    //TODO: check for multiple ids in VK

    /**
     * Finds the entry in the xml file belonging to the given value and updates it.
     * assumption: elements are distinctly distinguishable from each other by the ElementValidator
     * @param elementName The name of the xml-tags the elements are stored in
     * @param validator Checks, if a given element is the searched one
     * @param updater Updates the found element accordingly
     * @param value The value, whose entry should be updated
     * @throws JDOMException when errors occur in parsing the XML document
     * @throws IOException when an I/O error prevents a XML document from being fully parsed
     * @throws ElementNotFoundException when no element fitting the searched value was found
     */
    public void updateEntry(String elementName, ElementValidator<E> validator, ElementUpdater<E> updater, E value) throws JDOMException, IOException, ElementNotFoundException {
        Document doc = new SAXBuilder().build(dataFile);
        Element parentElement = doc.getRootElement();

        for(Element v: parentElement.getChildren(elementName)){
            if(validator.isElementValid(v, value)) {
                updater.update(v, value);
                return;
            }
        }

        throw new ElementNotFoundException(value.toString());
    }

    /*
    public void setEntry(XmlEntry changedEntry, XmlEntry[] criteria) throws JDOMException, IOException, ReadDataException, InvalidCriterion, DatabaseEntryNotFoundException{
        Document doc = new SAXBuilder().build(dataFile);

        Element currElement = doc.getRootElement();

        Element searchedElement = getEntryRecursive(currElement, changedEntry.getElementNames(), criteria);
        if(searchedElement!=null)searchedElement.setText(changedEntry.getValue());
        else throw new DatabaseEntryNotFoundException(changedEntry, criteria);

        XMLOutputter xmlOutput = new XMLOutputter();

        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, Auxiliary.getUTF8BufferedWriter(dataFile.getAbsolutePath()));
        System.out.println(dataFile.getAbsolutePath());
    }

    private Element getEntryRecursive(Element currElem, List<String> elementNames, XmlEntry[] criteria) throws InvalidCriterion{
        if(elementNames.isEmpty()){
            if(checkForCriteria(currElem, criteria)){
                return currElem;
            }else return null;
        }
        for(String s: elementNames)System.out.print(s + ", ");
        System.out.println();

        for(Element child: currElem.getChildren(elementNames.get(0))){
            System.out.println("Teste child " + child.getName());
            Element e = getEntryRecursive(child, elementNames.subList(1, elementNames.size()), criteria);
            System.out.println(e== null?"null":"nicht null");
            if(e != null){
                return e;
            }
        }
        return null;
    }

    private boolean checkForCriteria(Element currElem, XmlEntry[] criteria) throws InvalidCriterion{
        for(XmlEntry criterion: criteria){
            System.out.print(criterion + " ");
            if(!checkForCriterion(currElem, criterion)){
                System.out.println("false");return false;
            }
            System.out.println("true");
        }
        return true;
    }
    //TODO: review and COMMENT method
    private boolean checkForCriterion(Element currElem, XmlEntry criterion) throws InvalidCriterion{
        //TODO: INSERT ACTUAL CHECK
//        if(criterion.getElementNames().isEmpty())return currElem.getText().equals(criterion.getValue());
//
//        Element tempElem = currElem;
//        while(!tempElem.getParentElement().isRootElement()){
//            tempElem = tempElem.getParentElement();
//            stepsUpInHierachy++;
//        }
//
//
//        Element focussedElement = stepUpInHierachy(currElem, stepsUpInHierachy);
//        int counter = 0;
//        for(String hierachyStep: criterion.getElementNames()){
//            if(focussedElement.getName().equals(hierachyStep)){
//                counter++;
//                if(stepsUpInHierachy - counter > 0){
//                    focussedElement = stepUpInHierachy(currElem, stepsUpInHierachy - counter);
//                }else if(stepsUpInHierachy - counter < 0){
//
//                }
//            }
//        }
        int maxStepsInHierachy = 0;
        List<String> currElemNames = new ArrayList<String>();
        //TODO: FILL LIST
        Element tempElem = currElem;
        while(!tempElem.isRootElement()){
            currElemNames.add(0, tempElem.getName());
            tempElem = tempElem.getParentElement();
            if(!tempElem.isRootElement())maxStepsInHierachy++;
        }

        boolean contained = true;
        int stepsDownInHierachy = 0;
        Element focussedElement = null;
        for(; stepsDownInHierachy < criterion.getElementNames().size(); stepsDownInHierachy++){
            if(contained){
                if(currElemNames.get(stepsDownInHierachy) != null){
                    if(currElemNames.get(stepsDownInHierachy).equals(criterion.getElementNames().get(stepsDownInHierachy))){
                        continue;
                    }
                }
                focussedElement = stepUpInHierachy(currElem, maxStepsInHierachy - stepsDownInHierachy + 1); //+1 because we need the parent element
                contained = false;
            }

            List<Element> childrenOfFocussedElement = focussedElement.getChildren(criterion.getElementNames().get(stepsDownInHierachy));
            if(childrenOfFocussedElement.size() != 1)throw new InvalidCriterion(criterion, "There are multiple children called " + criterion.getElementNames().get(stepsDownInHierachy) + " of the parent " + focussedElement.getName());
            focussedElement = childrenOfFocussedElement.get(0);
        }

        if(contained){
            return currElem.getText().equals(criterion.getValue());
        }else{
            return focussedElement.getText().equals(criterion.getValue());
        }
    }
    private Element stepUpInHierachy(Element currElem, int steps){
        Element rootElement = currElem;
        for(int i = 0; i < steps; i++){
            if(rootElement.isRootElement())return rootElement;
            rootElement = rootElement.getParentElement();
        }
        return rootElement;
    }

    */
    public String getFilePath(){
        return dataFile.getAbsolutePath();
    }
    public void setFilePath(String filePath){
        this.dataFile = new File(filePath);
    }
}
