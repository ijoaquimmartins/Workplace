package in.megasoft.workplace;

public class LocationTimings {
    private String id;
    private String name;
    private String intime;
    private String outtime;

    public LocationTimings(String id, String name, String intime, String outtime) {
        this.id = id;
        this.name = name;
        this.intime = intime;
        this.outtime = outtime;
    }
    public String getId() { return id; }
    public String getname() { return name; }
    public String getIntime() { return intime; }
    public String getOuttime() { return outtime; }

    public void setIntime(String intime) { this.intime = intime; }
    public void setOuttime(String outtime) { this.outtime = outtime; }
}
