package uni.project.mylocalbooking.activities.provider;

public class ModelPrenotationToday {
    private String title;
    private String clientName;
    private String fromHour;
    private String toHour;
    private String numberPrenotation;

    public ModelPrenotationToday(String title, String clientName, String fromHour, String toHour, String numberPrenotation) {
        this.title = title;
        this.clientName = clientName;
        this.fromHour = fromHour;
        this.toHour = toHour;
        this.numberPrenotation = numberPrenotation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFromHour() {
        return fromHour;
    }

    public void setFromHour(String fromHour) {
        this.fromHour = fromHour;
    }

    public String getToHour() {
        return toHour;
    }

    public void setToHour(String toHour) {
        this.toHour = toHour;
    }

    public String getNumberPrenotation() {
        return numberPrenotation;
    }

    public void setNumberPrenotation(String numberPrenotation) {
        this.numberPrenotation = numberPrenotation;
    }
}
