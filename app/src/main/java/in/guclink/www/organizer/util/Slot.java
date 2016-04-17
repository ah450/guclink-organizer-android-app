package in.guclink.www.organizer.util;



public class Slot {
    public boolean isFree, isLecture, isTutorial, isLab;
    public String location, name, group;

    @Override
    public String toString() {
        return "Location: " + location + "\nName: " + name + "\nGroup: " + group + "\nisLab: " + isLab;
    }

}
