package ca.rightsomegoodgames.mayacharm.resources;

import java.util.ResourceBundle;

/** Singleton front end for the {@code resources/MayaCharm.properties} file
 *  <p>
 *      Typical usage would be like {@code MayaCharmProperties.getString("commandport.host");} to get a string value
 *      (so the{@code "commandport.host"}). Integers can also be fetched using {@code MayaCharmProperties.getInt()}
 *      function
 *  </p>
 *
 */
public class MayaCharmProperties {
    /** Resource bundle initialised from {@code resources.MayaCharm.properties} file */
    private static final ResourceBundle ourBundle = ResourceBundle.getBundle("ca.rightsomegoodgames.mayacharm.resources.MayaCharm");;

    /** Singleton Instance */
    private static MayaCharmProperties ourInstance = new MayaCharmProperties();

    /**Fetch the value for a key as a string
     *
     * @param propertyKey   Name of the property key to fetch value for  e.g. {@code "commandport.host"}
     * @return  Value for the property key as a string e.g. {@code "localhost"}
     */
    public static String getString(String propertyKey) {
        return ourBundle.getString(propertyKey);
    }

    /**Fetch the value for a key as a integer
     *
     * @implNote Not sure if I should catch the exception and return defaultValue (like now) or just let it raise
     *
     * @param propertyKey   Name of the property key to fetch value for  e.g. {@code "commandport.port"}
     * @param defaultValue  Integer value to use in case failure to convert property value (string) to integer
     * @return Value for the property key as a integer e.g. {@code 12345}
     */
    public static int getInt(String propertyKey, int defaultValue) {
        try {
            return Integer.parseInt(ourBundle.getString(propertyKey));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    /**Access the internal resource bundle
     *
     * @return Internal resource bundle used for querying properties
     */
    public static ResourceBundle getResourceBundle() {
        return ourBundle;
    }

    /**Access the singleton instance
     *
     * @return Singleton instance
     */
    public static MayaCharmProperties getInstance() {
        return ourInstance;
    }

    /**Private constructor for singleton
     *
     * @return Singleton instance
     */
    private MayaCharmProperties() {
    }
}
