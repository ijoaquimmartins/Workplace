package in.megasoft.workplace;

import java.util.List;

public class Employee {
    private String name;
    private List<String> leaveDates;

    public Employee(String name, List<String> leaveDates) {
        this.name = name;
        this.leaveDates = leaveDates;
    }

    public String getName() {
        return name;
    }

    public List<String> getLeaveDates() {
        return leaveDates;
    }
}
