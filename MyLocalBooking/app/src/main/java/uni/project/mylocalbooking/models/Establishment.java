package uni.project.mylocalbooking.models;

import java.util.Collection;

public class Establishment implements IDatabaseModel {
    private final Long id;
    public final Provider provider;
    public final String name;
    public final String address;
    public final Coordinates position;

    public Collection<SlotBlueprint> blueprints;

    public Establishment(Long id, Provider provider, String name, String address, Coordinates position) {
        this.id = id;
        this.provider = provider;
        this.name = name;
        this.address = address;
        this.position = position;
    }

    public Establishment(Provider provider, String name, String address, Coordinates position) {
        this(null, provider, name, address, position);
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
