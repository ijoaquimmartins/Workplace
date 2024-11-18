package in.megasoft.workplace;

public class requestAppliedList {
    private String userL, leaveapptypeL, leavetypeL, leaveidL, useridL, typeL, statusL;
    public requestAppliedList(String userL, String leaveapptypeL, String leavetypeL, String leaveidL, String useridL, String typeL, String statusL) {
        this.userL = userL;
        this.leaveapptypeL = leaveapptypeL;
        this.leavetypeL = leavetypeL;
        this.leaveidL = leaveidL;
        this.useridL = useridL;
        this.typeL = typeL;
        this.statusL = statusL;
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
    public String gettypeL() {
        return typeL;
    }
    public String getStatusL(){return statusL;}
}
