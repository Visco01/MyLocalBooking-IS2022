package uni.project.mylocalbooking.models;

import java.util.Collection;

public class Establishment extends DatabaseModel {
    public final Provider provider;
    public final String name;
    public final String address;
    public final Coordinates position;
    public final String placeId;

    public Collection<SlotBlueprint> blueprints;

    public Establishment(Long id, Provider provider, String name, String address, Coordinates position, String placeId) {
        super(id);
        this.provider = provider;
        this.name = name;
        this.address = address;
        this.position = position;
        this.placeId = placeId;
    }

    public Establishment(Long id, String name, String address, Coordinates position, String placeId) {
        super(id);
        this.placeId = placeId;
        this.provider = null;
        this.name = name;
        this.address = address;
        this.position = position;
    }

    public Establishment(Provider provider, String name, String address, Coordinates position, String placeId) {
        this(null, provider, name, address, position, placeId);
    }
}
