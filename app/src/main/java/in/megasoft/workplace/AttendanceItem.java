package in.megasoft.workplace;

public class AttendanceItem {
    private String text;
    private char status;

    public AttendanceItem(String text, char status) {
        this.text = text;
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public char getStatus() {
        return status;
    }
}

