package in.guclink.www.organizer.models;

/**
 * Has a GCM Topic.
 */
public interface Followable {

    String getGCMTopic();
    boolean hasParentTopic();
    String getGCMParentTopic();
}
