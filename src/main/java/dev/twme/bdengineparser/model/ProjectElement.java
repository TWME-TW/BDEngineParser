package dev.twme.bdengineparser.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents an element in a BD Engine project.
 * This class contains various properties that define the characteristics of the element,
 * such as its name, type, transformations, and children elements.
 */
public class ProjectElement {

    @SerializedName("isCollection")
    private Boolean isCollection;
    @SerializedName("name")
    private String name;
    @SerializedName("nbt")
    private String nbt;
    @SerializedName("transforms")
    private List<Double> transforms;
    @SerializedName("children")
    private List<ProjectElement> children;

    @SerializedName("isBackCollection")
    private Boolean isBackCollection;
    @SerializedName("defaultTransform")
    private DefaultTransform defaultTransform;

    @SerializedName("isItemDisplay")
    private Boolean isItemDisplay;
    @SerializedName("tagHead")
    private TagHead tagHead;
    @SerializedName("textureValueList")
    private List<String> textureValueList;
    @SerializedName("paintTexture")
    private Object paintTexture;
    @SerializedName("defaultTextureValue")
    private String defaultTextureValue;

    @SerializedName("isBlockDisplay")
    private Boolean isBlockDisplay;

    @SerializedName("isTextDisplay")
    private Boolean isTextDisplay;

    @SerializedName("options")
    private TextOptions options;

    @SerializedName("brightness")
    private Brightness brightness;

    /**
     * Gets whether this element is a collection.
     * @return true if this element is a collection, false otherwise
     */
    public Boolean getIsCollection() { return isCollection; }

    /**
     * Sets whether this element is a collection.
     * @param collection true if this element is a collection, false otherwise
     */
    public void setIsCollection(Boolean collection) { isCollection = collection; }

    /**
     * Gets the name of this element.
     * @return the name of the element
     */
    public String getName() { return name; }

    /**
     * Sets the name of this element.
     * @param name the name to set for the element
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the NBT data associated with this element.
     * @return the NBT data as a string
     */
    public String getNbt() { return nbt; }

    /**
     * Sets the NBT data for this element.
     * @param nbt the NBT data to set
     */
    public void setNbt(String nbt) { this.nbt = nbt; }

    /**
     * Gets the transformations applied to this element.
     * @return a list of doubles representing the transformations
     */
    public List<Double> getTransforms() { return transforms; }

    /**
     * Sets the transformations for this element.
     * @param transforms a list of doubles representing the transformations
     */
    public void setTransforms(List<Double> transforms) { this.transforms = transforms; }

    /**
     * Gets the children elements of this element.
     * @return a list of ProjectElement representing the children
     */
    public List<ProjectElement> getChildren() { return children; }

    /**
     * Sets the children elements for this element.
     * @param children a list of ProjectElement representing the children
     */
    public void setChildren(List<ProjectElement> children) { this.children = children; }

    /**
     * Gets whether this element is a back collection.
     * @return true if this element is a back collection, false otherwise
     */
    public Boolean getIsBackCollection() { return isBackCollection; }

    /**
     * Sets whether this element is a back collection.
     * @param backCollection true if this element is a back collection, false otherwise
     */
    public void setIsBackCollection(Boolean backCollection) { isBackCollection = backCollection; }

    /**
     * Gets the default transformation settings for this element.
     * @return a DefaultTransform object representing the default transformations
     */
    public DefaultTransform getDefaultTransform() { return defaultTransform; }

    /**
     * Sets the default transformation settings for this element.
     * @param defaultTransform a DefaultTransform object representing the default transformations
     */
    public void setDefaultTransform(DefaultTransform defaultTransform) { this.defaultTransform = defaultTransform; }

    /**
     * Gets whether this element is displayed as an item.
     * @return true if this element is displayed as an item, false otherwise
     */
    public Boolean getIsItemDisplay() { return isItemDisplay; }

    /**
     * Sets whether this element is displayed as an item.
     * @param itemDisplay true if this element should be displayed as an item, false otherwise
     */
    public void setIsItemDisplay(Boolean itemDisplay) { isItemDisplay = itemDisplay; }

    /**
     * Gets the brightness settings for this element.
     * @return a Brightness object representing the brightness settings
     */
    public Brightness getBrightness() { return brightness; }

    /**
     * Sets the brightness settings for this element.
     * @param brightness a Brightness object representing the brightness settings
     */
    public void setBrightness(Brightness brightness) { this.brightness = brightness; }

    /**
     * Gets the tag head associated with this element.
     * @return a TagHead object representing the tag head
     */
    public TagHead getTagHead() { return tagHead; }

    /**
     * Sets the tag head for this element.
     * @param tagHead a TagHead object representing the tag head
     */
    public void setTagHead(TagHead tagHead) { this.tagHead = tagHead; }

    /**
     * Gets the list of texture values for this element.
     * @return a list of strings representing the texture values
     */
    public List<String> getTextureValueList() { return textureValueList; }

    /**
     * Sets the list of texture values for this element.
     * @param textureValueList a list of strings representing the texture values
     */
    public void setTextureValueList(List<String> textureValueList) { this.textureValueList = textureValueList; }

    /**
     * Gets the paint texture for this element.
     * @return an Object representing the paint texture
     */
    public Object getPaintTexture() { return paintTexture; }

    /**
     * Sets the paint texture for this element.
     * @param paintTexture an Object representing the paint texture
     */
    public void setPaintTexture(Object paintTexture) { this.paintTexture = paintTexture; }

    /**
     * Gets the default texture value for this element.
     * @return a string representing the default texture value
     */
    public String getDefaultTextureValue() { return defaultTextureValue; }

    /**
     * Sets the default texture value for this element.
     * @param defaultTextureValue a string representing the default texture value
     */
    public void setDefaultTextureValue(String defaultTextureValue) { this.defaultTextureValue = defaultTextureValue; }

    /**
     * Gets whether this element is displayed as a block.
     * @return true if this element is displayed as a block, false otherwise
     */
    public Boolean getIsBlockDisplay() { return isBlockDisplay; }

    /**
     * Sets whether this element is displayed as a block.
     * @param blockDisplay true if this element should be displayed as a block, false otherwise
     */
    public void setIsBlockDisplay(Boolean blockDisplay) { isBlockDisplay = blockDisplay; }

    /**
     * Gets whether this element is displayed as text.
     * @return true if this element is displayed as text, false otherwise
     */
    public Boolean getIsTextDisplay() { return isTextDisplay; }

    /**
     * Sets whether this element is displayed as text.
     * @param textDisplay true if this element should be displayed as text, false otherwise
     */
    public void setIsTextDisplay(Boolean textDisplay) { isTextDisplay = textDisplay; }

    /**
     * Gets the text options for this element.
     * @return a TextOptions object representing the text display options
     */
    public TextOptions getOptions() { return options; }

    /**
     * Sets the text options for this element.
     * @param options a TextOptions object representing the text display options
     */
    public void setOptions(TextOptions options) { this.options = options; }

    /**
     * Returns a string representation of the ProjectElement.
     * @return a string containing the name, collection status, display types, and number of children
     */
    @Override
    public String toString() {
        return "ProjectElement{" +
                "name='" + name + '\'' +
                ", isCollection=" + isCollection +
                ", isItemDisplay=" + isItemDisplay +
                ", isBlockDisplay=" + isBlockDisplay +
                ", isTextDisplay=" + isTextDisplay +
                ", childrenCount=" + (children != null ? children.size() : 0) +
                '}';
    }
}
