package uni.project.mylocalbooking;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import uni.project.mylocalbooking.models.Client;
import uni.project.mylocalbooking.models.Coordinates;
import uni.project.mylocalbooking.models.Establishment;
import uni.project.mylocalbooking.models.ManualSlot;
import uni.project.mylocalbooking.models.ManualSlotBlueprint;
import uni.project.mylocalbooking.models.PeriodicSlot;
import uni.project.mylocalbooking.models.PeriodicSlotBlueprint;
import uni.project.mylocalbooking.models.Provider;
import uni.project.mylocalbooking.models.SlotBlueprint;

public class MockAPI {
    private static final Random random = new Random();

    private static class ListRandomizer<T> {
        public List<T> selectRandom(List<T> list, Integer amount) {
            Collections.shuffle(list);
            int start;

            if(amount == null) {
                start = random.nextInt(list.size());
                amount = start + random.nextInt(list.size() - start);
            } else
                start = list.size() - amount - random.nextInt(amount + 1);

            return list.subList(start, start + amount);
        }
    }

    private static final Provider provider = new Provider(true, null, 3, new HashMap<>(), "3475322555", "nicola.marizza@gmail.com", "Nicola", "Marizza", LocalDate.of(2000, 6, 10), null);
    private static final Establishment establishment = new Establishment(provider, "Campetto da basket", "via ciao 13", new Coordinates(12, 12), "asefas");
    private static final List<Client> clients = new ArrayList<>();

    private static boolean initialized = false;

    public static List<SlotBlueprint> generateManualData() {
        init();
        HashSet<DayOfWeek> weekdays = new HashSet<>();
        weekdays.add(DayOfWeek.FRIDAY);
        weekdays.add(DayOfWeek.SUNDAY);

        LocalDate today = LocalDate.now();

        ManualSlotBlueprint b0 = new ManualSlotBlueprint(
                LocalTime.of(8, 0), LocalTime.of(12, 0),
                Duration.ofMinutes(90), establishment, 15, weekdays,
                today.minusWeeks(1), today.plusWeeks(1)
        );
        ManualSlotBlueprint b1 = new ManualSlotBlueprint(
                LocalTime.of(15, 0), LocalTime.of(18, 0),
                Duration.ofMinutes(90), establishment, 15, weekdays,
                today.minusWeeks(1), today.plusWeeks(1)
        );

        List<ManualSlot> s0 = new ArrayList<>();
        s0.add(new ManualSlot(
                LocalTime.of(8, 30), LocalTime.of(11, 15),
                LocalDate.now(), provider, b0
        ));
        List<ManualSlot> s1 = new ArrayList<>();
        s1.add(new ManualSlot(
                LocalTime.of(15, 0), LocalTime.of(16, 30),
                LocalDate.now(), provider, b1
        ));
        s1.add(new ManualSlot(
                LocalTime.of(16, 30), LocalTime.of(17, 45),
                LocalDate.now(), provider, b1
        ));

        List<SlotBlueprint> blueprints = new ArrayList<>();
        blueprints.add(b0);
        blueprints.add(b1);

        return blueprints;
    }


    public static List<SlotBlueprint> generatePeriodicData() {
        init();
        HashSet<DayOfWeek> everyday = new HashSet<>();
        HashSet<DayOfWeek> workdays = new HashSet<>();
        everyday.add(DayOfWeek.MONDAY);
        everyday.add(DayOfWeek.TUESDAY);
        everyday.add(DayOfWeek.WEDNESDAY);
        everyday.add(DayOfWeek.THURSDAY);
        everyday.add(DayOfWeek.FRIDAY);
        everyday.add(DayOfWeek.SATURDAY);
        everyday.add(DayOfWeek.SUNDAY);

        workdays.add(DayOfWeek.MONDAY);
        workdays.add(DayOfWeek.TUESDAY);
        workdays.add(DayOfWeek.WEDNESDAY);
        workdays.add(DayOfWeek.THURSDAY);
        workdays.add(DayOfWeek.FRIDAY);

        LocalDate today = LocalDate.now();

        List<SlotBlueprint> blueprints = new ArrayList<>();
        for (LocalTime start = LocalTime.of(8, 0); start.plusMinutes(90).compareTo(LocalTime.of(22, 0)) < 0; start = start.plusMinutes(90)) {
            Integer reservationLimit = random.nextInt(11) > 6 ? null : random.nextInt(31);
            LocalTime end = start.plusMinutes(90);
            blueprints.add(new PeriodicSlotBlueprint(
                    start, end, establishment, reservationLimit,
                    end.compareTo(LocalTime.of(13, 0)) < 0 ? everyday : workdays,
                    today.minusWeeks(1), today.plusWeeks(1)
            ));
        }

        for (SlotBlueprint instantiatedBlueprint : blueprints) {
            for (LocalDate date = instantiatedBlueprint.fromDate; date.compareTo(instantiatedBlueprint.toDate) < 0; date = date.plusDays(1)) {
                int bookedPeopleAmount = instantiatedBlueprint.reservationLimit != null ? instantiatedBlueprint.reservationLimit : 15;
                if(random.nextInt(11) > 6)
                    bookedPeopleAmount = random.nextInt(bookedPeopleAmount + 1);

                if(bookedPeopleAmount == 0)
                    continue;

                PeriodicSlot slot = new PeriodicSlot(date, provider, (PeriodicSlotBlueprint) instantiatedBlueprint);
                if(bookedPeopleAmount > 0 && random.nextInt(11) > 5)
                    slot.passwordProtected = true;
                slot.reservations.addAll(new ListRandomizer<Client>().selectRandom(clients, bookedPeopleAmount));
            }
        }

        return blueprints;
    }

    private static void init() {
        if(initialized)
            return;

        initialized = true;
        for(int i = 0; i < 100; i++)
           clients.add(new Client(new Coordinates(1,1), "", "", "", "", LocalDate.now(), null));
    }
}
