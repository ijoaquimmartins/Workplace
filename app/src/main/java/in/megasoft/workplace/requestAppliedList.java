package in.megasoft.workplace;

public class requestAppliedList {
    private String userL, leaveapptypeL, leavetypeL, leaveidL, useridL;
    public requestAppliedList(String userL, String leaveapptypeL, String leavetypeL, String leaveidL, String useridL) {
        this.userL = userL;
        this.leaveapptypeL = leaveapptypeL;
        this.leavetypeL = leavetypeL;
        this.leaveidL = leaveidL;
        this.useridL = useridL;
    }
    public String getUserL() {
        return userL;
    }
    public String getLeaveapptypeL() {
        return leaveapptypeL;
    }
    public String getLeavetypeL() {
        return leavetypeL;
    }
    public String getLeaveidL() {
        return leaveidL;
    }
    public String getUseridL() {
        return useridL;
    }
}
