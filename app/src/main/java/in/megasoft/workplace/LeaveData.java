package in.megasoft.workplace;

public class LeaveData {
    private String name;
    private String leaveDates;

    // Constructor
    public LeaveData(String name, String leaveDates) {
        this.name = name;
        this.leaveDates = leaveDates;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getLeaveDates() {
        return leaveDates;
    }

}

