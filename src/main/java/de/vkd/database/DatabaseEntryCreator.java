package de.vkd.database;

/**
 * Creates an entry of an database.
 * @author Felix
 * The type of the entry, that will be created
 * @param <E>
 */
public class DatabaseEntryCreator<E> {
    private ObjectCreator<E> creator;
    private String[] argNames;
    /**
     * Initialized the database creator.
     * @param creator This actually creates the entry (abstract class)
     * @param argNames The names of the parameters, that the {@link ObjectCreator} uses, to create an entry
     */
    public DatabaseEntryCreator(ObjectCreator<E> creator, String... argNames) {
        this.creator = creator;
        this.argNames = argNames;
    }

    /**
     * Returns the names of the parameters, that the {@link ObjectCreator} uses, to create an entry.
     * @return the parameters, that the {@link ObjectCreator} uses, to create an entry
     */
    public String[] getArgNames(){
        return argNames;
    }
    /**
     * Actually creates an Entry.
     * @param args The parameters, the {@link ObjectCreator} uses, to create the object.
     * @return The created object.
     * @throws ReadDataException Thrown if not enough elements were given
     */
    public E create(String... args) throws ReadDataException{
        if(args.length != argNames.length)throw new ReadDataException("Invalid arg length");
        return creator.create(args);
    }
}
