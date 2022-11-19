package uni.project.mylocalbooking.models;

public abstract class DatabaseModel {
    private Long id;

    public DatabaseModel(Long id) {
        this.id = id;
    }

    public final Long getId() {
        return id;
    }

    public final void setId(Long id) {
        this.id = id;
    }
}
